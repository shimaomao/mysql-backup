package com.go2wheel.mysqlbackup.job;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.model.UpTime;
import com.go2wheel.mysqlbackup.service.ServerService;
import com.go2wheel.mysqlbackup.service.UpTimeService;
import com.go2wheel.mysqlbackup.util.SSHcommonUtil;
import com.go2wheel.mysqlbackup.util.SshSessionFactory;
import com.go2wheel.mysqlbackup.value.UptimeAllString;
import com.jcraft.jsch.Session;

@Component
public class UpTimeJob implements Job {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UpTimeService upTimeService;
	
	@Autowired
	private ServerService serverService;

	@Autowired
	private SshSessionFactory sshSessionFactory;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		int sid = data.getInt(CommonJobDataKey.JOB_DATA_KEY_ID);
		Server sv = serverService.findById(sid);
		
		Session session = null;
		try {
			session = sshSessionFactory.getConnectedSession(sv).getResult();
			UptimeAllString uta = SSHcommonUtil.getUpTime(session);
			UpTime ut = uta.toUpTime();
			if (sv.getCoreNumber() == 0) {
				int cn = SSHcommonUtil.coreNumber(session);
				sv.setCoreNumber(cn);
				serverService.save(sv);
			}
			if (sv != null) {
				ut.setServerId(sv.getId());
				upTimeService.save(ut);
			}
		} finally {
			if (session != null) {
				session.disconnect();
			}
		}
	}

}
