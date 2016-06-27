/* 
	Description:
		ZK Essentials
	History:
		Created by dennis
Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.demo.zkSocial.util;

import org.ws.util.entidad.Usuario;
import org.zkoss.essentials.services.AuthenticationService;
import org.zkoss.essentials.services.AuthenticationServiceChapter8Impl;
import org.zkoss.essentials.services.UserCredential;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;

public class LoginController extends SelectorComposer<Component> {
	private static final long serialVersionUID = 1L;
	
	//wire components
	@Wire
	Textbox account;
	@Wire
	Textbox password;
	@Wire
	Label message;
	
	//services
	AuthenticationService authService = (AuthenticationService) new AuthenticationServiceChapter8Impl();

	
	@Listen("onClick=#login; onOK=#loginWin")
	public void doLogin(){
		String nm = account.getValue();
		String pd = password.getValue();
		
		if(authService==null)
			authService = (AuthenticationService) new AuthenticationServiceChapter8Impl();
		
		if(!authService.login(nm,pd)){
			message.setValue("account or password are not correct.");
			return;
		}
		Usuario cre= authService.getUserCredential();
		message.setValue("Welcome, "+cre.getUsuarioLogin().getName());
		message.setSclass("");
		
		Executions.sendRedirect("/index.zul");
		
	}


	public Textbox getAccount() {
		return account;
	}


	public void setAccount(Textbox account) {
		this.account = account;
	}


	public Textbox getPassword() {
		return password;
	}


	public void setPassword(Textbox password) {
		this.password = password;
	}


	public Label getMessage() {
		return message;
	}


	public void setMessage(Label message) {
		this.message = message;
	}


	public AuthenticationService getAuthService() {
		return authService;
	}


	public void setAuthService(AuthenticationService authService) {
		this.authService = authService;
	}
	
	
}