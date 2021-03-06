package com.go2wheel.mysqlbackup.expect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.quartz.SchedulerException;

import com.go2wheel.mysqlbackup.SpringBaseFort;
import com.go2wheel.mysqlbackup.exception.MysqlWrongPasswordException;
import com.go2wheel.mysqlbackup.exception.RunRemoteCommandException;
import com.jcraft.jsch.JSchException;

public class TestMysqlFlushLogExpect extends SpringBaseFort {
	
	private String oriPwd;
	
    @Rule
    public TemporaryFolder tfolder= new TemporaryFolder();	
	
	@Before
	public void b() throws IOException, SchedulerException, JSchException {
		clearDb();
		createSession();
		createMysqlIntance();
		oriPwd = server.getMysqlInstance().getPassword();
	}
	
	@After
	public void after() throws IOException, JSchException, RunRemoteCommandException {
		server.getMysqlInstance().setPassword(oriPwd);
	}
	
	@Test
	public void t() throws Exception {
		MysqlFlushLogExpect mfe = new MysqlFlushLogExpect(session, server);
		assertTrue(mfe.start().size() == 2);
	}
	
	@Test(expected = MysqlWrongPasswordException.class)
	public void tWrongPassword() throws Exception {
		server.getMysqlInstance().setPassword("wrongpassword");
		MysqlFlushLogExpect mfe = new MysqlFlushLogExpect(session, server);
		assertFalse(mfe.start().size() == 1);
	}

}
