package com.go2wheel.mysqlbackup.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	
	public static class LinuxFileInfo {
		
		public static final Pattern LINUX_LSL_PATTERN = Pattern.compile("^([^\\s]+)\\s+(\\d+)\\s+([^ ]+)\\s+([^ ]+)\\s+(\\d+)\\s+([^ ]+)\\s+(\\d+)\\s+([^ ]+)\\s+(.*)$");
		
		public void setMd5ByMd5sumOutput(String expectOut) {
			Optional<String[]> md5pair = splitLines(expectOut).stream().map(l -> l.trim()).map(l -> l.split("\\s+")).filter(pair -> pair.length == 2).filter(pair -> pair[1].equals(filename) && pair[0].length() == 32).findAny();
			if (md5pair.isPresent()) {
				setMd5(md5pair.get()[0]);
			}
		}
		
		public LinuxFileInfo(Matcher m) {
			pm = m.group(1);
			numberOfLinks = Integer.valueOf(m.group(2));
			owner= m.group(3);
			group = m.group(4);
			size = Long.valueOf(m.group(5));
			lastModified = String.format("%s %s %S", m.group(6), m.group(7), m.group(8));
			filename = m.group(9);
		}
		
		private String pm;
		private int numberOfLinks;
		private String owner;
		private String group;
		private long size;
		private String lastModified;
		private String filename;
		
		public String md5;

		public String getMd5() {
			return md5;
		}
		public void setMd5(String md5) {
			this.md5 = md5;
		}
		public String getPm() {
			return pm;
		}
		public void setPm(String pm) {
			this.pm = pm;
		}
		public int getNumberOfLinks() {
			return numberOfLinks;
		}
		public void setNumberOfLinks(int numberOfLinks) {
			this.numberOfLinks = numberOfLinks;
		}
		public String getOwner() {
			return owner;
		}
		public void setOwner(String owner) {
			this.owner = owner;
		}
		public String getGroup() {
			return group;
		}
		public void setGroup(String group) {
			this.group = group;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
		public String getLastModified() {
			return lastModified;
		}
		public void setLastModified(String lastModified) {
			this.lastModified = lastModified;
		}
		public String getFilename() {
			return filename;
		}
		public void setFilename(String filename) {
			this.filename = filename;
		}
	}
	
	public static String NEWLINE_PTN = "[\\r\\n]+";
	
	public static List<String> splitLines(String str) {
		return Arrays.asList(str.split("\\R+"));
	}
	
	public static Optional<String> notEmptyValue(String maybeEmpty) {
		if (maybeEmpty == null || maybeEmpty.trim().isEmpty()) {
			return Optional.empty();
		} else {
			return Optional.of(maybeEmpty);
		}
	}
	
	public static String getLastPartOfUrl(String url) {
		int i = url.lastIndexOf('/');
		return url.substring(i + 1);
	}
	
	public static Optional<LinuxFileInfo> matchLinuxLsl(String oneLine) {
		String s = oneLine.trim();
		Matcher m = LinuxFileInfo.LINUX_LSL_PATTERN.matcher(s);
		boolean success = m.matches();
		if (success) {
			return Optional.of(new LinuxFileInfo(m));
		}
		return Optional.empty();
	}

}
