package org.red.ws.conexion;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.sql.DataSource;
import javax.naming.InitialContext;

public class ConexionBase {

    public Connection conexion;
  
     public ConexionBase(){
    	 /** Constructor de DbConnection */
    	      try{
    	    	  Class.forName ("com.microsoft.sqlserver.jdbc.SQLServerDriver");
              	String db_url = "jdbc:sqlserver://localhost;database=redSocial";
              	conexion = DriverManager.getConnection (db_url, "sa", "Promeinfo2008");
                
    	 
    	         if (conexion!=null){
    	            System.out.println("Conexión a base de datos OK\n");
    	         }
    	      }
    	      catch(SQLException e){
    	         System.out.println(e);
    	      }catch(ClassNotFoundException e){
    	         System.out.println(e);
    	      }catch(Exception e){
    	         System.out.println(e);
    	      }
    	   }
    	   /**Permite retornar la conexión*/
    	   public Connection getConnection(){
    	      return conexion;
    	   }
    	 
    	   public void desconectar(){
    		   conexion = null;
    	   }
    	}