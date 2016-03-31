package org.zkoss.demo.zkSocial.registro;


import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpServletRequest;

import org.red.ws.ApacheHttpClientGet;
import org.red.ws.validaciones.Validaciones;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.Form;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.demo.zkSocial.util.EmailSenderService;
import org.zkoss.demo.zkSocial.util.FormatoNotificacion;
import org.zkoss.image.AImage;
import org.zkoss.util.media.Media;
import org.zkoss.web.fn.ServletFns;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Image;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.bind.annotation.BindingParam;
import org.red.ws.StringEncrypt;
import org.ws.util.entidad.AuthorBean;
import org.ws.util.entidad.User;
import org.ws.util.entidad.DatosMed;
import org.ws.util.entidad.Usuario;

public class FormViewModel extends UserForm {
	@Wire
	Window editDatos;
	private String dateFormat;
	private int memoHeight = 6;
	private String foregroundColour = "#000000", backgroundColour = "#FDC966";	
	private Media media;
	private AImage myImage;// get,set method
	@Wire
	private Image imagen;
	private Validaciones validacion;
	private AuthorBean currentUser;
	
	public String key = "92AE31A79FEEB2A3"; //llave
	public String iv = "0123456789ABCDEF"; // vector de inicialización
	public StringEncrypt encrypt;

	/**
	 * Currently logged in user
	 */
	public AuthorBean getCurrentUser() {
		return this.currentUser;
	}
	
	public Validaciones getValidacion() {
		return validacion;
	}

	public void setValidacion(Validaciones validacion) {
		this.validacion = validacion;
	}



	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	public AImage getMyImage() {
		return myImage;
	}

	public void setMyImage(AImage myImage) {
		this.myImage = myImage;
	}

	public Image getImagen() {
		return imagen;
	}

	public void setImagen(Image imagen) {
		this.imagen = imagen;
	}

	public String getForegroundColour() {
		return foregroundColour;
	}

	public void setForegroundColour(String foregroundColor) {
		this.foregroundColour = foregroundColor;
	}

	public String getBackgroundColour() {
		return backgroundColour;
	}

	public void setBackgroundColour(String backgroundColor) {
		this.backgroundColour = backgroundColor;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}


	public int getMemoHeight() {
		return memoHeight;
	}

	public void setMemoHeight(int memoHeight) {
		this.memoHeight = memoHeight;
	}

	@Command
	@NotifyChange("memoHeight")
	public void changeMemoHeight(
			@ContextParam(ContextType.TRIGGER_EVENT) InputEvent change) {
		try {
			int parsed = Integer.parseInt(change.getValue());
			if (parsed > 0) {
				this.memoHeight = parsed;
			}
		} catch (NumberFormatException nfe) {
			// nothing that we can do here, the validation should pick it up
		}
	}

	@Command
	@NotifyChange("captcha")
	public void regenerate() {
		this.regenerateCaptcha();
	}
	
	private RandomStringGenerator rsg = new RandomStringGenerator(6);
	private HttpServletRequest request = (HttpServletRequest)ServletFns.getCurrentRequest();
	
	private Session sess = Sessions.getCurrent();
	private Usuario userLogin;
	
	@Command("grabar")
	public void grabar(@ContextParam(ContextType.VIEW) Window modalDialog) throws Exception {
		userLogin = (Usuario) sess.getAttribute("userCredential");
		currentUser = userLogin.getUsuarioLogin();
		String imagen = null;
		if(media == null){
			 Messagebox.show("Debe seleccionar una imagen", "Debe seleccionar una imagen", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		imagen=media.getName();	
		currentUser.setAvatar("images/avatars/"+imagen);
		String respuesta = ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/updatePerfil",currentUser, request.getSession());
		
		if(!respuesta.equals("OK")){
			 Messagebox.show(respuesta, "Error al actualizar la imagen de perfil del usuario", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("la imagen de perfil se actualizo con exito", "Information", Messagebox.OK, Messagebox.INFORMATION);
	
		
		guardarArchivo(imagen, media.getByteData());
		Executions.sendRedirect("/index.zul");
	}
	
	@Command
	public void submit(@BindingParam("fx") Form fx) throws Exception {
		String imagen = null;
		try{
			if(media!=null){
				imagen=media.getName();
			}
		}catch(Exception e){
			
		}
		User usuario = new User();
		DatosMed datos = new DatosMed();
		
	    Object TempCategoryList = fx.getField("email");
	    Object age = fx.getField("age");
	    Object userName = fx.getField("userName");
	    Object nombre = fx.getField("nombre");
	    Object telephone = fx.getField("telephone");
	    Object especialidad = fx.getField("especialidad");
	    Object profesion = fx.getField("profesion");
	    //Object imagen = fx.getField("imagen");
	    java.util.Date birthday = (java.util.Date) fx.getField("birthday");
	    String pass = rsg.getRandomString();
		EmailSenderService sendEmail = new EmailSenderService();
		usuario.setUserName(userName.toString());
		usuario.setNombre(nombre.toString());
		usuario.setAge((Integer) age);
		usuario.setBirthday(birthday);
		usuario.setEmail(TempCategoryList.toString());
		usuario.setTelephone(telephone.toString());
		usuario.setPassword(pass);
		if (imagen != null)
			usuario.setImagen("images/avatars/"+imagen.toString());
		if (especialidad != null){
			usuario.setEspecialidad(especialidad.toString());
			usuario.setProfesion(profesion.toString());
		}		
		String respuesta = ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/addUser",usuario);
		if(!respuesta.equals("OK")){
			 Messagebox.show(respuesta, "Error al registrar los datos del usuario", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("El usuario fue creado con exito", "Information", Messagebox.OK, Messagebox.INFORMATION);
	
		if (imagen != null)
			guardarArchivo(imagen, media.getByteData());
		String mail = FormatoNotificacion.Registro;
		String mailaux = mail.replace("%usuario%", userName.toString());
		mailaux = mailaux.replace("%contrasenia%", pass);
		sendEmail.sendEmail(TempCategoryList.toString(),mailaux);
		Executions.sendRedirect("/pass.zul");
	}
	
	@Command
	public void edit(@BindingParam("fx") Form fx) throws Exception {
		userLogin = (Usuario) sess.getAttribute("userCredential");
		currentUser = userLogin.getUsuarioLogin();
		String imagen = null;
		try{
			if(media!=null){
				imagen=media.getName();
			}
		}catch(Exception e){
			
		}
		User usuario = new User();
		DatosMed datos = new DatosMed();
		
	    Object TempCategoryList = fx.getField("email");
	    Object age = fx.getField("age");
	    Object nombre = fx.getField("nombre");
	    Object telephone = fx.getField("telephone");
	    Object especialidad = fx.getField("especialidad");
	    Object profesion = fx.getField("profesion");
	    //Object imagen = fx.getField("imagen");
	    java.util.Date birthday = (java.util.Date) fx.getField("birthday");
	    String pass = rsg.getRandomString();
		EmailSenderService sendEmail = new EmailSenderService();
		usuario.setUserName(userLogin.getUsuario());
		usuario.setNombre(nombre.toString());
		usuario.setAge((Integer) age);
		usuario.setBirthday(birthday);
		if(TempCategoryList != null)
			usuario.setEmail(TempCategoryList.toString());
		if(telephone != null)
			usuario.setTelephone(telephone.toString());
		usuario.setPassword(pass);
		if (imagen != null)
			usuario.setImagen("images/avatars/"+imagen.toString());
		if (especialidad != null){
			usuario.setEspecialidad(especialidad.toString());
			usuario.setProfesion(profesion.toString());
		}		
		String respuesta = ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/editUser",usuario, request.getSession());
		if(!respuesta.equals("OK")){
			 Messagebox.show(respuesta, "Error al actualizar datos del usuario", Messagebox.OK, Messagebox.ERROR);
			return;
		}
		Messagebox.show("Los datos fueron actualizados con exito", "Information", Messagebox.OK, Messagebox.INFORMATION);
	
		if (imagen != null)
			guardarArchivo(imagen, media.getByteData());

		Executions.sendRedirect("/index.zul");
		
	}
	
	@Command
	public void cambiarPass(@BindingParam("fx") Form fx) throws Exception {
		User usuario = new User();
		Validaciones validacion = new Validaciones();
	    Object TempCategoryList = fx.getField("email");
	    Object userName = fx.getField("userName");
	    String pass = rsg.getRandomString();
		EmailSenderService sendEmail = new EmailSenderService();
		usuario.setUserName(userName.toString());
		usuario.setEmail(TempCategoryList.toString());
		usuario.setPassword(pass);
		String mail = FormatoNotificacion.GeneracionContrasenia;
		String mailaux = mail.replace("%usuario%", userName.toString());
		mailaux = mailaux.replace("%contrasenia%", pass);
		int respuesta = validacion.validaUsuarios(userName.toString());
		if (respuesta == 0){
			 Messagebox.show("usuario ingresado no es correcto", "Error", Messagebox.OK, Messagebox.ERROR);
			 return; 
		}else{
			String resp = ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/changePass",usuario);
			if(!resp.equals("OK")){
				 Messagebox.show(resp, "Error al intenatr cambiar la contraseña", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			sendEmail.sendEmail(TempCategoryList.toString(),mailaux);
			Messagebox.show("Conrtraseña cambiada con éxito", "Information", Messagebox.OK, Messagebox.INFORMATION);
			Executions.sendRedirect("/login.zul");
		}
	}
	
	private void guardarArchivo(String nombre, byte[] data) throws Exception {
		// Con este código se agregan los bytes al archivo.
		// aqui obtengo el camino absoluto de mi directorio actual
		String directorio = System.getProperty("user.dir");

		// aqui obtengo el tipo de separador que hay entre las carpetas
		String separador = System.getProperty("file.separator");
		System.out.println(directorio + separador);
		File file = new File(".");
		System.out.println(file.getAbsolutePath());
		
		FileOutputStream fileOuputStream = new FileOutputStream("C:/Users/Administrador/Documents/Instaladores/workspace/redS/redS/WebContent/images/avatars/"+nombre);
		fileOuputStream.write(data);
		fileOuputStream.close();
	}
	
	// para cargar la imagen cuando se haga una publicacion
		@Command("upload")
		@NotifyChange({"myImage"})
		public void onImageUpload(
				@ContextParam(ContextType.BIND_CONTEXT) BindContext ctx) {
			UploadEvent upEvent = null;
			Object objUploadEvent = ctx.getTriggerEvent();
			if (objUploadEvent != null && (objUploadEvent instanceof UploadEvent)) {
				upEvent = (UploadEvent) objUploadEvent;
			}
			if (upEvent != null) {
				media = upEvent.getMedia();
				int lengthofImage = media.getByteData().length;
				if (media instanceof org.zkoss.image.Image) {
					if (lengthofImage > 1000 * 1024) {
						System.out.println("Please Select a Image of size less than 500Kb.");
						return;
					} else {
						myImage = (AImage) media;// Initialize the bind object to
													// show image in zul page and
													// Notify it also
					}
				} 
			}
			else {
				System.out.println("Upload Event Is not Coming");
			}
		}
		
		@Command
		public void cambiar(@BindingParam("fx") Form fx) throws Exception {
			User usuario = new User();
			Validaciones validacion = new Validaciones();
		    Object pass = fx.getField("password");
		    Object passold = fx.getField("passwordact");
		    Object user = fx.getField("userName");
		    usuario.setPassword(pass.toString());
		    usuario.setUserName(user.toString());
		    String respuesta =ApacheHttpClientGet.POST("http://localhost:8080/Prueba/WsRed/changePass",usuario);
			if(!respuesta.equals("OK")){
				 Messagebox.show(respuesta, "Error al intentar cambiar la contraseña", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			Messagebox.show("contraseña cambiada con exito", "Information", Messagebox.OK, Messagebox.INFORMATION);
		
			Executions.sendRedirect("/login.zul");
		}
		
		@Command
		 public void close2() {
			 Executions.sendRedirect("/login.zul");
		}
}
