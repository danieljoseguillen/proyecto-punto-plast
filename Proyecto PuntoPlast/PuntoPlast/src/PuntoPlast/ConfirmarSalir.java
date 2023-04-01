/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PuntoPlast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Daniel
 */
public class ConfirmarSalir extends JFrame{
    public ConfirmarSalir (){
        super();
        setSize (200,100);
        
        //localizacion y valores de tamano, esteticos y localizacion del cuadro
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setTitle("Salir");
        JLabel nom= new JLabel ("Desea salir?");
        nom.setBounds(60, 5, 100, 20);
        this.add(nom);

        JButton acep= new JButton ("Si");
        acep.setBounds(40, 35, 50, 20);
        this.add(acep);
        JButton can= new JButton ("No");
        can.setBounds(100, 35, 50, 20);
        this.add(can);
    
        acep.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //salida del sistema
                System.exit(0); 
            }
        });
        can.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //solo se cerrara esta ventana y no el sistema
                dispose();
            }
        });
    }
}