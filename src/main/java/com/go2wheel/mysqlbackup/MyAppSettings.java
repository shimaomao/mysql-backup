package com.go2wheel.mysqlbackup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.util.ExceptionUtil;
import com.go2wheel.mysqlbackup.util.StringUtil;

@ConfigurationProperties(prefix = "myapp")
@Component
public class MyAppSettings {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private SshConfig ssh;

	private String dataDir;

	private Path dataRoot;

	private String downloadFolder;

	private Path downloadRoot;
	
	private Set<String> storageExcludes;

	@PostConstruct
	public void post() throws IOException {
		try {
			if (!StringUtil.hasAnyNonBlankWord(dataDir)) {
				this.dataDir = "boxes";
			}
			Path tmp = Paths.get(this.dataDir);

			if (!Files.exists(tmp)) {
				Files.createDirectories(tmp);
			}
			this.dataRoot = tmp;

			logger.error("downloadFolder cofiguration value is: {}", this.downloadFolder);

			tmp = Paths.get(this.downloadFolder);

			if (!Files.exists(tmp)) {
				Files.createDirectories(tmp);
			}
			this.downloadRoot = tmp;
		} catch (Exception e) {
			ExceptionUtil.logErrorException(logger, e);
		}
	}

	private Path getHostDir(Server server) {
		return getDataRoot().resolve(server.getHost());
	}

	public Path getLogBinDir(Server server) {
		Path dstDir = getHostDir(server).resolve("logbin");
		if (!Files.exists(dstDir) || Files.isRegularFile(dstDir)) {
			try {
				Files.createDirectories(dstDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dstDir;
	}

	public Path getBorgRepoDir(Server server) {
		Path dstDir = getHostDir(server).resolve("repo");
		if (!Files.exists(dstDir) || Files.isRegularFile(dstDir)) {
			try {
				Files.createDirectories(dstDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return dstDir;
	}

	public Path getDumpDir(Server server) throws IOException {
		Path dstDir = getHostDir(server).resolve("dump");
		if (!Files.exists(dstDir) || Files.isRegularFile(dstDir)) {
			Files.createDirectories(dstDir);
		}
		return dstDir;
	}

	public Path getLocalMysqlDir(Server server) throws IOException {
		Path dstDir = getHostDir(server).resolve("mysql");
		if (!Files.exists(dstDir) || Files.isRegularFile(dstDir)) {
			Files.createDirectories(dstDir);
		}
		return dstDir;
	}

	public SshConfig getSsh() {
		return ssh;
	}

	public void setSsh(SshConfig ssh) {
		this.ssh = ssh;
	}

	public void setDataDir(String dataDir) {
		this.dataDir = dataDir;
	}

	public Path getDataRoot() {
		return dataRoot;
	}

	public void setDataRoot(Path dataRoot) {
		this.dataRoot = dataRoot;
	}

	public String getDownloadFolder() {
		return downloadFolder;
	}

	public void setDownloadFolder(String downloadFolder) {
		this.downloadFolder = downloadFolder;
	}

	public Path getDownloadRoot() {
		return downloadRoot;
	}

	public void setDownloadRoot(Path downloadRoot) {
		this.downloadRoot = downloadRoot;
	}

	public Set<String> getStorageExcludes() {
		return storageExcludes;
	}

	public void setStorageExcludes(Set<String> storageExcludes) {
		this.storageExcludes = storageExcludes;
	}

	public static class SshConfig {
		private String sshIdrsa;
		private String knownHosts;

		public String getSshIdrsa() {
			return sshIdrsa;
		}

		public void setSshIdrsa(String sshIdrsa) {
			this.sshIdrsa = sshIdrsa;
		}

		public String getKnownHosts() {
			return knownHosts;
		}

		public void setKnownHosts(String knownHosts) {
			this.knownHosts = knownHosts;
		}

		public boolean knownHostsExists() {
			return knownHosts != null && !knownHosts.trim().isEmpty() && Files.exists(Paths.get(knownHosts));
		}

		public boolean sshIdrsaExists() {
			return sshIdrsa != null && !sshIdrsa.trim().isEmpty() && Files.exists(Paths.get(sshIdrsa.trim()));
		}
	}
}
