/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClusterDendrogramControlPanel.java
 *
 * Created on 30-Sep-2010, 00:16:42
 */
package vortex.gui2.clusterdendrogram;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import annotations.Annotation;
import clustering.ClusterSet;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class ClusterDendrogramControlPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Creates new form ClusterDendrogramControlPanel
     */
    public ClusterDendrogramControlPanel() {
        initComponents();

    }

    public void highlightClusterSet(ArrayList<ClusterSet> cs) {
        chpp.highlightClusterSet(cs);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        chpp = new vortex.gui2.clusterdendrogram.ClusterDendrogramPlot();
        zoomSlider = new javax.swing.JSlider();
        lblZoom = new javax.swing.JLabel();
        cmdGS1 = new samusik.glasscmp.GlassButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setViewportView(chpp);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);

        zoomSlider.setMajorTickSpacing(10);
        zoomSlider.setMinimum(-100000);
        zoomSlider.setMinorTickSpacing(1);
        zoomSlider.setValue(0);
        zoomSlider.setMaximumSize(new java.awt.Dimension(300, 25));
        zoomSlider.setMinimumSize(new java.awt.Dimension(300, 25));
        zoomSlider.setOpaque(false);
        zoomSlider.setPreferredSize(new java.awt.Dimension(300, 23));
        zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSliderStateChanged(evt);
            }
        });
        zoomSlider.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                zoomSliderPropertyChange(evt);
            }
        });
        zoomSlider.addVetoableChangeListener(new java.beans.VetoableChangeListener() {
            public void vetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {
                zoomSliderVetoableChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        add(zoomSlider, gridBagConstraints);

        lblZoom.setText("1:1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        add(lblZoom, gridBagConstraints);

        cmdGS1.setText("Gold standard");
        cmdGS1.setToolTipText("Import and visualize the distribution of golden standard classes in the cluster tree");
        cmdGS1.setMinimumSize(new java.awt.Dimension(120, 28));
        cmdGS1.setPreferredSize(new java.awt.Dimension(120, 28));
        cmdGS1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                cmdGS1MouseDragged(evt);
            }
        });
        cmdGS1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdGS1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 0);
        add(cmdGS1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmdGS1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmdGS1MouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdGS1MouseDragged

    private void cmdGS1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdGS1ActionPerformed
        try {
            Annotation ann = (Annotation) JOptionPane.showInputDialog(this, "Select annotation", "Select annotation", JOptionPane.QUESTION_MESSAGE, null, chpp.getClusterSets()[0].getDataset().getAnnotations(), chpp.getClusterSets()[0].getDataset().getAnnotations()[0]);
            chpp.setAnnotation(ann);

        } catch (SQLException e) {
            logger.showException(e);
        }

    }//GEN-LAST:event_cmdGS1ActionPerformed

    private void zoomSliderVetoableChange(java.beans.PropertyChangeEvent evt)throws java.beans.PropertyVetoException {//GEN-FIRST:event_zoomSliderVetoableChange
    }//GEN-LAST:event_zoomSliderVetoableChange

    private void zoomSliderPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_zoomSliderPropertyChange
    }//GEN-LAST:event_zoomSliderPropertyChange

    private void zoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomSliderStateChanged
        double scaleValue = Math.sqrt(chpp.getClusterSets()[0].getDataset().size());
        double val = zoomSlider.getValue();
        if (val > 0) {
            val /= scaleValue;
            lblZoom.setText("x" + ((val) + 1));
            chpp.setXScale(((val) + 1));
        } else {
            val /= scaleValue;
            lblZoom.setText("1:" + (1.0 - val));
            chpp.setXScale(1.0 / (1.0 - val));
        }
        if (val == 0) {
            lblZoom.setText("1:1");
            chpp.setXScale(1.0);
        }
    }//GEN-LAST:event_zoomSliderStateChanged

    public void setClusterSets(ClusterSet[] css) {
        if (css.length < 1) {
            return;
        }
        try {
            chpp = new ClusterDendrogramPlot();
            javax.swing.GroupLayout chppLayout = new javax.swing.GroupLayout(chpp);
            chpp.setLayout(chppLayout);
            chppLayout.setHorizontalGroup(
                    chppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 398, Short.MAX_VALUE));
            chppLayout.setVerticalGroup(
                    chppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 267, Short.MAX_VALUE));

            chpp.setClusterSets(css);

            jScrollPane1.setViewportView(chpp);
            zoomSliderStateChanged(null);
        } catch (SQLException ex) {
            logger.showException(ex);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private vortex.gui2.clusterdendrogram.ClusterDendrogramPlot chpp;
    private samusik.glasscmp.GlassButton cmdGS1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblZoom;
    private javax.swing.JSlider zoomSlider;
    // End of variables declaration//GEN-END:variables
}
