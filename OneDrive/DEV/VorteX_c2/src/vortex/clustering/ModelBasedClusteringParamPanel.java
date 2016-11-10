/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModelBasedClusteringParamPanel.java
 *
 * Created on 27-Oct-2010, 20:36:09
 */
package vortex.clustering;

import java.sql.SQLException;
import java.util.ArrayList;
import clustering.ClusterSet;

import clustering.Dataset;
import vortex.util.Config;
import vortex.util.ConnectionManager;
import util.logger;
import vortex.gui2.ClusteringResultList;

/**
 *
 * @author Nikolay
 */
public class ModelBasedClusteringParamPanel extends javax.swing.JPanel {

    private ClusterSet selectedCS = null;

    /**
     * Creates new form ModelBasedClusteringParamPanel
     */
    public ModelBasedClusteringParamPanel(Dataset ds) {
        initComponents();
        dsb.addDatasetSelectionListener(csb);
        csb.addClusterSetSelectionListener(new ClusteringResultList.ClusterSetSelectionListener() {
            @Override
            public void clusterSetSelected(ClusteringResultList.ClusterSetSelectionEvent evt) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
           
        });

        String[] s = Config.getDatasetIDsForLoading();

        ArrayList<String> al = new ArrayList<String>();
        for (String dsID : s) {
            try {
                Dataset d2 = ConnectionManager.getStorageEngine().loadDataset(dsID);
                if (d2.getFeatureNames().equals(ds.getFeatureNames()) && !d2.equals(ds)) {
                    al.add(dsID);
                }
            } catch (SQLException e) {
                logger.showException(e);
            }
        }
        if (al.size() > 0) {
            dsb.loadDatasets(al.toArray(new String[al.size()]));
        }
        try {
            Thread.currentThread().sleep(100);
        } catch (InterruptedException ex) {
            logger.showException(ex);
        }
        this.doLayout();
        this.invalidate();
        this.validate();
        this.repaint();
    }

    public ClusterSet getSelectedClusterSet() {
        return selectedCS;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        csb = new vortex.gui2.ClusteringResultList();
        dsb = new vortex.gui2.DatasetBrowser();

        setMinimumSize(new java.awt.Dimension(300, 300));
        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(300);

        csb.setMinimumSize(new java.awt.Dimension(300, 300));
        csb.setToolbarVisible(false);
        jSplitPane1.setRightComponent(csb);

        dsb.setMinimumSize(new java.awt.Dimension(300, 300));
        dsb.setToolbarVisible(false);
        jSplitPane1.setLeftComponent(dsb);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vortex.gui2.ClusteringResultList csb;
    private vortex.gui2.DatasetBrowser dsb;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
