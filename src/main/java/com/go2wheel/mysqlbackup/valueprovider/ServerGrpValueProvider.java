package com.go2wheel.mysqlbackup.valueprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.shell.CompletionContext;
import org.springframework.shell.CompletionProposal;
import org.springframework.shell.standard.ValueProvider;

import com.go2wheel.mysqlbackup.model.Server;
import com.go2wheel.mysqlbackup.model.ServerGrp;
import com.go2wheel.mysqlbackup.service.ServerGrpService;
import com.go2wheel.mysqlbackup.service.ServerService;

public class ServerGrpValueProvider  implements ValueProvider {
	
	@Autowired
	private ServerGrpService serverGrpService;

    @Override
    public boolean supports(MethodParameter parameter, CompletionContext completionContext) {
        return parameter.getParameterType().equals(ServerGrp.class);
    }

    @Override
    public List<CompletionProposal> complete(MethodParameter parameter, CompletionContext completionContext, String[] hints) {

        String input = completionContext.currentWordUpToCursor();
        // The input may be -- or --xxx. Because it's might a positional parameter.
        if (input.startsWith("-")) {
        	return new ArrayList<>();
        }
        List<ServerGrp> serverGrps = serverGrpService.findLikeEname(input); 
        return serverGrps.stream().map(sv -> sv.getEname()).map(h -> new CompletionProposal(h)).collect(Collectors.toList());
    }
    
}