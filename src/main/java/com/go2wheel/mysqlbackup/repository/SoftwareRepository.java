package com.go2wheel.mysqlbackup.repository;

import java.util.List;

import com.go2wheel.mysqlbackup.jooqschema.tables.records.SoftwareRecord;
import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.model.Software;

public interface SoftwareRepository extends RepositoryBase<SoftwareRecord, Software>{

	Software findByUniqueField(Software software);

	List<Software> findByName(String name);

	List<Software> findByServer(Server server);

	List<Software> findByServerAndName(Server server, String name);

}
