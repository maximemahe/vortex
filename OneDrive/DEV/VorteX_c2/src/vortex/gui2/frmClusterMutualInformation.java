/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * frmEnrichment.java
 *
 * Created on 16-Dec-2009, 13:15:52
 */
package vortex.gui2;

import java.util.List;
import java.util.Map.Entry;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import samusik.glasscmp.GlassFrame;
import clustering.ClusterSet;
import clustering.Dataset;
import util.DefaultEntry;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmClusterMutualInformation extends GlassFrame {

    private static final long serialVersionUID = 1L;
    private ValidationComputer computer;
    Dataset annDS;
    private ClusterSet[] css;

    /**
     * Creates new form frmEnrichment
     */
    @SuppressWarnings("unchecked")
    public frmClusterMutualInformation(ClusterSet[] css) {
        initComponents();

        if (css.length < 3) {
            throw new IllegalArgumentException("You should select at least 3 cluster sets");
        }
        this.css = css;
    }

    /*TODO
     * - adding annotations to the list
     * - computing annotations for selected clusters
     * - call insurance
     */
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        cmdComputeRatios = new samusik.glasscmp.GlassButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Clustering Stability");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(getBackground());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        cmdComputeRatios.setText("Compute");
        cmdComputeRatios.setMaximumSize(new java.awt.Dimension(100, 28));
        cmdComputeRatios.setMinimumSize(new java.awt.Dimension(100, 28));
        cmdComputeRatios.setPreferredSize(new java.awt.Dimension(100, 28));
        cmdComputeRatios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdComputeRatiosActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cmdComputeRatios, gridBagConstraints);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Cluster Set", "Mutual Information to Previous"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdComputeRatiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdComputeRatiosActionPerformed
        try {

            if (computer != null) {
                computer.cancel(true);
            }
            computer = new ValidationComputer(css, (DefaultTableModel) jTable1.getModel());
            computer.execute();
            cmdComputeRatios.setText("Computing...");
            cmdComputeRatios.setEnabled(false);
        } catch (Exception e) {
            logger.showException(e);
        }
    }//GEN-LAST:event_cmdComputeRatiosActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (computer != null) {
            computer.cancel(true);
            DefaultTableModel dtm = (DefaultTableModel) jTable1.getModel();
            for (int i = 0; i < dtm.getRowCount(); i++) {
                dtm.removeRow(0);
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private class ValidationComputer extends SwingWorker<DefaultTableModel, Entry<ClusterSet, Double>> {

        private ClusterSet[] css;
        private DefaultTableModel dtm;

        @Override
        protected void done() {
        }

        public ValidationComputer(ClusterSet[] css, DefaultTableModel dtm) {
            this.css = css;
            this.dtm = dtm;
        }

        @Override
        protected DefaultTableModel doInBackground() throws Exception {

            for (int i = 1; i < css.length - 1; i++) {
                publish(new DefaultEntry<>(css[i], 0.5 * (ClusterSet.getAdjustedRandIndex(css[i - 1], css[i]) + ClusterSet.getAdjustedRandIndex(css[i + 1], css[i]))));
            }

            cmdComputeRatios.setText("Compute");
            cmdComputeRatios.setEnabled(true);
            return dtm;
        }

        @Override
        protected void process(List<Entry<ClusterSet, Double>> chunks) {
            for (Entry<ClusterSet, Double> m : chunks) {
                dtm.addRow(new Object[]{m.getKey(), m.getValue()});
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private samusik.glasscmp.GlassButton cmdComputeRatios;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
