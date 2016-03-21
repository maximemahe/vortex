/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * panAnnotationsBrowser.java
 *
 * Created on 24-Apr-2012, 22:16:35
 */
package vortex.gui;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import clustering.Datapoint;
import main.Dataset;
import annotations.Annotation;
import util.IO;
import util.logger;

/**
 *
 * @author Nikolay
 */
@SuppressWarnings("serial")
public class panAnnotationsBrowser extends javax.swing.JPanel {

    Dataset nd;
    DefaultTableModel dtm;

    /**
     * Creates new form panAnnotationsBrowser
     */
    public panAnnotationsBrowser(Dataset nd) {
        initComponents();
        this.nd = nd;
        for (Annotation a : nd.getAnnotations()) {
            cmbAnnotations.addItem(a);
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

        glassComboBox1 = new samusik.glasscmp.GlassComboBox();
        cmdDeleteAnnotation1 = new samusik.glasscmp.GlassButton();
        toolbar = new samusik.glasscmp.GlassToolBar();
        cmdDeleteAnnotation = new samusik.glasscmp.GlassButton();
        cmbAnnotations = new samusik.glasscmp.GlassComboBox();
        cmdDeleteAnnotation2 = new samusik.glasscmp.GlassButton();
        scrollPane = new javax.swing.JScrollPane();
        tabAnnotationContents = new javax.swing.JTable();

        cmdDeleteAnnotation1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/Close.png"))); // NOI18N
        cmdDeleteAnnotation1.setToolTipText("Delete this annotation");
        cmdDeleteAnnotation1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdDeleteAnnotation1.setIconTextGap(0);
        cmdDeleteAnnotation1.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdDeleteAnnotation1.setMaximumSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation1.setMinimumSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation1.setPreferredSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation1.setVerifyInputWhenFocusTarget(false);
        cmdDeleteAnnotation1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteAnnotation1ActionPerformed(evt);
            }
        });

        setLayout(new java.awt.GridBagLayout());

        toolbar.setMinimumSize(new java.awt.Dimension(80, 50));
        toolbar.setPreferredSize(new java.awt.Dimension(80, 50));
        toolbar.setLayout(new java.awt.GridBagLayout());

        cmdDeleteAnnotation.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/Close.png"))); // NOI18N
        cmdDeleteAnnotation.setToolTipText("Delete this annotation");
        cmdDeleteAnnotation.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdDeleteAnnotation.setIconTextGap(0);
        cmdDeleteAnnotation.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdDeleteAnnotation.setMaximumSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation.setMinimumSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation.setPreferredSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation.setVerifyInputWhenFocusTarget(false);
        cmdDeleteAnnotation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteAnnotationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        toolbar.add(cmdDeleteAnnotation, gridBagConstraints);

        cmbAnnotations.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<New annotation>" }));
        cmbAnnotations.setSelectedIndex(-1);
        cmbAnnotations.setToolTipText("Select annotation");
        cmbAnnotations.setMaximumSize(new java.awt.Dimension(3000, 30));
        cmbAnnotations.setMinimumSize(new java.awt.Dimension(30, 30));
        cmbAnnotations.setPreferredSize(new java.awt.Dimension(30, 32));
        cmbAnnotations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAnnotationsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        toolbar.add(cmbAnnotations, gridBagConstraints);

        cmdDeleteAnnotation2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/vortex/resources/green 16x16.png"))); // NOI18N
        cmdDeleteAnnotation2.setToolTipText("Merge/rename terms");
        cmdDeleteAnnotation2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cmdDeleteAnnotation2.setIconTextGap(0);
        cmdDeleteAnnotation2.setLabel(" Merge/rename terms");
        cmdDeleteAnnotation2.setMargin(new java.awt.Insets(2, 2, 2, 2));
        cmdDeleteAnnotation2.setMaximumSize(new java.awt.Dimension(320, 32));
        cmdDeleteAnnotation2.setMinimumSize(new java.awt.Dimension(32, 32));
        cmdDeleteAnnotation2.setPreferredSize(new java.awt.Dimension(150, 32));
        cmdDeleteAnnotation2.setVerifyInputWhenFocusTarget(false);
        cmdDeleteAnnotation2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdDeleteAnnotation2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        toolbar.add(cmdDeleteAnnotation2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(toolbar, gridBagConstraints);

        tabAnnotationContents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(tabAnnotationContents);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 818;
        gridBagConstraints.ipady = 460;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cmbAnnotationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAnnotationsActionPerformed
        Object o = cmbAnnotations.getSelectedItem();
        if (o == null) {
            return;
        }
        if (o.equals("<New annotation>")) {
            Annotation ann = createNewAnnotation();
            if (ann != null) {
                cmbAnnotations.addItem(ann);
            }
            cmbAnnotations.setSelectedItem(ann);
        }
        o = cmbAnnotations.getSelectedItem();
        
        if (o instanceof Annotation) {
            Annotation ann = (Annotation) o;
            dtm = new DefaultTableModel(new String[]{"ProfileID", "Terms"}, 0);
            int k = 0;
            for (Datapoint d : nd.getDatapoints()) {
                if(k++>10000){
                    break;
                }
                String[] s = ann.getTermsForProfileID(d.getName());
                if (s != null) {
                    if (s.length != 0) {
                        dtm.addRow(new String[]{d.getName(), Arrays.deepToString(s)});
                    }
                }
            }
            tabAnnotationContents.setModel(dtm);
        }
        
    }//GEN-LAST:event_cmbAnnotationsActionPerformed
    
    private Annotation createNewAnnotation() {
        if (nd == null) {
            return null;
        }
        JOptionPane.showMessageDialog(this, "Annotations can be imported from a file with the following format:\nEach line begins with a ProfileID immediately followed by a tab.\nAfter the tab, a list of annotation terms for this ProfileID follows, \nwhere individual terms are separated by semicolon ';'.\n\nExample:\nRAB5A  <-tab->  vesicular transport;endocytosis;regulation of... ");

        File f = IO.chooseFileWithDialog("ImportDatasetAnnotation", "Plain text table", new String[]{"txt"}, false);

        if (f != null) {
            try {

                StringBuilder sb = new StringBuilder();
                sb.append("Following rows were skipped. Do you want to proceed with import?\n");
                ArrayList<String> al = IO.getListOfStringsFromStream(new FileInputStream(f));
                ArrayList<Entry<String, String[]>> alRows = new ArrayList<Entry<String, String[]>>();
                for (String s : al) {
                    String[] s2 = s.split("\t");
                    if (s2.length != 2) {
                        sb.append("\nInvalid number of tabs: ");
                        sb.append(s);
                        continue;
                    }
                    String[] terms = s2[1].split(";");

                    if (terms.length == 0) {
                        sb.append("\nAnnotation term list is empty: ");
                        sb.append(s);
                        continue;
                    }


                    if (nd.getDPbyName(s2[0]) == null) {
                        sb.append("\n This ProfileID has no matches in the given Dataset:");
                        sb.append(s);
                        continue;
                    }

                    for (int i = 0; i < terms.length; i++) {
                        terms[i] = terms[i].trim();
                    }

                    alRows.add(new SimpleEntry<String, String[]>(s2[0], terms));
                }
                boolean proceed = true;
                if (alRows.size() < al.size()) {
                    JScrollPane sp = new JScrollPane(new JTextArea(sb.toString()));
                    sp.setPreferredSize(new Dimension(500, 500));
                    sp.setMinimumSize(new Dimension(500, 500));
                    sp.setMaximumSize(new Dimension(500, 500));
                    proceed = JOptionPane.showConfirmDialog(this, sp, "Ready to import " + alRows.size() + " out of " + al.size() + "rows", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
                }
                if (proceed) {
                    String annName = "";
                    do {
                        annName = JOptionPane.showInputDialog(this, "Please specify the annotation name:");
                        for (Annotation ann : nd.getAnnotations()) {
                            if (ann.getAnnotationName().equals(annName)) {
                                JOptionPane.showMessageDialog(this, "Annotation with this name already exists. Choose a different title", "Error", JOptionPane.ERROR_MESSAGE);
                                annName = "";
                                break;
                            }
                        }

                    } while (annName.isEmpty());
                    Annotation ann = new Annotation(nd, annName);

                    HashMap<String, LinkedList<String>> hmProfileIDsForTerm = new HashMap<>();

                    for (Entry<String, String[]> entry : alRows) {

                        String[] terms = entry.getValue();
                        String pid = entry.getKey();
                        if (terms != null) {
                            for (String t : terms) {
                                if (hmProfileIDsForTerm.get(t) == null) {
                                    hmProfileIDsForTerm.put(t, new LinkedList<String>());
                                }
                                hmProfileIDsForTerm.get(t).add(pid);
                            }
                        }

                    }

                    for (String t : hmProfileIDsForTerm.keySet()) {
                        ann.addTerm(hmProfileIDsForTerm.get(t), t);
                    }

                    nd.addAnnotation(ann);
                    return ann;
                }
            } catch (Exception e) {
                logger.showException(e);
            }
        }
        return null;
    }

    public void setDataset(Dataset nd) {
        Annotation[] annS = nd.getAnnotations();
        for (Annotation a : annS) {
            cmbAnnotations.addItem(a);
        }

    }

    private void cmdDeleteAnnotationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteAnnotationActionPerformed
        Object o = cmbAnnotations.getSelectedItem();
        if (o instanceof Annotation) {
            nd.removeAnnotation((Annotation) o);
            cmbAnnotations.setSelectedIndex(-1);
            cmbAnnotations.removeItem(o);
        }
        dtm = new DefaultTableModel(new String[]{"ProfileID", "Terms"}, 0);
        tabAnnotationContents.setModel(dtm);
    }//GEN-LAST:event_cmdDeleteAnnotationActionPerformed

    private void cmdDeleteAnnotation1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteAnnotation1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmdDeleteAnnotation1ActionPerformed

    private void cmdDeleteAnnotation2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdDeleteAnnotation2ActionPerformed
        Object o = cmbAnnotations.getSelectedItem();
        if (o instanceof Annotation) {
         dlgRemapTerms frm = new dlgRemapTerms(frmMain.getInstance(), true, (Annotation)o);
         frm.setBounds(100, 100, frmMain.getInstance().getWidth() - 200, frmMain.getInstance().getHeight() - 200);
         frm.setVisible(true);
        }
        cmbAnnotations.setModel(new DefaultComboBoxModel(nd.getAnnotations()));
    }//GEN-LAST:event_cmdDeleteAnnotation2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private samusik.glasscmp.GlassComboBox cmbAnnotations;
    private samusik.glasscmp.GlassButton cmdDeleteAnnotation;
    private samusik.glasscmp.GlassButton cmdDeleteAnnotation1;
    private samusik.glasscmp.GlassButton cmdDeleteAnnotation2;
    private samusik.glasscmp.GlassComboBox glassComboBox1;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable tabAnnotationContents;
    private samusik.glasscmp.GlassToolBar toolbar;
    // End of variables declaration//GEN-END:variables
}
