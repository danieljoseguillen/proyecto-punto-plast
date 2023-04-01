package claseconexion;

import java.sql.*;
import javax.swing.*;
/**
 *
 * @author Sony
 */

public class Conectar {
Connection conect = null;
   public Connection conexion()
    {
      try {
             
           Class.forName("com.mysql.jdbc.Driver");
           conect = DriverManager.getConnection("jdbc:mysql://localhost/bd_puntoplast","root","");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,"Error: No hay conexion con la base de datos. ");
        }
        return conect;
     
}}
