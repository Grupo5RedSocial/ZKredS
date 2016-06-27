package org.zkoss.demo.zkSocial.registro;

	import java.util.Map;

import org.red.ws.StringEncrypt;
import org.red.ws.validaciones.Validaciones;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.validator.AbstractValidator;

	public class FormValidatorPass extends AbstractValidator {
		
		public String key = "92AE31A79FEEB2A3"; //llave
		public String iv = "0123456789ABCDEF"; // vector de inicialización
		public StringEncrypt encrypt;
		
		public void validate(ValidationContext ctx) {
			//all the bean properties
			Map<String,Property> beanProps = ctx.getProperties(ctx.getProperty().getBase());
			
			//first let's check the passwords match
			validateUser(ctx, (String)beanProps.get("userName").getValue());
			try {
				validatepasswordact(ctx,(String)beanProps.get("userName").getValue(), (String)beanProps.get("passwordact").getValue());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			validatePasswords(ctx, (String)beanProps.get("password").getValue(), (String)ctx.getValidatorArg("retypedPassword"));
		}
		
		private void validateUser(ValidationContext ctx, String userName) {	
			Validaciones validacion = new Validaciones();
			try {
				int respuesta = validacion.validaUsuarios(userName);
				if (respuesta == 0)
					this.addInvalidMessage(ctx, "userName", "Usuario incorrecto");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void validatepasswordact(ValidationContext ctx,String userName, String password) throws Exception {	
			Validaciones validacion = new Validaciones();
			String passencrypt = StringEncrypt.encrypt(key, iv,password);
			try {
				int respuesta = validacion.validaUsuarioPass(userName, passencrypt);
				if (respuesta == 0)
					this.addInvalidMessage(ctx, "passwordact", "password incorrecto");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		private void validatePasswords(ValidationContext ctx, String password, String retype) {	
			if(password == null || retype == null || (!password.equals(retype))) {
				this.addInvalidMessage(ctx, "password", "Your passwords do not match!");
			}
		}
		
	}
