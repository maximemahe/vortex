/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vortex.gui2;

import java.awt.Color;
import java.sql.SQLException;
import javax.swing.DefaultComboBoxModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import samusik.glasscmp.GlassBorder;
import samusik.glasscmp.GlassFrame;
import clustering.Cluster;
import clustering.Dataset;
import util.ColorPalette;
import util.MatrixOp;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmLDA extends GlassFrame {

    /**
     * Creates new form frm2DScatter
     */
    private JFreeChart chart;
    private DefaultXYDataset graphDS;
    private ChartPanel chartPane;
    private Dataset nd;
    private Cluster[] clusters;

    /**
     * Creates new form frmProfilePlot
     */
    public frmLDA(Cluster[] clusters) {
        super();
        initComponents();
        this.nd = nd;
        graphDS = new DefaultXYDataset();

        chart = ChartFactory.createScatterPlot(
                "Profiles", // chart title
                "Parameters", // domain axis label
                "Value", // range axis label
                graphDS, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );
        chartPane = new ChartPanel(chart, true, true, true, true, true);
        chart.setBackgroundPaint(new Color(255, 255, 255));
        //chart.setBackgroundImage(null);
        chartPane.setOpaque(true);
        chartPane.setBorder(new GlassBorder());
        chartPane.setBackground(new Color(0, 0, 0, 0));
        chartPane.setInitialDelay(10);
        jPanel1.add(chartPane);
        cmbX.setModel(new DefaultComboBoxModel(clusters[0].getClusterSet().getDataset().getFeatureNamesCombined()));
        cmbY.setModel(new DefaultComboBoxModel(clusters[0].getClusterSet().getDataset().getFeatureNamesCombined()));
        this.clusters = clusters;
    }

    private void populateChart(int x, int y) {
        //ArrayList<String> al = new ArrayList<>();
        for (int i = 0; i < clusters.length; i++) {
            graphDS.removeSeries(clusters[i].toString());
        }
        try {
            int r = 0;
            for (Cluster c : clusters) {
                double[][] data = new double[2][c.size()];
                for (int i = 0; i < c.size(); i++) {
                    double[] vec = MatrixOp.concat(c.getClusterMembers()[i].getDatapoint().getVector(), c.getClusterMembers()[i].getDatapoint().getSideVector());
                    data[0][i] = vec[x];
                    data[1][i] = vec[y];
                }
                graphDS.addSeries(c.toString(), data);
                Color col = ColorPalette.NEUTRAL_PALETTE.getColor(r);
                chart.getXYPlot().getRenderer().setSeriesPaint(r, new Color(col.getRed(), col.getGreen(), col.getBlue(), 128));
                r++;
            }
        } catch (SQLException e) {
            logger.showException(e);
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

        cmbY = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        cmbX = new javax.swing.JComboBox();
        jToggleButton1 = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        cmbY.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbY.setMaximumSize(new java.awt.Dimension(250, 30));
        cmbY.setMinimumSize(new java.awt.Dimension(100, 30));
        cmbY.setName(""); // NOI18N
        cmbY.setPreferredSize(new java.awt.Dimension(120, 30));
        cmbY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbYActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        getContentPane().add(cmbY, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 51)));
        jPanel1.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        cmbX.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbX.setMinimumSize(new java.awt.Dimension(100, 30));
        cmbX.setName(""); // NOI18N
        cmbX.setPreferredSize(new java.awt.Dimension(120, 30));
        cmbX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbXActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(cmbX, gridBagConstraints);

        jToggleButton1.setText("Colors On");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jToggleButton1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbYActionPerformed
        populateChart(cmbX.getSelectedIndex(), cmbY.getSelectedIndex());
    }//GEN-LAST:event_cmbYActionPerformed
    private boolean colorsOn = true;

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        colorsOn = !colorsOn;
        jToggleButton1.setText(colorsOn ? "Colors On" : "Colors Off");

        for (int r = 0; r < clusters.length; r++) {
            Color col = colorsOn ? ColorPalette.NEUTRAL_PALETTE.getColor(r) : Color.GRAY;
            chart.getXYPlot().getRenderer().setSeriesPaint(r, new Color(col.getRed(), col.getGreen(), col.getBlue(), 128));
        }

    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void cmbXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbXActionPerformed
        populateChart(cmbX.getSelectedIndex(), cmbY.getSelectedIndex());
    }//GEN-LAST:event_cmbXActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cmbX;
    private javax.swing.JComboBox cmbY;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
