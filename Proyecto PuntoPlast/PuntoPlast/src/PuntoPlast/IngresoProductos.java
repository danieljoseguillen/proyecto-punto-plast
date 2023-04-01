/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IngresoProductos.java
 *
 *
 */
package PuntoPlast;

import claseconexion.Conectar;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrador
 */
public class IngresoProductos extends javax.swing.JFrame {
    DefaultTableModel model;
    final String msj="Codigo o Descripcion...";
    
    public IngresoProductos() {
        initComponents();
        this.setLocationRelativeTo(null);
        iniciar();
        PlaceHolder();
        cargar("");
    }
    
    void PlaceHolder(){
        ph.Mensaje(txtbuscar, msj, 0);
    }
    
     void iniciar(){
        txtcod.setEnabled(true);
        btnguardar.setEnabled(true);
        btnactualizar.setEnabled(false);
    }
     
    void limpiar(){
        txtcod.setText("");
        txtdes.setText("");
        txttipo.setText("");
        txtprecio.setText("");
        txtstock.setText("");    
    }
    
    void iniciarActualizacion(){   
        btnguardar.setEnabled(false);
        btnactualizar.setEnabled(true);
        txtcod.setEnabled(false);
    }
    void cargar(String valor) {
        //carga todos los productos o los selecionados
        
        DefaultTableModel model = (DefaultTableModel) tbproductos.getModel();
        int a =tbproductos.getRowCount()-1;
        int i;
        for(i=a;i>=0;i--){
            model.removeRow(i);
        }
        
        String cons="SELECT * FROM producto WHERE (Cod_prod LIKE '"+valor+"%') OR (Nomb_prod LIKE '"+valor+"%')";
        String [] registros= new String[5];
        
        try{            
            Statement st= cn.createStatement();
            ResultSet rs = st.executeQuery(cons);
            
            while(rs.next()){
                registros[0]=rs.getString("Cod_prod");
                registros[1]=rs.getString("Nomb_prod");
                registros[2]=rs.getString("Tipo_prod");
                registros[3]=rs.getString("Precio_prod");
                registros[4]=rs.getString("Stock_prod");
                
                model.addRow(registros);      
            }
            
                tbproductos.setModel(model);
                tbproductos.getColumnModel().getColumn(0).setPreferredWidth(150);
                tbproductos.getColumnModel().getColumn(1).setPreferredWidth(250);
                tbproductos.getColumnModel().getColumn(2).setPreferredWidth(100);
                tbproductos.getColumnModel().getColumn(3).setPreferredWidth(100);
                tbproductos.getColumnModel().getColumn(4).setPreferredWidth(100);
                
            }catch(SQLException ex){
                System.out.println(ex);
                JOptionPane.showMessageDialog(null, "Hubo un error al consultar el producto.");
            }
     
    }
        void BuscarProducto(String cod) {
        //busca productos de la base de datos
            try{
                String codi="",desc="",tipo="",prec="",stock="";
                String cons="SELECT * FROM producto WHERE Cod_prod='"+cod+"'";
                
                Statement st = cn.createStatement();
                ResultSet rs = st.executeQuery(cons);
            
                while(rs.next()){
                    codi=rs.getString("Cod_prod");
                    desc=rs.getString("Nomb_prod");
                    tipo=rs.getString("Tipo_prod");
                    prec=rs.getString("Precio_prod");
                    stock=rs.getString("Stock_prod");                
                }
                
                txtcod.setText(codi);
                txtdes.setText(desc);
                txttipo.setText(tipo);
                txtprecio.setText(prec);
                txtstock.setText(stock);            
            }catch(SQLException ex){
                JOptionPane.showMessageDialog(null, "Parece que hubo un error al buscar el producto.");
                System.out.println(ex.getMessage());
            }
     
    }
    
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        mnactualizar = new javax.swing.JMenuItem();
        mneliminar = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtcod = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtdes = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txttipo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtprecio = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtstock = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        btnguardar = new javax.swing.JButton();
        btnactualizar = new javax.swing.JButton();
        btncancelar = new javax.swing.JButton();
        btnsalir = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbproductos = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        txtbuscar = new javax.swing.JTextField();
        btnmostrar = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        mnactualizar.setText("Modificar");
        mnactualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnactualizarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mnactualizar);

        mneliminar.setText("Eliminar");
        mneliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mneliminarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(mneliminar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("REGISTRO DE PRODUCTOS");
        setLocationByPlatform(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Detalle de Producto"));

        jLabel1.setText("Codigo:");

        txtcod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtcodActionPerformed(evt);
            }
        });
        txtcod.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtcodKeyTyped(evt);
            }
        });

        jLabel2.setText("Descripcion:");

        txtdes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtdesActionPerformed(evt);
            }
        });
        txtdes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtdesKeyTyped(evt);
            }
        });

        jLabel3.setText("Tipo:");

        txttipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttipoActionPerformed(evt);
            }
        });
        txttipo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txttipoKeyTyped(evt);
            }
        });

        jLabel5.setText("Precio:");

        txtprecio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtprecioActionPerformed(evt);
            }
        });
        txtprecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtprecioKeyTyped(evt);
            }
        });

        jLabel6.setText("Stock:");

        txtstock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtstockActionPerformed(evt);
            }
        });
        txtstock.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtstockKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtcod, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(txtdes)
                    .addComponent(txttipo)
                    .addComponent(txtprecio)
                    .addComponent(txtstock))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtcod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtdes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txttipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtstock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(28, 28, 28))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnguardar.setText("Guardar");
        btnguardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnguardarActionPerformed(evt);
            }
        });

        btnactualizar.setText("Actualizar");
        btnactualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnactualizarActionPerformed(evt);
            }
        });

        btncancelar.setText("Cancelar");
        btncancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncancelarActionPerformed(evt);
            }
        });

        btnsalir.setText("Salir");
        btnsalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalirActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnactualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnguardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btncancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnsalir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnguardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnactualizar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btncancelar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnsalir)
                .addContainerGap())
        );

        tbproductos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción", "Tipo", "Precio", "Stock"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbproductos.setComponentPopupMenu(jPopupMenu1);
        tbproductos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane2.setViewportView(tbproductos);

        jLabel4.setText("Buscar por:");

        txtbuscar.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtbuscarFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtbuscarFocusLost(evt);
            }
        });
        txtbuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtbuscarActionPerformed(evt);
            }
        });
        txtbuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbuscarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtbuscarKeyTyped(evt);
            }
        });

        btnmostrar.setText("Mostrar Todo");
        btnmostrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnmostrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(25, 25, 25)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(jLabel4)
                        .addGap(8, 8, 8)
                        .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnmostrar)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtbuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnmostrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void btnsalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalirActionPerformed
// TODO add your handling code here:
    //cierra la ventana
    this.dispose();
}//GEN-LAST:event_btnsalirActionPerformed

private void btncancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncancelarActionPerformed
// TODO add your handling code here:
    limpiar();
    iniciar();
}//GEN-LAST:event_btncancelarActionPerformed

private void btnmostrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnmostrarActionPerformed
// TODO add your handling code here:
    //cargar sin valores(muestra todo)
    cargar("");
}//GEN-LAST:event_btnmostrarActionPerformed

private void txtbuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarKeyReleased
// TODO add your handling code here:
    //cargado con valor agregado
    cargar(txtbuscar.getText());
}//GEN-LAST:event_txtbuscarKeyReleased

private void btnguardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnguardarActionPerformed
// TODO add your handling code here:
    //guardado de productos
    
    if((txtcod.getText().trim().length()==0)||(txtdes.getText().trim().length()==0)||(txttipo.getText().trim().length()==0)){
        JOptionPane.showMessageDialog(this,"Por favor, llene al menos el codigo, descripcion y tipo del producto.");
    }else{
        String cod, codbuscado="";
        cod=txtcod.getText();
        String select="SELECT * FROM producto WHERE Cod_prod="+cod+"";
            
        try{            
            Statement st=cn.createStatement();
            ResultSet rs=st.executeQuery(select);
            
            while(rs.next()){
                codbuscado=rs.getString("Cod_prod");
            }
                
            if(!cod.equals(codbuscado)){//Si no existe en la BD el codigo ingresado, lo guarda
                String desc,tipo,prec,stock;
                String sql="";
            
                desc=txtdes.getText();
                tipo=txttipo.getText();
                
                if(txtprecio.getText().length()==0){
                    prec="0";
                }else{
                    prec=txtprecio.getText();
                }
                
                if(txtstock.getText().length()==0){
                    stock="0";
                }else{                    
                    stock=txtstock.getText();
                }
                
                sql="INSERT INTO producto (Cod_prod,Nomb_prod,Tipo_prod,Precio_prod,Stock_prod) VALUES (?,?,?,?,?)";
                    
                try {
                    PreparedStatement pst  = cn.prepareStatement(sql);
                    pst.setString(1, cod);
                    pst.setString(2, desc);
                    pst.setString(3, tipo);
                    pst.setString(4, prec);
                    pst.setString(5, stock);    
                    int n=pst.executeUpdate();
            
                    if(n>0){
                        JOptionPane.showMessageDialog(this, "Registro Guardado con Exito.");
                        limpiar();
                        iniciar();
                    }
            
                    limpiar();
                    cargar("");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al guardar, operacion cancelada.");
                    Logger.getLogger(IngresoProductos.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }else{
                JOptionPane.showMessageDialog(null, "Lo sentimos, este codigo de producto ya existe.");
            }        
        }catch(SQLException ex){
            JOptionPane.showMessageDialog(null, "Error: No se pudo comprobar el codigo del producto.");
        }
    }
}//GEN-LAST:event_btnguardarActionPerformed

private void btnactualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnactualizarActionPerformed
// TODO add your handling code here:
    //actualizacion en base a id
    if((txtdes.getText().trim().length()==0)||(txttipo.getText().trim().length()==0)){
        JOptionPane.showMessageDialog(this,"Por favor, llene al menos el codigo, descripcion y tipo del producto.");
    }else{
        String prec, stock;
        
        if(txtprecio.getText().length()==0){
            prec="0";
        }else{
            prec=txtprecio.getText();
        }
                
        if(txtstock.getText().length()==0){
            stock="0";
        }else{                    
            stock=txtstock.getText();
        }
        
        String sql="UPDATE producto SET Nomb_prod='"+txtdes.getText()+"',Tipo_prod='"+txttipo.getText()+"',Precio_prod="+prec+", Stock_prod="+stock+" WHERE Cod_prod='"+txtcod.getText()+"'"; 
        
        try {
            PreparedStatement pst = cn.prepareStatement(sql);
            int n=pst.executeUpdate();
            
            if(n>0){
                JOptionPane.showMessageDialog(this, "Registro Guardado con Exito");
                limpiar();
                iniciar();
            }
            
            cargar("");
            limpiar();
            iniciar();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar, operacion cancelada");
            Logger.getLogger(IngresoProductos.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}//GEN-LAST:event_btnactualizarActionPerformed

    private void txtbuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtbuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtbuscarActionPerformed

    private void txtstockKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtstockKeyTyped
        // TODO add your handling code here:
        //validacion de numero
        char car = evt.getKeyChar();
        if(txtstock.getText().length()>=9) evt.consume();
        if((car<'0' || car>'9')&&(car!=KeyEvent.VK_BACK_SPACE)){ 
            evt.consume();
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtstockKeyTyped

    private void txtstockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtstockActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtstockActionPerformed

    private void txtprecioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtprecioKeyTyped
        // TODO add your handling code here:
        //validacion de numero
        char car = evt.getKeyChar();
        if((car<'0' || car>'9')&&(car!=KeyEvent.VK_BACK_SPACE)){ 
            evt.consume();
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtprecioKeyTyped

    private void txtprecioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtprecioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtprecioActionPerformed

    private void txttipoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txttipoKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        if((c <'a'||c>'z')&&(c<'A'||c>'Z')&&(c!='ñ')&&(c!=KeyEvent.VK_BACK_SPACE)){ 
            evt.consume();//Validar solo letras
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txttipoKeyTyped

    private void txttipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttipoActionPerformed
        // TODO add your handling code here:
        //mueve el cursor de escritura a ese campo
        txttipo.transferFocus();
    }//GEN-LAST:event_txttipoActionPerformed

    private void txtdesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtdesActionPerformed
        // TODO add your handling code here:
        //mueve el cursor de escritura a ese campo
        txtdes.transferFocus();
    }//GEN-LAST:event_txtdesActionPerformed

    private void txtcodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtcodActionPerformed
        // TODO add your handling code here:
        //mueve el cursor de escritura a ese campo        
        txtcod.transferFocus();
    }//GEN-LAST:event_txtcodActionPerformed

    private void mnactualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnactualizarActionPerformed
        // TODO add your handling code here:
        //captado de valores de la tabla inferior
        try {
            int filaMod=tbproductos.getSelectedRow();
            
            if(filaMod<0){
                JOptionPane.showMessageDialog(this, "Seleccione alguna fila");
            }else{
                limpiar();
                iniciarActualizacion();

                String cod=tbproductos.getValueAt(filaMod, 0).toString();
                BuscarProducto(cod);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_mnactualizarActionPerformed

    private void txtcodKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtcodKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        if(((c<'0' || c>'9')&&(c!=KeyEvent.VK_BACK_SPACE))||(txtcod.getText().length()>=13)){ 
            evt.consume(); //Validar solo numero con 13 caracteres
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtcodKeyTyped

    private void txtdesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtdesKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        if((c <'a'|| c>'z')&&(c<'A'||c>'Z')&&(c!='ñ')&&(c!=KeyEvent.VK_SPACE)&&(c!=KeyEvent.VK_BACK_SPACE)){
            evt.consume();//Validar solo letras
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtdesKeyTyped

    private void txtbuscarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbuscarKeyTyped
        // TODO add your handling code here:
        char c=evt.getKeyChar();
        
        if((c<'a'||c>'z')&&(c<'A'||c>'Z')&&(c<'0'||c>'9')&&(c!=KeyEvent.VK_BACK_SPACE)&&(c!=KeyEvent.VK_SPACE)){
            evt.consume();
            this.getToolkit().beep();
        }
    }//GEN-LAST:event_txtbuscarKeyTyped

    private void txtbuscarFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtbuscarFocusGained
        // TODO add your handling code here:        
        ph.Comparar(txtbuscar);
    }//GEN-LAST:event_txtbuscarFocusGained

    private void txtbuscarFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtbuscarFocusLost
        // TODO add your handling code here:
        if(txtbuscar.getText().trim().length()==0){
            PlaceHolder();
        }
    }//GEN-LAST:event_txtbuscarFocusLost

    private void mneliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mneliminarActionPerformed
        // TODO add your handling code here:
        int fila=tbproductos.getSelectedRow();        
            
        if(fila>=0){
            try{//Se corrobora si el producto ha sido comprado
                String cod;    
                cod=tbproductos.getValueAt(fila, 0).toString();
                String select="SELECT a.Cod_prod AS Cod_prod FROM producto a " +
                              "JOIN pedido_producto b ON a.Cod_prod=b.Cod_prod " +
                              "WHERE a.Cod_prod='"+cod+"' " +
                              "UNION " +
                              "SELECT a.Cod_prod AS Cod_prod FROM producto a " +
                              "JOIN factura_producto b ON a.Cod_prod=b.Cod_prod " +
                              "WHERE a.Cod_prod='"+cod+"'";
            
                Statement st=cn.createStatement();
                ResultSet rs=st.executeQuery(select);
                String comprado="";
            
                while(rs.next()){
                    comprado=rs.getString("Cod_prod");
                }
                
                if(comprado.equals("")){//Si no ha sido comprado
                    try{
                        PreparedStatement pst = cn.prepareStatement("DELETE FROM producto WHERE Cod_prod="+cod+"");
                        int n= pst.executeUpdate(); 
            
                        if(n > 0){
                            JOptionPane.showMessageDialog(null, "Registro Borrado exitosamente.");
                        }
                        
                        cargar("");
                    }catch(SQLException ex){
                        System.out.println("Error: "+ex);
                        JOptionPane.showMessageDialog(null, "Error al Borrar el Registro, Operacion Cancelada.");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Operacion cancelada: No se puede borrar un producto que ha sido comprado.");
                }
            } catch (SQLException ex) {//Si hay error con producto y compra_tiene o pedido_producto
                JOptionPane.showMessageDialog(this, "Operacion cancelada: Error al comprobar si el producto ha sido comprado.");
                Logger.getLogger(IngresoCliente.class.getName()).log(Level.SEVERE, null, ex);
            }    
        }else{
            JOptionPane.showMessageDialog(this, "Seleccione alguna fila");
        }        
    }//GEN-LAST:event_mneliminarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnactualizar;
    private javax.swing.JButton btncancelar;
    private javax.swing.JButton btnguardar;
    private javax.swing.JButton btnmostrar;
    private javax.swing.JButton btnsalir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JMenuItem mnactualizar;
    private javax.swing.JMenuItem mneliminar;
    private javax.swing.JTable tbproductos;
    private javax.swing.JTextField txtbuscar;
    private javax.swing.JTextField txtcod;
    private javax.swing.JTextField txtdes;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtstock;
    private javax.swing.JTextField txttipo;
    // End of variables declaration//GEN-END:variables
   Conectar cc= new Conectar();
   Connection cn=cc.conexion();
   PlaceHolder ph=new PlaceHolder();
}
