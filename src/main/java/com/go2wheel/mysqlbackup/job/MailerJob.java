package com.go2wheel.mysqlbackup.job;

import javax.mail.MessagingException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.go2wheel.mysqlbackup.aop.TrapException;
import com.go2wheel.mysqlbackup.mail.Mailer;
import com.go2wheel.mysqlbackup.mail.ServerGroupContext;
import com.go2wheel.mysqlbackup.model.Subscribe;
import com.go2wheel.mysqlbackup.service.TemplateContextService;
import com.go2wheel.mysqlbackup.service.SubscribeDbService;
import com.go2wheel.mysqlbackup.util.ExceptionUtil;

@Component
public class MailerJob implements Job {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SubscribeDbService userServerGrpDbService;


	@Autowired
	private TemplateContextService templateContextService;

	private Mailer mailer;

	@Override
	@TrapException(MailerJob.class)
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getMergedJobDataMap();
		int userServerGrpId = data.getInt(CommonJobDataKey.JOB_DATA_KEY_ID);
		Subscribe userServerGrp = userServerGrpDbService.findById(userServerGrpId);
		ServerGroupContext sgctx = templateContextService.createMailerContext(userServerGrp);
		mail(sgctx.getUser().getEmail(), userServerGrp.getTemplate(), sgctx);
	}
	
	@Autowired
	public void setMailer(Mailer mailer) {
		this.mailer = mailer;
	}
	
	public void mail(String email, String template, ServerGroupContext sgctx) {
		try {
			this.mailer.sendMailWithInline(email, template, sgctx);
		} catch (MessagingException e) {
			ExceptionUtil.logErrorException(logger, e);
		}
	}

	public String renderTemplate(String template, ServerGroupContext sgctx) {
		return mailer.renderTemplate(template, sgctx);
	}

}
