package com.go2wheel.mysqlbackup.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.go2wheel.mysqlbackup.borg.BorgService;
import com.go2wheel.mysqlbackup.exception.CommandNotFoundException;
import com.go2wheel.mysqlbackup.exception.UnExpectedOutputException;
import com.go2wheel.mysqlbackup.model.BorgDescription;
import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.propertyeditor.ListStringToLinesEditor;
import com.go2wheel.mysqlbackup.service.BorgDescriptionDbService;
import com.go2wheel.mysqlbackup.service.GlobalStore;
import com.go2wheel.mysqlbackup.service.GlobalStore.SavedFuture;
import com.go2wheel.mysqlbackup.ui.MainMenuItemImpl;
import com.go2wheel.mysqlbackup.service.ReusableCronDbService;
import com.go2wheel.mysqlbackup.service.ServerDbService;
import com.go2wheel.mysqlbackup.util.SshSessionFactory;
import com.go2wheel.mysqlbackup.value.AsyncTaskValue;
import com.go2wheel.mysqlbackup.value.CommonMessageKeys;
import com.go2wheel.mysqlbackup.value.FacadeResult;
import com.go2wheel.mysqlbackup.value.OsTypeWrapper;
import com.go2wheel.mysqlbackup.value.RemoteCommandResult;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;


@Controller
@RequestMapping(BorgDescriptionsController.MAPPING_PATH)
public class BorgDescriptionsController  extends CRUDController<BorgDescription, BorgDescriptionDbService> {
	
	public static final String MAPPING_PATH = "/app/borg-descriptions";
	
	
	@Autowired
	private ReusableCronDbService reuseableCronDbService;
	
	@Autowired
	private SshSessionFactory sshSessionFactory;
	
	@Autowired
	private ServerDbService serverDbService;
	
	@Autowired
	private BorgService borgService;
	
    @InitBinder
    public void initBinder(WebDataBinder binder) {
    	binder.registerCustomEditor(List.class, new ListStringToLinesEditor());
    }
	
	@Autowired
	public BorgDescriptionsController(BorgDescriptionDbService dbService) {
		super(BorgDescription.class, dbService, MAPPING_PATH);
	}

	@Override
	boolean copyProperties(BorgDescription entityFromForm, BorgDescription entityFromDb) {
		entityFromDb.setArchiveCron(entityFromForm.getArchiveCron());
		entityFromDb.setArchiveFormat(entityFromForm.getArchiveFormat());
		entityFromDb.setArchiveNamePrefix(entityFromForm.getArchiveNamePrefix());
		entityFromDb.setPruneCron(entityFromForm.getPruneCron());
		entityFromDb.setRepo(entityFromForm.getRepo());
		entityFromDb.setIncludes(entityFromForm.getIncludes());
		entityFromDb.setExcludes(entityFromForm.getExcludes());
		entityFromDb.setLocalBackupCron(entityFromForm.getLocalBackupCron());
		entityFromDb.setPruneStrategy(entityFromForm.getPruneStrategy());
		return true;
	}
	
	@GetMapping("/create")
	@Override
	String getCreate(Model model, HttpServletRequest httpRequest) {
		String serverId = httpRequest.getParameter("server");
		String rd = null;
		if (serverId == null) {
			rd =  redirectListingUrl(httpRequest);
		} else {
			Server server = serverDbService.findById(serverId);
			if (OsTypeWrapper.of(server.getOs()).isWin()) {
				ServletUriComponentsBuilder ucb = ServletUriComponentsBuilder.fromRequest(httpRequest);
				ucb.replacePath(RobocopyDescriptionsController.LISTING_PATH + "/create");
				String uri = ucb.build().toUriString();
				rd = "redirect:" + uri;
			} else {
				BorgDescription mi = getDbService().findByServerId(serverId);
				if (mi != null) {
					rd = redirectEditUrl(mi.getId());
				} else {
					mi = newModel();
					mi.setServerId(Integer.parseInt(serverId));
					model.addAttribute(OB_NAME, mi);
					model.addAttribute("editting", false);
					commonAttribute(model);
					formAttribute(model);
					return getFormTpl();
				}
			}
		}
		model.asMap().clear();
		return rd;
	}
	
	
	@PostMapping("/{borgdescription}/download")
	public String postDownloads(@PathVariable(name = "borgdescription") BorgDescription borgDescription, Model model, HttpServletRequest request, RedirectAttributes ras) {
		Server server = serverDbService.findById(borgDescription.getServerId());
		server = serverDbService.loadFull(server);
		
		Long aid = GlobalStore.atomicLong.getAndIncrement();
		
		String msgkey = getI18nedMessage(BorgService.BORG_DOWNLOAD_TASK_KEY, server.getHost());
		
		CompletableFuture<AsyncTaskValue> cf = borgService.downloadRepoAsync(server, msgkey, aid);
		String sid = request.getSession(true).getId();
		
		SavedFuture sf = SavedFuture.newSavedFuture(aid, msgkey, cf);
		
		globalStore.saveFuture(sid, sf);
		
		ras.addFlashAttribute("successMessage", "任务已异步发送，稍后会通知您。");
		return redirectListingUrl(request);
	}
	
	@PostMapping("/{borgdescription}/initrepo")
	public String initRepo(@PathVariable(name = "borgdescription") BorgDescription borgDescription, Model model, HttpServletRequest request, RedirectAttributes ras) throws JSchException, CommandNotFoundException, UnExpectedOutputException, IOException {
		Server server = serverDbService.findById(borgDescription.getServerId());
		server = serverDbService.loadFull(server);

		FacadeResult<Session> frSession = sshSessionFactory.getConnectedSession(server);
		Session session = frSession.getResult();
		try {
			FacadeResult<RemoteCommandResult> fr = borgService.initRepo(session, server.getBorgDescription().getRepo());
			if (!fr.isExpected()) {
				if (CommonMessageKeys.OBJECT_ALREADY_EXISTS.equals(fr.getMessage())) {
					ras.addFlashAttribute("warnMessage", "仓库之前已经初始化了。");
					return redirectListingUrl(request);
				} else {
					RemoteCommandResult rcr = fr.getResult();
					if (rcr != null) {
						throw new UnExpectedOutputException("10000", "borg.archive.unexpected", rcr.getAllTrimedNotEmptyLines().stream().collect(Collectors.joining("\n")));
					}
				}
				
			}
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
		ras.addFlashAttribute("successMessage", "仓库初始化完毕。");
		return redirectListingUrl(request);
	}

	@PostMapping("/{borgdescription}/bk-local-repo")
	public String postBackupLocalRepo(@PathVariable(name = "borgdescription") BorgDescription borgDescription, Model model, HttpServletRequest request, RedirectAttributes ras) throws IOException {
		Server server = serverDbService.findById(borgDescription.getServerId());
		borgService.backupLocalRepos(server);
		ras.addFlashAttribute("formProcessSuccessed", "任务已异步发送，稍后会通知您。");
		return redirectListingUrl(request);
	}
	
	@Override
	protected String afterCreate(BorgDescription entityFromForm, HttpServletRequest request) {
		return "redirect:" + MAPPING_PATH + "/" + entityFromForm.getId() + "/edit";
	}

	@Override
	public BorgDescription newModel() {
		return new BorgDescription.BorgDescriptionBuilder(0).build();
	}

	@Override
	protected void formAttribute(Model model) {
		model.addAttribute("crons", reuseableCronDbService.findAll());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void listExtraAttributes(Model model) {
		List<BorgDescription> mis = (List<BorgDescription>) model.asMap().get(LIST_OB_NAME);
		List<Server> servers = serverDbService.findByIds(mis.stream().map(BorgDescription::getServerId).toArray(size -> new Integer[size]));
		model.addAttribute(ID_ENTITY_MAP, servers.stream().collect(Collectors.toMap(Server::getId, s -> s)));
	}
	
	@Override
	public MainMenuItemImpl getMenuItem() {
		return null;
	}

}
