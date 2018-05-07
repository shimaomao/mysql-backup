package com.go2wheel.mysqlbackup.borg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go2wheel.mysqlbackup.MyAppSettings;
import com.go2wheel.mysqlbackup.aop.Exclusive;
import com.go2wheel.mysqlbackup.exception.CreateDirectoryException;
import com.go2wheel.mysqlbackup.exception.RunRemoteCommandException;
import com.go2wheel.mysqlbackup.exception.ScpToException;
import com.go2wheel.mysqlbackup.http.FileDownloader;
import com.go2wheel.mysqlbackup.util.ExceptionUtil;
import com.go2wheel.mysqlbackup.util.Md5Checksum;
import com.go2wheel.mysqlbackup.util.RemotePathUtil;
import com.go2wheel.mysqlbackup.util.SSHcommonUtil;
import com.go2wheel.mysqlbackup.util.ScpUtil;
import com.go2wheel.mysqlbackup.util.StringUtil;
import com.go2wheel.mysqlbackup.util.TaskLocks;
import com.go2wheel.mysqlbackup.value.BorgBackupDescription;
import com.go2wheel.mysqlbackup.value.BorgListResult;
import com.go2wheel.mysqlbackup.value.BorgPruneResult;
import com.go2wheel.mysqlbackup.value.Box;
import com.go2wheel.mysqlbackup.value.FileInAdirectory;
import com.go2wheel.mysqlbackup.value.InstallationInfo;
import com.go2wheel.mysqlbackup.value.RemoteCommandResult;
import com.jcraft.jsch.Session;

@Component
public class BorgTaskFacade {

	private Logger logger = LoggerFactory.getLogger(getClass());

	// https://borgbackup.readthedocs.io/en/stable/quickstart.html
	// sudo cp borg-linux64 /usr/local/bin/borg
	// sudo chown root:root /usr/local/bin/borg
	// sudo chmod 755 /usr/local/bin/borg
	// ln -s /usr/local/bin/borg /usr/local/bin/borgfs

	public static final String BORG_BINARY_URL = "https://github.com/borgbackup/borg/releases/download/1.1.5/borg-linux64";
	public static final String REMOTE_BORG_BINARY = "/usr/local/bin/borg";

	private MyAppSettings appSettings;
	
	private FileDownloader fileDownloader;

	private InstallationInfo getInstallationInfo(Session session) throws RunRemoteCommandException {
		InstallationInfo ii = new InstallationInfo();
		RemoteCommandResult rcr;
		rcr = SSHcommonUtil.runRemoteCommand(session, "which borg;borg -V");
		if (rcr.getExitValue() == 0) {
			List<String> lines = rcr.getAllTrimedNotEmptyLines();
			ii.setExecutable(lines.get(0));
			ii.setVersion(lines.get(1));
			ii.setInstalled(true);
		}
		return ii;
	}

	public InstallationInfo unInstall(Session session) throws RunRemoteCommandException {
		InstallationInfo ii = getInstallationInfo(session);
		if (ii.isInstalled()) {
			SSHcommonUtil.deleteRemoteFile(session, REMOTE_BORG_BINARY);
			return getInstallationInfo(session);
		} else {
			return ii;
		}
	}

	public InstallationInfo install(Session session) throws RunRemoteCommandException {
		InstallationInfo ii = getInstallationInfo(session);
		if (!ii.isInstalled()) {
			try {
				uploadBinary(session);
				String cmd = String.format("chown root:root %s;chmod 755 %s", REMOTE_BORG_BINARY, REMOTE_BORG_BINARY);
				SSHcommonUtil.runRemoteCommand(session, cmd);
				return getInstallationInfo(session);
			} catch (ScpToException e) {
				ii.setFailReason(e.getMessage());
				ii.setInstalled(false);
				ExceptionUtil.logErrorException(logger, e);
			}
		}
		return ii;
	}

	public void uploadBinary(Session session) throws ScpToException {
		Path localPath = appSettings.getDownloadRoot().resolve(StringUtil.getLastPartOfUrl(BORG_BINARY_URL));
		if (Files.exists(localPath)) {
			ScpUtil.to(session, localPath.toString(), REMOTE_BORG_BINARY);
		} else {
			try {
				fileDownloader.download(BORG_BINARY_URL);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public RemoteCommandResult initRepo(Session session, String repoPath) throws RunRemoteCommandException {
		RemoteCommandResult rcr = SSHcommonUtil.runRemoteCommand(session,
				String.format("borg init --encryption=none %s", repoPath));
		if (rcr.isCommandNotFound()) {
			return rcr;
		}
		if (rcr.getExitValue() != 0) {
			boolean isFileNotFound = rcr.getAllTrimedNotEmptyLines().stream()
					.anyMatch(line -> line.contains("FileNotFoundError"));
			if (isFileNotFound) {
				String parentPath = RemotePathUtil.getParentWithEndingSlash(repoPath);
				rcr = SSHcommonUtil.runRemoteCommand(session, String.format("mkdir -p %s", parentPath));
				rcr = SSHcommonUtil.runRemoteCommand(session,
						String.format("borg init --encryption=none %s", repoPath));
			}
		}
		return rcr;
	}
	
	public BorgPruneResult pruneRepo(Session session, Box box) throws RunRemoteCommandException {
		String cmd = String.format("borg prune --list --prefix %s --show-rc --keep-daily %s --keep-weekly %s --keep-monthly %s %s", box.getBorgBackup().getArchiveNamePrefix(), 7, 4, 6, box.getBorgBackup().getRepo());
		return new BorgPruneResult(SSHcommonUtil.runRemoteCommand(session, cmd));
	}
	
	public void downloadRepo(Session session, Box box) throws RunRemoteCommandException {
		String findCmd = String.format("find %s -type f -printf '%%s->%%p\n'", box.getBorgBackup().getRepo());
		RemoteCommandResult rcr = SSHcommonUtil.runRemoteCommand(session, findCmd);
		FileInAdirectory fid = new FileInAdirectory(rcr);
		
		String md5Cmd = String.format("md5sum %s", fid.getFileNamesSeparatedBySpace());
		rcr = SSHcommonUtil.runRemoteCommand(session, md5Cmd);
		List<String> md5s = rcr.getAllTrimedNotEmptyLines().stream().map(line -> line.split("\\s+", 2)).filter(ss -> ss.length == 2).map(ss -> ss[0]).collect(Collectors.toList());
		fid.setMd5s(md5s);
		
		final Path rRepo = Paths.get(box.getBorgBackup().getRepo());
		final Path localRepo = appSettings.getBorgRepoDir(box);
		
		fid.getFiles().stream().map(f -> {
			f.setLfileAbs(localRepo.resolve(rRepo.relativize(Paths.get(f.getRfileAbs()))));
			return f;
		}).forEach(f -> {
			try {
				if (Files.exists(f.getLfileAbs())) {
					String m = Md5Checksum.getMD5Checksum(f.getLfileAbs());
					if (m.equalsIgnoreCase(f.getMd5())) {
						f.setDownloaded(true);
						return;
					}
				}
				SSHcommonUtil.downloadWithTmpDownloadingFile(session, f.getRfileAbs(), f.getLfileAbs());
				f.setDownloaded(true);
			} catch (RunRemoteCommandException e) {
				e.printStackTrace();
			} catch (CreateDirectoryException e) {
				e.printStackTrace();
			}
		});
		
		// delete the files already deleted from remote repo.
		try {
			Files.walk(localRepo).filter(p -> Files.isRegularFile(p)).filter(p -> !fid.fileExists(p)).forEach(p -> {
				try {
					Files.delete(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Exclusive(TaskLocks.TASK_BORG)
	public RemoteCommandResult archive(Session session, Box box, String archiveNamePrefix)
			throws RunRemoteCommandException {
//		Lock lock = TaskLocks.getBoxLock(box.getHost(), TaskLocks.TASK_BORG);
		BorgBackupDescription borgDescription = box.getBorgBackup();
//		if (lock.tryLock()) {
//			try {
				List<String> cmdparts = new ArrayList<>();
				cmdparts.add("borg create --stats --verbose --compression lz4 --exclude-caches");
				for (String f : borgDescription.getExcludes()) {
					cmdparts.add("--exclude");
					cmdparts.add("'" + f + "'");
				}
				cmdparts.add(borgDescription.getRepo() + "::" + archiveNamePrefix
						+ new SimpleDateFormat(borgDescription.getArchiveFormat()).format(new Date()));
				for (String f : borgDescription.getIncludes()) {
					cmdparts.add(f);
				}
				String cmd = String.join(" ", cmdparts);
				return SSHcommonUtil.runRemoteCommand(session, cmd);
//			} finally {
//				lock.unlock();
//			}
//		} else {
//			return RemoteCommandResult.failedResult("任务进行中，请稍后再试。");
//		}
		// borg create /borg/repos/trepo::Monday /etc
	}

	@Autowired
	public void setAppSettings(MyAppSettings appSettings) {
		this.appSettings = appSettings;
	}
	
	

	public BorgListResult listArchives(Session session, Box box) throws RunRemoteCommandException {
		return new BorgListResult(SSHcommonUtil.runRemoteCommand(session, String.format("borg list %s", box.getBorgBackup().getRepo())));
	}

	public RemoteCommandResult listRepoFiles(Session session, Box box) throws RunRemoteCommandException {
		return SSHcommonUtil.runRemoteCommand(session, String.format("ls -lR %s", box.getBorgBackup().getRepo()));
	}
	
	@Autowired
	public void setFileDownloader(FileDownloader fileDownloader) {
		this.fileDownloader = fileDownloader;
	}
}
