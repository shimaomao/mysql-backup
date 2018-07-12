package com.go2wheel.mysqlbackup.job;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.quartz.SchedulerException;

import com.go2wheel.mysqlbackup.model.ServerGrp;
import com.go2wheel.mysqlbackup.model.UserAccount;
import com.go2wheel.mysqlbackup.model.Subscribe;

public class TestUserAndServerGrp extends JobBaseFort {

	@Test
	public void t() throws SchedulerException {
		assertThat(serverGrpDbService.count(), equalTo(0L));
		ServerGrp sg = new ServerGrp("oneGroup");
		sg = serverGrpDbService.save(sg);
		assertThat(serverGrpDbService.count(), equalTo(1L));
		UserAccount ua = createUser();

		assertThat(countJobs(), equalTo(0L));

		Subscribe usg = new Subscribe.UserServerGrpBuilder(ua.getId(), sg.getId(), A_VALID_CRON_EXPRESSION,
				"aname").build(); // no cron expression.
		usg = userServerGrpDbService.save(usg);
		assertThat(countJobs(), equalTo(1L));
	}

}
