package com.stevnsvig.remedy.sqlexecutor;

import com.bmc.arsys.api.ARException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;

/**
 *
 * @author <a href="mailto:jonas@stevnsvig.com">Jonas Stumph Stevnsvig</a>
 */
public class SQLWorker extends SwingWorker<ArrayList<ArrayList<String>>, String> {

    String queryString;
    SQLExecutor sqlExecutor;
    JTable resultsTable;
    /**
     * The text area where messages are written.
     */
    private final JTextPane messagesTextArea;

    public SQLWorker(final String query, final JTextPane messagesTextArea, final JTable table, final SQLExecutor se) {
        this.queryString = query;
        this.sqlExecutor = se;
        this.resultsTable = table;
        this.messagesTextArea = messagesTextArea;
    }

    /**
     * Update the resultsTable
     *
     * @param contents
     * @param headers
     */
    private void updateTable(ArrayList<ArrayList<String>> contents, String[] headers) {
        DefaultTableModel model = (DefaultTableModel) resultsTable.getModel();
        int rows = model.getRowCount();
        for (int i = rows - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        publish("headers: " + Arrays.toString(headers));
        model.setColumnCount(0);
        for (ArrayList<String> row : contents) {
            if (model.getColumnCount() == 0) {//if first row, set column count
                publish("cols: " + row.size());
                //model.setColumnCount(row.size());
                for (String header : headers) {
                    model.addColumn(header);
                }
            }
            model.addRow(row.toArray());
        }
    }

    /**
     * Gets columns from the SQL queryString
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
                    ArrayList<ArrayList<String>> sqlResult = sqlExecutor.executeSQL("SELECT column_name FROM user_tab_cols WHERE table_name = '" + tblList + "'");
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

    @Override
    protected ArrayList<ArrayList<String>> doInBackground() throws Exception {
        // More work was done
        publish("Executing Query " + new Date());
        setProgress(10);
        ArrayList<ArrayList<String>> sqlResult = sqlExecutor.executeSQL(queryString);
        publish("Refreshing table " + new Date());
        updateTable(sqlResult, getColumns(queryString));
        // Complete
        publish("Complete " + new Date());
        setProgress(100);
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (final String string : chunks) {
            try {
                messagesTextArea.getStyledDocument().insertString(messagesTextArea.getStyledDocument().getLength(), string + "\n", new SimpleAttributeSet());
            } catch (BadLocationException ex) {
                Logger.getLogger(SQLWorker.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
