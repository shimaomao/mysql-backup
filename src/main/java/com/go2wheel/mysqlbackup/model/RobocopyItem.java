package com.go2wheel.mysqlbackup.model;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.go2wheel.mysqlbackup.validator.FileNameOnlyConstraint;
import com.go2wheel.mysqlbackup.yml.YamlInstance;
import com.google.common.collect.Lists;

public class RobocopyItem extends BaseModel {
	
	public static final int EXIT_SKIPED = 0;
	public static final int EXIT_ALL_COPY_SUCCESS = 1;
	public static final int EXIT_NO_COPY_AND_DST_WIN = 2;
	
	@NotNull
	private Integer descriptionId;
	
	@NotEmpty
	private String source;
	
	@NotEmpty
	@FileNameOnlyConstraint
	private String dstRelative;
	
	private String dstCalced;
	
	/**
	 * default is *.* include file dosn't has an extension.
	 */
	private String fileParameters = "*.*";
	 
	private List<String> excludeFiles = new ArrayList<>();
	
	private List<String> excludeDirectories = new ArrayList<>();
	
	private List<String> copyOptions = new ArrayList<>();
	
	private List<String> fileSelectionOptions = new ArrayList<>();
	
	private List<String> retryOptions = new ArrayList<>();
	
	private List<String> loggingOptions = new ArrayList<>(); 
	
	private List<String> jobOptions = new ArrayList<>();
	
	public RobocopyItem(Integer descriptionId, String source, String dstRelative) {
		super();
		this.descriptionId = descriptionId;
		this.source = source;
		this.dstRelative = dstRelative;
	}
	
	
	public String getSource() {
		return source;
	}

	public String getSourceSlash() {
		return source.replace('\\', '/');
	}
	
	public void setSource(String source) {
		this.source = source;
	}

	public String getFileParameters() {
		return fileParameters;
	}

	public void setFileParameters(String fileParameters) {
		this.fileParameters = fileParameters;
	}

	public List<String> getExcludeFiles() {
		return excludeFiles;
	}

	public void setExcludeFiles(List<String> excludeFiles) {
		this.excludeFiles = excludeFiles;
	}

	public List<String> getExcludeDirectories() {
		return excludeDirectories;
	}

	public void setExcludeDirectories(List<String> excludeDirectories) {
		this.excludeDirectories = excludeDirectories;
	}

	public List<String> getCopyOptions() {
		return copyOptions;
	}

	public void setCopyOptions(List<String> copyOptions) {
		this.copyOptions = copyOptions;
	}

	public List<String> getFileSelectionOptions() {
		return fileSelectionOptions;
	}

	public void setFileSelectionOptions(List<String> fileSelectionOptions) {
		this.fileSelectionOptions = fileSelectionOptions;
	}

	public List<String> getRetryOptions() {
		return retryOptions;
	}

	public void setRetryOptions(List<String> retryOptions) {
		this.retryOptions = retryOptions;
	}

	public List<String> getLoggingOptions() {
		return loggingOptions;
	}

	public void setLoggingOptions(List<String> loggingOptions) {
		this.loggingOptions = loggingOptions;
	}

	public List<String> getJobOptions() {
		return jobOptions;
	}

	public void setJobOptions(List<String> jobOptions) {
		this.jobOptions = jobOptions;
	}

	public RobocopyItem() {
		super();
	}
	
	@Override
	public String toString() {
		return YamlInstance.INSTANCE.yaml.dumpAsMap(this);
	}
	

	@Override
	public String toListRepresentation(String... fields) {
		return null;
	}

	public Integer getDescriptionId() {
		return descriptionId;
	}

	public void setDescriptionId(Integer descriptionId) {
		this.descriptionId = descriptionId;
	}

	public String getDstRelative() {
		return dstRelative;
	}

	public void setDstRelative(String dstRelative) {
		this.dstRelative = dstRelative;
	}

	@Transient
	public String getDstCalced() {
		return dstCalced;
	}

	public void setDstCalced(String dstCalced) {
		this.dstCalced = dstCalced;
	}
	
	public String getFileParametersNullSafe() {
		return fileParameters != null ? fileParameters : "*.*";
	}


	public List<String> getExcludeFilesNullSafe() {
		return excludeFiles != null ? excludeFiles : new ArrayList<>();
	}

	public List<String> getExcludeDirectoriesNullSafe() {
		return excludeDirectories != null ? excludeDirectories : new ArrayList<>();
	}
	
	public List<String> getCopyOptionsNullSafe() {
		return copyOptions != null && copyOptions.size() > 0 ? copyOptions : Lists.newArrayList("/e");
	}

	public List<String> getFileSelectionOptionsNullSafe() {
		return fileSelectionOptions != null ? fileSelectionOptions : new ArrayList<>();
	}

	public List<String> getRetryOptionsNullSafe() {
		return retryOptions != null ? retryOptions : new ArrayList<>();
	}

	public List<String> getLoggingOptionsNullSafe() {
		return loggingOptions != null && loggingOptions.size() > 0 ? loggingOptions : Lists.newArrayList("/fp", "/njh", "/njs");
	}

	public List<String> getJobOptionsNullSafe() {
		return jobOptions != null  ? jobOptions : new ArrayList<>();
	}
}
