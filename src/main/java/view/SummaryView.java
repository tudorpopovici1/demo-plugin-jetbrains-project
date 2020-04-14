package view;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import data.SummaryData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Class creating summary method plugin view
 */
public class SummaryView extends JPanel {

    private DefaultTableModel model;

    /**
     * Constructor of class
     */
    public SummaryView() {
        super();
        JBTable table = new JBTable();
        Vector<? extends Vector> vector = new Vector<>();
        Vector<String> listVector = new Vector<>(Arrays.asList("Name", "Value"));
        model = new DefaultTableModel(vector, listVector);
        table.setModel(model);
        table.setCellSelectionEnabled(false);
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        JBScrollPane scrollPane = new JBScrollPane(table);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updating table view
     * @param summaries list including statistics
     */
    public void updateModel(List<SummaryData> summaries) {
        int noOfRows = model.getRowCount();
        for (int i = 0; i < noOfRows; i++) {
            model.removeRow(0);
        }
        for (SummaryData summary : summaries) {
            Vector<String> row = new Vector<>();
            row.add(summary.getName());
            row.add(summary.getValue());
            model.addRow(row);
        }
    }
}
