package org.zkoss.essentials.services;

import javax.servlet.http.HttpServletRequest;

import org.red.ws.ApacheHttpClientGet;
import org.red.ws.Respuesta;
import org.ws.util.entidad.Usuario;
import org.zkoss.essentials.chapter5.AuthenticationServiceChapter5Impl;
import org.zkoss.essentials.chapter5.UserInfoServiceChapter5Impl;
import org.zkoss.essentials.services.UserInfoService;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import com.google.gson.Gson;

public class AuthenticationServiceChapter8Impl extends AuthenticationServiceChapter5Impl{
	private static final long serialVersionUID = 1L;
	
	UserInfoService userInfoService = new UserInfoServiceChapter5Impl();
	private final Gson g= new Gson();
	
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	
	@Override
	public boolean login(String nm, String pd) {
		//User user = userInfoService.findUser(nm);
		Usuario user=new Usuario();
		user.setUsuario(nm);
		user.setPassword(pd);
		Respuesta res = g.fromJson(ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/login", user, request.getSession()), Respuesta.class);
		//a simple plan text password verification
		if(res.getData()==null){
			request.getSession().setAttribute("TokenWS", null);
			return false;
		}
		
		Session sess = Sessions.getCurrent();
		//session.setAttribute("TokenWS", token.getValue());
		//UserCredential cre = new UserCredential(user.getAccount(),user.getFullName());
		//just in case for this demo.
		/*if(cre.isAnonymous()){
			return false;
		}*/
		sess.setAttribute("userCredential",(Usuario)res.getData());
		System.out.println(sess.getAttribute("userCredential"));
		return true;
	}
	
	@Override
	public void logout() {
		Session sess = Sessions.getCurrent();
		sess.removeAttribute("userCredential");
	}
}