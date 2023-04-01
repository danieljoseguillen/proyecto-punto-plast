/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PuntoPlast;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;


/**
 *
 * @author Cedeños
 */
public class PlaceHolder {
    
    final String gris="#818181";
    final String negro="#000000";
    String mensaje;
    
    public void Mensaje(JTextField letra, String msj, int tamanio){
        
        if(tamanio==0){
            this.mensaje=msj;
            letra.setText(msj);
            letra.setForeground(Color.decode(gris));
            letra.setFont(new Font("Tahoma", Font.ITALIC, 11));
        }
    }
    
    public void Comparar(JTextField letra){
        
            if(letra.getText().equals(mensaje)){                
                letra.setText("");
                letra.setForeground(Color.decode(negro));
                letra.setFont(new Font("Tahoma",Font.PLAIN, 11));
            }
    }    
}
