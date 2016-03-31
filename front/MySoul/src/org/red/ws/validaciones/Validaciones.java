package org.red.ws.validaciones;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;

import org.red.ws.conexion.*;

public class Validaciones {

	private ConexionBase conexion;

	public void setConexion(ConexionBase conexion) {
		this.conexion = conexion;
	}

	public int validaUsuarios(String usuario) throws Exception{
		  int respuesta=0;		
		try {
			ConexionBase conex= new ConexionBase();
			   PreparedStatement consulta = conex.getConnection().prepareStatement("SELECT count(*) existe FROM [redSocial].[dbo].[RsPersonas] where usuario = ? ");
			   consulta.setString(1, usuario);
			   ResultSet res = consulta.executeQuery();
			   
			   while(res.next())
				   respuesta = res.getInt("existe");
	
			          res.close();
			          consulta.close();
			          conex.desconectar();;
			    
			  } catch (Exception e) {
				  System.out.println(e);
			  }

			return respuesta;
			 }
	
	public int validaMail(String correo) throws Exception{
		  int respuesta=0;		
		try {
			ConexionBase conex= new ConexionBase();
			   PreparedStatement consulta = conex.getConnection().prepareStatement("SELECT count(*) existe FROM [redSocial].[dbo].[RsPersonas] where correo = ? ");
			   consulta.setString(1, correo);
			   ResultSet res = consulta.executeQuery();
			   
			   while(res.next())
				   respuesta = res.getInt("existe");
	
			          res.close();
			          consulta.close();
			          conex.desconectar();;
			    
			  } catch (Exception e) {
				  System.out.println(e);
			  }

			return respuesta;
			 }
	
	public int validaMailUser(String usuario, String correo) throws Exception{
		  int respuesta=0;		
		try {
			ConexionBase conex= new ConexionBase();
			   PreparedStatement consulta = conex.getConnection().prepareStatement("SELECT count(*) existe FROM [redSocial].[dbo].[RsPersonas] where usuario = ? and correo = ? ");
			   consulta.setString(1, usuario);
			   consulta.setString(2, correo);
			   ResultSet res = consulta.executeQuery();
			   
			   while(res.next())
				   respuesta = res.getInt("existe");
	
			          res.close();
			          consulta.close();
			          conex.desconectar();;
			    
			  } catch (Exception e) {
				  System.out.println(e);
			  }

			return respuesta;
			 }
	
	
	
	public int validaUsuarioPass(String usuario, String pass) throws Exception{
		  int respuesta=0;		
		try {
			ConexionBase conex= new ConexionBase();
			   PreparedStatement consulta = conex.getConnection().prepareStatement("SELECT count(*) existe FROM [redSocial].[dbo].[RsPersonas] where usuario = ? and password = ?");
			   consulta.setString(1, usuario);
			   consulta.setString(2, pass);
			   ResultSet res = consulta.executeQuery();
			   
			   while(res.next())
				   respuesta = res.getInt("existe");
	
			          res.close();
			          consulta.close();
			          conex.desconectar();;
			    
			  } catch (Exception e) {
			   System.out.println(e);
			  }

			return respuesta;
			 }
		

	}
	
