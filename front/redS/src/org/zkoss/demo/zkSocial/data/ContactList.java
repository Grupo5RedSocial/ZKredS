package org.zkoss.demo.zkSocial.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.Usuario;
import org.zkoss.demo.zkSocial.tree.ContactTreeNode;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.DefaultTreeNode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ContactList {
	public final static String Category = "Category";
	public final static String Contact = "Contact";
	
	private AuthorBean currentUser;

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}
	
	public void setCurrentUser(AuthorBean currentUser) {
		this.currentUser = currentUser;
	}

	
	List<AuthorBean> list2=new ArrayList<AuthorBean>();

	private Session sess = Sessions.getCurrent();
	private Usuario userLogin;
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	private ContactTreeNode root;
	private ContactTreeNode[] cuerpo;
	private ContactTreeNode cab;
	public ContactList() {
		userLogin =  (Usuario) sess.getAttribute("userCredential");
		currentUser = userLogin.getUsuarioLogin();
		String json = ApacheHttpClientGet.GET("http://localhost:8080/Prueba/WsRed/getAmigos?id="+currentUser.getId_persona(), request.getSession());
		java.lang.reflect.Type type = new TypeToken<List<AuthorBean>>(){}.getType();
		list2 = new Gson().fromJson(json, type);
		cuerpo= new ContactTreeNode[list2.size()] ;
		for(int i=0 ; i<list2.size(); i++) {
			//lista.add(i, new Contact(list2.get(i).getName(),list2.get(i).getAvatar()));
			cuerpo[i]=new ContactTreeNode(new Contact(list2.get(i).getName(),list2.get(i).getAvatar()));
		}  
				
		root = new ContactTreeNode(null,
			new ContactTreeNode[] {
				new ContactTreeNode(new Contact("Friend"),cuerpo)
			},true
		);
	}
	public ContactTreeNode getRoot() {
		return root;
	}
}
