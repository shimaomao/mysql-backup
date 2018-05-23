package com.go2wheel.mysqlbackup.job;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.go2wheel.mysqlbackup.SpringBaseFort;
import com.go2wheel.mysqlbackup.service.UpTimeService;

public class TestUpTimeJob extends SpringBaseFort {
	
	@Autowired
	private UpTimeJob upTimeJob;
	
	@Autowired
	private UpTimeService upTimeService;
	
	@MockBean
	private JobExecutionContext context;
	
	@Test
	public void t() throws JobExecutionException {
		JobDataMap jdm = new JobDataMap();
		jdm.put("host", HOST_DEFAULT);
		given(context.getMergedJobDataMap()).willReturn(jdm);
		upTimeJob.execute(context);
		
		upTimeService.count();
		assertThat(upTimeService.count(), equalTo(1L));
		
	}

}