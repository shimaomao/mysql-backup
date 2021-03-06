package com.go2wheel.mysqlbackup.executablerunner;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.go2wheel.mysqlbackup.value.RemoteCommandResult;

public interface ExecutableRunnerSsh {
	
	RemoteCommandResult execute();
	
	void setPrevResult(RemoteCommandResult prevResult);
	
	
	default List<String> getWhatProcessWriteOut(InputStream outFromProcess) throws IOException {
	    BufferedReader reader = 
                new BufferedReader(new InputStreamReader(outFromProcess));
	    List<String> lines = new ArrayList<>();
		String line = null;
		while ( (line = reader.readLine()) != null) {
		   lines.add(line);
		}
		return lines;
	}
	
	default PrintWriter getProcessWriter(OutputStream os) {
		PrintWriter pw = new PrintWriter(new BufferedOutputStream(os));
		return pw;
	}
 
}
