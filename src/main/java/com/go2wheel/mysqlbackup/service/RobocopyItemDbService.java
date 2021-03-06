package com.go2wheel.mysqlbackup.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.go2wheel.mysqlbackup.jooqschema.tables.records.RobocopyItemRecord;
import com.go2wheel.mysqlbackup.model.RobocopyItem;
import com.go2wheel.mysqlbackup.repository.RobocopyItemRepository;

@Service
@Validated
public class RobocopyItemDbService extends DbServiceBase<RobocopyItemRecord, RobocopyItem> {
	
	public RobocopyItemDbService(RobocopyItemRepository repo) {
		super(repo);
	}

	public List<RobocopyItem> findByDescriptionId(String descriptionId) {
		return findByDescriptionId(Integer.parseInt(descriptionId));
	}

	public List<RobocopyItem> findByDescriptionId(Integer descriptionId) {
		return ((RobocopyItemRepository)repo).findByDescriptionId(descriptionId);
	}

}
