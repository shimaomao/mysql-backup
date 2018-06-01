package com.go2wheel.mysqlbackup.model;

public class JobError extends BaseModel {
	
	private Integer serverId;
	private String messageKey;
	private String messageDetail;
	
	public JobError() {
	}
	
	public JobError(Integer serverId, String messageKey, String messageDetail) {
		this.serverId = serverId;
		this.messageDetail = messageDetail;
		this.messageKey = messageKey;
	}
	
	public Integer getServerId() {
		return serverId;
	}
	public void setServerId(Integer serverId) {
		this.serverId = serverId;
	}
	public String getMessageKey() {
		return messageKey;
	}
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}
	public String getMessageDetail() {
		return messageDetail;
	}
	public void setMessageDetail(String messageDetail) {
		this.messageDetail = messageDetail;
	}

	@Override
	public String toListRepresentation(String... fields) {
		return null;
	}
}