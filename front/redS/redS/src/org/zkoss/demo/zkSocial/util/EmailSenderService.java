package org.zkoss.demo.zkSocial.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSenderService {
	private final Properties properties = new Properties();
	
	private String password;

	private Session session;

    InputStream entrada = null;

	private void init() {
		
		try {
		entrada = new FileInputStream("C:/Users/Administrador/Documents/Instaladores/workspace/redS/redS/WebContent/WEB-INF/redS.properties");
        // cargamos el archivo de propiedades
		properties.load(entrada);
		
		 } catch (IOException ex) {
		        ex.printStackTrace();
		    } finally {
		        if (entrada != null) {
		            try {
		                entrada.close();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		    }

		/*properties.put("mail.smtp.host", "smtp-mail.outlook.com");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port",587);
		properties.put("mail.smtp.mail.sender","johed_nb@hotmail.com");
		properties.put("mail.smtp.user", "johed_nb@hotmail.com");
		properties.put("mail.smtp.auth", "true");*/

		session = Session.getDefaultInstance(properties);
	}

	public void sendEmail(String mailRecibe, String texto){

		init();
		try{
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress((String)properties.get("mail.smtp.mail.sender")));
			//message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailRecibe));
			 StringTokenizer emailsSt = new StringTokenizer(mailRecibe,";,");
		        while (emailsSt.hasMoreTokens()) {
		            String email=emailsSt.nextToken();
		            try{
		                //agregamos las direcciones de email que reciben el email, en el primer parametro envíamos el tipo de receptor
		            	message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
		                //Message.RecipientType.TO;  para
		                //Message.RecipientType.CC;  con copia
		                //Message.RecipientType.BCC; con copia oculta
		            }catch(Exception ex){
		                //en caso que el email esté mal formado lanzará una exception y la ignoramos
		            }
		        }
			message.setSubject("Prueba");
			message.setContent(texto, "text/html");
			Transport t = session.getTransport("smtp");
			t.connect((String)properties.get("mail.smtp.user"), (String) properties.get("mail.smtp.pass"));
			t.sendMessage(message, message.getAllRecipients());
			t.close();
		}catch (MessagingException me){
                        //Aqui se deberia o mostrar un mensaje de error o en lugar
                        //de no hacer nada con la excepcion, lanzarla para que el modulo
                        //superior la capture y avise al usuario con un popup, por ejemplo.
			return;
		}
		
	}
}