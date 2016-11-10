/*
 * frmPCA.java
 *
 * Created on October 16, 2008, 3:28 PM
 */
package vortex.gui2;

import clustering.ClusterSet;
import clustering.Cluster;
import clustering.ClusterMember;
import clustering.Dataset;
import clustering.Datapoint;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.data.statistics.Statistics;
import org.math.plot.PlotPanel;
import org.math.plot.canvas.PlotCanvas;
import org.math.plot.components.LegendPanel;
import org.math.plot.plots.Plot;
//import tsne.tSNE3;
import samusik.glasscmp.GlassBorder;
import samusik.glasscmp.GlassFrame;
import clustering.AngularDistance;
import clustering.DistanceMeasure;
import clustering.EuclideanDistance;
import util.ColorPalette;
import util.MatrixOp;
import vortex.util.ProfilePCA;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmPCA extends GlassFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Creates new form frmPCA
     */
    public frmPCA(boolean labelsOn) {
        initComponents();
        setBounds(200, 200, 800, 600);
        LABELS_ON = labelsOn;
    }

    public frmPCA(ClusterSet clusterSet, boolean labelsOn) throws SQLException {
        initComponents();
        setBounds(200, 200, 800, 600);
        setClusterSet(clusterSet.getClusters());
        LABELS_ON = labelsOn;
    }

    public frmPCA(Cluster[] clusterSet, boolean labelsOn) throws SQLException {
        initComponents();
        setBounds(200, 200, 800, 600);
        setClusterSet(clusterSet);
        LABELS_ON = labelsOn;
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

        pan = new javax.swing.JPanel();
        plotpanel = new org.math.plot.Plot3DPanel();

        setBackground(new java.awt.Color(255, 255, 255));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        pan.setBackground(new Color(255,255,255,0));
        pan.setBorder(new GlassBorder());
        pan.setOpaque(false);
        pan.setLayout(new java.awt.GridBagLayout());

        plotpanel.setBackground(new java.awt.Color(0,0,0,0));
        plotpanel.setLegendOrientation("horizontal");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pan.add(plotpanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(pan, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private boolean LABELS_ON = false;
    private boolean UNITY_LEN = false;

    public final void setClusterSet(Cluster[] clusters) throws SQLException {
        if (clusters == null) {
            return;
        }

        if (clusters.length == 0) {
            return;
        }

        Dataset ds = clusters[0].getClusterSet().getDataset();

        setTitle("3D PCA Plot");

        plotpanel.removeAllPlots();

        Dataset d = clusters[0].getClusterSet().getDataset();
        int len = 0;

        @SuppressWarnings("unchecked")
        ArrayList<ClusterMember>[] filteredCm = new ArrayList[clusters.length];

        for (int i = 0; i < filteredCm.length; i++) {
            filteredCm[i] = new ArrayList<>();
            filteredCm[i].addAll(Arrays.asList(clusters[i].getClusterMembers()));
            len += filteredCm[i].size();
        }
        String[] labels = new String[len];

        ArrayList<Datapoint> datapoints = new ArrayList<>();
        int dim = d.getDimension();
        double[][] data = new double[dim][len];
        double[][] vectors = new double[len][];
        int k = 0;
        for (ArrayList<ClusterMember> al : filteredCm) {
            for (ClusterMember cm : al) {
                datapoints.add(cm.getDatapoint());
                double[] vec = ((UNITY_LEN) ? cm.getDatapoint().getUnityLengthVector() : cm.getDatapoint().getVector());
                vectors[k] = vec;
                labels[k] = cm.getDatapoint().getFullName();
                for (int j = 0; j < dim; j++) {
                    data[j][k] = vec[j];
                }
                k++;
            }
        }

        //DenseDoubleMatrix1D[] components = ProfilePCA.getPrincipalComponents(datapoints.toArray(new NDatapoint[datapoints.size()]), UNITY_LEN);
        DenseDoubleMatrix1D[] components = ProfilePCA.getPrincipalComponents(datapoints.toArray(new Datapoint[datapoints.size()]), UNITY_LEN);

        if (components == null) {
            return;
        }

        DistanceMeasure dm = UNITY_LEN ? new AngularDistance() : new EuclideanDistance();
        dm.init(ds);

        DenseDoubleMatrix2D threefirstcomp = new DenseDoubleMatrix2D(new double[][]{MatrixOp.toUnityLen(components[0].toArray()), MatrixOp.toUnityLen(components[1].toArray()), MatrixOp.toUnityLen(components[2].toArray())});

        DenseDoubleMatrix2D dataset = new DenseDoubleMatrix2D(data);

        DenseDoubleMatrix2D transformedDataset = (DenseDoubleMatrix2D) Algebra.DEFAULT.mult(threefirstcomp, dataset);
/*
        double[][] probMtx = new double[len][len];

        for (int i = 0; i < probMtx.length; i++) {
            for (int j = 0; j < probMtx.length; j++) {
                probMtx[i][j] = MatrixOp.getEuclideanCosine(vectors[i], vectors[j]);
            }
        }
        logger.print("len:" + len);*/

        double[][] xyarr =  Algebra.DEFAULT.transpose(transformedDataset).toArray();//tSNE3.tsne_p(probMtx, 3);//



        Double[] fpc = new Double[xyarr.length];

        for (int i = 0; i < fpc.length; i++) {
            fpc[i] = xyarr[i][0];
        }

        ColorPalette cp = ColorPalette.NEUTRAL_PALETTE;

        logger.print("First principal component SD: " + Statistics.getStdDev(fpc));
        plotpanel.setFont(plotpanel.getFont().deriveFont(Font.BOLD, 12));
        plotpanel.plotCanvas.setFont(plotpanel.getFont().deriveFont(Font.BOLD, 12));

        /*
         logger.print("EDGE BTW PAX6 and SOX2", graph.containsEdge(d.getDatapointByProfileID("PAX6"), d.getDatapointByProfileID("SOX2")));
         logger.print("EDGE BTW SOX2 and PAX6", graph.containsEdge(d.getDatapointByProfileID("SOX2"), d.getDatapointByProfileID("PAX6")));*/

        HashMap<Datapoint, Boolean> labelled = new HashMap<Datapoint, Boolean>();

        for (int i = 0; i < datapoints.size(); i++) {
            if (LABELS_ON) {
                labelled.put(datapoints.get(i), LABELS_ON);
            }
        }


        int cnt = 0;
        k = 0;

        double labelshift = 0.00;
        HashMap<Datapoint, double[]> hmProjections = new HashMap<>();



        for (ArrayList<ClusterMember> al : filteredCm) {
            Color col = cp.getColor(cnt++);
            Color col2 = new Color(col.getRed(), col.getGreen(), col.getBlue(), 100);
            double[][] curr = new double[al.size()][3];
            for (int i = 0; i < curr.length; i++) {

                curr[i][0] = xyarr[k][0];//* (ord / veclen) * 5;
                curr[i][1] = xyarr[k][1];//* (ord / veclen) * 5;
                curr[i][2] = xyarr[k][2];
                Datapoint dp1 = datapoints.get(k);
                //logger.print(dp1.getProfileID(), curr[i][0],curr[i][1],curr[i][2]);
                hmProjections.put(dp1, curr[i]);
                if (labelled.get(dp1) != null) {
                    double[] coord = new double[]{curr[i][0], curr[i][1], curr[i][2] + labelshift};
                    //plotpanel.addLabel(dp1.getProfileID(), Color.WHITE, coord);
                    plotpanel.addLabel(dp1.getFullName(), new Color(0, 0, 0, 50), coord);
                }
                /*
                 if (LABELS_ON || (currDP.getProfileID().equals("KIAA1324") || currDP.getProfileID().equals("EOMES") || currDP.getProfileID().equals("SOX2") || currDP.getProfileID().equals("PAX6"))) {
                 plotpanel.addLabel(currDP.getProfileID(), (currDP.getProfileID().equals("KIAA1324") || currDP.getProfileID().equals("EOMES") || currDP.getProfileID().equals("SOX2") || currDP.getProfileID().equals("PAX6")) ? new Color(0, 127, 0, 255) : new Color(50, 50, 50, 127), curr[i]);
                 }*/
                k++;
            }
            /*
             if (al.get(0).getCluster().toString().contains("MOCK")) {
             col = new Color(100, 100, 100, 127);
             cnt--;
             }*/
            if (curr.length > 0) {
                plotpanel.addScatterPlot(al.get(0).getCluster().toString(), col2, curr);
            } else {
                cnt--;
            }
        }



        //plotpanel.removeLegend();

        plotpanel.removeLegend();

        plotpanel.plotLegend.getParent();

        plotpanel.remove(plotpanel.plotLegend);
        plotpanel.removeLegend();

        plotpanel.plotLegend = new MyLegendPanel(plotpanel, 0);

        plotpanel.add(plotpanel.plotLegend, BorderLayout.EAST);


        /*
         for (int i = 0; i < datapoints.size(); i++) {
         NDatapoint dp1 = datapoints.get(i);
         for (NDatapoint dp2 : Prefetch.getInstance().getDatapointsFormingVT(ds, dp1, dm)) {
         if(hmProjections.containsKey(dp2)){ 
         double cos = MatrixOp.getEuclideanCosine(dp1.getVector(), dp2.getVector());
         if(cos > 0.9 ){
         plotpanel.addLinePlot("***", new Color(40, 80, 40, 50), new double[][]{hmProjections.get(dp1), hmProjections.get(dp2)});
         }                 
         }       
         }
         }         
         */

        //ADDING COLORED LINES TO REPRESENT THE DISTANCE
        /*
         for (ArrayList<ClusterMember> al : filteredCm) {
             
         ClusterMember mode = new Optimization<ClusterMember>(){
         @Override
         public double scoringFunction(ClusterMember arg) {
         return arg.getSimilarityToMode();
         }
         }.getArgMax(al).getKey();
             
         NDatapoint dp1 = mode.getDatapoint();
         for (ArrayList<ClusterMember> al2 : filteredCm) {
         for (ClusterMember cm : al2) {
         NDatapoint dp2 = cm.getDatapoint();
         double cos = Math.max(0,MatrixOp.getEuclideanCosine(dp1.getVector(), dp2.getVector()));
                 
         Color col = Color.getHSBColor((float)(Math.acos(cos)/(Math.PI/2)), 1, 1);
                 
         plotpanel.addLinePlot("***", new Color(col.getRed(), col.getGreen(), col.getBlue(), 100), new double[][]{hmProjections.get(dp1), hmProjections.get(dp2)});
         }
         }
             
             
         }*/




        //plotpanel.plotCanvas.getPlot(0).addGaussQuantiles(2, 5);
        // plotpanel.setLegendOrientation("SOUTH");
        ///ADDITIONAL SERIES displaying density estimate;
/*
         double[] dens = new double[cs.getDataset().getDatapoints().length];
        
         double K = 10;
        
         double maxKernelWeight = 0;
         double minKernelWeight = Double.MAX_VALUE;
        
         for(int x = 0; x < datapoints.length; x++){
         double kernelWeight = Math.exp(K);
         for (int y = 0; y < datapoints.length; y++) {
         if (y == x) {
         continue;
         }
         kernelWeight += Math.exp(K * ms.getEuclideanCosine(datapoints[x].getVector(), datapoints[y].getVector()));
         }
         dens[x] = Math.log(kernelWeight);
         maxKernelWeight = Math.max(maxKernelWeight, dens[x]);
         minKernelWeight = Math.min(minKernelWeight, dens[x]);
         }
        
         double [][] densarr = new double[xyarr.length][xyarr[0].length];
        
         for(int i = 0; i < len; i++){
         for(int j = 0; j < xyarr[0].length;j++){
         double veclen = ms.getEuclideanLength(new DenseDoubleMatrix1D(xyarr[i]));
         densarr[i][j] = xyarr[i][j]  * ((((dens[i] - minKernelWeight)/ ((maxKernelWeight - minKernelWeight)*3)) +1.0)/veclen) * 5.7;
         }
         }
         double [][] curr = Algebra.DEFAULT.transpose(new DenseDoubleMatrix2D(densarr)).toArray();
        
         //logger.print(curr.length);
         if(curr.length == 2) ds.addSeries("density", curr);
         */
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pan;
    private org.math.plot.Plot3DPanel plotpanel;
    // End of variables declaration//GEN-END:variables

    /**
     * BSD License
     *
     * @author Yann RICHET
     */
    private class MyLegendPanel extends LegendPanel implements ComponentListener {

        private static final long serialVersionUID = 1L;
        PlotCanvas plotCanvas;
        PlotPanel plotPanel;
        LinkedList<Legend> legends;
        public final static int INVISIBLE = -1;
        public final static int VERTICAL = 0;
        public final static int HORIZONTAL = 1;
        int orientation;
        private int maxHeight;
        private int maxWidth;
        JPanel container;
        private int inset = 5;

        public MyLegendPanel(PlotPanel _plotPanel, int _orientation) {
            super(_plotPanel, _orientation);
            this.removeAll();
            plotPanel = _plotPanel;
            plotCanvas = plotPanel.plotCanvas;
            plotCanvas.attachLegend(MyLegendPanel.this);
            orientation = _orientation;

            container = new JPanel();
            container.setBackground(plotCanvas.getBackground());
            container.setLayout(new GridLayout(1, 1, inset, inset));
            //container.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK, 1),null));

            updateLegends();

            setBackground(plotCanvas.getBackground());
            addComponentListener(this);
            setLayout(new GridBagLayout());

            add(container);
        }

        public void updateLegends() {
            if (container == null) {
                return;
            }
            if (orientation != INVISIBLE) {
                container.removeAll();

                maxHeight = 1;
                maxWidth = 1;

                legends = new LinkedList<Legend>();
                for (Plot plot : plotCanvas.getPlots()) {
                    if (plot.getName().contains("***")) {
                        continue;
                    }
                    Legend l = new Legend(plot);
                    legends.add(l);

                    maxWidth = (int) Math.max(maxWidth, l.getPreferredSize().getWidth());
                    maxHeight = (int) Math.max(maxHeight, l.getPreferredSize().getHeight());

                    container.add(l);

                }

                updateSize();
                //repaint();
            }
        }

        private void updateSize() {
            //System.out.println("LegendPanel.updateSize");
            if (orientation == VERTICAL) {
                int nh = 1;
                if (maxHeight < plotCanvas.getHeight()) {
                    nh = plotCanvas.getHeight() / (maxHeight + inset);
                }
                int nw = 1 + legends.size() / nh;

                ((GridLayout) (container.getLayout())).setColumns(nw);
                ((GridLayout) (container.getLayout())).setRows(1 + legends.size() / nw);

                container.setPreferredSize(new Dimension((maxWidth + inset) * nw, (maxHeight + inset) * (1 + legends.size() / nw)));

            } else if (orientation == HORIZONTAL) {
                int nw = 1;
                if (maxWidth < plotCanvas.getWidth()) {
                    nw = plotCanvas.getWidth() / (maxWidth + inset);
                }
                int nh = 1 + legends.size() / nw;

                ((GridLayout) (container.getLayout())).setRows(nh);
                ((GridLayout) (container.getLayout())).setColumns(1 + legends.size() / nh);

                container.setPreferredSize(new Dimension((maxWidth + inset) * (1 + legends.size() / nh), (maxHeight + inset) * nh));
            }
            container.updateUI();
        }

        public void note(int i) {
            if (i < legends.size()) {
                if (orientation != INVISIBLE) {
                    legends.get(i).setBackground(PlotCanvas.NOTE_COLOR);
                    legends.get(i).name.setForeground(plotPanel.getBackground());
                }
            }
        }

        public void nonote(int i) {
            if (i < legends.size()) {
                if (orientation != INVISIBLE) {
                    legends.get(i).setBackground(plotPanel.getBackground());
                    legends.get(i).name.setForeground(PlotCanvas.NOTE_COLOR);
                }
            }
        }

        public void componentResized(ComponentEvent e) {
            //System.out.println("LegendPanel.componentResized");
            //System.out.println("PlotCanvas : "+plotCanvas.panelSize[0]+" x "+plotCanvas.panelSize[1]);
            if (orientation != INVISIBLE) {
                updateSize();
            }
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
        }

        public void componentHidden(ComponentEvent e) {
        }

        public class Legend extends JPanel {

            private static final long serialVersionUID = 1L;
            JPanel color;
            JLabel name;
            Plot plot;

            public Legend(Plot p) {
                plot = p;

                setLayout(new BorderLayout(2, 2));

                color = new JPanel();
                name = new JLabel();
                name.setFont(plotPanel.getFont());

                setBackground(Color.WHITE);

                update();

                add(color, BorderLayout.WEST);
                add(name, BorderLayout.CENTER);

                name.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
                            if (plotCanvas.allowEdit && e.getClickCount() > 1) {
                                editText();
                            }
                        }
                        if (plotCanvas.allowNote && e.getClickCount() <= 1) {
                            note_nonote();
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                });

                color.addMouseListener(new MouseListener() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getModifiers() == MouseEvent.BUTTON1_MASK) {
                            if (plotCanvas.allowEdit && e.getClickCount() > 1) {
                                editColor();
                            }
                        }
                        if (plotCanvas.allowNote && e.getClickCount() <= 1) {
                            note_nonote();
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                    }

                    public void mouseReleased(MouseEvent e) {
                    }

                    public void mouseEntered(MouseEvent e) {
                    }

                    public void mouseExited(MouseEvent e) {
                    }
                });
            }

            public void editText() {
                String name1 = JOptionPane.showInputDialog(plotCanvas, "Choose name", plot.getName());
                if (name1 != null) {
                    plot.setName(name1);
                    update();
                    updateLegends();
                }
            }

            public void editColor() {
                Color c = JColorChooser.showDialog(plotCanvas, "Choose plot color", plot.getColor());
                if (c != null) {
                    plot.setColor(c);
                    update();
                    plotCanvas.repaint();
                }
            }

            public void update() {
                int size = name.getFont().getSize();
                color.setSize(new Dimension(size, size));
                color.setPreferredSize(new Dimension(size, size));

                // TODO change legend when plot is invisible
			/*if (!plot.visible)
                 color.setBackground(Color.LIGHT_GRAY);
                 else*/
                color.setBackground(plot.getColor());

                /*if (!plot.visible) {
                 name.setFont(name.getFont().deriveFont(Font.ITALIC));
                 name.setForeground(Color.LIGHT_GRAY);
                 }*/
                name.setText(plot.getName());
                repaint();
            }

            public void note_nonote() {
                plot.noted = !plot.noted;
                plotCanvas.repaint();
            }
        }
    }
}