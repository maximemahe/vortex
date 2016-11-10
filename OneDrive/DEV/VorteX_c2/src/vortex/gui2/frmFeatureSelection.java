/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vortex.gui2;

import executionslave.ReusableObject;
import executionslave.ReusingTask;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import samusik.glasscmp.GlassBorder;
import umontreal.iro.lecuyer.probdist.EmpiricalDist;
import clustering.Datapoint;
import clustering.Dataset;
import util.ColorPalette;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmFeatureSelection extends javax.swing.JFrame {

    private JFreeChart chart;
    private DefaultCategoryDataset graphDS;
    private ChartPanel chartPane;
    private Dataset ds;
    private ColorPalette cp = ColorPalette.NEUTRAL_PALETTE;
    ArrayList<SwingWorker> workers;
    String profileDelimiter;

    public frmFeatureSelection(Dataset ds) {
        initComponents();
        pb.setVisible(false);
        graphDS = new DefaultCategoryDataset();
        workers = new ArrayList<>();
        this.ds = ds;
        chart = ChartFactory.createLineChart(
                "Distribution of correlation between profile couples", // chart title
                "Correlation", // domain axis label
                "Normalized frequency", // range axis label
                graphDS, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
                );
        chartPane = new ChartPanel(chart, true, true, true, true, true) {
            @Override
            public void paintComponent(Graphics g) {
                //g.setClip(this.getBorder().);
                super.paintComponent(g);
            }
        };
        chart.setBackgroundPaint(new Color(255, 255, 255));
        chartPane.setBackground(new Color(255, 255, 255));
        chartPane.setOpaque(true);
        chartPane.setBorder(new GlassBorder());
        chartPane.setInitialDelay(10);
        jSplitPane1.setRightComponent(chartPane);
        chart.getCategoryPlot().getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
        chart.getCategoryPlot().getDomainAxis().setMaximumCategoryLabelWidthRatio(0.5f);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        cmdRun = new samusik.glasscmp.GlassButton();
        jLabel1 = new javax.swing.JLabel();
        txtDel = new samusik.glasscmp.GlassEdit();
        jLabel2 = new javax.swing.JLabel();
        pb = new javax.swing.JProgressBar();
        txtControl = new samusik.glasscmp.GlassEdit();
        jLabel3 = new javax.swing.JLabel();
        chkWeight = new javax.swing.JCheckBox();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Feature #", "Feature name", "Corr in Similar Pairs", "Corr in Random Pairs", "zSimilar-zTotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setDragEnabled(true);
        jScrollPane1.setViewportView(jTable1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jSplitPane1, gridBagConstraints);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        cmdRun.setText("Run Feature Selection");
        cmdRun.setMaximumSize(new java.awt.Dimension(95, 26));
        cmdRun.setMinimumSize(new java.awt.Dimension(95, 26));
        cmdRun.setPreferredSize(new java.awt.Dimension(95, 26));
        cmdRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdRunActionPerformed(evt);
            }
        });

        jLabel1.setText("<html>If your dataset contains repeated treatmets with the same perturbagen, e.g. several siRNA targeting the same gene or repeated treatments with the same siRNA (drug), this feature selection tool will help you find the features, removal of which will improve the correlation between repeat, and decrease the correlation between the random profile pairs in the dataset.<br><br>For this, ProfileIDs have to be named according to a certain convention: First part specifies the name of the treatment followed by a  delimiter and then by the id of the repeat. For instance, different siRNA against the same gene could be named \"ABC1:SI0012\", \"ABC1:SI0013\" etc.<br>If the dataset contains repeated negative control profiles, you can specify the corresponding label prefix, and those measurements will be removed from consideration<br>Select the 'weighting' check box if you wish that the parameter correlations will be weighted against the weak measurements that are likely to come from the negative control.");
        jLabel1.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel1.setMinimumSize(new java.awt.Dimension(200, 90));
        jLabel1.setPreferredSize(new java.awt.Dimension(500, 300));

        txtDel.setText(":");
        txtDel.setMinimumSize(new java.awt.Dimension(56, 26));
        txtDel.setPreferredSize(new java.awt.Dimension(56, 26));
        txtDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDelActionPerformed(evt);
            }
        });

        jLabel2.setText("Specify the delimiter:");

        pb.setIndeterminate(true);

        txtControl.setText("MOCK");
        txtControl.setMinimumSize(new java.awt.Dimension(56, 26));
        txtControl.setPreferredSize(new java.awt.Dimension(56, 26));

        jLabel3.setText("Negative control prefix:");

        chkWeight.setText("weighting by the negative control probability");
        chkWeight.setOpaque(false);
        chkWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkWeightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(txtDel, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtControl, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(chkWeight)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmdRun, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtDel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(chkWeight)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(pb, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdRun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmdRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdRunActionPerformed
        if (!workers.isEmpty()) {
            for (int i = 0; i < workers.size(); i++) {
                workers.get(i).cancel(true);
            }

            workers.clear();
            setRunningState(false);
        } else {
            profileDelimiter = txtDel.getText().trim();
            final HashMap<String, ArrayList<Datapoint>> hm = new HashMap<>();
            Datapoint[] nd = ds.getDatapoints();

            ArrayList<Datapoint> negControlList = new ArrayList<>();
            String negCtrPrefix = txtControl.getText().trim();

            final double[][] sortedNegCtr;

            for (Datapoint dp : nd) {
                if (!negCtrPrefix.isEmpty() && dp.getFullName().startsWith(negCtrPrefix)) {
                    if (chkWeight.isSelected()) {
                        negControlList.add(dp);
                    }
                    continue;
                }
                if (dp.getFullName().contains(profileDelimiter)) {
                    String id = dp.getFullName().split(profileDelimiter)[0];
                    if (hm.get(id) == null) {
                        hm.put(id, new ArrayList<Datapoint>());
                    }
                    hm.get(id).add(dp);
                }
            }

            if (negControlList.size() > 50) {
                sortedNegCtr = new double[ds.getDimension()][negControlList.size()];
                for (int i = 0; i < negControlList.size(); i++) {
                    for (int j = 0; j < ds.getDimension(); j++) {
                        sortedNegCtr[j][i] = negControlList.get(i).getVector()[j];
                    }
                }
                for (int i = 0; i < sortedNegCtr.length; i++) {
                    Arrays.sort(sortedNegCtr[i]);
                }
            } else {
                sortedNegCtr = null;
                if (!negCtrPrefix.isEmpty() && chkWeight.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Insufficient number of negative control repeats (" + negControlList.size() + "). At least 50 are required.\nUnweighted correlation will be computed.");
                }
            }

            int count = 0;
            for (ArrayList<Datapoint> dp : hm.values()) {
                int i = dp.size();
                count += (i * (i - 1)) / 2;
            }

            for (int i = 0; i < ds.getFeatureNames().length; i++) {
                workers.add(new FeatureReproducibilityWorker(ds, hm, count, (DefaultTableModel) jTable1.getModel(), i, (sortedNegCtr == null) ? null : sortedNegCtr[i], negCtrPrefix));
            }

            TableRowSorter<TableModel> trs = new TableRowSorter<TableModel>(jTable1.getModel()) {
                @Override
                public void rowsInserted(int firstRow, int endRow) {
                    allRowsChanged();
                }
            };

            while (jTable1.getModel().getRowCount() > 0) {
                ((DefaultTableModel) jTable1.getModel()).removeRow(0);
            }

            jTable1.setRowSorter(trs);

            jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    tableSelectionChanged();
                }
            });
            setRunningState(true);
            for (SwingWorker worker : workers) {
                worker.execute();
            }

            for (SwingWorker worker : workers) {
                worker.execute();
            }


        }
    }//GEN-LAST:event_cmdRunActionPerformed

    private void txtDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDelActionPerformed

    private void chkWeightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkWeightActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkWeightActionPerformed

    private void tableSelectionChanged() {

        if (true) {
            return;
        }

        graphDS.clear();
        DefaultTableModel tm = ((DefaultTableModel) jTable1.getModel());
        int idx = 0;
        for (int i : jTable1.getSelectedRows()) {
            int row = jTable1.convertRowIndexToModel(i);
            KSDistributionStatistics ksStat = (KSDistributionStatistics) tm.getValueAt(row, 3);

            String paramName = (String) tm.getValueAt(row, 1);
            Color pairs = cp.getColor(idx++);
            Color bg = pairs.darker().darker();
            for (double d = 0; d < 0.2; d += 0.01) {
                graphDS.addValue((Number) ksStat.distBg.cdf(d), tm.getValueAt(row, 0) + " Rnd", d);
            }
            chart.getCategoryPlot().getRenderer().setSeriesPaint(graphDS.getRowCount() - 1, bg);

            for (double d = 0; d < 0.2; d += 0.01) {
                graphDS.addValue((Number) ksStat.distPairs.cdf(d), tm.getValueAt(row, 0) + " Sim", d);
            }
            chart.getCategoryPlot().getRenderer().setSeriesPaint(graphDS.getRowCount() - 1, pairs);
        }
    }

    private void setRunningState(boolean state) {
        if (state) {
            cmdRun.setText("Cancel");
            pb.setVisible(true);
        } else {
            cmdRun.setText("Run Feature Selection");
            pb.setVisible(false);
        }
    }

    private class KSDistributionStatistics {

        EmpiricalDist distBg;
        EmpiricalDist distPairs;
        double KSpValue;

        public KSDistributionStatistics(double KSpValue, EmpiricalDist distBg, EmpiricalDist distPairs) {
            this.KSpValue = KSpValue;
            this.distBg = distBg;
            this.distPairs = distPairs;
        }

        public EmpiricalDist getDistBg() {
            return distBg;
        }

        public EmpiricalDist getDistPairs() {
            return distPairs;
        }

        @Override
        public String toString() {
            return String.valueOf(-Math.log(KSpValue));
        }
    }

    private class FeatureReproducibilityWorker extends SwingWorker<Double, Object[]> {

        private final int count;
        private DefaultTableModel tm;
        private HashMap<String, ArrayList<Datapoint>> hm;
        private Dataset nd;
        private int featureToTest;
        private double[] sortedNegCtr;
        private String negCtrPrefix;
        // private static final int SAMPLING_COEFF = 100;

        @Override
        protected Double doInBackground() throws Exception {

            logger.print("PreselectWorker#" + featureToTest + " has started");

            double dotProdPairs = 0;
            double sumSq1Pairs = 0;
            double sumSq2Pairs = 0;
            double numPairs = 0;

            Datapoint[] dpArray = nd.getDatapoints();

            double negCtrSize = (double) ((sortedNegCtr == null) ? 0 : sortedNegCtr.length);

            for (ArrayList<Datapoint> dp : hm.values()) {
                for (int i = 0; i < dp.size(); i++) {
                    if (isCancelled()) {
                        return null;
                    }
                    double vec1Val = dp.get(i).getVector()[featureToTest];
                    if (sortedNegCtr != null) {
                        double weight1 = Math.abs(Arrays.binarySearch(sortedNegCtr, vec1Val)) / negCtrSize;
                        weight1 = Math.max(weight1, 1.0 - weight1) - 0.5;
                        vec1Val *= weight1;
                    }
                    for (int j = i + 1; j < dp.size(); j++) {
                        double vec2Val = dp.get(j).getVector()[featureToTest];
                        if (sortedNegCtr != null) {
                            double weight2 = Math.abs(Arrays.binarySearch(sortedNegCtr, vec2Val)) / negCtrSize;
                            weight2 = Math.max(weight2, 1.0 - weight2) - 0.5;
                            vec2Val *= weight2;
                        }
                        dotProdPairs += vec1Val * vec2Val;
                        sumSq1Pairs += Math.pow(vec1Val, 2);
                        sumSq2Pairs += Math.pow(vec2Val, 2);
                        numPairs++;
                        if (((int) numPairs) % 1000 == 0) {
                            logger.print("Worker#" + featureToTest + " PAIRS progress: " + Math.floor((numPairs / (double) count) * 100) + "%");
                        }
                    }
                }
            }
            double rPairs = dotProdPairs / Math.sqrt(sumSq1Pairs * sumSq2Pairs);
            double zPairs = Math.log((1 + rPairs) / (1 - rPairs)) / 2;

            double dotProdTotal = 0;
            double sumSq1Total = 0;
            double sumSq2Total = 0;
            double numTotal = 0;

            try {
                for (int i = 0; i < dpArray.length; i++) {
                    if (isCancelled()) {
                        return null;
                    }
                    if (!dpArray[i].getFullName().contains(profileDelimiter)) {
                        continue;
                    }
                    if (!negCtrPrefix.isEmpty() && dpArray[i].getFullName().startsWith(negCtrPrefix)) {
                        continue;
                    }
                    String cid1 = dpArray[i].getFullName().split(profileDelimiter)[0];

                    double vecVal1 = dpArray[i].getVector()[featureToTest];

                    if (sortedNegCtr != null) {
                        double weight1 = Math.abs(Arrays.binarySearch(sortedNegCtr, vecVal1)) / negCtrSize;
                        weight1 = Math.max(weight1, 1.0 - weight1) - 0.5;
                        vecVal1 *= weight1;
                    }

                    for (int j = i + 1; j < dpArray.length; j++) {
                        Datapoint dp2 = dpArray[j];
                        if (!dpArray[i].getFullName().contains(profileDelimiter)) {
                            continue;
                        }
                        if (cid1.equals(dp2.getFullName().split(profileDelimiter)[0]) || (!negCtrPrefix.isEmpty() && dp2.getFullName().startsWith(negCtrPrefix))) {
                            continue;
                        }

                        double vec2Val = dpArray[j].getVector()[featureToTest];

                        if (sortedNegCtr != null) {
                            double weight2 = Math.abs(Arrays.binarySearch(sortedNegCtr, vec2Val)) / negCtrSize;
                            weight2 = Math.max(weight2, 1.0 - weight2) - 0.5;
                            vec2Val *= weight2;
                        }

                        dotProdTotal += vecVal1 * vec2Val;
                        sumSq1Total += Math.pow(vecVal1, 2);
                        sumSq2Total += Math.pow(vec2Val, 2);
                        numTotal++;
                        if (((int) numTotal) % dpArray.length == 0) {
                            logger.print("Worker#" + featureToTest + " TOTAL progress: " + Math.floor((numTotal / Math.pow(dpArray.length, 2) / 2) * 100) + "%");
                        }
                    }
                }
            } catch (Exception e) {
                logger.showException(e);
                e.printStackTrace();
            }

            double rTotal = dotProdTotal / Math.sqrt(sumSq1Total * sumSq2Total);
            double zTotal = Math.log((1 + rTotal) / (1 - rTotal)) / 2;


            double diff = (zPairs - zTotal); // / Math.sqrt((1 / numPairs) + (1 / numTotal));


            // double pval = StudentDist.cdf((int) numPairs, studT);

            Object[] obj = new Object[]{
                featureToTest,
                ((featureToTest >= 0) ? (nd.getFeatureNames()[featureToTest]) : "Full Feature set"),
                rPairs, rTotal, diff};
            workers.remove(this);
            if (workers.isEmpty()) {
                setRunningState(false);
            }
            publish(obj);
            return diff;
        }

        public FeatureReproducibilityWorker(Dataset nd, HashMap<String, ArrayList<Datapoint>> hm, int count, DefaultTableModel tm, int featureToTest, double[] sortedNegCtr, String negCtrPrefix) {
            this.count = count;
            this.tm = tm;
            this.hm = hm;
            this.nd = nd;
            this.featureToTest = featureToTest;
            this.sortedNegCtr = sortedNegCtr;
            this.negCtrPrefix = negCtrPrefix;
        }

        @Override
        protected void process(List<Object[]> chunks) {
            for (Object[] obj : chunks) {
                logger.print("Worker#" + featureToTest + " is processing");
                tm.addRow(obj);
            }
        }
    }

    private class FeatureSelectionWorker extends SwingWorker<Double, Object[]> {

        private final int count;
        private DefaultTableModel tm;
        private HashMap<String, ArrayList<Datapoint>> hm;
        private Dataset nd;
        private int featureToLeaveOut;
        private static final int SAMPLING_COEFF = 100;

        @Override
        protected Double doInBackground() throws Exception {
            if (featureToLeaveOut == -1) {
                logger.print("Worker#" + featureToLeaveOut + " has started");
            }

            double[] distPairs = new double[count];
            double[] distBg = new double[count * SAMPLING_COEFF];
            Datapoint[] dpArray = nd.getDatapoints();
            int idx = 0;
            for (ArrayList<Datapoint> dp : hm.values()) {
                for (int i = 0; i < dp.size(); i++) {

                    double[] vec1 = dp.get(i).getVector();
                    for (int j = i + 1; j < dp.size(); j++) {
                        distPairs[idx++] = getEuclideanCosOneOut(vec1, dp.get(j).getVector(), featureToLeaveOut);
                        if (idx % 1000 == 0) {
                            if (featureToLeaveOut == -1) {
                                logger.print("Worker#" + featureToLeaveOut + " SIM progress: " + Math.floor((idx / (double) count) * 100) + "%");
                            }
                        }
                    }
                }
            }
            Random r = new Random();
            int size = dpArray.length;
            for (int i = 0; i < count * SAMPLING_COEFF; i++) {

                try {
                    int x = r.nextInt(size - 1);
                    int y = r.nextInt(size - (x + 1));
                    distBg[i] = getEuclideanCosOneOut(dpArray[x].getVector(), dpArray[y].getVector(), featureToLeaveOut);
                    if (i % 1000 == 0) {
                        if (featureToLeaveOut == -1) {
                            logger.print("Worker#" + featureToLeaveOut + " BG progress: " + Math.floor((i / (double) count) * 100) + "%");
                        }
                    }
                } catch (Exception e) {
                    logger.showException(e);
                }
            }

            Arrays.sort(distBg);
            Arrays.sort(distPairs);

            EmpiricalDist dist = new EmpiricalDist(distBg);

            double maxDiff = 0;
            for (int i = 0; i < count; i++) {
                double d = distPairs[i];
                // double val1 = (i / (double) count);
                //double val2 = dist.cdf(d);
                maxDiff += (distPairs[i] - distBg[i * SAMPLING_COEFF]);
                if (i % 1000 == 0) {
                    if (featureToLeaveOut == -1) {
                        logger.print("Worker#" + featureToLeaveOut + " KS progress: " + Math.floor((i / (double) count) * 100) + "%");
                    }
                }
            }

            double pval = 0.5;//StudentDist.

            Object[] obj = new Object[]{
                featureToLeaveOut,
                ((featureToLeaveOut >= 0) ? (nd.getFeatureNames()[featureToLeaveOut]) : "Full Feature set"),
                maxDiff, new KSDistributionStatistics(pval, dist, new EmpiricalDist(distPairs))};
            workers.remove(this);
            if (workers.isEmpty()) {
                setRunningState(false);
            }
            if (featureToLeaveOut == -1) {
                logger.print("Worker#" + featureToLeaveOut + " ended");
            }
            publish(obj);
            return maxDiff;
        }

        private double getDotProductOneOut(double[] vec1, double[] vec2, int idxLeaveOut) {
            double sumprod = 0;
            for (int i = 0; i < vec1.length; i++) {
                if (i == idxLeaveOut) {
                    continue;
                }
                sumprod += vec1[i] * vec2[i];
            }
            return sumprod;
        }

        private synchronized double getEuclideanCosOneOut(double[] vec1, double[] vec2, int idxLeaveOut) {
            return getDotProductOneOut(vec1, vec2, idxLeaveOut)
                    / Math.sqrt(
                    getDotProductOneOut(vec1, vec1, idxLeaveOut)
                    * getDotProductOneOut(vec2, vec2, idxLeaveOut));
        }

        public FeatureSelectionWorker(Dataset nd, HashMap<String, ArrayList<Datapoint>> hm, int count, DefaultTableModel tm, int featureToLeaveOut) {
            this.count = count;
            this.tm = tm;
            this.hm = hm;
            this.nd = nd;
            this.featureToLeaveOut = featureToLeaveOut;
        }

        @Override
        protected void process(List<Object[]> chunks) {
            for (Object[] obj : chunks) {
                if (featureToLeaveOut == -1) {
                    logger.print("Worker#" + featureToLeaveOut + " is processing");
                }
                tm.addRow(obj);
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkWeight;
    private samusik.glasscmp.GlassButton cmdRun;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JProgressBar pb;
    private samusik.glasscmp.GlassEdit txtControl;
    private samusik.glasscmp.GlassEdit txtDel;
    // End of variables declaration//GEN-END:variables
}
