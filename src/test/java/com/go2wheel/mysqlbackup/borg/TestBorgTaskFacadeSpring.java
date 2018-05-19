package com.go2wheel.mysqlbackup.borg;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

import com.go2wheel.mysqlbackup.ApplicationState;
import com.go2wheel.mysqlbackup.UtilForTe;
import com.go2wheel.mysqlbackup.exception.RunRemoteCommandException;
import com.go2wheel.mysqlbackup.util.SshSessionFactory;
import com.go2wheel.mysqlbackup.value.Box;
import com.go2wheel.mysqlbackup.value.FacadeResult;
import com.jcraft.jsch.Session;

@SpringBootTest("spring.shell.interactive.enabled=false")
@RunWith(SpringRunner.class)
public class TestBorgTaskFacadeSpring {
	
	@Autowired
	private BorgService borgTaskFacade;
	
	@Autowired
	private ApplicationState appState;
	
	@Autowired
	private Environment environment;
	
	@Autowired
	private SshSessionFactory sshSessionFactory;
	
	@Test
	public void tArchive() throws RunRemoteCommandException, InterruptedException, IOException {
		Box box = UtilForTe.loadDemoBox();
		Session session = sshSessionFactory.getConnectedSession(box).getResult();
		borgTaskFacade.archive(session, box, "", false);
	}
	
	@Test
	public void tProfile() {
		String[] ss = environment.getActiveProfiles();
		assertTrue(ss.length > 0);
	}
	
	@Test
	public void tDownloadRepo() throws IOException {
		Box box = UtilForTe.loadDemoBox();
		Session session = sshSessionFactory.getConnectedSession(box).getResult();
		FacadeResult<?> fr = borgTaskFacade.downloadRepo(session, box);
		assertThat(fr.getStartTime(), greaterThan(0L));
		assertTrue(fr.isExpected());
	}

}
