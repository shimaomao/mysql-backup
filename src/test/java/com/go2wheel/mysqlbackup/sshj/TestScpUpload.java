package com.go2wheel.mysqlbackup.sshj;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

import com.go2wheel.mysqlbackup.executablerunner.ExecutableRunnerSshBase;
import com.go2wheel.mysqlbackup.value.ExternalExecuteResult;

import net.schmizz.sshj.xfer.FileSystemFile;

public class TestScpUpload extends SshBaseFort {
	
	@Test
	public void tUploadaFile() throws IOException {
            // Present here to demo algorithm renegotiation - could have just put this before connect()
            // Make sure JZlib is in classpath for this to work
		Path p = createALocalFile();
        sshClient.useCompression();
        sshClient.newSCPFileTransfer().upload(new FileSystemFile(p.toFile()), "/tmp/");
        ExternalExecuteResult<List<String>> er = new ExecutableRunnerSshBase(sshClient, demoInstance) {
			@Override
			protected String[] getLinesToFeed() {
				return null;
			}
			
			@Override
			protected String getCommandString() {
				return String.format("ls -lh /tmp/%s", p.getFileName().toString());
			}
			
			@Override
			protected ExternalExecuteResult<List<String>> afterSuccessInvoke(
					ExternalExecuteResult<List<String>> externalExecuteResult) {
				return externalExecuteResult;
			}
		}.execute();
		assertTrue(er.getResult().get(0).indexOf(p.getFileName().toString()) != -1);
	}
	
	@Test
	public void tUploadaAdirectory() throws IOException {
            // Present here to demo algorithm renegotiation - could have just put this before connect()
            // Make sure JZlib is in classpath for this to work
		Path p = createALocalFileDirectory(2);
        sshClient.useCompression();
        sshClient.newSCPFileTransfer().upload(new FileSystemFile(p.toFile()), "/tmp/");
        ExternalExecuteResult<List<String>> er = new ExecutableRunnerSshBase(sshClient, demoInstance) {
			@Override
			protected String[] getLinesToFeed() {
				return null;
			}
			
			@Override
			protected String getCommandString() {
				return String.format("ls -lh /tmp/%s", p.getFileName().toString());
			}
			
			@Override
			protected ExternalExecuteResult<List<String>> afterSuccessInvoke(
					ExternalExecuteResult<List<String>> executeInternal) {
				return executeInternal;
			}
		}.execute();
		assertThat(er.getResult().size(), equalTo(4));
	}


}
