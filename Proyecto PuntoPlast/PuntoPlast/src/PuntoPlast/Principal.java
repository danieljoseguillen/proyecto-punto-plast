/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PuntoPlast;

import claseconexion.Conectar;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
/**
 *
 * @author Daniel
 */

public class Principal extends JFrame{

    public Principal (){
    
        System.out.println("");
        String nombre= log.nombre;
        String tipo=log.tipousu;
        setSize (720,550);
        ((JPanel)getContentPane()).setOpaque(false); 
        ImageIcon uno;
        uno = new ImageIcon(this.getClass().getResource("/imagenes/punto.jpg"));
        JLabel fondo= new JLabel(); 
        fondo.setIcon(uno); 
        getLayeredPane().add(fondo,JLayeredPane.FRAME_CONTENT_LAYER); 
        fondo.setBounds(0,0,uno.getIconWidth(),uno.getIconHeight());
        this.setTitle("Sistema de Facturacion e Inventario Punto Plast.CA");
        this.setLocationRelativeTo(null);
        
        //menus para agregar a la barra de menu
        MenuBar mb= new MenuBar ();
        Menu ingd= new Menu ("Ingresar Datos");
        MenuItem i,cl;
        
        //parte registros
        i= new MenuItem ("Registro de Productos");
        cl= new MenuItem ("Registro de Clientes");
        setMenuBar (mb);
        ingd.add(cl);
        if(tipo.equals("Administrador")){
            ingd.add(i);
        }        
        mb.add(ingd);
        
        // parte facturas
        Menu fac= new Menu ("Facturar");
        MenuItem c;
        c= new MenuItem ("Facturar cliente");
        fac.add(c);
        mb.add(fac);
        
        //parte consultas
        Menu con= new Menu ("Consultas");
        MenuItem ci,ccl,cf;
        ci= new MenuItem ("Productos");
        ccl= new MenuItem ("Clientes");
        cf= new MenuItem ("Facturas");

        con.add(ci);con.add(ccl);con.add(cf);
        mb.add(con);

        //parte reportes
        Menu rep= new Menu ("Reportes");
        MenuItem rp,rc,rf;
        rp= new MenuItem ("Reporte de Inventario");
        rc= new MenuItem ("Reporte de Clientes");
        rf= new MenuItem ("Reporte de Facturas");
        rep.add(rp);rep.add(rc);rep.add(rf);
        mb.add(rep);
        
        //otre parte
        MenuItem sa, adc, iv, cp;
        Menu op= new Menu("Opciones");
        adc= new MenuItem("Administrar cuentas");
        cp=new MenuItem("Cambiar contraseña");
        iv= new MenuItem("Cambiar valor de Iva");
        sa= new MenuItem ("Salir");
        
        if(tipo.equals("Administrador")){
            op.add(adc);
            op.add(cp);
            op.add(iv);            
        }
        op.add(sa);
        mb.add(op);
        
        c.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
                Facturacion ven2= new Facturacion ();
                ven2.setVisible(true);
                ven2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        sa.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
                ConfirmarSalir ven3= new ConfirmarSalir ();
                ven3.setVisible(true);
                ven3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });

       this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ConfirmarSalir ven3= new ConfirmarSalir ();
                ven3.setVisible(true);
                ven3.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
       
        cl.addActionListener(new ActionListener(){
            @Override
             public void actionPerformed(ActionEvent e) {
        
                IngresoCliente ven4= new IngresoCliente ();
                ven4.setVisible(true);
                ven4.requestFocus();
                ven4.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        i.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
            IngresoProductos ven6= new IngresoProductos();
            ven6.setVisible(true);
            ven6.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        ccl.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
            ConsultasClientes ven7= new ConsultasClientes ();
            ven7.setVisible(true);
            ven7.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        ci.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
            ConsultasProductos ven8= new ConsultasProductos ();
            ven8.setVisible(true);
            ven8.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        cf.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
                ConsultasFacturas ven9= new ConsultasFacturas();
                ven9.setVisible(true);
                ven9.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        
        
        rp.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JasperReport jru = null;
                
                try {
                    jru = (JasperReport) JRLoader.loadObject(getClass().getResource("reporteProductos.jasper"));
                    JasperPrint jpu = JasperFillManager.fillReport(jru, null, cc.conexion());
                    JasperViewer jvu = new JasperViewer(jpu, false);
                    jvu.setVisible(true);
                    jvu.setTitle("Reporte de Inventario");
                    jvu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                } catch (JRException ex) {
                    Logger.getLogger(ConsultasProductos.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
        });
        
        rc.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JasperReport jru = null;
                
                try {
                    jru = (JasperReport) JRLoader.loadObject(getClass().getResource("reporteClientes.jasper"));
                    JasperPrint jpu = JasperFillManager.fillReport(jru, null, cc.conexion());
                    JasperViewer jvu = new JasperViewer(jpu, false);
                    jvu.setVisible(true);
                    jvu.setTitle("Reporte de Clientes");
                    jvu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                } catch (JRException ex) {
                    Logger.getLogger(ConsultasClientes.class.getName()).log(Level.SEVERE, null, ex);
                }   
            }
        });
        
        rf.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                   ReporteFacturas venf= new ReporteFacturas();
                   venf.setVisible(true);
                   venf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        adc.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
                Usuarios ven10= new Usuarios ();
                ven10.setVisible(true);
                ven10.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        
        cp.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                CambiarPassword ven11=new CambiarPassword();
                ven11.setVisible(true);
                ven11.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        
        });
        
        iv.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
        
                Iva ven12= new Iva ();
                ven12.setVisible(true);
                ven12.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });        
        
    }
    
    Login log= new Login();
    Conectar cc= new Conectar();
    Connection cn = cc.conexion();
}
