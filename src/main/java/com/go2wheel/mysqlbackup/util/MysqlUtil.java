package com.go2wheel.mysqlbackup.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go2wheel.mysqlbackup.MyAppSettings;
import com.go2wheel.mysqlbackup.commands.BackupCommand;
import com.go2wheel.mysqlbackup.mysqlcfg.MyCnfContentGetter;
import com.go2wheel.mysqlbackup.mysqlcfg.MyCnfFirstExist;
import com.go2wheel.mysqlbackup.mysqlcfg.MysqlCnfFileLister;
import com.go2wheel.mysqlbackup.value.RemoteCommandResult;
import com.go2wheel.mysqlbackup.value.Box;
import com.go2wheel.mysqlbackup.value.MyCnfFileLikeHolder;
import com.go2wheel.mysqlbackup.yml.YamlInstance;
import com.jcraft.jsch.Session;

@Component
public class MysqlUtil {
	
	private SshSessionFactory sshClientFactory;
	
	private MyAppSettings appSettings;

	public MyCnfFileLikeHolder getMycnf(Box box) {
		Session sshSession = sshClientFactory.getConnectedSession(box).get();
		
		RemoteCommandResult<List<String>> er = ExecutorUtil.runListOfCommands(Arrays.asList(
				new MysqlCnfFileLister(sshSession, box),
				new MyCnfFirstExist(sshSession, box),
				new MyCnfContentGetter(sshSession, box)));
		List<String> lines = er.getResult();
		return new MyCnfFileLikeHolder(lines);
	}
	
	public void writeDescription(Box box) throws IOException {
		String ds = YamlInstance.INSTANCE.getYaml().dumpAsMap(box);
		Path dstDir = appSettings.getDataRoot().resolve(box.getHost());
		if (!Files.exists(dstDir) || Files.isRegularFile(dstDir)) {
			Files.createDirectories(dstDir);
		}
		Path dstFile = dstDir.resolve(BackupCommand.DESCRIPTION_FILENAME);
		Files.write(dstFile, ds.getBytes());
	}
	
	public Path getDescriptionFile(Box instance) {
		return appSettings.getDataRoot().resolve(instance.getHost()).resolve(BackupCommand.DESCRIPTION_FILENAME);
	}

	
	@Autowired
	public void setSshClientFactory(SshSessionFactory sshClientFactory) {
		this.sshClientFactory = sshClientFactory;
	}
	
	@Autowired
	public void setAppSettings(MyAppSettings appSettings) {
		this.appSettings = appSettings;
	}
}
