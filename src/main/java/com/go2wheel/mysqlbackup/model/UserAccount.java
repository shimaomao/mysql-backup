package com.go2wheel.mysqlbackup.model;

import java.util.Date;
import java.util.UUID;

public class UserAccount extends BaseModel {
	
	private String name;
	private String mobile;
	private String description;
	private String email;
	private Date createdAt;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public static class UserAccountBuilder {
		private final String name;
		private final String email;
		private String mobile;
		private String description;
		
		public UserAccountBuilder(String name, String email) {
			super();
			this.name = name;
			this.email = email;
		}
		
		public UserAccountBuilder withMobile(String mobile) {
			this.mobile = mobile;
			return this;
		}
		
		public UserAccountBuilder withDescription(String description) {
			this.description = description;
			return this;
		}
		
		public UserAccount build() {
			UserAccount ua = new UserAccount();
			ua.setCreatedAt(new Date());
			ua.setDescription(description);
			ua.setEmail(email);
			if (mobile == null) {
				ua.setMobile(UUID.randomUUID().toString());
			} else {
				ua.setMobile(mobile);
			}
			ua.setName(name);
			return ua; 
		}
		
	}

}
