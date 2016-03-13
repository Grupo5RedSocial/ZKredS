package org.zkoss.demo.zkSocial.registro;

import java.util.Map;

import org.red.ws.validaciones.Validaciones;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;
import org.zkoss.image.AImage;
import org.zkoss.zul.Image;

public class FormValidator extends AbstractValidator {
	
	public void validate(ValidationContext ctx) {
		//all the bean properties
		Map<String,Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
		
		String comando = ctx.getCommand();
		
		if ("cambiarPass".equals(comando)){
			validateUserNo(ctx, (String)beanProps.get("userName").getValue());
			validateEmail(ctx, (String)beanProps.get("email").getValue());
		}
		else{
			if ("edit".equals(comando)){
				validateName(ctx, (String)beanProps.get("nombre").getValue());
				validateAge(ctx, (Integer)beanProps.get("age").getValue());
				validateEmail(ctx, (String)beanProps.get("email").getValue());
				validateImage(ctx, (AImage)ctx.getValidatorArg("myImage"));
			}else{
			validateUser(ctx, (String)beanProps.get("userName").getValue());
			validateName(ctx, (String)beanProps.get("nombre").getValue());
			validateAge(ctx, (Integer)beanProps.get("age").getValue());
			validateEmail(ctx, (String)beanProps.get("email").getValue());
			validateCaptcha(ctx, (String)ctx.getValidatorArg("captcha"), (String)ctx.getValidatorArg("captchaInput"));
			validateImage(ctx, (AImage)ctx.getValidatorArg("myImage"));
			}
		}
	}
	
	/*private void validatePasswords(ValidationContext ctx, String password, String retype) {	
		if(password == null || retype == null || (!password.equals(retype))) {
			this.addInvalidMessage(ctx, "password", "Your passwords do not match!");
		}
	}*/
	
	private void validateUser(ValidationContext ctx, String userName) {
		Validaciones validacion = new Validaciones();
		try {
			int respuesta = validacion.validaUsuarios(userName);
			if (userName == null || respuesta == 1)
				this.addInvalidMessage(ctx, "userName", "    Usuario nulo o ya existe");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void validateUserNo(ValidationContext ctx, String userName) {
		Validaciones validacion = new Validaciones();
		try {
			int respuesta = validacion.validaUsuarios(userName);
			if (respuesta == 0)
				this.addInvalidMessage(ctx, "userName", "    Usuario no existe");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void validateName(ValidationContext ctx, String nombre) {
		if( nombre == null ) {
			this.addInvalidMessage(ctx, "nombre", "     Nombre no puede ser nulo");
		}
	}
	
	private void validateAge(ValidationContext ctx, int age) {
		if(age <= 0) {
			this.addInvalidMessage(ctx, "age", "    Edad deberia ser > 0!");
		}
	}
	
	private void validateEmail(ValidationContext ctx, String email) {
		if(email == null || !email.matches(".+@.+\\.[a-z]+")) {
			this.addInvalidMessage(ctx, "email", "    Email no valido!");			
		}
	}
	
	private void validateCaptcha(ValidationContext ctx, String captcha, String captchaInput) {
		if(captchaInput == null || !captcha.equals(captchaInput)) {
			this.addInvalidMessage(ctx, "captcha", "    El captcha es diferente!");
		}
	}
	
	private void validateImage(ValidationContext ctx, AImage myImage) {
		if(myImage == null ) {
			this.addInvalidMessage(ctx, "myImage", "    Imagen no puede ser nula");
		}
	}
	
}
