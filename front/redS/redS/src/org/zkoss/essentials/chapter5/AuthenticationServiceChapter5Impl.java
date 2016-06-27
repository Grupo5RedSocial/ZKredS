/* 
	Description:
		ZK Essentials
	History:
		Created by dennis
Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.essentials.chapter5;

import java.io.Serializable;

import org.ws.util.entidad.Usuario;
import org.zkoss.essentials.services.AuthenticationService;
import org.zkoss.essentials.services.UserCredential;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

public class AuthenticationServiceChapter5Impl implements AuthenticationService,Serializable{
	private static final long serialVersionUID = 1L;

	public Usuario getUserCredential(){
		Session sess = Sessions.getCurrent();
		Usuario cre = (Usuario)sess.getAttribute("userCredential");
		return cre;
	}
	

	public boolean login(String nm, String pd) {
		// will be implemented in chapter 8
		return false;
	}


	public void logout() {
		// will be implemented in chapter 8
	}
}