package com.go2wheel.mysqlbackup.mysqlcfg;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.go2wheel.mysqlbackup.executablerunner.ExecutableRunnerSshBase;
import com.go2wheel.mysqlbackup.value.ExternalExecuteResult;
import com.go2wheel.mysqlbackup.value.MysqlInstance;

import net.schmizz.sshj.SSHClient;

public class MyCnfFirstExist extends ExecutableRunnerSshBase {

	public MyCnfFirstExist(SSHClient sshClient, MysqlInstance instance, ExternalExecuteResult<List<String>> prevResult) {
		super(sshClient, instance, prevResult);
	}

	public MyCnfFirstExist(SSHClient sshClient, MysqlInstance instance) {
		super(sshClient, instance);
	}


	@Override
	protected String getCommandString() {
		return "ls " + String.join(" ", prevResult.getResult());
	}


	@Override
	protected ExternalExecuteResult<List<String>> afterSuccessInvoke(ExternalExecuteResult<List<String>> externalExecuteResult) {
		Optional<String> firstExists = externalExecuteResult.getResult().stream().filter(line -> line.indexOf("No such file or directory") == -1).findFirst();
		if (firstExists.isPresent()) {
			externalExecuteResult.setResult(Arrays.asList(firstExists.get()));
			return externalExecuteResult;
		} else {
			return ExternalExecuteResult.failedResult("found mysql config file failed.");
		}
	}


	@Override
	protected String[] getLinesToFeed() {
		return null;
	}
	


}