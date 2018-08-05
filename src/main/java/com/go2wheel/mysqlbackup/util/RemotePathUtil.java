package com.go2wheel.mysqlbackup.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.go2wheel.mysqlbackup.model.Server;

public class RemotePathUtil {
	
	public static String getParentWithEndingSlash(String remotePath) {
		return remotePath.substring(0, remotePath.lastIndexOf('/') + 1);
	}
	
	public static String getParentWithoutEndingSlash(String remotePath) {
		return remotePath.substring(0, remotePath.lastIndexOf('/'));
	}
	
	public static String getRidOfLastSlash(String remotePath) {
		if (remotePath.endsWith("/")) {
			return remotePath.replaceAll("/+$", "");
					
		} else {
			return remotePath;
		}
	}

	public static String join(String first, String...others) {
		if (others.length == 0) return first;
		
		if (first.endsWith("/")) {
			first = first.substring(0, first.length() - 1);
		}
		String os = Stream.of(others).map(s -> {
			if (s.startsWith("/")) {
				return s;
			} else {
				return "/" + s;
			}
		}).collect(Collectors.joining(""));
		return first + os;
	}

	
	public static String getLogBinFile(Server server, String onlyFilename) {
		String remoteDir = server.getMysqlInstance().getLogBinSetting().getLogBinDirWithEndingSlash();
		return remoteDir + onlyFilename;
	}

}
