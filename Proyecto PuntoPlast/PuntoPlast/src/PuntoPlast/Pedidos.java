/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * Productos.java
 *
 *
 */
package PuntoPlast;

import static PuntoPlast.Facturacion.fechaactual;
import static PuntoPlast.Facturacion.tbfact;
import static PuntoPlast.Facturacion.txtced;
import static PuntoPlast.Facturacion.txtdir;
import static PuntoPlast.Facturacion.txtempresa;
import static PuntoPlast.Facturacion.txtfac;
import static PuntoPlast.Facturacion.txtfec;
import static PuntoPlast.Facturacion.txtiva;
import static PuntoPlast.Facturacion.txtnomape;
import static PuntoPlast.Facturacion.txtpedi;
import static PuntoPlast.Facturacion.txtrif;
import static PuntoPlast.Facturacion.txtsubtotal;
import static PuntoPlast.Facturacion.txttlf;
import static PuntoPlast.Facturacion.txttotal;
import claseconexion.Conectar;
import java.awt.Font;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrador
 */
public class Pedidos extends javax.swing.JFrame {

    DefaultTableModel tabla;
    static String cedula;
    static String tipo;
    static int fila_selec;

    public Pedidos(String ced, String tipo_clie, int fila_seleccionada) {
        initComponents();        
        cedula=ced;
        tipo=tipo_clie; 
        fila_selec=fila_seleccionada;
        Facturacion.pedidos=true;
        cargarPedidos("");
        this.setVisible(true);

    }

    public static void cargarPedidos(String dato) {
        DefaultTableModel tabla = (DefaultTableModel) tbpedidos.getModel();
        int a = tbpedidos.getRowCount() - 1;
        int i;
        for (i = a; i >= 0; i--) {
            tabla.removeRow(i);
        }
        
            String[] Registro = new String[4];
            String sql = "SELECT Num_ped, b.Ci_clie AS Ci_clie, Nomb_clie, Ape_clie FROM cliente a " +
                         "JOIN pedido b ON a.Ci_clie=b.Ci_clie " +                        
                         "WHERE (b.Ci_clie="+cedula+" AND Estado_ped='Procesado' AND Num_fac IS NULL) AND (Num_ped LIKE '"+dato+"%')";
            
            try {
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(sql);
                    while (rs.next()) {
                        Registro[0] = rs.getString("Num_ped");
                        Registro[1] = rs.getString("Ci_clie");
                        Registro[2] = rs.getString("Nomb_clie");
                        Registro[3] = rs.getString("Ape_clie");

                        tabla.addRow(Registro);
                    }
                    tbpedidos.setModel(tabla);
            } catch (SQLException ex) {
                Logger.getLogger(Pedidos.class.getName()).log(Level.SEVERE, null, ex);
            }
        
    }
    
    void facturarPedidos(int fila, int cant_ped){
        
        int ult_fila=cant_ped-1;
        
        try{//Se genera el nuevo numero de la factura
            String ult_fact="", nueva_fact="";
            String select="SELECT max(Num_fac) AS Factura_actual FROM factura";
            
            Statement st=cn.createStatement();
            ResultSet rs=st.executeQuery(select);
            
            while(rs.next()){
                ult_fact=rs.getString("Factura_actual");
            }
            
            if(ult_fact.equals("")){
                nueva_fact="00000001";
            }else{                 
                 GenerarNumero gen= new GenerarNumero();
                 gen.generar(Integer.parseInt(ult_fact));
                 nueva_fact=gen.serie();
            }
            
            try{//Se obtiene el iva
                String coniva="";                
            
                String select1="SELECT Porc_iva, Fecha_iva FROM iva WHERE Fecha_iva=(SELECT max(Fecha_iva) FROM iva)";
            
                Statement st1=cn.createStatement();
                ResultSet rs1=st1.executeQuery(select1);
            
                while(rs1.next()){
                    coniva=rs1.getString("Porc_iva");
                }
            
                if(!coniva.equals("")){//Si existe IVA
                    float subtotal=0, iva=0, total=0;
                    
                    iva=Float.parseFloat(coniva)/100;
                    String Num_ped=tbpedidos.getValueAt(fila, 0).toString();
                    
                    String select2="SELECT Num_ped, Precio_cant_ped FROM pedido_producto " +
                                   "WHERE Num_ped="+Num_ped+"";
                    
                    try{//Se calcula el subtotal, iva y total                    
                        Statement st2=cn.createStatement();
                        ResultSet rs2=st2.executeQuery(select2);
            
                        while(rs2.next()){
                            subtotal = subtotal + rs2.getFloat("Precio_cant_ped");
                        }
                        
                        if(iva==0){//Si el IVA es 0
                            total=subtotal;
                        }else{//Si es mayor que 0
                            total=subtotal+(subtotal*iva);
                        }
                        
                        try{//Se insertan los datos en Factura
                            String insert="INSERT INTO factura(Num_fac, Subtotal_fac, Porc_iva, Total_fac, Fecha_fac, Hora_fac) VALUES (?, ?, ?, ?, curdate(), curtime())";                            
                    
                            PreparedStatement pst=cn.prepareStatement(insert);
                            
                            pst.setString(1, nueva_fact);
                            pst.setFloat(2, subtotal);
                            pst.setInt(3, Integer.parseInt(coniva));
                            pst.setFloat(4, total);
                            
                            int n=pst.executeUpdate();
                            
                            if(n>0){//Si se realizo la factura
                                String insert2="UPDATE pedido SET Num_fac='"+nueva_fact+"' WHERE Num_ped="+Num_ped+"";
                                
                                try{//Se inserta la factura en la tabla PEDIDO
                                    PreparedStatement pst1=cn.prepareStatement(insert2); 
                                    int n1=pst1.executeUpdate();
                                    
                                    if(n1>0){//Si se facturo correctamente, se descuenta al stock
                                        
                                        try{
                                            String select3="SELECT * FROM pedido_producto WHERE Num_ped="+Num_ped+"";
                                            
                                            Statement st3=cn.createStatement();
                                            ResultSet rs3=st3.executeQuery(select3);
                                            
                                            while(rs3.next()){
                                                String cod_prod="", cant_prod_ped="", precio_cant_ped="";
                                                
                                                cod_prod=rs3.getString("Cod_prod");
                                                cant_prod_ped=rs3.getString("Cant_prod_ped");
                                                precio_cant_ped=rs3.getString("precio_cant_ped");
                                                
                                                try{
                                                    String insert3="INSERT INTO factura_producto VALUES(?,?,?,?)";
                                                    
                                                    PreparedStatement pst3=cn.prepareStatement(insert3);
                                                    pst3.setString(1, nueva_fact);
                                                    pst3.setString(2, cod_prod);
                                                    pst3.setString(3, cant_prod_ped);
                                                    pst3.setString(4, precio_cant_ped);
                                                    int n2=pst3.executeUpdate();
                                                    
                                                    if(n2>0){
                                                        Facturacion.descontarstock(cod_prod, cant_prod_ped);
                                                    }else{
                                                        JOptionPane.showMessageDialog(null, "Parece que hubo un problema al descontar los productos en el pedido N° "+Num_ped);
                                                    }                                                    
                                                }catch(SQLException ex){
                                                    System.out.println(ex);
                                                    JOptionPane.showMessageDialog(null, "Parece que hubo un problema al guardar los productos en el pedido N° "+Num_ped);
                                                }                                       
                                            }
                                            System.out.println("--------------------------------");
                                            
                                        }catch(SQLException ex){
                                            System.out.println("");
                                            System.out.println(ex);
                                            JOptionPane.showMessageDialog(null, "Parece que hubo un problema al consultar los productos en el pedido N° "+Num_ped);
                                        }
                                        
                                        if(fila==ult_fila){//Si se facturo todo con exito                                    
                                            JOptionPane.showMessageDialog(null, "Se han faturado exitosamente los pedidos.");
                                            JOptionPane.showMessageDialog(null, "Para imprimir las facturas, vaya a Consultas > Facturas de Cliente (Naturales/Juridicos).");
                                        }
                                    }                           
                                }catch(SQLException ex){
                                    System.out.println("No se lleno la tabla FACTURA_USUARIO ("+nueva_fact+", "+Num_ped+")");
                                    System.out.println(ex);
                                    JOptionPane.showMessageDialog(null,  "No se pudo facturar el Pedido N° "+Num_ped+" para el usuario escogido.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                
                
                        }catch(SQLException ex){
                            System.out.println(ex);
                            JOptionPane.showMessageDialog(null, "No se pudo facturar el Pedido N° "+Num_ped+".", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                
                    }catch(SQLException ex){
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Operacion cancelada: No se pudo calcular el subtotal.");
                    }
                
                
                }else{//Si no existe IVA                
                     JOptionPane.showMessageDialog(null, "Operacion cancelada: IVA no definido.");
                }
            
            }catch(SQLException ex){
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, "Operacion cancelada: Error al cansultar IVA.");
            }
        }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "Error al generar N° de pedido.");
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        mnenviarped = new javax.swing.JMenuItem();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbpedidos = new javax.swing.JTable();
        txtped = new javax.swing.JTextField();
        btnmostrar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnfacturar = new javax.swing.JButton();

        mnenviarped.setText("Enviar a Factura");
        mnenviarped.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnenviarpedActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mnenviarped);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("PEDIDOS");
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        tbpedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° Pedido", "Cédula", "Nombre", "Apellido"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbpedidos.setComponentPopupMenu(jPopupMenu1);
        jScrollPane2.setViewportView(tbpedidos);

        txtped.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtpedKeyReleased(evt);
            }
        });

        btnmostrar.setText("Mostrar todo");
        btnmostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmostrarActionPerformed(evt);
            }
        });

        jLabel1.setText("Buscar por N° de pedido:");

        btnfacturar.setText("Facturar todos");
        btnfacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnfacturarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtped, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnmostrar)
                        .addGap(0, 53, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnfacturar)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(btnmostrar)
                    .addComponent(txtped, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnfacturar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnmostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmostrarActionPerformed
// TODO add your handling code here:
    cargarPedidos("");
    txtped.setText("");
}//GEN-LAST:event_btnmostrarActionPerformed

private void txtpedKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtpedKeyReleased
// TODO add your handling code here:
    cargarPedidos(txtped.getText());
}//GEN-LAST:event_txtpedKeyReleased

    private void mnenviarpedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnenviarpedActionPerformed
        // TODO add your handling code here:
            int fila = tbpedidos.getSelectedRow();

            if (fila<0) {
                JOptionPane.showMessageDialog(null, "No  ha seleccionado ningun registro");
            } else {
                String num_ped=tbpedidos.getValueAt(fila, 0).toString();
                
                DefaultTableModel tablafact = (DefaultTableModel) Facturacion.tbfact.getModel();
                String[] dato = new String[5];
            
                String selectProd="SELECT b.Cod_prod AS Cod_prod, Nomb_prod, Precio_prod, Cant_prod_ped, Precio_cant_ped FROM producto a "
                                + "JOIN pedido_producto b ON a.Cod_prod=b.Cod_prod "
                                + "WHERE Num_ped="+num_ped+"";
                
                try{
                    Statement st=cn.createStatement();
                    ResultSet rs=st.executeQuery(selectProd);
                    
                    while(rs.next()){
                        dato[0]=rs.getString("Cod_prod");
                        dato[1]=rs.getString("Nomb_prod");
                        dato[2]=rs.getString("Precio_prod");
                        dato[3]=rs.getString("Cant_prod_ped");
                        dato[4]=rs.getString("Precio_cant_ped");
                        
                        tablafact.addRow(dato);
                    }
                    
                    Facturacion.tbfact.setModel(tablafact);
                    Facturacion.calcularPedido();
                    
                    switch(tipo){
                        case "Natural":{
                                try {   
                                    String tipoci="",ced="",nom="",ape="",tlf="",dir="";
                                    Font fuente=new Font("Tahoma", Font.PLAIN, 11);
                                
                                    tipoci = Clientes.tbclientes.getValueAt(fila_selec, 0).toString();
                                    ced = Clientes.tbclientes.getValueAt(fila_selec, 1).toString();
                                    nom = Clientes.tbclientes.getValueAt(fila_selec, 2).toString();
                                    ape = Clientes.tbclientes.getValueAt(fila_selec, 3).toString();
                                    tlf = Clientes.tbclientes.getValueAt(fila_selec, 5).toString();
                                    dir = Clientes.tbclientes.getValueAt(fila_selec, 6).toString();
         
                                    Facturacion.txtced.setText(ced);
                                    Facturacion.txtnomape.setText(nom+" "+ape);
                                    Facturacion.txtrif.setText("(Vacio)");
                                    Facturacion.txtrif.setFont(fuente);
                                    
                                    Facturacion.txtempresa.setText("(Vacio)");
                                    
                                    if(dir.equals("")){
                                        Facturacion.txtdir.setText("(Vacio)");
                                    }else{
                                        Facturacion.txtdir.setText(dir);
                                    }
                                    
                                    if(tlf.equals("")){
                                        Facturacion.txttlf.setText("(Vacio)");
                                    }else{
                                        Facturacion.txttlf.setText(tlf);
                                    }
                                    
                                    Facturacion.txtpedi.setText(num_ped);
                                    this.dispose();
         
                                } catch (Exception e) {
                                    JOptionPane.showMessageDialog(null, "Error, no se pudo enviar los datos.", "ERROR",JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                        }                       
                                
                        case "Juridico":{               
                                try {   
                                    String rif="",empresa="";
                                    String juridico="SELECT * FROM juridico "                                             
                                                 + "WHERE Ci_clie="+cedula+"";
                        
                                    Statement st2=cn.createStatement();
                                    ResultSet rs2=st2.executeQuery(juridico);
                        
                                    while(rs2.next()){
                                        rif=rs2.getString("Rif_juri");
                                        empresa=rs2.getString("Empresa_juri");
                                    }
                                
                                    try {   
                                        String tipoci="",ced="",nom="",ape="",tlf="",dir="";
                                        Font fuente=new Font("Tahoma",Font.BOLD, 11);
                                
                                        //tipoci = Clientes.tbclientes.getValueAt(fila, 0).toString();
                                        ced = Clientes.tbclientes.getValueAt(fila_selec, 1).toString();
                                        nom = Clientes.tbclientes.getValueAt(fila_selec, 2).toString();
                                        ape = Clientes.tbclientes.getValueAt(fila_selec, 3).toString();
                                        tlf = Clientes.tbclientes.getValueAt(fila_selec, 5).toString();
                                        dir = Clientes.tbclientes.getValueAt(fila_selec, 6).toString();
         
                                        Facturacion.txtced.setText(ced);
                                        Facturacion.txtnomape.setText(nom+" "+ape);
                                        Facturacion.txtrif.setText(rif);
                                        Facturacion.txtrif.setFont(fuente);
                                        
                                        Facturacion.txtempresa.setText(empresa);
                                        
                                        if(dir.equals("")){
                                            Facturacion.txtdir.setText("(Vacio)");
                                        }else{
                                            Facturacion.txtdir.setText(dir);
                                        }
                                    
                                        if(tlf.equals("")){
                                            Facturacion.txttlf.setText("(Vacio)");
                                        }else{
                                            Facturacion.txttlf.setText(tlf);
                                        }
                                        
                                        Facturacion.txtpedi.setText(num_ped);
                                        this.dispose();
         
                                    } catch (Exception ex) {
                                        JOptionPane.showMessageDialog(null, "Error, no se pudo enviar los datos.", "ERROR",JOptionPane.ERROR_MESSAGE);
                                    }     
                                } catch (SQLException ex) {
                                    JOptionPane.showMessageDialog(null, "Hubo un error en la consulta: Juridico.", "ERROR", JOptionPane.ERROR_MESSAGE);
                                }
                                break;
                        }
                    }//Fin SWITCH
                    
                }catch(SQLException ex){
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Operacion cancelada: No se pudo encontrar los productos del pedido.");
                }         
            }
    }//GEN-LAST:event_mnenviarpedActionPerformed

    private void btnfacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnfacturarActionPerformed
        // TODO add your handling code here:
        int cant_ped=tbpedidos.getRowCount();
        
        if(cant_ped>0){//Si existen filas
            for(int i=0;i<cant_ped;i++){
                facturarPedidos(i, cant_ped);
            }        
            cargarPedidos("");  
            Facturacion.limpiar();
            Clientes.mostrarclientes("");
        }else{
            JOptionPane.showMessageDialog(null, "No existen pedidos por facturar.");
        }
        
    }//GEN-LAST:event_btnfacturarActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        Facturacion.pedidos=false;
    }//GEN-LAST:event_formWindowClosed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnfacturar;
    private javax.swing.JButton btnmostrar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem mnenviarped;
    public static javax.swing.JTable tbpedidos;
    private javax.swing.JTextField txtped;
    // End of variables declaration//GEN-END:variables
    public static Conectar cc = new Conectar();
    public static Connection cn = cc.conexion();
}
