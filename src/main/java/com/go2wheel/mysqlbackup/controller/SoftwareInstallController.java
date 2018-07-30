package com.go2wheel.mysqlbackup.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.go2wheel.mysqlbackup.installer.Installer;
import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.model.Software;
import com.go2wheel.mysqlbackup.service.SoftwareDbService;
import com.go2wheel.mysqlbackup.service.GlobalStore.Gobject;
import com.go2wheel.mysqlbackup.ui.MainMenuItem;
import com.go2wheel.mysqlbackup.util.SshSessionFactory;
import com.google.common.collect.Maps;

@Controller
@RequestMapping(SoftwareInstallController.MAPPING_PATH)
public class SoftwareInstallController extends ControllerBase {
	
	
	public static final String MAPPING_PATH = "/app/software-install";

	@Autowired
	private SshSessionFactory sshSessionFactory;

	@Autowired
	private SoftwareDbService softwareDbService;
	
	private List<Installer<?>> installers;
	
	public SoftwareInstallController() {
		super(MAPPING_PATH);
	}
	
	@Autowired
	public void setInstallers(List<Installer<?>> installers) {
		this.installers = installers;
	}

	@GetMapping("/{server}")
	public String getInstall(@PathVariable(name = "server") Server server,
			@RequestParam(required = false) Software software, Model model, HttpServletRequest httpRequest) {
		model.addAttribute("server", server);
		List<Software> softwares = softwareDbService.findAll();
		if (software == null && softwares.size() > 0) {
			software = softwares.get(0);
		}
		model.addAttribute("software", software);
		model.addAttribute("softwares", softwares);
		
		List<Software> installed = softwareDbService.findByServer(server);
		model.addAttribute("listItems", installed);
		return "software-install";
	}
	
	@DeleteMapping("")
	public String unInstall(@RequestParam Server server,
			@RequestParam Software software, HttpServletRequest request) {
		
		for(Installer<?> il: installers) {
			if(il.canHandle(software)) {
				CompletableFuture<?> cf = il.uninstallAsync(server, software);
				String sid = request.getSession(true).getId();
				globalStore.saveObject(sid, server.getId() + "-" + software.getId(), Gobject.newGobject(software.getName() + "的反安装", cf));
			}
		}
		ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromRequest(request);
		Map<String, Object> tmap = Maps.newHashMap();
		tmap.put("server", server.getId());
		
		ucb.replacePath("/app/software-install/" + server.getId()).build();
		String url = ucb.toUriString();
		return "redirect:" + url;
	}

	@PostMapping("/{server}")
	public String install(@PathVariable(name = "server") Server server, @RequestParam Software software, Model model,
			HttpServletRequest request, RedirectAttributes ras) throws UnsupportedEncodingException {
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> parameters = parameterMap.entrySet().stream().filter(es -> es.getValue().length > 0)
				.collect(Collectors.toMap(es -> es.getKey(), es -> es.getValue()[0]));
		
		for(Installer<?> il: installers) {
			if(il.canHandle(software)) {
				CompletableFuture<?> cf = il.installAsync(server, software, parameters);
				String sid = request.getSession(true).getId();
				globalStore.saveObject(sid, server.getId() + "-" + software.getId(), Gobject.newGobject("MYSQL安装", cf));
			}
		}
		ras.addFlashAttribute("formProcessSuccessed", encodeConvertor.convert("任务已异步发送，稍后会通知您。"));
		ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromRequest(request);
		String uri = ucb.replaceQueryParam("software", software.getId()).build().toUriString();
		return "redirect:" + uri;
	}
	
	@GetMapping("/systems")
	@ResponseBody
	public Properties allSystemProperties() {
		return System.getProperties();
	}

	@Override
	public List<MainMenuItem> getMenuItems() {
		return null;
	}

}