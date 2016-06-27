package org.red.ws;

import org.ws.util.entidad.Usuario;

public class Respuesta {
	private String codigo;
	private String mensaje;
	private Usuario data;
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public Usuario getData() {
		return data;
	}
	public void setData(Usuario data) {
		this.data = data;
	}
}
