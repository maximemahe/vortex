/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vortex.clustergraph;

import com.itextpdf.text.Font;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.UndirectedGraph;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.preview.api.ManagedRenderer;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.preview.plugin.renderers.ArrowRenderer;
import org.gephi.preview.plugin.renderers.EdgeRenderer;
import org.gephi.preview.types.DependantColor;
import org.gephi.preview.types.DependantOriginalColor;
import org.gephi.preview.types.EdgeColor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;
import processing.core.PApplet;
import umontreal.iro.lecuyer.probdist.NormalDist;
import annotations.Annotation;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import vortex.gui.frmMain;
import clustering.Cluster;
import clustering.ClusterMember;
import util.HypergeometricDist;
import clustering.Datapoint;
import main.Dataset;
import util.DefaultEntry;
import util.IO;
import vortex.main.QuantileMap;
import util.MatrixOp;
import util.logger;

/**
 *
 * @author Nikolay
 */
public class frmFDL extends javax.swing.JFrame {

    private static final double COLOR_MAP_NOISE_THRESHOLD = 0.19;
    private final double[][] scalingBounds;
    private static final float EDGE_SCALING = 15.0f;
    private UndirectedGraph graph;
    private String[] paramList;
    private HashMap<Integer, Cluster> cid = new HashMap<>();
    private HashMap<Cluster, double[]> expVectors = new HashMap<>();
    private HashMap<Cluster, double[]> pValVectors = new HashMap<>();
    private PreviewController previewController;
    private ProcessingTarget target;
    private final Dataset ds;
    String currParam = "";
    String[] currAnnTerms = null;
    Annotation currAnn;
    private int fixedParamVal = -1;
    private final Map.Entry<String, Color>[] groupOptionList = new Map.Entry[]{
        new DefaultEntry<>("Assay", new Color(120, 194, 235)),
        new DefaultEntry<>("Control", Color.GREEN),
        new DefaultEntry<>("None", Color.GRAY)};
    private boolean mahalonobis = false;
    private boolean use_all_params = false;
    private double[][] annotationFrequency;
    private Cluster[] clusters;
    GroupingWorker groupingWorker;

    public double[][] getTermFrequencies(Cluster[] cl, Annotation ann) {
        try {
            String[] terms = ann.getTerms();
            double[][] vec = new double[cl.length][terms.length];
            for (int j = 0; j < terms.length; j++) {
                String term = terms[j];
                String[] pid = ann.getProfileIDsForTerm(term);
                Arrays.sort(pid);
                for (int i = 0; i < vec.length; i++) {
                    for (ClusterMember cm : cl[i].getClusterMembers()) {
                        if (Arrays.binarySearch(pid, cm.getDatapointName()) >= 0) {
                            vec[i][j]++;
                        }
                    }
                    vec[i][j] /= pid.length;
                }
            }
            return vec;
        } catch (SQLException e) {
            logger.showException(e);
            return null;
        }
    }

    private class MyEdge {

        public final Node n1;
        public final Node n2;
        public final float weight;

        public MyEdge(Node n1, Node n2, float weight) {
            this.n1 = n1;
            this.n2 = n2;
            this.weight = weight;
        }
    }
    QuantileMap qm, sqm;

    private void colorNodesByExpressionLevel(int paramIDX) {
        if (paramIDX < 0) {
            return;
        }
        NodeIterable ni = graph.getNodes();
        for (final Node n : ni.toArray()) {
            if (n.getAttributes().getValue("cluster") == null) {
                continue;
            }
            Cluster cl = cid.get((int) n.getAttributes().getValue("cluster"));
            if (expVectors.get(cl) == null) {
                expVectors.put(cl, MatrixOp.concat(MatrixOp.concat(cl.getMode().getVector(), cl.getMode().getSideVector()), new double[]{cl.size()}));
            }
            double[] vec = expVectors.get(cl);
            if (qm == null) {
                qm = QuantileMap.getQuantileMap(ds);
                sqm = QuantileMap.getQuantileMapForSideParam(ds);
            }
            double scaledVal = 0;
            if (paramIDX < ds.getFeatureNamesCombined().length) {
                double noiseQuantile = (paramIDX >= ds.getDimension()) ? sqm.getQuantileForValue(paramIDX - ds.getDimension(), COLOR_MAP_NOISE_THRESHOLD) : qm.getQuantileForValue(paramIDX, COLOR_MAP_NOISE_THRESHOLD);
                scaledVal = (paramIDX >= ds.getDimension()) ? (float) sqm.getQuantileForValue(paramIDX - ds.getDimension(), vec[paramIDX]) : (float) qm.getQuantileForValue(paramIDX, vec[paramIDX]);//(float) ((vec[paramIDX] - scalingBounds[paramIDX][0]) / (scalingBounds[paramIDX][1] - scalingBounds[paramIDX][0]));
                scaledVal = (scaledVal - noiseQuantile) / (1.0 - noiseQuantile);
                currParam = paramList[paramIDX];
                scaleImg = ScaleImageGenerator.generateScaleImage(100, paramIDX, ds, COLOR_MAP_NOISE_THRESHOLD, qm, sqm);
            } else {
                double maxSize = 0;
                for (double[] d : expVectors.values()) {
                    maxSize = Math.max(d[paramIDX], maxSize);
                }
                scaledVal = vec[paramIDX] / maxSize;
                currParam = "Number of Cells";
                scaleImg = ScaleImageGenerator.generateScaleImage(100, 0, maxSize);
            }
            Color c = getColorForValue(scaledVal);

            if (pValVectors.get(cl) != null) {
                c = getColorForValue(pValVectors.get(cl)[paramIDX]);
            }
            n.getNodeData().setR(c.getRed() / 255f);
            n.getNodeData().setG(c.getGreen() / 255f);
            n.getNodeData().setB(c.getBlue() / 255f);
        }

        panScale.repaint();
    }

    private BufferedImage scaleImg = null;

    private void adjustSizesByAnnotationTerm(final Annotation ann, final String[] termsAssay, final String[] termsControl, boolean inBackground) {

        if (groupingWorker != null) {
            groupingWorker.cancel(true);
        }

        groupingWorker = new GroupingWorker(ann, termsAssay, termsControl);

        if (inBackground) {
            groupingWorker.execute();
        } else {
            try {
                groupingWorker.doInBackground();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private Color getColorForValue(double val) {
        if (Double.isNaN(val)) {
            return Color.GRAY;
        }
        if (val < 0) {
            val = 0;
        }
        if (val > 1) {
            val = 1;
        }
        return new Color(Color.HSBtoRGB(0.60f - (float) val * 0.60f, 1, 0.85f));
    }

    /**
     * Creates new form frmClusterGraphX
     */
    public frmFDL(Cluster[] clusters) {

        initComponents();
        this.clusters = clusters;

        ds = clusters[0].getClusterSet().getDataset();

        //ds.getQuantileMap().getSourceDatasetQuantile(1, 0.5);
        paramList = ds.getFeatureNamesCombined();
        DefaultComboBoxModel cmb = new DefaultComboBoxModel();
        cmb.addElement("---none---");

        for (Annotation a : ds.getAnnotations()) {
            cmb.addElement(a);
        }

        Annotation[] options = ds.getAnnotations();
        Annotation ann = null;
        try {
            ann = (Annotation) JOptionPane.showInputDialog(this, "Select the annotation to compute the correlations", "Annotation", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        } catch (NullPointerException e) {
            logger.showException(e);
            scalingBounds = null;
            graph = null;
            applet = null;
            return;
        }

        double[][] annFreq = getTermFrequencies(clusters, ann);
        this.annotationFrequency = annFreq;
        graph = buildGraph(clusters, annFreq, (Double) spinK.getValue());

        cmbAnnotations.setModel(cmb);
        cmbAnnotations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object i = cmbAnnotations.getSelectedItem();
                if (i instanceof Annotation) {
                    currAnn = (Annotation) i;
                    String[] terms = currAnn.getTerms();
                    Arrays.sort(terms);
                    final DefaultTableModel tabModel = new DefaultTableModel(new String[]{"Group", "Term"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    for (int j = 0; j < terms.length; j++) {
                        tabModel.addRow(new Object[]{2, terms[j]});
                    }
                    tabAnnTerms.setCellEditor(null);
                    tabAnnTerms.setModel(tabModel);
                    tabAnnTerms.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            int row = tabAnnTerms.rowAtPoint(e.getPoint());
                            int col = tabAnnTerms.columnAtPoint(e.getPoint());
                            if (col == 0 && row < tabAnnTerms.getRowCount()) {
                                int val = (Integer) ((DefaultTableModel) tabAnnTerms.getModel()).getValueAt(row, col);
                                if (e.getButton() == MouseEvent.BUTTON1) {
                                    val++;
                                    if (val == groupOptionList.length) {
                                        val = 0;
                                    }
                                }
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                    for (int j = 0; j < tabAnnTerms.getRowCount(); j++) {
                                        ((DefaultTableModel) tabAnnTerms.getModel()).setValueAt(groupOptionList.length - 1, j, col);
                                    }
                                    val = 0;
                                }

                                ((DefaultTableModel) tabAnnTerms.getModel()).setValueAt(val, row, col);
                            }

                            ArrayList<String> assayTerms = new ArrayList<>();
                            ArrayList<String> controlTerms = new ArrayList<>();
                            for (int j = 0; j < tabAnnTerms.getModel().getRowCount(); j++) {
                                if ((int) tabAnnTerms.getModel().getValueAt(j, 0) == 0) {
                                    assayTerms.add((String) tabAnnTerms.getModel().getValueAt(j, 1));
                                }
                                if ((int) tabAnnTerms.getModel().getValueAt(j, 0) == 1) {
                                    controlTerms.add((String) tabAnnTerms.getModel().getValueAt(j, 1));
                                }
                            }
                            if (assayTerms.size() > 0) {
                                adjustSizesByAnnotationTerm(currAnn, assayTerms.toArray(new String[assayTerms.size()]), controlTerms.toArray(new String[controlTerms.size()]), true);
                            } else {
                                adjustSizesByAnnotationTerm(currAnn, null, null, true);
                            }
                        }
                    });
                    tabAnnTerms.getColumnModel().getColumn(0).setCellRenderer(new TableCellRenderer() {
                        @Override
                        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                            JLabel lbl = new JLabel() {
                                @Override
                                protected void paintComponent(Graphics g) {
                                    g.setColor(this.getBackground());
                                    g.fillRect(0, 0, this.getWidth(), this.getHeight());
                                }
                            };
                            lbl.setText("");
                            int idx = (Integer) ((DefaultTableModel) tabAnnTerms.getModel()).getValueAt(row, column);
                            lbl.setBackground(idx < groupOptionList.length ? groupOptionList[idx].getValue() : Color.GREEN);
                            return lbl;
                        }
                    });

                } else {
                    tabAnnTerms.setModel(new DefaultTableModel(new String[]{"Group", "Term"}, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    });
                    adjustSizesByAnnotationTerm(null, null, null, true);
                }
            }
        });

        DefaultListModel<String> lm = new DefaultListModel<>();

        // NDataset ds = clusters[0].getClusterSet().getDataset();
        scalingBounds = new double[ds.getFeatureNamesCombined().length][];
        System.arraycopy(Algebra.DEFAULT.transpose(new DenseDoubleMatrix2D(ds.getDataBounds())).toArray(), 0, scalingBounds, 0, ds.getDimension());
        System.arraycopy(Algebra.DEFAULT.transpose(new DenseDoubleMatrix2D(ds.getSideDataBounds())).toArray(), 0, scalingBounds, ds.getDimension(), ds.getSideVarNames().length);

        for (String p : ds.getFeatureNamesCombined()) {
            lm.addElement(p);
        }
        lm.addElement("Number of Cells");
        paramList = ds.getFeatureNamesCombined();
        lstParam.setModel(lm);

        //Append imported data to GraphAPI
        ///importController.process(container, new DefaultProcessor(), workspace);
        //Preview configuration
        previewController = Lookup.getDefault().lookup(PreviewController.class);
        PreviewModel previewModel = previewController.getModel();
        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.BLACK));
        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 100);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_RADIUS, 0f);
        previewModel.getProperties().putValue(PreviewProperty.EDGE_COLOR, new EdgeColor(new Color(127, 127, 127, 127)));
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_WIDTH, 2);
        previewModel.getProperties().putValue(PreviewProperty.NODE_BORDER_COLOR, new DependantColor(new Color(0, 0, 0)));
        previewModel.getProperties().putValue(PreviewProperty.NODE_OPACITY, 100);
        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.WHITE);

        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_PROPORTIONAL_SIZE, false);
        //new DependantColor(new Color(255, 255, 255, 50)));

        ManagedRenderer[] mr2 = new ManagedRenderer[]{
            new ManagedRenderer(new ArrowRenderer(), true),
            new ManagedRenderer(new EdgeRenderer(), true),
            new ManagedRenderer(nlr, true),
            new ManagedRenderer(nr, true),
            new ManagedRenderer(new MyPreviewMouseResponsiveRenderer(), true)
        };

        previewModel.setManagedRenderers(mr2);

        //New Processing target, get the PApplet
        target = (ProcessingTarget) previewController.getRenderTarget(RenderTarget.PROCESSING_TARGET);
        applet = target.getApplet();
        applet.init();
        panGraph.setDoubleBuffered(true);
        panGraph.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panGraph.add(applet, BorderLayout.CENTER);
        ComponentAdapter ca = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                target.resetZoom();
                panScale.setBounds(panGraph.getWidth() - 100, panGraph.getHeight() - 120, panScale.getWidth(), panScale.getHeight());
            }
        };
        panGraph.addComponentListener(ca);

        this.pack();
        this.setVisible(true);
        ca.componentResized(null);
        jSplitPane1.setDividerLocation(0.9);
        lstParam.setSelectedIndex(0);
    }
    private final PApplet applet;

    private void paintScale(Graphics g) {
        if (scaleImg != null) {
            Graphics2D g2 = (Graphics2D) g;
            previewController = Lookup.getDefault().lookup(PreviewController.class);
            PreviewModel previewModel = previewController.getModel();
            g2.setPaint(previewModel.getProperties().getColorValue(PreviewProperty.BACKGROUND_COLOR));
            g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2.drawImage(scaleImg, null, 0, -10);
        }
    }

    private void rebuildGraph(UndirectedGraph graph, Cluster[] clusters, double[][] annFreq, double K) {
        graph.clearEdges();

        for (int i = 0; i < clusters.length; i++) {
            for (int j = i + 1; j < clusters.length; j++) {
                double cos = MatrixOp.getEuclideanCosine(annFreq[i], annFreq[j]);
                /*double weight = 1; 
                 for (double k = cos; k <= 1.0; k+=0.01) {
                 weight -= vonMisesFisherDistribution.getProbabilityDensityOverCosAforK(k, 0, annFreq[0].length);
                 }
                 weight+=Math.max(weight,0)*2;
                 */
                double weight = Math.exp(K * cos) / Math.exp(K);

                float dist = (float) (weight * EDGE_SCALING);
                if (dist > 0.05) {
                    graph.addEdge(nodes[i], nodes[j]);
                    graph.getEdge(nodes[i], nodes[j]).setWeight(dist);
                }
            }
        }
        for (int i = 0; i < 1000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
    }

    private UndirectedGraph buildGraph(Cluster[] clusters, double[][] annFreq, double K) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();

        //Get a graph model - it exists because we have a workspace
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
        UndirectedGraph localGraph = graphModel.getUndirectedGraph();

        //Add boolean column
        AttributeController ac = Lookup.getDefault().lookup(AttributeController.class);
        AttributeModel attributeModel = ac.getModel();

        for (Cluster c : clusters) {
            cid.put(c.getID(), c);
        }

        nodes = new Node[clusters.length];
        int idx = 0;
        try {
            for (Cluster c : clusters) {
                Node n0 = graphModel.factory().newNode(c.toString());
                n0.getNodeData().setLabel(c.toString());
                n0.getNodeData().setX((float) c.getMode().getVector()[0]);
                n0.getNodeData().setY((float) c.getMode().getVector()[1]);
                n0.getNodeData().setSize((float) Math.max(1, Math.pow(c.size(), 0.33)));
                n0.getNodeData().setLabel(c.getComment().length() > 0 ? c.getComment() : "");//c.toString());
                n0.getAttributes().setValue("cluster", c.getID());
                n0.getAttributes().getValue("cluster");
                localGraph.addNode(n0);
                nodes[idx++] = n0;
            }
        } catch (Exception e) {
            logger.showException(e);
        }

        Datapoint[] clusterCentroids = new Datapoint[nodes.length];

        for (int i = 0; i < clusterCentroids.length; i++) {
            clusterCentroids[i] = new Datapoint("avg" + clusters[i].toString(), clusters[i].getMode().getVector(), clusters[i].getMode().getSideVector(), i);
        }

        //final DistanceMeasure dm = clusters[0].getClusterSet().getDistanceMeasure();
        List<MyEdge> edges = new ArrayList<>();

        for (int i = 0; i < clusterCentroids.length; i++) {
            for (int j = i + 1; j < clusterCentroids.length; j++) {
                double cos = MatrixOp.getEuclideanCosine(annFreq[i], annFreq[j]);
                /*double weight = 1; 
                 for (double k = cos; k <= 1.0; k+=0.01) {
                 weight -= vonMisesFisherDistribution.getProbabilityDensityOverCosAforK(k, 0, annFreq[0].length);
                 }
                 weight+=Math.max(weight,0)*2;
                 */
                double weight = Math.exp(K * cos) / Math.exp(K);

                float dist = (float) (weight * EDGE_SCALING);
                if (dist > 0) {
                    edges.add(new MyEdge(nodes[i], nodes[j], dist));
                    localGraph.addEdge(nodes[i], nodes[j]);
                    localGraph.getEdge(nodes[i], nodes[j]).setWeight(dist);
                }
            }
        }

        layout = new ForceAtlas2(new ForceAtlas2Builder());
        layout.setGraphModel(graphModel);
        layout.initAlgo();
        layout.setScalingRatio(layout.getScalingRatio() / 2);
        for (int i = 0; i < 1000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }

        return localGraph;
    }
    private Node[] nodes;
    public ForceAtlas2 layout;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        glassEdit1 = new samusik.glasscmp.GlassEdit();
        jSplitPane1 = new javax.swing.JSplitPane();
        panGraphContainer = new javax.swing.JPanel();
        panGraph = new javax.swing.JPanel();
        panScale = new javax.swing.JPanel(){
            public void paintComponent(Graphics g){
                paintScale(g);
            }
        };
        jButton1 = new javax.swing.JButton();
        chkCreateStacks = new javax.swing.JCheckBox();
        spinImgPerRow = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        togFlip = new javax.swing.JToggleButton();
        jLabel7 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        spinScalingRatio = new javax.swing.JSpinner();
        chkAdjustSizes = new javax.swing.JCheckBox();
        spinRescale = new javax.swing.JSpinner();
        jButton2 = new javax.swing.JButton();
        spinK = new javax.swing.JSpinner();
        jLabel8 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        cmbAnnotations = new samusik.glasscmp.GlassComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        scpColumns = new javax.swing.JScrollPane();
        tabAnnTerms = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        spParamSelectionContainer = new javax.swing.JScrollPane();
        lstParam = new javax.swing.JList();
        legend = new javax.swing.JLabel();
        chkShowBorders = new javax.swing.JCheckBox();
        chkShowLabels = new javax.swing.JCheckBox();

        glassEdit1.setText(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.glassEdit1.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jSplitPane1.setDividerLocation(900);
        jSplitPane1.setDividerSize(4);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setMinimumSize(new java.awt.Dimension(300, 158));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1000, 158));

        panGraphContainer.setLayout(new java.awt.GridBagLayout());

        panGraph.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panGraph.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        panGraph.setMinimumSize(new java.awt.Dimension(100, 100));
        panGraph.setName(""); // NOI18N
        panGraph.setPreferredSize(new java.awt.Dimension(1000, 100));
        panGraph.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panGraphMouseClicked(evt);
            }
        });
        panGraph.setLayout(new java.awt.GridBagLayout());

        panScale.setBackground(new Color(0,0,0,0));
        panScale.setMaximumSize(new java.awt.Dimension(80, 120));
        panScale.setMinimumSize(new java.awt.Dimension(80, 120));
        panScale.setName(""); // NOI18N
        panScale.setPreferredSize(new java.awt.Dimension(80, 120));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        panGraph.add(panScale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panGraphContainer.add(panGraph, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panGraphContainer.add(jButton1, gridBagConstraints);

        chkCreateStacks.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(chkCreateStacks, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.chkCreateStacks.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        panGraphContainer.add(chkCreateStacks, gridBagConstraints);

        spinImgPerRow.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(5), Integer.valueOf(1), null, Integer.valueOf(1)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panGraphContainer.add(spinImgPerRow, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panGraphContainer.add(jLabel5, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(togFlip, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.togFlip.text")); // NOI18N
        togFlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togFlipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        panGraphContainer.add(togFlip, gridBagConstraints);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 123, 255));
        org.openide.awt.Mnemonics.setLocalizedText(jLabel7, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel7.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panGraphContainer.add(jLabel7, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton3, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jButton3.text")); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        panGraphContainer.add(jButton3, gridBagConstraints);

        spinScalingRatio.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(5.0d), Double.valueOf(0.0d), null, Double.valueOf(1.0d)));
        spinScalingRatio.setMinimumSize(new java.awt.Dimension(61, 20));
        spinScalingRatio.setPreferredSize(new java.awt.Dimension(61, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panGraphContainer.add(spinScalingRatio, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(chkAdjustSizes, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.chkAdjustSizes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        panGraphContainer.add(chkAdjustSizes, gridBagConstraints);

        spinRescale.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(5.0d), Double.valueOf(0.0d), null, Double.valueOf(1.0d)));
        spinRescale.setMinimumSize(new java.awt.Dimension(61, 20));
        spinRescale.setPreferredSize(new java.awt.Dimension(61, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panGraphContainer.add(spinRescale, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        panGraphContainer.add(jButton2, gridBagConstraints);

        spinK.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(5.0d), null, null, Double.valueOf(1.0d)));
        spinK.setMinimumSize(new java.awt.Dimension(61, 20));
        spinK.setPreferredSize(new java.awt.Dimension(61, 20));
        spinK.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinKStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        panGraphContainer.add(spinK, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel8, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel8.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        panGraphContainer.add(jLabel8, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jToggleButton1, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jToggleButton1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        panGraphContainer.add(jToggleButton1, gridBagConstraints);

        jSplitPane1.setLeftComponent(panGraphContainer);

        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel5.setLayout(new java.awt.GridBagLayout());

        cmbAnnotations.setModel(new DefaultComboBoxModel<Annotation>());
        cmbAnnotations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAnnotationsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel5.add(cmbAnnotations, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanel5.add(jLabel1, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel7.setOpaque(false);
        jPanel7.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 8);
        jPanel7.add(jLabel2, gridBagConstraints);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/azure 16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel3.text")); // NOI18N
        jLabel3.setToolTipText(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel3.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel7.add(jLabel3, gridBagConstraints);

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/green 16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel4.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        jPanel7.add(jLabel4, gridBagConstraints);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/grey 16x16.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLabel6, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel6.text")); // NOI18N
        jLabel6.setToolTipText(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.jLabel6.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(7, 7, 7, 7);
        jPanel7.add(jLabel6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(jPanel7, gridBagConstraints);

        tabAnnTerms.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Group", "Term"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scpColumns.setViewportView(tabAnnTerms);
        if (tabAnnTerms.getColumnModel().getColumnCount() > 0) {
            tabAnnTerms.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmClusterGraphX.tabAnnTerms.columnModel.title1")); // NOI18N
            tabAnnTerms.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(frmFDL.class, "frmClusterGraphX.tabAnnTerms.columnModel.title2")); // NOI18N
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(scpColumns, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 0.5;
        jPanel5.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        lstParam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstParamMouseClicked(evt);
            }
        });
        lstParam.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstParamValueChanged(evt);
            }
        });
        spParamSelectionContainer.setViewportView(lstParam);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel6.add(spParamSelectionContainer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        jPanel4.add(jPanel6, gridBagConstraints);

        legend.setForeground(new java.awt.Color(0, 106, 255));
        org.openide.awt.Mnemonics.setLocalizedText(legend, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.legend.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(legend, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(chkShowBorders, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.chkShowBorders.text")); // NOI18N
        chkShowBorders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowBordersActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(chkShowBorders, gridBagConstraints);

        chkShowLabels.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(chkShowLabels, org.openide.util.NbBundle.getMessage(frmFDL.class, "frmFDL.chkShowLabels.text")); // NOI18N
        chkShowLabels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowLabelsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        jPanel4.add(chkShowLabels, gridBagConstraints);

        jSplitPane1.setRightComponent(jPanel4);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void panGraphMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panGraphMouseClicked
    }//GEN-LAST:event_panGraphMouseClicked

    private void lstParamValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstParamValueChanged
        if (evt != null) {
            colorNodesByExpressionLevel(lstParam.getSelectedIndex());
        }

        previewController.render(target);
        target.refresh();
        previewController.refreshPreview();
    }//GEN-LAST:event_lstParamValueChanged


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        boolean createStack = chkCreateStacks.isSelected() && tabAnnTerms.getModel().getRowCount() > 0;
        int imgPerRow = ((Integer) spinImgPerRow.getValue());
        imgPerRow = Math.min(tabAnnTerms.getModel().getRowCount(), imgPerRow);
        int rows = 1;
        try {
            rows = (tabAnnTerms.getModel().getRowCount() / imgPerRow);
            if (tabAnnTerms.getModel().getRowCount() % imgPerRow != 0) {
                rows++;
            }
        } catch (Exception e) {
        }

        //Export full graph
        File file = IO.chooseFileWithDialog("frmDMTexport", "Directories", null, true);
        if (file == null) {
            return;
        }
        String path = file.getPath();
        int w = target.getApplet().getWidth();
        int h = target.getApplet().getHeight();
        try {
            for (int i = 0; i < paramList.length; i++) {
                BufferedImage stack = (createStack) ? new BufferedImage(w * imgPerRow, h * rows, BufferedImage.TYPE_INT_RGB) : null;
                int curRow = 0;
                int curCol = 0;
                if (createStack) {
                    ((Graphics2D) stack.getGraphics()).setPaint(Color.WHITE);
                    ((Graphics2D) stack.getGraphics()).fillRect(0, 0, w * imgPerRow, h * rows);
                }
                for (int j = 0; j < tabAnnTerms.getModel().getRowCount(); j++) {
                    adjustSizesByAnnotationTerm(currAnn, new String[]{(String) tabAnnTerms.getModel().getValueAt(j, 1)}, new String[0], false);
                    colorNodesByExpressionLevel(i);

                    previewController.render(target);
                    target.refresh();
                    previewController.refreshPreview();

                    try {
                        Thread.currentThread().sleep(200);
                    } catch (Exception e) {
                        logger.showException(e);
                    }

                    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    target.getApplet().paintAll(img.createGraphics());
                    File f = new File(path + File.separator + "_" + currParam.replaceAll("\\\\", "-").replaceAll("/", "-") + "_" + currAnnTerms[0] + ".png");
                    ImageIO.write(img, "PNG", f);
                    if (createStack) {
                        BufferedImage bi = ImageIO.read(f);
                        f.delete();
                        Graphics2D g2 = ((Graphics2D) bi.createGraphics());
                        double fsize = (int) (200.0 * (400.0 / bi.getHeight()));
                        g2.setFont(new java.awt.Font("Arial", Font.BOLD, (int) fsize));
                        g2.setColor(new Color(0, 0, 0, 255));
                        g2.drawString(currAnnTerms[0], 5, (int) fsize + 5);
                        g2.setStroke(new BasicStroke(5.0f));
                        g2.drawRect(0, 0, w - 1, h - 1);
                        stack.getGraphics().drawImage(bi, curCol * w, curRow * h, null);
                        curCol++;
                        if (curCol == imgPerRow) {
                            curCol = 0;
                            curRow++;
                        }
                    }
                }
                if (createStack) {
                    ImageIO.write(stack, "PNG", new File(path + File.separator + "stack_" + ds.getName() + "_" + paramList[i].replaceAll("\\\\", "-").replaceAll("/", "-") + ".png"));
                }
            }

            if (tabAnnTerms.getModel().getRowCount() == 0) {
                for (int i = 0; i < paramList.length; i++) {
                    colorNodesByExpressionLevel(i);

                    previewController.render(target);
                    target.refresh();
                    previewController.refreshPreview();

                    try {
                        Thread.currentThread().sleep(100);
                    } catch (Exception e) {
                        logger.showException(e);
                    }
                    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                    target.getApplet().paintAll(img.createGraphics());
                    File f = new File(path + File.separator + ds.getName() + "_" + currParam.replaceAll("\\\\", "-").replaceAll("/", "-") + ".png");
                    ImageIO.write(img, "PNG", f);
                }
            }

            for (int i = 0; i < paramList.length; i++) {
                colorNodesByExpressionLevel(i);
                BufferedImage bi = ScaleImageGenerator.generateScaleImage(100, i, ds, COLOR_MAP_NOISE_THRESHOLD, qm, sqm);
                ImageIO.write(bi, "PNG", new File(path + File.separator + ds.getName() + "_scaleFor" + "_" + currParam.replaceAll("\\\\", "-").replaceAll("/", "-") + ".png"));
            }

        } catch (IOException ex) {
            logger.showException(ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void lstParamMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstParamMouseClicked
        if (evt.getButton() == MouseEvent.BUTTON3) {
            if (fixedParamVal != lstParam.getSelectedIndex()) {
                fixedParamVal = lstParam.getSelectedIndex();
            } else {
                fixedParamVal = -1;
            }
        }
    }//GEN-LAST:event_lstParamMouseClicked

    private void cmbAnnotationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAnnotationsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAnnotationsActionPerformed

    private void togFlipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togFlipActionPerformed
        for (Node n : graph.getNodes()) {
            n.getNodeData().setY(-n.getNodeData().y());
        }
        lstParamValueChanged(null);
    }//GEN-LAST:event_togFlipActionPerformed

    private void chkShowBordersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowBordersActionPerformed
        nr.setNodeBordersVisible(
                chkShowBorders.isSelected());
        lstParamValueChanged(null);
    }//GEN-LAST:event_chkShowBordersActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        layout.setAdjustSizes(chkAdjustSizes.isSelected());
        layout.setBarnesHutOptimize(true);
        layout.setScalingRatio((Double) spinScalingRatio.getValue());
        for (int i = 0; i < 1000 && layout.canAlgo(); i++) {
            layout.goAlgo();
        }
        for (Node n : graph.getNodes()) {
            Edge[] e = graph.getEdges(n).toArray();
            if (e.length == 1) {
                Node parent = e[0].getSource();
                //float diffX = n.getNodeData().x()-parent.getNodeData().x();
                //float diffY = n.getNodeData().y()-parent.getNodeData().y();
                //n.getNodeData().setX(n.getNodeData().x() + diffX);
                //n.getNodeData().setY(n.getNodeData().y() + diffY);
            }
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        for (Node n : graph.getNodes()) {
            double f = (Double) spinRescale.getValue();
            n.getNodeData().setX((float) (n.getNodeData().x() * f));
            n.getNodeData().setY((float) (n.getNodeData().y() * f));
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void chkShowLabelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowLabelsActionPerformed
        nlr.setTextOn(chkShowLabels.isSelected());
    }//GEN-LAST:event_chkShowLabelsActionPerformed

    private void spinKStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinKStateChanged
        rebuildGraph(graph, clusters, annotationFrequency, (Double) spinK.getValue());
    }//GEN-LAST:event_spinKStateChanged

    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox chkAdjustSizes;
    private javax.swing.JCheckBox chkCreateStacks;
    private javax.swing.JCheckBox chkShowBorders;
    private javax.swing.JCheckBox chkShowLabels;
    private samusik.glasscmp.GlassComboBox cmbAnnotations;
    private samusik.glasscmp.GlassEdit glassEdit1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel legend;
    private javax.swing.JList lstParam;
    private javax.swing.JPanel panGraph;
    private javax.swing.JPanel panGraphContainer;
    private javax.swing.JPanel panScale;
    private javax.swing.JScrollPane scpColumns;
    private javax.swing.JScrollPane spParamSelectionContainer;
    private javax.swing.JSpinner spinImgPerRow;
    private javax.swing.JSpinner spinK;
    private javax.swing.JSpinner spinRescale;
    private javax.swing.JSpinner spinScalingRatio;
    private javax.swing.JTable tabAnnTerms;
    private javax.swing.JToggleButton togFlip;
    // End of variables declaration//GEN-END:variables

    private CustomNodeRenderer nr = new CustomNodeRenderer(true, false, true);
    private CustomNodeLabelRenderer nlr = new CustomNodeLabelRenderer(false);

    private class GroupingWorker extends SwingWorker<Object, Object> {

        private final Annotation ann;
        private final String[] termsAssay;
        private final String[] termsControl;

        public GroupingWorker(Annotation ann, String[] termsAssay, String[] termsControl) {
            this.ann = ann;
            this.termsAssay = termsAssay;
            this.termsControl = termsControl;
        }

        @Override
        protected void process(List<Object> chunks) {
            if (chunks.get(0).equals("no groups")) {
                legend.setText("<html>Node fill is showing <b>expression levels (rainbow scale)</b><br>Node border is showing <b>nothing</b></html>");
            }
            if (chunks.get(0).equals("one group")) {
                legend.setText("<html>Node fill is showing <b>expression levels (rainbow scale)</b><br>Node border is showing <b>nothing</b></html>");
            }
            colorNodesByExpressionLevel(lstParam.getSelectedIndex());
            lstParamValueChanged(null);
        }

        @Override
        public Object doInBackground() throws Exception {
            if (ann == null || termsAssay == null) {
                NodeIterable ni = graph.getNodes();
                for (final Node n : ni.toArray()) {
                    Cluster cl = cid.get((int) n.getAttributes().getValue("cluster"));
                    n.getNodeData().setSize((float) Math.max(1, Math.pow(cl.size(), 0.33)));
                    n.getNodeData().getAttributes().setValue("logRatio", 0.33);
                }
                currAnnTerms = null;
                expVectors = new HashMap<>();

                publish("no groups");
                return null;
            }

            ArrayList<String> dpIDsAssayAL = new ArrayList<>();

            for (String term : termsAssay) {
                String[] pid = ann.getProfileIDsForTerm(term);
                // int[] dpids = new int[pid.length];
                for (int i = 0; i < pid.length; i++) {
                    Datapoint dp = ann.getBaseDataset().getDPbyName(pid[i]);
                    if (dp != null) {
                        dpIDsAssayAL.add(dp.getName());
                    }
                }
                // logger.print("term len:" + pid.length + ", dpIDsAssayAL len:" + dpIDsAssayAL.size());
            }
            Collections.sort(dpIDsAssayAL);

            // logger.print("dpIDsAssayAL len after filtering:" + dpIDsAssayAL.size());
            String[] dpIDsAssay = new String[dpIDsAssayAL.size()];
            for (int i = 0; i < dpIDsAssay.length; i++) {
                dpIDsAssay[i] = dpIDsAssayAL.get(i);
            }

            String[] dpIDsControl = null;
            if (termsControl != null) {
                ArrayList<String> dpIDsControlAL = new ArrayList<>();

                for (String term : termsControl) {
                    String[] pid = ann.getProfileIDsForTerm(term);
                    // int[] dpids = new int[pid.length];
                    for (int i = 0; i < pid.length; i++) {
                        Datapoint dp = ann.getBaseDataset().getDPbyName(pid[i]);
                        if (dp != null) {
                            dpIDsControlAL.add(dp.getName());
                        }
                    }
                }
                Collections.sort(dpIDsControlAL);

                dpIDsControl = new String[dpIDsControlAL.size()];
                for (int i = 0; i < dpIDsControl.length; i++) {
                    dpIDsControl[i] = dpIDsControlAL.get(i);
                }

            }

            //logger.print("termsAssay", Arrays.toString(termsAssay));
            //logger.print("termsControl", Arrays.toString(termsControl));
            NodeIterable ni = graph.getNodes();
            for (final Node n : ni.toArray()) {
                Cluster cl = cid.get((int) n.getAttributes().getValue("cluster"));
                int count = 0;
                int countControl = 0;
                try {
                    double[] avgVec = new double[ann.getBaseDataset().getFeatureNamesCombined().length];
                    for (ClusterMember cm : cl.getClusterMembers()) {
                        if (Arrays.binarySearch(dpIDsAssay, cm.getDatapoint().getName()) >= 0) {
                            count++;
                            avgVec = MatrixOp.sum(avgVec, MatrixOp.concat(cm.getDatapoint().getVector(), cm.getDatapoint().getSideVector()));
                        }
                    }
                    MatrixOp.mult(avgVec, 1.0 / (double) count);

                    expVectors.put(cl, MatrixOp.concat(avgVec, new double[]{count}));
                    if (termsControl != null && termsControl.length > 0) {
                        double[] avgVecControl = new double[ann.getBaseDataset().getFeatureNamesCombined().length];
                        for (ClusterMember cm : cl.getClusterMembers()) {
                            if (Arrays.binarySearch(dpIDsControl, cm.getDatapoint().getName()) >= 0) {
                                countControl++;
                                avgVecControl = MatrixOp.sum(avgVecControl, MatrixOp.concat(cm.getDatapoint().getVector(), cm.getDatapoint().getSideVector()));
                            }
                        }
                        if (countControl > 1) {
                            MatrixOp.mult(avgVecControl, 1.0 / (double) countControl);

                        }

                        double[] sErrVector = new double[ann.getBaseDataset().getFeatureNamesCombined().length];

                        for (ClusterMember cm : cl.getClusterMembers()) {
                            if (Arrays.binarySearch(dpIDsControl, cm.getDatapoint().getName()) >= 0) {
                                double[] vec = MatrixOp.concat(cm.getDatapoint().getVector(), cm.getDatapoint().getSideVector());

                                for (int i = 0; i < vec.length; i++) {
                                    sErrVector[i] += Math.pow(vec[i] - avgVecControl[i], 2);
                                }
                            }
                            if (Arrays.binarySearch(dpIDsAssay, cm.getDatapoint().getName()) >= 0) {
                                double[] vec = MatrixOp.concat(cm.getDatapoint().getVector(), cm.getDatapoint().getSideVector());

                                for (int i = 0; i < vec.length; i++) {
                                    sErrVector[i] += Math.pow(vec[i] - avgVec[i], 2);
                                }
                            }
                        }

                        if (count + countControl > 2) {
                            for (int i = 0; i < sErrVector.length; i++) {
                                sErrVector[i] = Math.sqrt(sErrVector[i] / (count + countControl));
                            }
                            System.err.println(cl.toString());
                            double[] pValVector = new double[ann.getBaseDataset().getFeatureNamesCombined().length];
                            for (int i = 0; i < pValVector.length; i++) {
                                if (sErrVector[i] > 0) {
                                    NormalDist dist = new NormalDist(avgVecControl[i], sErrVector[i]);
                                    pValVector[i] = Math.abs(dist.cdf(avgVec[i]) - 0.5) * 2;
                                } else {
                                    pValVector[i] = 0.5;
                                }
                            }
                            pValVectors.put(cl, pValVector);
                        } else {
                            double[] pValVector = new double[ann.getBaseDataset().getFeatureNamesCombined().length];
                            pValVectors.put(cl, pValVector);
                        }
                    } else {
                        pValVectors.remove(cl);
                    }

                } catch (SQLException e) {
                    logger.showException(e);
                }

                n.getNodeData().setSize(Math.max(1, (float) Math.pow(count * ((ds.size()) / (double) dpIDsAssay.length), 0.33)));
                if (termsAssay.length > 0) {
                    if (termsControl != null && termsControl.length > 0) {
                        double ratio = countControl == 0 ? 1 : ((double) count / (double) countControl);
                        if (dpIDsControl.length > 0) {
                            ratio /= (dpIDsAssay.length / (double) dpIDsControl.length);
                        }
                        double logRatio = 0.5 + (Math.log(ratio) / 4);
                        //logger.print("logRatio" + ratio);
                        n.getNodeData().getAttributes().setValue("logRatio", 0.5);//new Double(Math.max(0, Math.min(1, logRatio))));
                        publish("two groups");
                    } else {
                        double ratio = 1 - HypergeometricDist.pValHyperGeom(ds.size(), dpIDsAssay.length, cl.size(), count);
                        n.getNodeData().getAttributes().setValue("logRatio", ratio);
                        publish("one group");
                    }
                } else {
                    n.getNodeData().getAttributes().setValue("logRatio", 0.5);
                    publish("no groups");
                }
                //logger.print(n.getNodeData().getAttributes().getVgalue("logRatio"));
            }
            currAnnTerms = termsAssay;
            publish("done");
            return null;
        }
    }

}
