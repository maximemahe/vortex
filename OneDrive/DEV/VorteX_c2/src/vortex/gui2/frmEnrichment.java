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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import samusik.glasscmp.GlassFrame;
import samusik.glasscmp.GlassListSelector;
import samusik.objecttable.ObjectTableModel;
import samusik.objecttable.TableTransferHandler;
import clustering.Cluster;
import clustering.ClusterMember;
import annotations.EnrichmentInfo;
import clustering.Dataset;
import clustering.Score;
import annotations.Annotation;
import annotations.Enrichment;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmEnrichment extends GlassFrame {

    private static final long serialVersionUID = 1L;
    private final Dataset ds;

    /**
     * Creates new form frmEnrichment
     */
    private frmEnrichment(Dataset nd) {
        initComponents();
        this.setTitle("Enrichment analysis");
        this.ds = nd;
        Annotation[] ann = nd.getAnnotations();
        for (Annotation annotation : ann) {
            ((GlassListSelector<Annotation>) lstAnnotations).getAvailableListModel().addElement(annotation);
        }
    }

    //private static HashMap<Annotation.EntityType, frmEnrichment> hmInstanes = new HashMap<Annotation.EntityType, frmEnrichment>();
    public static frmEnrichment getInstance(Dataset nd) {
        //if (hmInstanes.get(entityType) == null) {
        //  hmInstanes.put(entityType, new frmEnrichment(entityType));
        //}
        return new frmEnrichment(nd);//hmInstanes.get(entityType);
    }
    private HashMap<Cluster, JTable> hmTables = new HashMap<Cluster, JTable>();

    private Annotation[] getSelectedAnnotations() {
        ArrayList<Annotation> al = ((GlassListSelector<Annotation>) lstAnnotations).getSelectedItems();
        return al.toArray(new Annotation[al.size()]);
    }

    /*TODO
     * - adding annotations to the list
     * - computing annotations for selected clusters
     * - call insurance
     */

    public void addCluster(Cluster c) throws SQLException {
        JTable tab = new JTable();
        fillTableWithEnrichment(tab, c);
        JScrollPane scr = new JScrollPane(tab);
        hmTables.put(c, tab);
        tabClusters.add(c.toString(), scr);
    }

    public Cluster[] getClusters() {
        return hmTables.keySet().toArray(new Cluster[hmTables.keySet().size()]);
    }

    public void removeCluster(Cluster c) {
        if (hmTables.get(c) != null) {
            tabClusters.remove(hmTables.get(c).getParent().getParent());
            hmTables.remove(c);
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
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        tabClusters = new javax.swing.JTabbedPane();
        lstAnnotations = new samusik.glasscmp.GlassListSelector<Annotation>();
        jLabel1 = new javax.swing.JLabel();
        cmdComputeEnrichment = new samusik.glasscmp.GlassButton();
        jSlider1 = new javax.swing.JSlider();
        lblThs = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBackground(getBackground());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        tabClusters.setMinimumSize(new java.awt.Dimension(400, 200));
        tabClusters.setPreferredSize(new java.awt.Dimension(500, 500));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(tabClusters, gridBagConstraints);

        lstAnnotations.setMinimumSize(new java.awt.Dimension(80, 200));
        lstAnnotations.setPreferredSize(new java.awt.Dimension(80, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lstAnnotations, gridBagConstraints);

        jLabel1.setText("Select annotations:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jLabel1, gridBagConstraints);

        cmdComputeEnrichment.setText("Compute");
        cmdComputeEnrichment.setMaximumSize(new java.awt.Dimension(75, 28));
        cmdComputeEnrichment.setMinimumSize(new java.awt.Dimension(75, 28));
        cmdComputeEnrichment.setPreferredSize(new java.awt.Dimension(75, 28));
        cmdComputeEnrichment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdComputeEnrichmentActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(cmdComputeEnrichment, gridBagConstraints);

        jSlider1.setValue(0);
        jSlider1.setMinimumSize(new java.awt.Dimension(200, 25));
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jSlider1, gridBagConstraints);

        lblThs.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(lblThs, gridBagConstraints);

        jLabel3.setText("PhenoScore Threshold");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel3, gridBagConstraints);

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

    private void cmdComputeEnrichmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdComputeEnrichmentActionPerformed
        for (Cluster c : hmTables.keySet()) {
            try {
                fillTableWithEnrichment(hmTables.get(c), c);
            } catch (SQLException ex) {
                logger.showException(ex);
            }
        }
    }//GEN-LAST:event_cmdComputeEnrichmentActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        lblThs.setText(String.valueOf(jSlider1.getValue() / (double) jSlider1.getMaximum()));
    }//GEN-LAST:event_jSlider1StateChanged

    private void fillTableWithEnrichment(JTable tab, Cluster c) throws SQLException {
        ArrayList<EnrichmentInfo> alEi = new ArrayList<>();

        ArrayList<Integer> alHits = new ArrayList<>();

        if (c.getComputedScores().contains(Score.ScoringMethod.PSS)) {
            double PSSThs = Double.parseDouble(lblThs.getText());
            for (ClusterMember cm : c.getClusterMembers()) {
                if (cm.getScores().get(Score.ScoringMethod.PSS).score >= PSSThs) {
                    alHits.add(cm.getDatapoint().getID());
                }
            }
        } else {
            for (ClusterMember cm : c.getClusterMembers()) {
                alHits.add(cm.getDatapoint().getID());
            }
            jSlider1.setVisible(false);
            lblThs.setVisible(false);
        }
        int[] pidsTotal = new int[ds.size()];

        for (int i = 0; i < pidsTotal.length; i++) {
            pidsTotal[i] = ds.getDatapoints()[i].getID();
        }

        int[] pidsSample = new int[alHits.size()];

        for (int i = 0; i < pidsSample.length; i++) {
            pidsSample[i] = alHits.get(i);
        }

        for (Annotation ann : getSelectedAnnotations()) {
            alEi.addAll(Arrays.asList(Enrichment.computeEnrichment(pidsTotal, pidsSample, ann)));
        }

        for (int i = 0; i < alEi.size(); i++) {
            if (alEi.get(i).total < 2 || alEi.get(i).count < 2 || alEi.get(i).term.trim().isEmpty()) {
                alEi.remove(i);
                i--;
            }
        }

        if (alEi.size() > 0) {
            ObjectTableModel<EnrichmentInfo> tm = new ObjectTableModel<>(alEi.toArray(new EnrichmentInfo[alEi.size()]));
            tab.setModel(tm);

            tab.getColumnCount();
            tab.setTransferHandler(new TableTransferHandler());
            tab.setRowSorter(new TableRowSorter<>(tm));

        } else {
            tab.setModel(new DefaultTableModel());
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private samusik.glasscmp.GlassButton cmdComputeEnrichment;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel lblThs;
    private samusik.glasscmp.GlassListSelector lstAnnotations;
    private javax.swing.JTabbedPane tabClusters;
    // End of variables declaration//GEN-END:variables
}
