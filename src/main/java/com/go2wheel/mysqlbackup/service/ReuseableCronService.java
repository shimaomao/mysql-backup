package com.go2wheel.mysqlbackup.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.go2wheel.mysqlbackup.jooqschema.tables.records.ReuseableCronRecord;
import com.go2wheel.mysqlbackup.model.ReusableCron;
import com.go2wheel.mysqlbackup.repository.ReusableCronRepository;

@Service
@Validated
public class ReuseableCronService extends ServiceBase<ReuseableCronRecord, ReusableCron> {

	public ReuseableCronService(ReusableCronRepository reusableCronRepository) {
		super(reusableCronRepository);
	}

	public ReusableCron findByExpression(String expression) {
		return ((ReusableCronRepository)repo).findByExpression(expression);
	}
	
}
