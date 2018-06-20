package com.go2wheel.mysqlbackup.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.quartz.JobKey;
import org.quartz.TriggerKey;

import com.go2wheel.mysqlbackup.exception.StringReplaceException;

public class StringUtil {
	
	public static String NEWLINE_PTN = "[\\r\\n]+";
	
	public static Pattern ALL_DIGITS_PTN = Pattern.compile("^\\d+$");
	
	public static Pattern NUMBER_HEADED = Pattern.compile("\\s*(\\d+).*");
	
	public static final long KB = 1024;
	public static final long MB = KB * 1024;
	public static final long GB = MB * 1024;
	
	public static List<String> splitLines(String str) {
		return Arrays.asList(str.split("\\R+"));
	}
	
	/**
	 * input a=b\nc:d or a=b,c=d
	 * @param lines
	 * @return
	 */
	public static Map<String, String> toPair(List<String> lines) {
		return lines.stream().map(line -> line.split("=", 2)).filter(ss -> ss.length == 2).collect(Collectors.toMap(ss -> ss[0], ss -> ss[1]));
	}
	
	public static List<String> toLines(Map<String, String> pairs) {
		return pairs.entrySet().stream().map(es -> es.getKey() + "=" + es.getValue()).collect(Collectors.toList());
	}
	
	public static String toOneLine(Map<String, String> pairs, String separator) {
		return pairs.entrySet().stream().map(es -> es.getKey() + "=" + es.getValue()).collect(Collectors.joining(separator));
	}
	
	public static String toOneLine(Map<String, String> pairs) {
		return toOneLine(pairs, ",");
	}

	
	public static List<String> toLines(String line) {
		return toLines(line, ",");
	}

	
	/**
	 * input a=b,c=d
	 * @param line
	 * @return
	 */
	public static List<String> toLines(String line, String separator) {
		Pattern ptn = Pattern.compile(String.format("^[a-zA-Z]+=|%s[a-zA-Z]+=", separator));
		Matcher m = ptn.matcher(line);
		List<String> lines = new ArrayList<>();
		int pos=0;
		String key = null;
		String value = null;
		int count = 0;
		while(m.find()) {
			int start = m.start();
			if (start > pos) { // it's value.
				value = line.substring(pos, start);
				lines.add(key + value);
			}
			int end = m.end();
			pos = end;
			if (count == 0) {
				key = line.substring(start, end);
			} else {
				key = line.substring(start + 1, end);
			}
			count++;
		}
		if (pos < line.length()) {
			lines.add(key + line.substring(pos));
		}
		return lines;
	}
	
	
	public static String formatJobkey(JobKey jobkey) {
		return String.format("%s-%s", jobkey.getName(), jobkey.getGroup());
	}

	public static String formatTriggerkey(TriggerKey triggerkey) {
		return String.format("%s-%s", triggerkey.getName(), triggerkey.getGroup());
	}
	
	public static Optional<String> notEmptyValue(String maybeEmpty) {
		if (maybeEmpty == null || maybeEmpty.trim().isEmpty() || "null".equals(maybeEmpty)) {
			return Optional.empty();
		} else {
			return Optional.of(maybeEmpty);
		}
	}
	
	public static String getLastPartOfUrl(String url) {
		int i = url.lastIndexOf('/');
		return url.substring(i + 1);
	}
	
	public static int parseInt(String numberHeaded) {
		Matcher m = NUMBER_HEADED.matcher(numberHeaded);
		if (m.matches()) {
			return Integer.valueOf(m.group(1));
		} else {
			return 0;
		}
	}

	public static long parseLong(String numberHeaded) {
		Matcher m = NUMBER_HEADED.matcher(numberHeaded);
		if (m.matches()) {
			return Long.valueOf(m.group(1));
		} else {
			return 0;
		}
	}	
	
	public static String[] matchGroupValues(Matcher m) {
		int c = m.groupCount();
		String[] ss = new String[c];
		for(int i = 0; i<c; i++) {
			ss[i] = m.group(i + 1);
		}
		return ss;
	}
	
	public static boolean hasAnyNonBlankWord(String s) {
		return s != null && !(s.trim().isEmpty());
	}
	
	public static Object[] matchGroupReplace(Matcher m, Object...replaces) {
		Object[] oo = matchGroupValues(m);
		int l = oo.length;
		for(int i = 0; i< l; i++) {
			if (replaces[i] == null) {
				continue;
			} else {
				oo[i] = replaces[i];
			}
		}
		return oo;
	}
	
//	private static String placeHoderPtn = "\\(.*?\\)";
	
	public static String replacePattern(String origin, String pattern,String fmt, Object...replaces) throws StringReplaceException {
		Pattern ptn = Pattern.compile(pattern); // 
		Matcher m = ptn.matcher(origin);
		if (!m.matches()) {
			throw new StringReplaceException(origin, pattern);
		}
		if (m.groupCount() != replaces.length) {
			throw new StringReplaceException(origin, pattern, replaces);
		}
//		String fmt = pattern.replaceAll(placeHoderPtn, "%s");
		return String.format(fmt, matchGroupReplace(m, replaces));
		
	}

	
	public static String formatSize(Long size) {
		if (size == null) {
			size = 0L;
		}
		return formatSize(size, 2);
	}

	
	public static String formatSize(long size, int digits) {
		String fs = "%." + digits + "f";
		long unit = 1;
		String ext = "B";
		if (size <= KB) {
			return size + "B";
		} else if (size <= MB) {
			unit = KB;
			ext = "KB";
		} else if (size <= GB) {
			unit = MB;
			ext = "MB";
		} else {
			unit = GB;
			ext = "GB";
		}
		return String.format(fs, (double)size/ unit) + ext;
	}
	
	
	public static String inputstreamToString(InputStream inputStream) {
		try {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
			    result.write(buffer, 0, length);
			}
			// StandardCharsets.UTF_8.name() > JDK 7
			return result.toString("UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isNullString(String str) {
		return "null".equalsIgnoreCase(str);
	}
	
	
	public static String getTimeCost(long delta, TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return TimeUnit.MILLISECONDS.toSeconds(delta) + "s";
		case DAYS:
			return TimeUnit.MILLISECONDS.toDays(delta) + unit.toString();
		case HOURS:
			return TimeUnit.MILLISECONDS.toHours(delta) + unit.toString();
		case MICROSECONDS:
			return TimeUnit.MILLISECONDS.toMicros(delta) + unit.toString();
		case MILLISECONDS:
			return TimeUnit.MILLISECONDS.toMillis(delta) + "ms";
		case MINUTES:
			return TimeUnit.MILLISECONDS.toMinutes(delta) + unit.toString();
		case NANOSECONDS:
			return TimeUnit.MILLISECONDS.toNanos(delta) + unit.toString();
		default:
			break;
		}
		return "";
	}
	
	public static String getTimeCost(long delta) {
		TimeUnit unit = TimeUnit.MILLISECONDS;
		long properValue = delta;
		if (delta > 3000) {
			properValue = TimeUnit.MILLISECONDS.toSeconds(delta);
			unit = TimeUnit.SECONDS;
			if (properValue > 360) {
				properValue = TimeUnit.MICROSECONDS.toMinutes(delta);
				unit = TimeUnit.MINUTES;
				if (properValue > 180) {
					properValue = TimeUnit.MICROSECONDS.toHours(delta);
					unit = TimeUnit.HOURS;
				}
				if (properValue > 72) {
					properValue = TimeUnit.MICROSECONDS.toDays(delta);
					unit = TimeUnit.DAYS;
				}
			}
		}
		return properValue + " " + unit.toString();
	}
}
