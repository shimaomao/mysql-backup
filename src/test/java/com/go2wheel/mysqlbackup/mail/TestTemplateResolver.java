package com.go2wheel.mysqlbackup.mail;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.go2wheel.mysqlbackup.SpringBaseFort;

public class TestTemplateResolver extends SpringBaseFort {
	
	@Autowired
	private TemplateEngine emailTemplateEngine;
	
	@Test
	public void tResolveInFile() {
		Set<ITemplateResolver> rv = emailTemplateEngine.getTemplateResolvers();
		
		assertThat(rv.size(), equalTo(4));
		final Context ctx = new Context();
		String s = emailTemplateEngine.process("html/hello.html", ctx);
		assertThat(s, equalTo("hello."));
	}
	
	@Test
	public void tResolveInClassLoader() {
		Set<ITemplateResolver> rv = emailTemplateEngine.getTemplateResolvers();
		
		assertThat(rv.size(), equalTo(4));
		final Context ctx = new Context();
		String s = emailTemplateEngine.process("html/helloc.html", ctx);
		assertThat(s, equalTo("hello."));
	}

	
	@Test(expected = TemplateInputException.class)
	public void tNoTemplate() {
		final Context ctx = new Context();
		String s = emailTemplateEngine.process("html/hello-zh.html", ctx);
		assertThat(s, equalTo("hello."));
	}
}
