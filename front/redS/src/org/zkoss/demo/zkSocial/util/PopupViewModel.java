package org.zkoss.demo.zkSocial.util;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.zkoss.demo.zkSocial.util.EmailSenderService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.red.ws.ApacheHttpClientGet;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.PostBean;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Binder;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.demo.zkSocial.vm.Person;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.web.servlet.Servlets;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;
import org.ws.util.entidad.Usuario;

import com.lowagie.text.List;

public class PopupViewModel extends GenericForwardComposer {

	String value="prueba";
	private Binder parBinder;
	private Media media;
	private AImage myImage;// get,set method
	@Wire
	private Image imagen;
	private String currentComment = null;
	private String video = "";
	private AuthorBean currentUser;
	private Usuario user;
	private PostBean currentPost = null;
	ArrayList<AuthorBean> list2=new ArrayList<AuthorBean>();
	ArrayList<AuthorBean> listaMedicos=new ArrayList<AuthorBean>();
	ArrayList<AuthorBean> listaMed = new ArrayList<AuthorBean>();
	@Wire Div divWorkspace;
	
	private String correosMed = null;
	
	private Component component;
	@Wire
	private Window modalDialog;
	
	@Wire
	private Html miHtml;
	@Wire
	private Label lbl;
	
	public AImage getMyImage() {
		return myImage;
	}

	public void setMyImage(AImage myImage) {
		this.myImage = myImage;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Comment for current post
	 */
	public String getCurrentComment() {
		return currentComment;
	}

	public void setCurrentComment(String currentComment) {
		this.currentComment = currentComment;
	}

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}

	/**
	 * Current post that the user is interested in getting feedback
	 */
	public PostBean getCurrentPost() {
		return currentPost;
	}

	public void setCurrentPost(PostBean currentPost) {
		this.currentPost = currentPost;
	}
	
	

	public String getMedicos() {
		return medicos;
	}

	public void setMedicos(String medicos) {
		this.medicos = medicos;
	}
	
	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public ArrayList<AuthorBean> getListaMedicos() {
		return listaMedicos;
	}

	public void setListaMedicos(ArrayList<AuthorBean> listaMedicos) {
		this.listaMedicos = listaMedicos;
	}

	public ArrayList<AuthorBean> getListaMed() {
		return listaMed;
	}

	public void setListaMed(ArrayList<AuthorBean> listaMed) {
		this.listaMed = listaMed;
	}

	@Init
	public void Init(@ExecutionArgParam("value") String val,
			@ExecutionArgParam("parent") Component par, @ExecutionArgParam("person") Person person) {
		this.parBinder = (Binder) par.getAttribute("binder");
		setValue(val);
	}

	@Command("change")
	public void change(@ContextParam(ContextType.VIEW) Component view) {

		Binder bind = (Binder) view.getParent().getAttribute("binder");
		if (bind == null)
			return;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("newValue", getValue());

		// this.parBinder.postCommand("change", params);
		bind.postCommand("change", params);

		view.detach();
	}
	
	// para cargar la imagen cuando se haga una publicacion
			@Command
			public void addDr(
					@ContextParam(ContextType.COMPONENT) Component comp,
					@BindingParam("pos") String pos,
					@BindingParam("ref") Component view) {
				
				    Map<String, Object> params = new HashMap<String, Object>();
				    params.put("parent", comp);
				    Window popup = (Window) Executions.createComponents("addDr.zul",view, params);
				    popup.doModal();
			}

	// para cargar la imagen cuando se haga una publicacion
	@Command("upload")
	@NotifyChange({"myImage","miHtml","lbl"})
	public void onImageUpload(
			@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) throws IOException {
		UploadEvent upEvent = null;
		Object objUploadEvent = ctx.getTriggerEvent();
		if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
			upEvent = (UploadEvent) objUploadEvent;
		}
		if (upEvent != null) {
			media = upEvent.getMedia();
			String type = media.getContentType();
			int lengthofImage = media.getByteData().length;
			imagen.setVisible(false);
			miHtml.setContent("");
			if ("application/octet-stream".equals(type)){
				lbl.setValue("Nombre del archivo: " + media.getName());
			}else if (media instanceof org.zkoss.image.Image) {
				if (lengthofImage > 1000 * 1024) {
					System.out.println("Please Select a Image of size less than 500Kb.");
					return;
				} else {
					myImage = (AImage) media;// Initialize the bind object to
												// show image in zul page and
												// Notify it also
					imagen.setVisible(true);
				}
			} else {
				String base = StringUtils.newStringUtf8(Base64.encodeBase64(media.getByteData(), false));
				//String type = media.getContentType();
				String name = media.getName();
				
				video="data:"+type+";base64,"+base;
				miHtml.setContent("<video controls='true' autoplay='false' loop='loop' id='bg-vid' class='video-playing' height='150px' width='150px'>"+
                        " <source src='"+ video +"'"+
                        " type='video/mp4'/>"+
                    " </video>");
			}
		} else {
			System.out.println("Upload Event Is not Coming");
		}
	}
	
	private Session sess = Sessions.getCurrent();
	private Usuario userLogin;

	// agregar comentario e insertarlo en la base desde el ws
	@Command
	@NotifyChange({ "currentComment" })
	public void addPost(@ContextParam(ContextType.VIEW) Window modalDialog) {
		String nombre = null;
		userLogin = (Usuario) sess.getAttribute("userCredential");
		currentUser = userLogin.getUsuarioLogin();
		try{
			if(media!=null){
				nombre=media.getName();
				guardarArchivo(nombre, media.getByteData());
			}
		}catch(Exception e){
			
		}
		Session sess = Sessions.getCurrent();
		System.out.println(sess.getAttribute("userCredential"));
		String medAsignados = getMedicos();
		PostBean comment = new PostBean();
		comment.setAuthor(currentUser);
		comment.setContent(currentComment);
		comment.setTime(new Date());
		PostBean Publicacion = new PostBean();
		Publicacion.setContent(comment.getContent());
		Publicacion.setAuthor(currentUser);
		if(media!=null){
			Publicacion.setRutaImg("images/archivos/"+nombre);
			Publicacion.setTipo(media.getContentType());
		}
		ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/addPost",Publicacion, request.getSession());
		currentComment = "";
		if(medAsignados!=null){
			
			for(int i=0; i<listaMedicos.size(); i++){
				   if (correosMed != null)
					   correosMed = correosMed+";"+listaMedicos.get(i).getEmail();
				   else
					   correosMed = listaMedicos.get(i).getEmail();
				}
			String mail = FormatoNotificacion.MailMedicos;
			String mailaux = mail.replace("%paciente%", comment.getAuthor().getName());
			EmailSenderService sendEmail = new EmailSenderService();
			sendEmail.sendEmail(correosMed,mailaux);
		}
		modalDialog.detach();
		Executions.sendRedirect("/index.zul");
	}

	private void guardarArchivo(String nombre, byte[] data) throws Exception {
		// Con este código se agregan los bytes al archivo.
		FileOutputStream fileOuputStream = new FileOutputStream("C:/Users/Administrador/Documents/Instaladores/workspace/redS/redS/WebContent/images/archivos/"+nombre);
		fileOuputStream.write(data);
		fileOuputStream.close();
	}
	
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	
	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Double ie = Servlets.getBrowser(request, "ie");
		if (ie != null && ie < 8.0) {
			Clients.showNotification("This demo does not support IE6/7", true);
		}

		Selectors.wireComponents(view, this, false);
	}
	
	@Command("edit-person")
	public void onEditPerson() {
		component = divWorkspace.getLastChild();
		component.setVisible(false);
		Map<String, Object> params = new HashMap<String, Object>();
		Executions.createComponents("page2.zul", divWorkspace, params);	
	}
	
	@GlobalCommand("close-person-editor")
	@NotifyChange("list2")
	public void onUpdatePersonsList(@BindingParam("list2") List list2) {
		divWorkspace.getLastChild().detach();
		component.setVisible(true);
	}

	private String medicos;
	@GlobalCommand() @NotifyChange("value")
	public void dlgClose(@BindingParam("result")ArrayList<AuthorBean> result){

		for(int i=0; i<result.size(); i++){
		   listaMedicos.add(result.get(i));	
		   if (medicos != null)
		   	medicos = medicos+","+result.get(i).getName();
		   else
			   medicos = result.get(i).getName();
		}
		this.value = medicos;
	}
	
	@Command
	 public void close2(@ContextParam(ContextType.VIEW) Window modalDialog) {
		 modalDialog.detach();
	}
}
