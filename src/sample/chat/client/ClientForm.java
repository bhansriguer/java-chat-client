/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sample.chat.client;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author bhans
 */
public class ClientForm extends javax.swing.JFrame {

    private static final String IPADDRESS = "172.104.91.153";
    private static final int PORT = 6969;

    private String LOGIN = "client1";

    private Socket mSocket;

    private BufferedWriter writer = null;
    private BufferedReader reader = null;

    private ArrayList<PrivateMessageForm> pmFormList;

    /**
     * Creates new form ClientForm
     */
    public ClientForm() {
        initComponents();
        pmFormList = new ArrayList<>();
        tbl_userList.addMouseListener(userListTableMouseAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    public MouseAdapter userListTableMouseAdapter = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            JTable table = (JTable) e.getSource();
            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            if (e.getClickCount() == 2) {
                DefaultTableModel tm = (DefaultTableModel) tbl_userList.getModel();
                String user = tm.getValueAt(row, 0).toString();
                if (!pmFormList.isEmpty()) {
                    for (PrivateMessageForm pmf : pmFormList) {
                        if (!pmf.getPMUser().equals(user)) {
                            showNewPMForm(user);
                            break;
                        }
                    }
                } else {
                    showNewPMForm(user);
                }
            }
        }
    };

    private void showNewPMForm(String user) {
        PrivateMessageForm pmf = new PrivateMessageForm(ClientForm.this, user);
        pmFormList.add(pmf);
        pmf.setVisible(true);
    }

    public boolean removePrivateMessageWindow(PrivateMessageForm pmf) {
        boolean isRemoved = false;
        pmf.dispose();
        isRemoved = pmFormList.remove(pmf);
        System.out.println("PMFormList: " + pmFormList.size());
        return isRemoved;
    }

    private void connect() {
        try {
            mSocket = new Socket(IPADDRESS, PORT);
            if (mSocket.isConnected()) {
                writer = new BufferedWriter(
                        new OutputStreamWriter(mSocket.getOutputStream()));
                reader = new BufferedReader(
                        new InputStreamReader(mSocket.getInputStream()));
                writer.write("login||" + LOGIN + "\r\n");
                writer.flush();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().startsWith("server: connected:")) {
                        // msg[0] = server | msg[1] = connected | msg[0] = username
                        String[] msg = line.split("\\:");
                        addUserToTable(msg[2]);
                    } else if (line.toLowerCase().startsWith("server: disconnected:")) {
                        // msg[0] = server | msg[1] = disconnected | msg[0] = username
                        String[] msg = line.split("\\:");
                        removeUserFromTable(msg[2]);
                    } else if (line.toLowerCase().startsWith("server: userlist:")) {
                        // msg[0] = server | msg[1] = userlist | msg[2] = username|username|...
                        String[] msg = line.split("\\:");
                        String[] users = msg[2].split("\\|");
                        for (String user : users) {
                            if (!user.trim().equals(LOGIN)) {
                                addUserToTable(user);
                            }
                        }
                    } else if (line.toLowerCase().startsWith("pm:")) {
                        String[] msg = line.split("\\:");
                        for (PrivateMessageForm pmf : pmFormList) {
                            if (pmf.getPMUser().equals(msg[1])) {
                                pmf.writeToTextArea(line.toLowerCase().replace("pm:", "") + "\n");
                            }
                        }
                    } else {
                        writeToTextArea(line + "\n");
                    }
                }
                System.exit(0);
            }
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private void removeUserFromTable(String username) {
        username = username.trim();
        DefaultTableModel model = (DefaultTableModel) tbl_userList.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(username)) {
                model.removeRow(i);
                break;
            }
        }
    }

    private void addUserToTable(String username) {
        username = username.trim();
        DefaultTableModel model = (DefaultTableModel) tbl_userList.getModel();
        model.addRow(new Object[]{username});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        ta_messagesArea = new javax.swing.JTextArea();
        tf_messageField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbl_userLogin = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_userList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ta_messagesArea.setColumns(20);
        ta_messagesArea.setRows(5);
        jScrollPane1.setViewportView(ta_messagesArea);

        tf_messageField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tf_messageFieldKeyPressed(evt);
            }
        });

        jLabel1.setText("Logged in as");

        lbl_userLogin.setText("user");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_userLogin)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lbl_userLogin))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(tf_messageField, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tf_messageField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tbl_userList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Users"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tbl_userList);
        if (tbl_userList.getColumnModel().getColumnCount() > 0) {
            tbl_userList.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 39, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void sendMessage(String message) {
        try {
            writer.write(message + "\r\n");
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void writeToTextArea(String message) {
        ta_messagesArea.append(message);
        ta_messagesArea.setCaretPosition(ta_messagesArea.getText().length());
        if (ta_messagesArea.getLineCount() > 1000) {
            ta_messagesArea.setText("");
        }
    }

    private void tf_messageFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tf_messageFieldKeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMessage(tf_messageField.getText());
            tf_messageField.setText("");
        }
    }//GEN-LAST:event_tf_messageFieldKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_userLogin;
    private javax.swing.JTextArea ta_messagesArea;
    private javax.swing.JTable tbl_userList;
    private javax.swing.JTextField tf_messageField;
    // End of variables declaration//GEN-END:variables

    public void setLOGIN(String LOGIN) {
        this.LOGIN = LOGIN;
        lbl_userLogin.setText(this.LOGIN);
    }
}
