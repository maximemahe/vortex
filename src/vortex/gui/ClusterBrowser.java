/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClusterBrowser.java
 *
 * Created on 15-Sep-2010, 18:03:09
 */
package vortex.gui;

import clustering.BarCode;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import samusik.glasscmp.GlassTableHeader;
import samusik.objecttable.ObjectTableModel;
import samusik.objecttable.TableTransferHandler;
import clustering.Cluster;
import clustering.ClusterMember;
import vortex.main.TableCellEditorEx;

/**
 *
 * @author Nikolay
 */
public final class ClusterBrowser extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private Cluster cluster = null;
    private ObjectTableModel<ClusterMember> tm = null;

    private class CustomTableRowComparator implements Comparator<Object> {

        @Override
        public int compare(Object a, Object b) {
            if (Number.class.isAssignableFrom(a.getClass()) && Number.class.isAssignableFrom(b.getClass())) {
                return (int) Math.signum(((Number) a).doubleValue() - ((Number) b).doubleValue());
            } else {
                return a.toString().compareTo(b.toString());
            }
        }
    }

    public ClusterBrowser(Cluster cluster) throws SQLException {
        super();
        initComponents();
        setCluster(cluster);
    }

    public void selectClusterMember(ClusterMember cm) {
        if (tm == null) {
            return;
        }
        int idx = tm.getRowOfObject(cm);
        if (idx < 0) {
            return;
        }
        int row = table.convertRowIndexToView(idx);
        table.setRowSelectionInterval(row, row);
        table.scrollRectToVisible(new Rectangle(0, table.getRowHeight() * row, table.getWidth(), table.getRowHeight() * 2));
    }

    public void setCluster(Cluster cluster) throws SQLException {
        this.cluster = cluster;
        tm = new ObjectTableModel<ClusterMember>(cluster.getClusterMembers()) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                Class cl = super.getColumnClass(columnIndex);
                if (BarCode.class.isAssignableFrom(cl)) {
                    cl = BarCode.class;
                }
                return cl;
            }
        };

        table.setModel(tm);

        TableRowSorter<ObjectTableModel<ClusterMember>> trs = new TableRowSorter<>(tm);
        table.setRowSorter(trs);

        TableCellEditorEx editor = new TableCellEditorEx();

        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellEditor(editor);
            table.getColumnModel().getColumn(i).setHeaderRenderer(new GlassTableHeader(table.getTableHeader()));
            trs.setComparator(i, new CustomTableRowComparator());
        }

        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(table.getColumnModel().getColumnIndex("Barcode")).setPreferredWidth(cluster.getClusterSet().getDataset().getDimension() * 15);
        table.setColumnSelectionAllowed(false);
        table.setShowVerticalLines(true);
        table.setDefaultRenderer(Double.class, new DefaultTableCellRenderer());
        table.setDefaultRenderer(BarCode.class, new BarCodeTableCellRenderer());
        table.setDefaultRenderer(Color.class, new ColorTableCellRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    int idx = table.rowAtPoint(e.getPoint());
                    if (!table.isRowSelected(idx)) {
                        table.getSelectionModel().setSelectionInterval(idx, idx);
                    }
                    showProfilePopup(e);
                }
            }
        });

        table.setTransferHandler(new TableTransferHandler());

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ClusterMember[] cm = tm.getObjects(new ClusterMember[tm.getRowCount()]);
                int[] rows = table.getSelectedRows();
                if (rows.length > 0) {
                    selectedProfiles = new ClusterMember[rows.length];
                    int j = 0;
                    for (int i = 0; i < rows.length; i++) {
                        int idx = table.convertRowIndexToModel(rows[i]);
                        selectedProfiles[j] = cm[idx];
                        j++;
                    }
                } else {
                    selectedProfiles = new ClusterMember[0];
                }
            }
        });
    }

    public ClusterMember[] getSelectedProfiles() {
        return selectedProfiles;
    }

    private void showProfilePopup(MouseEvent evt) {
        if (this.getParent() != null) {
            if (this.getParent().getParent() instanceof ClusterBrowserPane) {
                ClusterBrowserPane.getProfilePopup().show(table, evt.getX(), evt.getY());
            }
        }
    }
    private ClusterMember[] selectedProfiles = new ClusterMember[0];

    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Creates new form ClusterBrowser
     */
    public ClusterBrowser() {
        super();
        initComponents();
    }

    public JTable getTable() {
        return table;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable(){
            ArrayList<Integer> colWid = null;

            @Override
            public void tableChanged(TableModelEvent e) {

                if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    colWid = new ArrayList<Integer>();
                    for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
                        colWid.add(this.getColumnModel().getColumn(i).getPreferredWidth());
                    }
                }

                super.tableChanged(e);
                if (e.getFirstRow() == TableModelEvent.HEADER_ROW) {
                    for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
                        this.getColumnModel().getColumn(i).setHeaderRenderer(new samusik.glasscmp.GlassTableHeader(this.getTableHeader()));
                    }
                    if (colWid.size() == this.getColumnModel().getColumnCount()) {
                        for (int i = 0; i < this.getColumnModel().getColumnCount(); i++) {
                            this.getColumnModel().getColumn(i).setPreferredWidth(colWid.get(i));
                        }
                    }

                }

            }
        } ;

        setLayout(new java.awt.BorderLayout());

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        jScrollPane1.setViewportView(table);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
