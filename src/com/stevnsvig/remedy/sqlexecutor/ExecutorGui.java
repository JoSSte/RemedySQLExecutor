package com.stevnsvig.remedy.sqlexecutor;

import com.bmc.arsys.api.ARException;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.net.URL;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author g45877
 */
public class ExecutorGui extends javax.swing.JFrame {

    SQLExecutor se;
    Timer timer;

    /**
     * Creates new form ExecutorGui
     */
    public ExecutorGui() {
        try {
            this.se = new SQLExecutor(SQLExecutor.loadPropertiesFromXML());
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        initComponents();
        initCustomFields();

        getImage("sql-logo.png");
    }

    /**
     * initialise MessageConsole Component
     */
    private void initCustomFields() {
        MessageConsole mc = new MessageConsole(txtPaneSysLog);
        mc.redirectOut();
        mc.redirectErr(Color.RED, null);
        mc.setMessageLines(100);
        txtPaneSysLog.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                startBlink();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                startBlink();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                startBlink();
            }
        });
        //set username and Servername
        serverNameLabel.setText(se.getArsServer());
        userNameLabel.setText(se.getArsUser());

    }

    private void blink(boolean blinkFlag) {
        if (blinkFlag) {
            mainPane.setForegroundAt(1, Color.red);
            mainPane.setBackgroundAt(1, Color.orange);
        } else {
            mainPane.setForegroundAt(1, mainPane.getForegroundAt(2));
            mainPane.setBackgroundAt(1, mainPane.getBackgroundAt(2));
        }
        mainPane.repaint();
    }

    /**
     * Get image for Icon
     *
     * @param imageName
     */
    private void getImage(String imageName) {
        URL url = ClassLoader.getSystemResource("images/" + imageName);
        Toolkit kit = Toolkit.getDefaultToolkit();
        this.setIconImage(kit.createImage(url));
    }

    /**
     * Update the table
     *
     * @param contents
     * @param headers
     */
    private void updateTable(ArrayList<ArrayList<String>> contents, String[] headers) {
        DefaultTableModel model = (DefaultTableModel) tblResult.getModel();
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        System.out.println("headers: " + Arrays.toString(headers));
        model.setColumnCount(0);
        for (ArrayList<String> row : contents) {
            if (model.getColumnCount() == 0) {//if first row, set column count
                System.out.println("cols: " + row.size());
                //model.setColumnCount(row.size());
                for (String header : headers) {
                    model.addColumn(header);
                }
            }
            model.addRow(row.toArray());
        }
    }

    /**
     * Gets columns from the SQL query
     *
     * @param queryString
     * @return
     */
    private String[] getColumns(String queryString) {
        String[] returnString = {};

        int selectIndex = queryString.toUpperCase().indexOf("SELECT") + 6;
        int fromIndex = queryString.toUpperCase().indexOf("FROM") - 1;
        String colList = queryString.substring(selectIndex, fromIndex);
        if (colList.contains("*")) {
            System.err.println("[getColumns]\tNo columns defined. * used.");

            String tblList = queryString.substring(fromIndex + 5);
            if (queryString.contains("WHERE")) {
                tblList = queryString.substring(fromIndex + 5, queryString.indexOf("WHERE") - 1);
            }
            if (!tblList.contains(",")) {
                tblList = tblList.replaceAll("\\s", "");
                try {
                    ArrayList<ArrayList<String>> sqlResult = se.executeSQL("SELECT column_name FROM user_tab_cols WHERE table_name = '" + tblList + "'");
                    ArrayList<String> colll = new ArrayList<>();
                    for (ArrayList<String> next : sqlResult) {
                        colll.add(next.get(0));
                    }
                    returnString = colll.toArray(new String[0]);
                } catch (ARException ex) {
                    System.err.println("[getColumns]\tError fetching Columns for result: " + ex.getMessage());
                }
            } else {
                System.err.println("[getColumns]\tSeveral tables. Not attempting to get field list");
            }

        } else {
            returnString = colList.split(",");
        }

        return returnString;
    }

    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPane = new javax.swing.JTabbedPane();
        queryScrollPane = new javax.swing.JScrollPane();
        txtQuery = new javax.swing.JTextPane();
        logScrollPane = new javax.swing.JScrollPane();
        txtPaneSysLog = new javax.swing.JTextPane();
        queryLogScrollPane = new javax.swing.JScrollPane();
        txtQueryLog = new javax.swing.JTextArea();
        scrollResult = new javax.swing.JScrollPane();
        tblResult = new javax.swing.JTable();
        btnExecute = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        serverNameLabel = new javax.swing.JLabel();
        userNameLabel = new javax.swing.JLabel();
        btnSettings = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SQL Executor");

        mainPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearBlink(evt);
            }
        });

        txtQuery.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txtQuery.setText("SELECT schemaid, name FROM arschema WHERE name like 'AR%'");
        txtQuery.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtQueryKeyTyped(evt);
            }
        });
        queryScrollPane.setViewportView(txtQuery);

        mainPane.addTab("Query", queryScrollPane);

        txtPaneSysLog.setFont(new java.awt.Font("Lucida Console", 0, 10)); // NOI18N
        txtPaneSysLog.addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                startBlink(evt);
            }
        });
        txtPaneSysLog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                clearBlink(evt);
            }
        });
        logScrollPane.setViewportView(txtPaneSysLog);

        mainPane.addTab("Log", logScrollPane);

        txtQueryLog.setColumns(20);
        txtQueryLog.setRows(5);
        queryLogScrollPane.setViewportView(txtQueryLog);

        mainPane.addTab("QueryLog", queryLogScrollPane);

        tblResult.setModel(new javax.swing.table.DefaultTableModel(
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
        scrollResult.setViewportView(tblResult);

        btnExecute.setText("Execute Query");
        btnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExecuteActionPerformed(evt);
            }
        });

        btnClear.setText("Clear Query field");
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        statusPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        serverNameLabel.setText("ServerName: ");

        userNameLabel.setText("UserName");

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(serverNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 447, Short.MAX_VALUE)
                .addComponent(userNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverNameLabel)
                    .addComponent(userNameLabel)))
        );

        btnSettings.setText("Settings");

        btnExport.setText("Export to CSV");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollResult, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnExecute, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
                    .addComponent(btnSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnExport, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainPane, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGap(152, 152, 152)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(statusPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnExecute)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSettings)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnExport)
                .addGap(64, 64, 64)
                .addComponent(scrollResult, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(mainPane, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(499, Short.MAX_VALUE)))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(0, 685, Short.MAX_VALUE)
                    .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExecuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExecuteActionPerformed
        clearBlink(null);
        try {
            String queryString = txtQuery.getText();
            txtQueryLog.append(queryString + "\n");
            ArrayList<ArrayList<String>> sqlResult = se.executeSQL(queryString);

            updateTable(sqlResult, getColumns(queryString));
        } catch (ARException arEx) {
            System.err.println("Error fetching SQL: " + arEx.getMessage());
        }
    }//GEN-LAST:event_btnExecuteActionPerformed

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        txtQuery.setText("");
        System.err.println("Query field cleared");
        clearBlink(null);
    }//GEN-LAST:event_btnClearActionPerformed

    private void txtQueryKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtQueryKeyTyped
        boolean restrictedQuery = !(txtQuery.getText().contains("INSERT") || txtQuery.getText().contains("TRUNCATE") || txtQuery.getText().contains("UPDATE") || txtQuery.getText().contains("DROP"));
        btnExecute.setEnabled(restrictedQuery);
        if (!restrictedQuery) {
            System.err.println("Cannot execute non-SELECT Query");
        }
    }//GEN-LAST:event_txtQueryKeyTyped

    private void clearBlink(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_clearBlink
        if (timer != null) {
            timer.stop();
        }
    }//GEN-LAST:event_clearBlink

    private void startBlink(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_startBlink
        // TODO add your handling code here:
    }//GEN-LAST:event_startBlink

    /**
     * start timer
     *
     * @param comp
     */
    private void startBlink() {
        timer = new Timer(500, new ActionListener() {
            boolean blinkFlag = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                blink(blinkFlag);
                blinkFlag = !blinkFlag;

            }
        });
        timer.start();
    }

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

            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            System.err.println("[" + ex.getClass().getCanonicalName() + "]\t" + ex.getMessage());
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ExecutorGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnExecute;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnSettings;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTabbedPane mainPane;
    private javax.swing.JScrollPane queryLogScrollPane;
    private javax.swing.JScrollPane queryScrollPane;
    private javax.swing.JScrollPane scrollResult;
    private javax.swing.JLabel serverNameLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable tblResult;
    private javax.swing.JTextPane txtPaneSysLog;
    private javax.swing.JTextPane txtQuery;
    private javax.swing.JTextArea txtQueryLog;
    private javax.swing.JLabel userNameLabel;
    // End of variables declaration//GEN-END:variables
}