package com.go2wheel.mysqlbackup.value;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.go2wheel.mysqlbackup.exception.Md5ChecksumException;

public class FileInAdirectory {
	
	private List<FileInfo> files = new ArrayList<>();
	
	
	
	public FileInAdirectory(RemoteCommandResult rcr) {
		if (rcr.getExitValue() == 0) {
			files = rcr.getAllTrimedNotEmptyLines().stream().map(line -> line.split("->", 2)).filter(ss -> ss.length == 2).map(FileInfo::new).collect(Collectors.toList());
		}
	}
	
	public String getFileNamesSeparatedBySpace() {
		return files.stream().map(f -> f.getRfileAbs()).collect(Collectors.joining(" "));
	}
	
	public boolean fileExists(Path path) {
		return files.stream().anyMatch(fi -> fi.getLfileAbs().equals(path));
	}

	public void setMd5s(List<String> md5s) {
		if (md5s.size() != files.size()) {
			throw new Md5ChecksumException();
		} else {
			for(int i = 0; i < files.size(); i++) {
				files.get(i).setMd5(md5s.get(i));
			}
		}
	}

	public List<FileInfo> getFiles() {
		return files;
	}

	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}

}
