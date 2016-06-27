package org.zkoss.demo.zkSocial.util;

public class FormatoNotificacion {

	public static String Registro = " <html> "+ 
            "    <head> "+ 
            "       <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> "+ 
            "       <title>Generación de Contraseña</title> "+ 
            "    </head> "+ 
            "    <body> "+ 
            "        <table border=\"0\" width=\"800\"> "+ 
            "            <tr>" + 
            "               <td> "+ 
            "                  <center> "+ 
            "                       <table border=\"1\" width=\"400\" cellpadding=\"10\"> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\"> "+ 
            "                                    <img src=\"cid:image\" /> "+ 
            "                               </td> "+ 
            "                            </tr> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\" > "+ 
            "                                   Estimado <b>%usuario%</b>, <br><br> "+ 
            "                                   Ud se ha registrado en la apliacaion T-SOUL. Su "+ 
            "                                   contraseña es <b>%contrasenia%</b> .<br><br> Proceda a conectarse "+ 
            "                                   a la aplicación con esta nueva credencial. "+ 
            "                               </td> "+ 
            "                           </tr> "+ 
            "                           <tr> "+ 
            "                              <td colspan=\"2\"> "+ 
            "                                 <center> "+ 
            "                                    El presente correo es automático y no necesita respuesta. "+           
            "                                 </center> "+ 
            "                              </td> "+ 
            "                           </tr> "+ 
            "                        </table> "+ 
            "                  </center> "+ 
            "               </td> "+ 
            "            </tr> "+ 
            "        </table> "+ 
            "     </body> "+ 
            " </html> ";

	
	public static String GeneracionContrasenia = " <html> "+ 
            "    <head> "+ 
            "       <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> "+ 
            "       <title>Generación de Contraseña</title> "+ 
            "    </head> "+ 
            "    <body> "+ 
            "        <table border=\"0\" width=\"800\"> "+ 
            "            <tr>" + 
            "               <td> "+ 
            "                  <center> "+ 
            "                       <table border=\"1\" width=\"400\" cellpadding=\"10\"> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\"> "+ 
            "                                    <img src=\"cid:image\" /> "+ 
            "                               </td> "+ 
            "                            </tr> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\" > "+ 
            "                                   Estimado %usuario%, <br><br> "+ 
            "                                   Se ha procesado su solicitud de generación de contraseña. Su "+ 
            "                                   nueva contraseña es <b>%contrasenia%</b> .<br><br> Proceda a conectarse "+ 
            "                                   a la aplicación con esta nueva credencial. "+ 
            "                               </td> "+ 
            "                           </tr> "+ 
            "                           <tr> "+ 
            "                              <td colspan=\"2\"> "+ 
            "                                 <center> "+ 
            "                                    El presente correo es automático y no necesita respuesta. "+           
            "                                 </center> "+ 
            "                              </td> "+ 
            "                           </tr> "+ 
            "                        </table> "+ 
            "                  </center> "+ 
            "               </td> "+ 
            "            </tr> "+ 
            "        </table> "+ 
            "     </body> "+ 
            " </html> ";

	public static String MailMedicos = " <html> "+ 
            "    <head> "+ 
            "       <meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"> "+ 
            "       <title>Generación de Contraseña</title> "+ 
            "    </head> "+ 
            "    <body> "+ 
            "        <table border=\"0\" width=\"800\"> "+ 
            "            <tr>" + 
            "               <td> "+ 
            "                  <center> "+ 
            "                       <table border=\"1\" width=\"400\" cellpadding=\"10\"> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\"> "+ 
            "                                    <img src=\"cid:image\" /> "+ 
            "                               </td> "+ 
            "                            </tr> "+ 
            "                            <tr> "+ 
            "                               <td colspan=\"2\" > "+ 
            "                                   Estimado Usuario, <br><br> "+ 
            "                                   Ha recibido un mensaje del paciente <b>%paciente%</b>.  "+ 
            "                                   <br><br> Proceda a conectarse "+ 
            "                                   a la aplicación para realizar sus respectivos comentarios. "+ 
            "                               </td> "+ 
            "                           </tr> "+ 
            "                           <tr> "+ 
            "                              <td colspan=\"2\"> "+ 
            "                                 <center> "+ 
            "                                    El presente correo es automático y no necesita respuesta. "+           
            "                                 </center> "+ 
            "                              </td> "+ 
            "                           </tr> "+ 
            "                        </table> "+ 
            "                  </center> "+ 
            "               </td> "+ 
            "            </tr> "+ 
            "        </table> "+ 
            "     </body> "+ 
            " </html> ";
	
	public static String getRegistro() {
		return Registro;
	}

	public static void setRegistro(String registro) {
		Registro = registro;
	}

	public static String getGeneracionContrasenia() {
		return GeneracionContrasenia;
	}

	public static void setGeneracionContrasenia(String generacionContrasenia) {
		GeneracionContrasenia = generacionContrasenia;
	}

	public static String getMailMedicos() {
		return MailMedicos;
	}

	public static void setMailMedicos(String mailMedicos) {
		MailMedicos = mailMedicos;
	}


	
}
