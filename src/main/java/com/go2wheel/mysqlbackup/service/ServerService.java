package com.go2wheel.mysqlbackup.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.go2wheel.mysqlbackup.jooqschema.tables.records.ServerRecord;
import com.go2wheel.mysqlbackup.model.BorgDescription;
import com.go2wheel.mysqlbackup.model.MysqlInstance;
import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.repository.ServerRepository;

@Service
@Validated
public class ServerService extends ServiceBase<ServerRecord, Server> {
	
	@Autowired
	private MysqlInstanceService mysqlInstanceService;
	
	@Autowired
	private BorgDescriptionService borgDescriptionService;

	@Autowired
	public ServerService(ServerRepository serverRepository) {
		super(serverRepository);
	}
	
	public Server findByHost(String host) {
		return ((ServerRepository)repo).findByHost(host);
	}

	public List<Server> findLikeHost(String partOfHostName) {
		return ((ServerRepository)repo).findLikeHost(partOfHostName);
	}
	
	@Override
	public void delete(Server server) {
		// get lastest version of server.
		MysqlInstance mi = mysqlInstanceService.findByServerId(server.getId());
		if (mi != null) {
			mysqlInstanceService.delete(mi);
		}
		
		BorgDescription bd = borgDescriptionService.findByServerId(server.getId());
		
		if (bd != null) {
			borgDescriptionService.delete(bd);
		}
		
		super.delete(server);
	}

	public Server loadFull(Server server) {
		MysqlInstance mi  = mysqlInstanceService.findByServerId(server.getId());
		BorgDescription bd = borgDescriptionService.findByServerId(server.getId());
		server.setMysqlInstance(mi);
		server.setBorgDescription(bd);
		return server;
	}
}
