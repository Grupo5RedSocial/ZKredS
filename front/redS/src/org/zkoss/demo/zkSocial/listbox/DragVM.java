package org.zkoss.demo.zkSocial.listbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.ListaMed;
import org.ws.util.entidad.Usuario;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DragVM {

	List<ListaMed> list1=new ArrayList<ListaMed>();
	List<ListaMed> list2=new ArrayList<ListaMed>();
	@Wire Div divWorkspace;
	@Wire
	private Layout lista;
	
	private Label message;
	
	
	
	public Label getMessage() {
		return message;
	}

	public void setMessage(Label message) {
		this.message = message;
	}

	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
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

	@Command
	public void close(@ContextParam(ContextType.VIEW) Window comp) {
		BindUtils.postGlobalCommand(null, null, "close-person-editor", null);
		//comp.detach();
		//comp.setVisible(false);
	}

	@Command
	@NotifyChange({"list1","list2"})
	 public void close2(@ContextParam(ContextType.VIEW) Window modalDialog) {
		 modalDialog.detach();;
	}
	
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	private Session sess = Sessions.getCurrent();
	private Usuario userLogin;
	
	@Init
	public void init(@BindingParam("param1")String param1) {
		userLogin =  (Usuario) sess.getAttribute("userCredential");
		currentUser = userLogin.getUsuarioLogin();
		value = param1;
		String json = ApacheHttpClientGet.GET("http://localhost:8080/Prueba/WsRed/getContacts?id="+currentUser.getId_persona(), request.getSession());
		java.lang.reflect.Type type = new TypeToken<List<ListaMed>>(){}.getType();
		list1 = new Gson().fromJson(json, type);
	
	}
	
		public List<ListaMed> getList1() {
		return list1;
	}
	public List<ListaMed> getList2() {
		return list2;
	}
	
	@Command
	@NotifyChange({"list1","list2"})
	public void dropToList1(@BindingParam("item") ListaMed item){
		if(item!=null){
			list1.add(item);
			list2.remove(item);
		}
	}
	
	@Command
	@NotifyChange({"list1","list2"})
	public void dropToList2(@BindingParam("item") ListaMed item){
		if(item!=null){
			item.setCurrentUser(currentUser.getId_persona());
			list2.add(item);
			list1.remove(item);
		}
	}
	
	@Command
	@NotifyChange({"list1","list2"})
	public void insertToList1(@BindingParam("base") ListaMed base,@BindingParam("item") ListaMed item){
		if(item!=null && base!=null && list1.contains(base) && list2.contains(item)){
			list1.add(list1.indexOf(base),item);
			list2.remove(item);
		}
	}
	
	@Command
	@NotifyChange({"list1","list2"})
	public void insertToList2(@BindingParam("base") ListaMed base,@BindingParam("item") ListaMed item){
		if(item!=null && base!=null && list2.contains(base) && list1.contains(item)){
			item.setCurrentUser(currentUser.getId_persona());
			list2.add(list2.indexOf(base),item);
			list1.remove(item);
		}
	}
	
	// agregar comentario e insertarlo en la base desde el ws
		@Command
		@NotifyChange({"list2","message"})
		public void addContact(@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) throws IOException {
				String respuesta =ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/addContact",list2, request.getSession());
				if(!respuesta.equals("OK")){
					 Messagebox.show(respuesta, "Error", Messagebox.OK, Messagebox.ERROR);
					return;
				}
				Messagebox.show("Contactos agregados con éxito", "Information", Messagebox.OK, Messagebox.INFORMATION);
			}
		
		// agregar comentario e insertarlo en la base desde el ws
		@Command
		@NotifyChange({ "list2" })
		public void addDr(@ContextParam(ContextType.VIEW) Window modalDialog) {
			    String json = ApacheHttpClientGet.GET("http://localhost:8080/Prueba/WsRed/getAmigos?id="+currentUser.getId_persona(), request.getSession());
			    java.lang.reflect.Type type = new TypeToken<List<ListaMed>>(){}.getType();
				list1 = new Gson().fromJson(json, type);
		}
			
}