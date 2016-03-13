package org.zkoss.essentials.services;


import java.util.Map;

import org.ws.util.entidad.Usuario;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

public class AuthenticationInit implements Initiator {

	//services
	AuthenticationService authService = (AuthenticationService) new AuthenticationServiceChapter8Impl();
	
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		
		Usuario cre = authService.getUserCredential();
		if(cre==null){
			Executions.sendRedirect("/chapter8/login.zul");
			return;
		}
	}
}
