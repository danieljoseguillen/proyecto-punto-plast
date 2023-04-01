/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Factura.java
 *
 *
 */
package PuntoPlast;

import claseconexion.Conectar;
import static com.sun.org.apache.xalan.internal.lib.ExsltDynamic.map;
import java.awt.Color;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Administrador
 */
public class Facturacion extends javax.swing.JFrame {
    static int iva;
    static boolean clientes=false;
    static boolean pedidos=false;
    static boolean productos=false;

    /** Creates new form facture */
    public Facturacion() {
        initComponents();
        txtfec.setDisabledTextColor(Color.blue);
        txtfec.setText(fechaactual());
        this.setLocationRelativeTo(null);
        numeros();
        
        String coniva="SELECT Porc_iva, Fecha_iva FROM IVA WHERE Fecha_iva=(SELECT MAX(Fecha_iva) FROM IVA)";//Busca el IVA actual
        
        try {
            Statement st= cn.createStatement();
            ResultSet rs= st.executeQuery(coniva);
            
            while(rs.next()){
                iva= rs.getInt("Porc_iva");
            }
        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Advertencia: No se pudo definir IVA.", "Error", JOptionPane.ERROR_MESSAGE);
            iva=0;
        }
  }
    
    public static void calcular(){              
        String pre;
        String can;

        float total=0;
        float subtotal=0;
        float precio;
        int cantidad;
        float imp=0;
        float iva_calculado=(float)iva/100;
        
        
        for(int i=0;i<tbfact.getRowCount();i++){
            pre=Facturacion.tbfact.getValueAt(i, 2).toString();
            can=Facturacion.tbfact.getValueAt(i, 3).toString();
            
            precio=Float.parseFloat(pre);
            cantidad=Integer.parseInt(can);            
            
            imp=precio*cantidad;
            subtotal=+imp;          
            total=subtotal+(subtotal*iva_calculado);            
            tbfact.setValueAt(imp, i, 4);//Mostrar importe            
        }
        
        //DecimalFormat d=new DecimalFormat(".##");
        
        txtsubtotal.setText(""+subtotal);
        txtiva.setText(iva+"%");
        txttotal.setText(""+total);
    }
    
    public static void calcularPedido(){              
        String pre;
        String can;
        String impor;

        float total=0;
        float subtotal=0;
        float precio;
        int cantidad;
        float imp;
        float iva_calculado=(float)iva/100;        
        
        for(int i=0;i<tbfact.getRowCount();i++){
            pre=Facturacion.tbfact.getValueAt(i, 2).toString();
            can=Facturacion.tbfact.getValueAt(i, 3).toString();
            impor=Facturacion.tbfact.getValueAt(i, 4).toString();
            
            precio=Float.parseFloat(pre);
            cantidad=Integer.parseInt(can);
            imp=Float.parseFloat(impor);            
            
            subtotal=subtotal+imp;          
            total=subtotal+(subtotal*iva_calculado);  
        }
        
        txtsubtotal.setText(""+subtotal);
        txtiva.setText(iva+"%");
        txttotal.setText(""+total);
    }
        
    public static void descontarstock(String codi, String can){
        int des = Integer.parseInt(can);
        String cap="", prod="";
        int desfinal;
        String consul="SELECT * FROM producto WHERE Cod_prod='"+codi+"'";
        
        try {
            Statement st= cn.createStatement();
            ResultSet rs= st.executeQuery(consul);
            
            while(rs.next()){
                cap= rs.getString("Stock_prod");
                prod=rs.getString("Nomb_prod");
            }

            if(!cap.equals("")){
                desfinal=Integer.parseInt(cap)-des;
                String modi="UPDATE producto SET Stock_prod='"+desfinal+"' WHERE Cod_prod = '"+codi+"'";
        
                try {
                    PreparedStatement pst = cn.prepareStatement(modi);
                    pst.executeUpdate();
                    
                    System.out.println("Se descontó "+can+" "+prod);
                    
                    } catch (SQLException ex) {
                         System.out.println("No se puedo descontar el producto "+prod+".");
                         System.out.println(ex);
                         JOptionPane.showMessageDialog(null, "Error: No se pudo descontar un producto al stock.");
                    }
            }            
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(null, "Hubo un error al consultar un producto.");
        }              
    }
    
    public static void desbloquear(){
       btnclientes.setEnabled(true);
       btnproductos.setEnabled(true);
       btnfacturar.setEnabled(true);
    }
        
    public static void numeros(){
        try {
            int j;
            int cont=1;
            String num="";
            String c="";
            String SQL="SELECT max(Num_fac) as Num_fac FROM factura";
            
            Statement st = cn.createStatement();
            ResultSet rs=st.executeQuery(SQL);
            
            if(rs.next()){              
                c=rs.getString("Num_fac");
            }       
           
            if(c.equals("")){
                txtfac.setText("00000001");
            }else{
                j=Integer.parseInt(c);
                GenerarNumero gen= new GenerarNumero();
                gen.generar(j);
                txtfac.setText(gen.serie());     
            }     
        } catch (SQLException ex) {
           Logger.getLogger(Facturacion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void insertarProducto(){
        int filas_total=tbfact.getRowCount();
        int ult_fila=filas_total-1;
        
        String insert2="INSERT INTO factura_producto VALUES(?, ?, ?, ?)";                        
                        
                for(int i=0;i<filas_total;i++){
                    try{
                        String fact= txtfac.getText();
                        String cod_prod= tbfact.getValueAt(i, 0).toString();                                
                        String cant_prod= tbfact.getValueAt(i, 3).toString();
                        String imp= tbfact.getValueAt(i, 4).toString();                                
                                
                        PreparedStatement pst2=cn.prepareStatement(insert2);
                        pst2.setString(1, fact);
                        pst2.setLong(2, Long.parseLong(cod_prod));
                        pst2.setInt(3, Integer.parseInt(cant_prod));
                        pst2.setFloat(4, Float.parseFloat(imp));
                                
                        int n2=pst2.executeUpdate();
                        
                        descontarstock(cod_prod, cant_prod);
               
                        if((n2>0)&&(i==ult_fila)){//Si todo se proceso correctamente
                            System.out.println("----------------------------------------");
                            JOptionPane.showMessageDialog(null, "Se ha generado la factura correctamente. Para consultar facturas, vaya a Consultas > Facturas de Clientes.");
                            
                            if(clientes){
                                Clientes.mostrarclientes("");
                            }                        
                            if(pedidos){
                                Pedidos.cargarPedidos("");
                            }                        
                            if(clientes){
                                Productos.cargarlistaproductos("");
                            }
                        }                                       
                    }catch(SQLException ex){
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Error: No se guardaron los productos.");
                    }
                }    
    }
    
    public static void insertarProductoPedido(){
        int filas_total=tbfact.getRowCount();
        int ult_fila=filas_total-1;
        
        String insert2="INSERT INTO factura_producto VALUES(?, ?, ?, ?)";                        
                        
                for(int i=0;i<filas_total;i++){
                    try{
                        String fact= txtfac.getText();
                        String cod_prod= tbfact.getValueAt(i, 0).toString();                                
                        String cant_prod= tbfact.getValueAt(i, 3).toString();
                        String imp= tbfact.getValueAt(i, 4).toString();                                
                                
                        PreparedStatement pst2=cn.prepareStatement(insert2);
                        pst2.setString(1, fact);
                        pst2.setLong(2, Long.parseLong(cod_prod));
                        pst2.setInt(3, Integer.parseInt(cant_prod));
                        pst2.setFloat(4, Float.parseFloat(imp));
                                
                        int n2=pst2.executeUpdate();
               
                        if((n2>0)&&(i==ult_fila)){//Si todo se proceso correctamente
                            JOptionPane.showMessageDialog(null, "Se ha generado la factura correctamente. Para consultar facturas, vaya a Consultas > Facturas de Clientes");
                            
                            if(clientes){
                            Clientes.mostrarclientes("");
                            }                        
                            if(pedidos){
                                Pedidos.cargarPedidos("");
                            }                        
                            if(clientes){
                                Productos.cargarlistaproductos("");
                            }
                        }                                       
                    }catch(SQLException ex){
                        System.out.println(ex);
                        JOptionPane.showMessageDialog(null, "Error: No se guardaron los productos.");
                    }
                }    
    }
  
    void facturar(){       
       
       String num_fac=txtfac.getText();
       String ced=txtced.getText();      
       String subtotal=txtsubtotal.getText();
       int coniva=iva;
       String total=txttotal.getText();
       
       try{
           String insert="INSERT INTO factura VALUES (?, ?, ?, ?, ?, curdate(), curtime(), ?)";
                                    
           PreparedStatement pst=cn.prepareStatement(insert);           
           pst.setString(1, num_fac);
           pst.setString(2, ced);
           pst.setFloat(3, Float.parseFloat(subtotal));
           pst.setInt(4, iva);
           pst.setFloat(5, Float.parseFloat(total));
           pst.setString(6, "Procesado");
                                        
           int n=pst.executeUpdate();
                                        
           if(n>0){//Si se inserta la factura, se insertan los productos y se descuenta alstock                                           
                 insertarProducto();
                 limpiar();
           }else{
              JOptionPane.showMessageDialog(this, "Error: No se genero copiaron los productos a la factura."); 
           }                                                
        }catch(SQLException ex){
            System.out.println(ex);
            JOptionPane.showMessageDialog(this, "Error: No se genero la factura.");
        }      
    }
    
    
    void facturarPedido(){                
        String fact=txtfac.getText();
        String ped=txtpedi.getText();
        
        float subtotal=Float.parseFloat(txtsubtotal.getText());
        float total=Float.parseFloat(txttotal.getText());
        
        try{//Inserta el numero de la factura
            String insert="INSERT INTO factura(Num_fac, Subtotal_fac, Porc_iva, Total_fac, Fecha_fac, Hora_fac) VALUES (?, ?, ?, ?, curdate(), curtime())";
            
            PreparedStatement pst1 = cn.prepareStatement(insert);
            pst1.setString(1, fact);
            pst1.setFloat(2, subtotal);
            pst1.setInt(3, iva);
            pst1.setFloat(4, total);
            
            int n1=pst1.executeUpdate();
            
            if(n1>0){//Se actualiza el pedido como facturado
                try{
                    String insert2="UPDATE pedido SET Num_fac='"+fact+"' WHERE Num_ped="+ped+"";
                    
                    PreparedStatement pst2=cn.prepareStatement(insert2);
                    
                    int n2=pst2.executeUpdate();
                    
                    if(n2>0){
                        insertarProductoPedido();
                        limpiar();   
                    }         
                }catch(SQLException ex){
                    System.out.println("Error: "+ex);
                    JOptionPane.showMessageDialog(null, "Error, no se pudo facturar el pedido.");
                }         
            }
        } catch (SQLException ex) {
            System.out.println(ex);
            JOptionPane.showMessageDialog(this, "Error: hubo un problema al generar la factura.");
        }       
    }
 
    public static void limpiar(){
        
        txtnomape.setText("");
        txtced.setText("");        
        txtrif.setText("");
        txtempresa.setText("");
        txttlf.setText("");
        txtdir.setText("");        
        txtiva.setText("");
        txtsubtotal.setText("");
        txttotal.setText("");
        txtfac.setText("");
        txtfec.setText("");
        txtpedi.setText("");
        desbloquear();
        
        DefaultTableModel modelo = (DefaultTableModel) tbfact.getModel();
        int a =tbfact.getRowCount()-1;
        int i;
        
        for(i=a;i>=0;i--){
            modelo.removeRow(i);
        }
      
        numeros();
        txtfec.setText(fechaactual());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        mneliminar = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtnomape = new javax.swing.JTextField();
        btnclientes = new javax.swing.JButton();
        txtced = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtdir = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtempresa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txttlf = new javax.swing.JTextField();
        txtrif = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        txtsubtotal = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        txtiva = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txttotal = new javax.swing.JTextField();
        tbdetalle = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbfact = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        btnfacturar = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtfac = new javax.swing.JTextField();
        txtfec = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtpedi = new javax.swing.JTextField();
        btnproductos = new javax.swing.JButton();
        btnlimpiar = new javax.swing.JButton();
        btnsalir = new javax.swing.JButton();

        mneliminar.setText("Eliminar");
        mneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mneliminarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mneliminar);

        setTitle("FACTURA");
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Tempus Sans ITC", 0, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 255));
        jLabel1.setText("PUNTO PLAST.CA");

        jLabel3.setText("DE: VALENTINA ESCUDERO GUTIERREZ");

        jLabel4.setText("C.C Paseo Paraparal, Los Guayos");

        jLabel5.setText("Telf:(0241)-4175000");

        jLabel16.setText("LOS GUAYOS - CARABOBO");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(173, 173, 173)
                        .addComponent(jLabel16))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addContainerGap(81, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(177, 177, 177))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(121, 121, 121))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(jLabel16)
                .addGap(14, 14, 14))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel8.setText("Señor(a):");

        txtnomape.setEditable(false);
        txtnomape.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtnomape.setForeground(new java.awt.Color(0, 51, 204));
        txtnomape.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnomapeActionPerformed(evt);
            }
        });

        btnclientes.setText("Buscar cliente");
        btnclientes.setMaximumSize(new java.awt.Dimension(111, 23));
        btnclientes.setMinimumSize(new java.awt.Dimension(111, 23));
        btnclientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnclientesActionPerformed(evt);
            }
        });

        txtced.setEditable(false);
        txtced.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jLabel11.setText("Cedula:");

        txtdir.setEditable(false);

        jLabel10.setText("Direccion:");

        jLabel2.setText("Rif:");

        jLabel7.setText("Empresa:");

        txtempresa.setEditable(false);

        jLabel9.setText("Telefono:");

        txttlf.setEditable(false);
        txttlf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttlfActionPerformed(evt);
            }
        });

        txtrif.setEditable(false);
        txtrif.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtrifActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtced, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtdir, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                        .addComponent(txtempresa, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txttlf)
                        .addComponent(txtrif, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtnomape, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnclientes, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtnomape, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(btnclientes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtced, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtrif, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtempresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtdir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txttlf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel18.setText("SubTotal:");

        txtsubtotal.setEditable(false);

        jLabel19.setText("IVA:");

        txtiva.setEditable(false);

        jLabel20.setText("Total:");

        txttotal.setEditable(false);

        tbdetalle.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));

        tbfact.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Precio", "Cantidad", "Importe"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbfact.setComponentPopupMenu(jPopupMenu1);
        jScrollPane2.setViewportView(tbfact);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtiva, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 11, Short.MAX_VALUE)
                    .addComponent(tbdetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 11, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(txtsubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtiva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txttotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel4Layout.createSequentialGroup()
                    .addGap(0, 121, Short.MAX_VALUE)
                    .addComponent(tbdetalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 121, Short.MAX_VALUE)))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnfacturar.setText("Facturar");
        btnfacturar.setMaximumSize(new java.awt.Dimension(111, 23));
        btnfacturar.setMinimumSize(new java.awt.Dimension(111, 23));
        btnfacturar.setPreferredSize(new java.awt.Dimension(111, 23));
        btnfacturar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnfacturarActionPerformed(evt);
            }
        });

        jLabel6.setText("RIF: J-40200312");

        jLabel15.setText("FACTURA DE VENTA");

        jLabel17.setText("Factura N°:");

        jLabel12.setText("Fecha:");

        txtfac.setEditable(false);
        txtfac.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        txtfac.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtfacActionPerformed(evt);
            }
        });

        txtfec.setEditable(false);

        jLabel13.setText("Pedido N°:");

        txtpedi.setEditable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtfac, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtfec, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(txtpedi))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(40, 40, 40))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(btnfacturar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(31, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(43, 43, 43))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtfac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtfec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtpedi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnfacturar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnproductos.setText("Buscar Producto");
        btnproductos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnproductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnproductosActionPerformed(evt);
            }
        });

        btnlimpiar.setText("Limpiar");
        btnlimpiar.setMaximumSize(new java.awt.Dimension(150, 23));
        btnlimpiar.setMinimumSize(new java.awt.Dimension(150, 23));
        btnlimpiar.setPreferredSize(new java.awt.Dimension(150, 23));
        btnlimpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlimpiarActionPerformed(evt);
            }
        });

        btnsalir.setText("Salir");
        btnsalir.setMinimumSize(new java.awt.Dimension(111, 23));
        btnsalir.setPreferredSize(new java.awt.Dimension(111, 23));
        btnsalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnproductos)
                            .addComponent(btnsalir, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(110, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(btnproductos)
                        .addGap(18, 18, 18)
                        .addComponent(btnlimpiar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnsalir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(56, 56, 56))))
        );

        setBounds(0, 0, 732, 653);
    }// </editor-fold>//GEN-END:initComponents
    public static String fechaactual(){
        Date fecha= new Date();
        SimpleDateFormat formatofecha= new SimpleDateFormat("dd/MM/YYYY");
        return formatofecha.format(fecha);
    }
private void btnclientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnclientesActionPerformed
// TODO add your handling code here:
       Clientes clie=new Clientes();
       clie.toFront();
       clie.setVisible(true);       
}//GEN-LAST:event_btnclientesActionPerformed

private void txtnomapeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnomapeActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_txtnomapeActionPerformed

private void btnfacturarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnfacturarActionPerformed
// TODO add your handling code here:
    if((txtced.getText().equals("")) || (txtsubtotal.getText().equals(""))){//Si estan los campos vacios
        JOptionPane.showMessageDialog(this, "Ingrese un cliente o productos para facturar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
    }else{//Si los campos tienen informacion
        if(txtpedi.getText().equals("(Vacio)")){//Si no es un pedido        
          facturar();     
          limpiar();      
        }else{//Si es un pedido      
          facturarPedido();  
          limpiar();
          
          if(pedidos){
            Pedidos.cargarPedidos("");
          }
    }  
  }
}//GEN-LAST:event_btnfacturarActionPerformed

    private void mneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mneliminarActionPerformed
        // TODO add your handling code here:      
            DefaultTableModel tabla = (DefaultTableModel) tbfact.getModel();
            int i= tbfact.getSelectedRow();
            tabla.removeRow(i);
            tbfact.setModel(tabla);
            calcular();        
    }//GEN-LAST:event_mneliminarActionPerformed

    private void txtrifActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtrifActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtrifActionPerformed

    private void txttlfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttlfActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttlfActionPerformed

    private void txtfacActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtfacActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtfacActionPerformed

    private void btnproductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnproductosActionPerformed
        // TODO add your handling code here:
        Productos prod=new Productos();
        prod.setVisible(true);
        prod.toFront();
    }//GEN-LAST:event_btnproductosActionPerformed

    private void btnlimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlimpiarActionPerformed
        // TODO add your handling code here:
        limpiar();
    }//GEN-LAST:event_btnlimpiarActionPerformed

    private void btnsalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalirActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnsalirActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JButton btnclientes;
    public static javax.swing.JButton btnfacturar;
    private javax.swing.JButton btnlimpiar;
    public static javax.swing.JButton btnproductos;
    private javax.swing.JButton btnsalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JMenuItem mneliminar;
    private javax.swing.JTable tbdetalle;
    public static javax.swing.JTable tbfact;
    public static javax.swing.JTextField txtced;
    public static javax.swing.JTextField txtdir;
    public static javax.swing.JTextField txtempresa;
    public static javax.swing.JTextField txtfac;
    public static javax.swing.JTextField txtfec;
    public static javax.swing.JTextField txtiva;
    public static javax.swing.JTextField txtnomape;
    public static javax.swing.JTextField txtpedi;
    public static javax.swing.JTextField txtrif;
    public static javax.swing.JTextField txtsubtotal;
    public static javax.swing.JTextField txttlf;
    public static javax.swing.JTextField txttotal;
    // End of variables declaration//GEN-END:variables
   public static Conectar cc=new Conectar();
   public static Connection cn= cc.conexion();
}
