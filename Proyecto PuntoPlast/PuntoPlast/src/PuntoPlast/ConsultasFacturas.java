/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ConsultasFacturas.java
 *
 *
 */
package PuntoPlast;

import claseconexion.Conectar;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import javax.swing.JOptionPane;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import static PuntoPlast.ConsultasFacturas.tbfacturas;
import static PuntoPlast.Facturacion.cn;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTable;

/**
 *
 * @author Daniel
 */
public class ConsultasFacturas extends javax.swing.JFrame {
    final String msj1="Cedula o Nombre...";
    final String msj2="N° de Factura...";

    /** Creates new form ConsultasFacturas */
    public ConsultasFacturas() {
        initComponents();
        cargartodasfacturas();
        
        String tipo=log.tipousu;
        String Invitado="Invitado";
        
        if(tipo.equals(Invitado)){
            mnanular.setVisible(false);
        }
        
        limpiar();
        bloquear();
        rdbncedula.setSelected(true);
        txtced.setEnabled(true);
        PlaceHolder();
    }
    
    void PlaceHolder(){
        ph1.Mensaje(txtced, msj1, 0);
        ph2.Mensaje(txtnumero, msj2, 0);
    }
    
    void bloquear(){
        txtced.setEnabled(false);
        txtnumero.setEnabled(false);
        calendario.setEnabled(false);
        btnbuscador.setEnabled(false);
    }
    
    void limpiar(){
        PlaceHolder();
        calendario.setDate(null);
    }
    
  void cargartodasfacturas(){
       //carga todas las facturas de la base de datos
        
        DefaultTableModel tabla = (DefaultTableModel) tbfacturas.getModel();
        int a =tbfacturas.getRowCount()-1;
        int i;
        for(i=a;i>=0;i--){
            tabla.removeRow(i);
        }
        String consulta= "SELECT Num_fac, Tipo_ci_clie, b.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                         "JOIN cliente b ON a.Ci_clie=b.Ci_clie " +
                         "UNION " +
                         "SELECT a.Num_fac AS Num_fac, Tipo_ci_clie, c.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                         "JOIN pedido b ON a.Num_fac=b.Num_fac " +
                         "JOIN cliente c ON b.Ci_clie=c.Ci_clie " +
                         "ORDER BY Num_fac";
        
        String []Datos=new String[10];
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs= st.executeQuery(consulta);
            
            while(rs.next()){
                Datos[0]=rs.getString("Num_fac");
                Datos[1]=rs.getString("Tipo_ci_clie");
                Datos[2]=rs.getString("Ci_clie");
                Datos[3]=rs.getString("Nomb_clie");
                Datos[4]=rs.getString("Ape_clie");
                Datos[5]=rs.getString("Tipo_clie");
                Datos[6]=rs.getString("Total_fac");
                Datos[7]=rs.getString("Fecha_fac");
                Datos[8]=rs.getString("Hora_fac");
                Datos[9]=rs.getString("Estado_fac");
                
                tabla.addRow(Datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasProductos.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
  
    void devolverstock(String codi, int cant_dev){

        int cant_p=-1, nueva_cant;
        String consul="SELECT * FROM producto WHERE Cod_prod='"+codi+"'";
        
        try {
            String prod="";
            Statement st= cn.createStatement();
            ResultSet rs= st.executeQuery(consul);
            
            while(rs.next()){
                cant_p=rs.getInt("Stock_prod");
                prod=rs.getString("Nomb_prod");
            }

            if(cant_p>=0){
                nueva_cant=cant_p+cant_dev;
                String modi="UPDATE producto SET Stock_prod="+nueva_cant+" WHERE Cod_prod ='"+codi+"'";
        
                try {
                    PreparedStatement pst = cn.prepareStatement(modi);
                    int n=pst.executeUpdate();
                    
                    if(n>0){
                        System.out.println("Se devolvio "+cant_dev+" "+prod);
                    }
                    
                    } catch (SQLException ex) {
                         System.out.println("No se pudo devolver el producto "+prod+".");
                         System.out.println(ex);
                         JOptionPane.showMessageDialog(null, "Error: No se pudo devolver un producto al stock.");
                    }
            }            
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Hubo un error al consultar un producto.");
        }              
    }
    
    public void anularFactura(String Num_fac){        
        try{
            String titulos[]={"Cod_prod", "Cant_prod"};
            DefaultTableModel tb_temp=new DefaultTableModel(titulos, 0);
                    
            String Datos[]=new String[2];
            String selectProd="SELECT Cod_prod, Cant_prod_comprados FROM factura_producto "
                            + "WHERE Num_fac='"+Num_fac+"'";
                            
            Statement st=cn.createStatement();
            ResultSet rs=st.executeQuery(selectProd);                            
                            
            while(rs.next()){
                Datos[0]=rs.getString("Cod_prod");
                Datos[1]=rs.getString("Cant_prod_comprados");
                                
                tb_temp.addRow(Datos);
            }
                    
            try {
                PreparedStatement pst = cn.prepareStatement("UPDATE factura SET Estado_fac='Anulado' WHERE Num_fac='"+Num_fac+"'");
                int n=pst.executeUpdate();
                
                if(n > 0){//Si se actualiza correctamente, se devuelve al stock
                    int filas=tb_temp.getRowCount();
                                
                    for(int i=0;i<filas;i++){
                        String Cod_prod=tb_temp.getValueAt(i, 0).toString();
                        int Cant_prod=Integer.parseInt(tb_temp.getValueAt(i, 1).toString());                            
                            
                        devolverstock(Cod_prod, Cant_prod);
                    }
                            
                    System.out.println("------------------------------");                        
                    JOptionPane.showMessageDialog(null, "Factura anulada exitosamente.");
                }else{
                    JOptionPane.showMessageDialog(null, "No se pudo delvolver al stock, debido a un problema.");
                }
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error al anular la factura, operacion cancelada");
                Logger.getLogger(ConsultasFacturas.class.getName()).log(Level.SEVERE, null, ex);
            }
                
        }catch(SQLException ex){
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Hubo un error al consultar los productos facturados.");
        }
        cargartodasfacturas();    
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        mnver = new javax.swing.JMenuItem();
        mnanular = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        rdbnnumero = new javax.swing.JRadioButton();
        rdbtnfecha = new javax.swing.JRadioButton();
        rdbntodos = new javax.swing.JRadioButton();
        txtnumero = new javax.swing.JTextField();
        btnbuscador = new javax.swing.JButton();
        calendario = new com.toedter.calendar.JDateChooser();
        btnreporte = new javax.swing.JButton();
        rdbncedula = new javax.swing.JRadioButton();
        txtced = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbfacturas = new javax.swing.JTable();

        mnver.setText("Ver Detalle");
        mnver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnverActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mnver);

        mnanular.setText("Anular");
        mnanular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnanularActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mnanular);

        setTitle("FACTURAS DE CLIENTES");
        setResizable(false);

        buttonGroup1.add(rdbnnumero);
        rdbnnumero.setSelected(true);
        rdbnnumero.setText("Mostrar por factura:");
        rdbnnumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbnnumeroActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbtnfecha);
        rdbtnfecha.setText("Mostrar por fecha:");
        rdbtnfecha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbtnfechaActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbntodos);
        rdbntodos.setText("Mostrar todas");
        rdbntodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbntodosActionPerformed(evt);
            }
        });

        txtnumero.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtnumeroFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtnumeroFocusLost(evt);
            }
        });
        txtnumero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnumeroActionPerformed(evt);
            }
        });
        txtnumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtnumeroKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtnumeroKeyTyped(evt);
            }
        });

        btnbuscador.setText("BUSCAR");
        btnbuscador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnbuscadorActionPerformed(evt);
            }
        });

        btnreporte.setText("GENERAR REPORTE");
        btnreporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnreporteActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbncedula);
        rdbncedula.setText("Mostrar por:");
        rdbncedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbncedulaActionPerformed(evt);
            }
        });

        txtced.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtcedFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtcedFocusLost(evt);
            }
        });
        txtced.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcedActionPerformed(evt);
            }
        });
        txtced.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtcedKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcedKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(208, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(rdbncedula, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(rdbtnfecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rdbnnumero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rdbntodos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtnumero, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtced, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnbuscador, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnreporte))
                .addContainerGap(172, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(20, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbncedula)
                            .addComponent(txtced, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdbnnumero)
                            .addComponent(txtnumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rdbtnfecha)
                            .addComponent(calendario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnbuscador)
                        .addGap(3, 3, 3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rdbntodos)
                    .addComponent(btnreporte))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        tbfacturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° Fact.", "Tipo_CI", "Cédula", "Nombre", "Apellido", "Tipo_clie", "Total", "Fecha", "Hora", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbfacturas.setComponentPopupMenu(jPopupMenu1);
        jScrollPane1.setViewportView(tbfacturas);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBounds(0, 0, 815, 519);
    }// </editor-fold>//GEN-END:initComponents

private void btnbuscadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnbuscadorActionPerformed
// TODO add your handling code here:
    
      //busca en base a la fecha o el codigo, o todos
    
    String num=txtnumero.getText();
    
    String consulta="";
   
    if(rdbtnfecha.isSelected()==true){
        Date fecha=calendario.getDate();
        SimpleDateFormat formatofecha= new SimpleDateFormat("yyyy-MM-dd");
        String fec=""+formatofecha.format(fecha);
        consulta= "SELECT Num_fac, Tipo_ci_clie, b.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN cliente b ON a.Ci_clie=b.Ci_clie " +                  
                  "WHERE Fecha_fac='"+fec+"' " +
                  "UNION " +
                  "SELECT a.Num_fac AS Num_fac, Tipo_ci_clie, c.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN pedido b ON a.Num_fac=b.Num_fac " +
                  "JOIN cliente c ON b.Ci_clie=c.Ci_clie " +
                  "WHERE Fecha_fac='"+fec+"' " +
                  "ORDER BY Hora_fac";
    }
    
    if(rdbntodos.isSelected()==true){
        cargartodasfacturas();
    }
        
        DefaultTableModel tabla = (DefaultTableModel) tbfacturas.getModel();
        int a =tbfacturas.getRowCount()-1;
        int i;
        for(i=a;i>=0;i--){
            tabla.removeRow(i);
        }
        
        String []Datos= new String [10];
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs= st.executeQuery(consulta);
            while(rs.next()){
                Datos[0]=rs.getString("Num_fac");
                Datos[1]=rs.getString("Tipo_ci_clie");
                Datos[2]=rs.getString("Ci_clie");
                Datos[3]=rs.getString("Nomb_clie");
                Datos[4]=rs.getString("Ape_clie");
                Datos[5]=rs.getString("Tipo_clie");
                Datos[6]=rs.getString("Total_fac");
                Datos[7]=rs.getString("Fecha_fac");
                Datos[8]=rs.getString("Hora_fac");
                Datos[9]=rs.getString("Estado_fac");
                
                tabla.addRow(Datos);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasProductos.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_btnbuscadorActionPerformed

private void rdbnnumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbnnumeroActionPerformed
// TODO add your handling code here:
    if(rdbnnumero.isSelected()==true){
        //almacena datos
        limpiar();
        bloquear();
        txtnumero.setEnabled(true);
    }
}//GEN-LAST:event_rdbnnumeroActionPerformed

private void rdbtnfechaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbtnfechaActionPerformed
// TODO add your handling code here:
    if(rdbtnfecha.isSelected()==true){
        //almacena mas datos
        limpiar();
        bloquear();
        calendario.setEnabled(true);
        btnbuscador.setEnabled(true);
    }
}//GEN-LAST:event_rdbtnfechaActionPerformed

private void rdbntodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbntodosActionPerformed
// TODO add your handling code here:
    if(rdbntodos.isSelected()==true){
                //almacena AUN MAS datos
        limpiar();
        bloquear();
        cargartodasfacturas();
    }
}//GEN-LAST:event_rdbntodosActionPerformed

private void mnverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnverActionPerformed
// TODO add your handling code here:
    //accede y envia todos los datos necesarios a detalle factura
    int filasele= tbfacturas.getSelectedRow();
    if(filasele<0){
        JOptionPane.showMessageDialog(null, "No selecciono ninguna fila");
    }else{
        switch(tbfacturas.getValueAt(filasele, 5).toString()){
        
            case "Natural":{        
                            DetalleFacturaNatural detalle = new DetalleFacturaNatural();
                            detalle.toFront();
                            detalle.setVisible(true);
        
                            String numfac=tbfacturas.getValueAt(filasele, 0).toString();
                            String ced=tbfacturas.getValueAt(filasele, 1).toString()+"-"+tbfacturas.getValueAt(filasele, 2).toString();
                            String total=tbfacturas.getValueAt(filasele, 5).toString();
                            String fecha=tbfacturas.getValueAt(filasele, 6).toString();
                            String hora=tbfacturas.getValueAt(filasele, 7).toString();
                            String fechacompleta=" "+fecha+"  "+hora;
        
                            DetalleFacturaNatural.txtfac.setText(numfac);
                            DetalleFacturaNatural.txtced.setText(ced);
                            DetalleFacturaNatural.txttot.setText(total);
                            DetalleFacturaNatural.txtfecha.setText(fechacompleta);
        
                            DefaultTableModel model = (DefaultTableModel) DetalleFacturaNatural.tbdetalle.getModel();
                            
                            String ver="SELECT b.Cod_prod AS Cod_prod, Nomb_prod, Cant_prod_comprados AS Cant_prod, Precio_prod, Precio_cant_comprados AS Precio_cant, Subtotal_fac, Porc_iva FROM producto a " +
                                       "JOIN factura_producto b ON a.Cod_prod=b.Cod_prod " +
                                       "JOIN factura c ON b.Num_fac=c.Num_fac " +
                                       "WHERE b.Num_fac='"+numfac+"'";
        
                            String []datos= new String[5];
        
                            try {
                                    Statement st = cn.createStatement();
                                    ResultSet rs= st.executeQuery(ver);
                                    
                                    while(rs.next()){
                                        datos[0]=rs.getString("Cod_prod");
                                        datos[1]=rs.getString("Nomb_prod");
                                        datos[2]=rs.getString("Precio_prod");
                                        datos[3]=rs.getString("Cant_prod");
                                        datos[4]=rs.getString("Precio_cant");
                                        DetalleFacturaNatural.txtsub.setText(rs.getString("Subtotal_fac"));
                                        DetalleFacturaNatural.txtiva.setText(rs.getString("Porc_iva")+"%");
                    
                                        model.addRow(datos);
                    
                                    }
                                    DetalleFacturaNatural.tbdetalle.setModel(model);
                            } catch (SQLException ex) {
                                    Logger.getLogger(ConsultasFacturas.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
            }
            
            case "Juridico":{
                            DetalleFacturaJuridico detalle = new DetalleFacturaJuridico();
                            detalle.toFront();
                            detalle.setVisible(true);
        
                            String numfac=tbfacturas.getValueAt(filasele, 0).toString();
                            String ced="J-"+tbfacturas.getValueAt(filasele, 2).toString();
                            String total=tbfacturas.getValueAt(filasele, 6).toString();
                            String fecha=tbfacturas.getValueAt(filasele, 7).toString();
                            String hora=tbfacturas.getValueAt(filasele, 8).toString();
                            String fechacompleta=" "+fecha+"  "+hora;
        
                            DetalleFacturaJuridico.txtfac.setText(numfac);
                            DetalleFacturaJuridico.txtced.setText(ced);
                            DetalleFacturaJuridico.txttot.setText(total);
                            DetalleFacturaJuridico.txtfecha.setText(fechacompleta);        
        
                            DefaultTableModel model = (DefaultTableModel) DetalleFacturaJuridico.tbdetalle.getModel();
                            String ver="SELECT b.Cod_prod AS Cod_prod, Nomb_prod, Cant_prod_comprados AS Cant_prod, Precio_prod, Precio_cant_comprados AS Precio_cant, Subtotal_fac, Porc_iva FROM producto a " +
                                       "JOIN factura_producto b ON a.Cod_prod=b.Cod_prod " +
                                       "JOIN factura c ON b.Num_fac=c.Num_fac " +
                                       "WHERE b.Num_fac='"+numfac+"'";
        
                            String []datos= new String[5];
                                
                            try {
                                Statement st = cn.createStatement();
                                ResultSet rs= st.executeQuery(ver);
                
                                while(rs.next()){
                                    datos[0]=rs.getString("Cod_prod");
                                    datos[1]=rs.getString("Nomb_prod");
                                    datos[2]=rs.getString("Precio_prod");
                                    datos[3]=rs.getString("Cant_prod");
                                    datos[4]=rs.getString("Precio_cant");                    
                                    DetalleFacturaJuridico.txtsub.setText(rs.getString("Subtotal_fac"));
                                    DetalleFacturaJuridico.txtiva.setText(rs.getString("Porc_iva")+"%");
                    
                                    model.addRow(datos);                   
                                }
                
                                DetalleFacturaJuridico.tbdetalle.setModel(model);
                            } catch (SQLException ex) {
                                Logger.getLogger(ConsultasFacturas.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
            }
        } 
    }
}//GEN-LAST:event_mnverActionPerformed

private void mnanularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnanularActionPerformed
// TODO add your handling code here:
    int fila=tbfacturas.getSelectedRow();    
    
    if(fila>=0){//Anular ventas
        String cod=tbfacturas.getValueAt(fila, 0).toString();
        String ced=tbfacturas.getValueAt(fila, 2).toString();
        
        try{
            String estado="";
            String sql="SELECT * FROM factura WHERE Num_fac='"+cod+"'";
        
            Statement st=cn.createStatement();
            ResultSet rs=st.executeQuery(sql);
        
            while(rs.next()){
                estado=rs.getString("Estado_fac");
            }
            
            if(estado.equals("Anulado")){//Si la factura esta anulada
                JOptionPane.showMessageDialog(null, "La factura ya está anulada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);                 
            }else{//Si no se ha anulado                
                int resp=JOptionPane.showConfirmDialog(null, "Atencion: Al anular la factura se devolveran los productos al stock. ¿Desea continuar?");
                
                if(resp==JOptionPane.YES_OPTION){// Si la respuesta es afirmativa
                    try{
                        String pedido="";
                        String consulta="SELECT * FROM factura WHERE Num_fac='"+cod+"' AND Ci_clie="+ced+"";
                        
                        Statement st1=cn.createStatement();
                        ResultSet rs1=st1.executeQuery(consulta);
                        
                        while(rs1.next()){
                            pedido=rs1.getString("Ci_clie");                            
                        }
                        
                        if(pedido.equals("")){//Si la factura proviene de un pedido
                            try{//Se anula el pedido                               
                                String update="UPDATE pedido SET Estado_ped='Anulado', Fecha_estado_ped=CURRENT_TIMESTAMP WHERE Num_fac="+cod+"";
        
                                PreparedStatement pst=cn.prepareStatement(update);
                                int n=pst.executeUpdate();
        
                                if(n>0){//Si se anulo el pedido
                                    anularFactura(cod);
                                }else{
                                    JOptionPane.showMessageDialog(null, "Error al anular la factura del pedido.");
                                }       
                            }catch(SQLException ex){
                                System.out.println("Error: "+ex);
                                JOptionPane.showMessageDialog(null, "Error al actualizar tabla pedido.");
                            }
                        }else{//Si es una factura factura particular
                            anularFactura(cod);
                        }
                    }catch(SQLException ex){
                        System.out.println("Error: "+ex);
                        JOptionPane.showMessageDialog(null, "Error al verificar si la factura proviene de un pedido.");
                    }                                    
                }
            }
        }catch(SQLException ex){
            System.out.println("Error: "+ex);
            JOptionPane.showMessageDialog(null, "Operacion cancelada: No se pudo verificar el estado de la factura.", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }else{
        JOptionPane.showMessageDialog(this, "Seleccione alguna fila, por favor.");
    }
}//GEN-LAST:event_mnanularActionPerformed

    private void btnreporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreporteActionPerformed
        // TODO add your handling code here: 
        int fila=tbfacturas.getSelectedRow();       
        
        if(fila>=0){
            switch(tbfacturas.getValueAt(fila, 5).toString()){
            
                case "Natural":{            
                                String numfac=tbfacturas.getValueAt(fila, 0).toString();
                                JasperReport jru = null;
            
                                try {
                                    jru = (JasperReport) JRLoader.loadObject(getClass().getResource("reporteFacturaNatural.jasper"));
                
                                    //Parametro para generar una consulta especifica
                                    Map parametros = new HashMap();
                
                                    parametros.put("Num_fac", numfac);//Nombre del parametro del reporte y la variable que guardara
                
                                    JasperPrint jpu = JasperFillManager.fillReport(jru, parametros, cc.conexion());//Le pasamos el reporte y el parametro para llenarlo
                
                                    JasperViewer jvu = new JasperViewer(jpu, false);//Visor del reporte
                                    jvu.setVisible(true);
                                    jvu.setTitle("Reporte de Factura");
                                    jvu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                } catch (JRException ex) {
                                    System.out.println("Error: "+ex);
                                    JOptionPane.showMessageDialog(null, "Lo sentimos, hubo un error al generar el reporte.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                }
                case "Juridico":{
                                String numfac=tbfacturas.getValueAt(fila, 0).toString();
                                JasperReport jru = null;
            
                                try {
                                    jru = (JasperReport) JRLoader.loadObject(getClass().getResource("reporteFacturaJuridico.jasper"));
                
                                    //Parametro para generar una consulta especifica
                                    Map parametros = new HashMap();
                
                                    parametros.put("Num_fac", numfac);//Nombre del parametro del reporte y la variable que guardara
                
                                    JasperPrint jpu = JasperFillManager.fillReport(jru, parametros, cc.conexion());//Le pasamos el reporte y el parametro para llenarlo
                
                                    JasperViewer jvu = new JasperViewer(jpu, false);//Visor del reporte
                                    jvu.setVisible(true);
                                    jvu.setTitle("Reporte de Factura");
                                    jvu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                } catch (JRException ex) {
                                    System.out.println("Error: "+ex);
                                    JOptionPane.showMessageDialog(null, "Lo sentimos, hubo un error al generar el reporte.", "Error", JOptionPane.ERROR_MESSAGE);
                                }                
                                break;
                }                
            }          
        }else{
            JOptionPane.showMessageDialog(this, "Seleccione alguna fila.");
        }
    }//GEN-LAST:event_btnreporteActionPerformed

    private void txtnumeroKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnumeroKeyReleased
        // TODO add your handling code here:
        String num=txtnumero.getText();
        String consulta="";
        consulta= "SELECT Num_fac, Tipo_ci_clie, b.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN cliente b ON a.Ci_clie=b.Ci_clie " +
                  "WHERE Num_fac LIKE '"+num+"%' " +
                  "UNION " +
                  "SELECT a.Num_fac AS Num_fac, Tipo_ci_clie, c.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN pedido b ON a.Num_fac=b.Num_fac " +
                  "JOIN cliente c ON b.Ci_clie=c.Ci_clie " +
                  "WHERE a.Num_fac LIKE '"+num+"%' " +
                  "ORDER BY Num_fac";        
        
        DefaultTableModel tabla = (DefaultTableModel) tbfacturas.getModel();
        int a =tbfacturas.getRowCount()-1;
        int i;
        for(i=a;i>=0;i--){
            tabla.removeRow(i);           
        }
        String []Datos=new String[10];
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs= st.executeQuery(consulta);
            
            while(rs.next()){
                Datos[0]=rs.getString("Num_fac");
                Datos[1]=rs.getString("Tipo_ci_clie");
                Datos[2]=rs.getString("Ci_clie");
                Datos[3]=rs.getString("Nomb_clie");
                Datos[4]=rs.getString("Ape_clie");
                Datos[5]=rs.getString("Tipo_clie");
                Datos[6]=rs.getString("Total_fac");
                Datos[7]=rs.getString("Fecha_fac");
                Datos[8]=rs.getString("Hora_fac");
                Datos[9]=rs.getString("Estado_fac");
           
                tabla.addRow(Datos);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasProductos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtnumeroKeyReleased

    private void txtcedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtcedActionPerformed

    private void rdbncedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbncedulaActionPerformed
        // TODO add your handling code here:
        if(rdbncedula.isSelected()==true){
            limpiar();
            bloquear();
            txtced.setEnabled(true);
        }
    }//GEN-LAST:event_rdbncedulaActionPerformed

    private void txtnumeroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnumeroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnumeroActionPerformed

    private void txtcedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcedKeyReleased
        // TODO add your handling code here:
        String ced=txtced.getText();
        String consulta="";
        consulta= "SELECT Num_fac, Tipo_ci_clie, b.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN cliente b ON a.Ci_clie=b.Ci_clie " +
                  "WHERE (b.Ci_clie LIKE '"+ced+"%') OR (Nomb_clie LIKE '"+ced+"%') " +
                  "UNION " +
                  "SELECT a.Num_fac AS Num_fac, Tipo_ci_clie, c.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie, Tipo_clie, Total_fac, DATE_FORMAT(Fecha_fac, '%d-%m-%Y') AS Fecha_fac, Hora_fac, Estado_fac FROM factura a " +
                  "JOIN pedido b ON a.Num_fac=b.Num_fac " +
                  "JOIN cliente c ON b.Ci_clie=c.Ci_clie " +
                  "WHERE (c.Ci_clie LIKE '"+ced+"%') OR (Nomb_clie LIKE '"+ced+"%') " +
                  "ORDER BY Num_fac";        
        
        DefaultTableModel tabla = (DefaultTableModel) tbfacturas.getModel();
        int a =tbfacturas.getRowCount()-1;
        int i;
        for(i=a;i>=0;i--){
            tabla.removeRow(i);           
        }
        String []Datos=new String[10];
        
        try {
            Statement st = cn.createStatement();
            ResultSet rs= st.executeQuery(consulta);
            
            while(rs.next()){
                Datos[0]=rs.getString("Num_fac");
                Datos[1]=rs.getString("Tipo_ci_clie");
                Datos[2]=rs.getString("Ci_clie");
                Datos[3]=rs.getString("Nomb_clie");
                Datos[4]=rs.getString("Ape_clie");
                Datos[5]=rs.getString("Tipo_clie");
                Datos[6]=rs.getString("Total_fac");
                Datos[7]=rs.getString("Fecha_fac");
                Datos[8]=rs.getString("Hora_fac");
                Datos[9]=rs.getString("Estado_fac");
           
                tabla.addRow(Datos);                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConsultasProductos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_txtcedKeyReleased

    private void txtcedKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcedKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        
        if((c<'a'||c>'z')&&(c<'A'||c>'Z')&&(c<'0'||c>'9')&&(c!=KeyEvent.VK_BACK_SPACE)&&(c!=KeyEvent.VK_SPACE)){
            evt.consume();
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtcedKeyTyped

    private void txtnumeroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtnumeroKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        
        if((c<'0'||c>'9')&&(c!=KeyEvent.VK_BACK_SPACE)){
            evt.consume();
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtnumeroKeyTyped

    private void txtcedFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcedFocusGained
        // TODO add your handling code here:
        ph1.Comparar(txtced);
    }//GEN-LAST:event_txtcedFocusGained

    private void txtcedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtcedFocusLost
        // TODO add your handling code here:
        ph1.Mensaje(txtced, msj1, 0);
    }//GEN-LAST:event_txtcedFocusLost

    private void txtnumeroFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnumeroFocusGained
        // TODO add your handling code here:
         ph2.Comparar(txtnumero);
    }//GEN-LAST:event_txtnumeroFocusGained

    private void txtnumeroFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtnumeroFocusLost
        // TODO add your handling code here:
         ph2.Mensaje(txtnumero, msj2, 0);
    }//GEN-LAST:event_txtnumeroFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnbuscador;
    private javax.swing.JButton btnreporte;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser calendario;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuItem mnanular;
    private javax.swing.JMenuItem mnver;
    private javax.swing.JRadioButton rdbncedula;
    private javax.swing.JRadioButton rdbnnumero;
    private javax.swing.JRadioButton rdbntodos;
    private javax.swing.JRadioButton rdbtnfecha;
    public static javax.swing.JTable tbfacturas;
    private javax.swing.JTextField txtced;
    private javax.swing.JTextField txtnumero;
    // End of variables declaration//GEN-END:variables
    Conectar cc= new Conectar();
    Connection cn= cc.conexion();
    Login log= new Login();
    PlaceHolder ph1=new PlaceHolder();
    PlaceHolder ph2=new PlaceHolder();
}
