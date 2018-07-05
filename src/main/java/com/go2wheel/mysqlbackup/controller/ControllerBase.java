package com.go2wheel.mysqlbackup.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.go2wheel.mysqlbackup.ui.MainMenuGroups;
import com.go2wheel.mysqlbackup.ui.MainMenuItem;

public abstract class ControllerBase   implements ApplicationContextAware {
	
	protected ApplicationContext applicationContext;
	
	@Autowired
	private MainMenuGroups menuGroups;

	@ModelAttribute
	public void populateMainMenu(Model model, HttpServletRequest request) {
		List<MainMenuItem> items = menuGroups.clone().prepare(request.getRequestURI()).getMenuItems();
		model.addAttribute("menus", items);
	}
	
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	public abstract List<MainMenuItem> getMenuItems();
	
	protected String getTplName(String full) {
		int p = full.lastIndexOf('/');
		if (p == -1) {
			return full;
		} else {
			return full.substring(p);
		}
	}

}
