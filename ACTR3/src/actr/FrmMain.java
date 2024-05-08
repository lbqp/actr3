/*
 * The MIT License
 *
 * Copyright 2024 Laboratorio de Bioquímica e Química de Proteínas / 
 * Instituto de Ciências Biológicas / Universidade de Brasília
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package actr;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public final class FrmMain extends javax.swing.JFrame implements EnsaioListener {

    private final DefaultListModel model = new DefaultListModel();
    private Ensaio ensaio = null;
    private static final String INI_FILENAME = "ACTR.ini";
    private final Object[] columnNames = {"Data","Hora","A1","B1","C1","D1",
                                                "E1","F1","G1","H1",
                                                "A2","B2","C2","D2",
                                                "E2","F2","G2","H2"};
    
    /**
     * Creates new form FrmMain
     */
    public FrmMain() {
        initComponents();

        lstAgenda.setModel(model);
        
        ensaio = Ensaio.load(INI_FILENAME, this);
        if(ensaio == null)
            ensaio = new Ensaio(this);
        
        
        atualizarControles();
        
        
    }
    
    protected void atualizarControles()
    {

        
        DefaultListModel listModel = (DefaultListModel) (lstAgenda.getModel());
        listModel.clear();
        for(int i=0; i<ensaio.getAgenda().conteProgramacoes(); i++)
        {
            Programacao p = ensaio.getAgenda().getProgramacao(i);
            listModel.addElement(p);        
        }
        
        btnIniciar.setEnabled(ensaio.getAgenda().conteProgramacoes() >=1 && ensaio.getStatusEnsaio() == StatusEnsaio.EmEspecificacao);
        btnCancelar.setEnabled(ensaio.getStatusEnsaio() == StatusEnsaio.EmAndamento);
        
        btnAdicionar.setEnabled(ensaio.getStatusEnsaio() == StatusEnsaio.EmEspecificacao);
        btnExcluir.setEnabled(ensaio.getStatusEnsaio() == StatusEnsaio.EmEspecificacao);
        
        lblStatusEnsaio.setText(ensaio.getStatusEnsaio().toString());
        
        //if(ensaio.getStatusEnsaio() == StatusEnsaio.EmAndamento || ensaio.getStatusEnsaio() == StatusEnsaio.Finalizado)
            atualizarTabela();
        
        
        rdbZ_Zo_17.setSelected(ensaio.getCIFormula() == CIFormula.Z_Zo_17);
        rdbZ_Zo_15.setSelected(ensaio.getCIFormula() == CIFormula.Z_Zo_15);
        rdbZ_Zo.setSelected(ensaio.getCIFormula() == CIFormula.Z_Zo);
        rdbZ.setSelected(ensaio.getCIFormula() == CIFormula.Z);

        
        chkA1.setSelected(ensaio.isVisibleGraph(Well.WELL_A1));
        chkB1.setSelected(ensaio.isVisibleGraph(Well.WELL_B1));
        chkC1.setSelected(ensaio.isVisibleGraph(Well.WELL_C1));
        chkD1.setSelected(ensaio.isVisibleGraph(Well.WELL_D1));
        chkE1.setSelected(ensaio.isVisibleGraph(Well.WELL_E1));
        chkF1.setSelected(ensaio.isVisibleGraph(Well.WELL_F1));
        chkG1.setSelected(ensaio.isVisibleGraph(Well.WELL_G1));
        chkH1.setSelected(ensaio.isVisibleGraph(Well.WELL_H1));

        chkA2.setSelected(ensaio.isVisibleGraph(Well.WELL_A2));
        chkB2.setSelected(ensaio.isVisibleGraph(Well.WELL_B2));
        chkC2.setSelected(ensaio.isVisibleGraph(Well.WELL_C2));
        chkD2.setSelected(ensaio.isVisibleGraph(Well.WELL_D2));
        chkE2.setSelected(ensaio.isVisibleGraph(Well.WELL_E2));
        chkF2.setSelected(ensaio.isVisibleGraph(Well.WELL_F2));
        chkG2.setSelected(ensaio.isVisibleGraph(Well.WELL_G2));
        chkH2.setSelected(ensaio.isVisibleGraph(Well.WELL_H2));
        
        chkNormalizar.setSelected(ensaio.isNormalizedCi());
        
        atualizarGrafico();
        
    }

    private void atualizarTabela()
    {
        int row=0;
        SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
        DefaultTableModel dataModel = new DefaultTableModel(columnNames, ensaio.getAgenda().conteTotalVarreduras());
        if(ensaio.getStatusEnsaio() != StatusEnsaio.EmEspecificacao)
        {
            for(int p=0; p<ensaio.getAgenda().conteProgramacoes(); p++)
            {
                Programacao programacao = ensaio.getAgenda().getProgramacao(p);
                for(int v=0; v<programacao.getQtdVarreruras(); v++)
                {
                    Varredura varredura = programacao.getVarredura(v);
                    Date d = varredura.getScheduleTime();
                    String strDate = formatDate.format(d);
                    String strTime = formatTime.format(d);
                    dataModel.setValueAt(strDate, row, 0);
                    dataModel.setValueAt(strTime, row, 1);

                    Well[] wells = Well.values();
                    for(int w=0; w<wells.length; w++)
                    {
                        Well well = wells[w];
                        CellIndexCalculator.Response ci_response = CellIndexCalculator.calcCellIndex(varredura, well);
                        dataModel.setValueAt(ci_response, row, w+2);
                    }

                    row++;

                }
            }
        }
        tblACTR.setModel(dataModel);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane = new javax.swing.JTabbedPane();
        pnlParametros = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstAgenda = new javax.swing.JList<>();
        btnCancelar = new javax.swing.JButton();
        btnIniciar = new javax.swing.JButton();
        btnAdicionar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        rdbZ_Zo_15 = new javax.swing.JRadioButton();
        rdbZ_Zo_17 = new javax.swing.JRadioButton();
        rdbZ_Zo = new javax.swing.JRadioButton();
        rdbZ = new javax.swing.JRadioButton();
        pnlTabela = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblACTR = new javax.swing.JTable();
        pnlGrafico = new javax.swing.JPanel();
        pnlGraficoLeft = new javax.swing.JPanel();
        pnlGraficoRight = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        chkA1 = new javax.swing.JCheckBox();
        chkB1 = new javax.swing.JCheckBox();
        chkC1 = new javax.swing.JCheckBox();
        chkD1 = new javax.swing.JCheckBox();
        chkE1 = new javax.swing.JCheckBox();
        chkF1 = new javax.swing.JCheckBox();
        chkG1 = new javax.swing.JCheckBox();
        chkH1 = new javax.swing.JCheckBox();
        chkA2 = new javax.swing.JCheckBox();
        chkB2 = new javax.swing.JCheckBox();
        chkC2 = new javax.swing.JCheckBox();
        chkD2 = new javax.swing.JCheckBox();
        chkE2 = new javax.swing.JCheckBox();
        chkF2 = new javax.swing.JCheckBox();
        chkG2 = new javax.swing.JCheckBox();
        chkH2 = new javax.swing.JCheckBox();
        chkNormalizar = new javax.swing.JCheckBox();
        pnlStatus = new javax.swing.JPanel();
        lblStatusEnsaio = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        mnuArquivo = new javax.swing.JMenu();
        mnuNovo = new javax.swing.JMenuItem();
        mnuAbrir = new javax.swing.JMenuItem();
        mnuSalvar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExportarTabela = new javax.swing.JMenuItem();
        mnuEngenharia = new javax.swing.JMenu();
        mnuImpedancias = new javax.swing.JMenuItem();
        mnuSobre = new javax.swing.JMenu();
        mnuACTRMigracao = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Análise Celular em Tempo Real 3.0 - Migração e Adesão");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jTabbedPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Agenda:");

        jScrollPane2.setViewportView(lstAgenda);

        btnCancelar.setText("Cancelar Ensaio");
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnIniciar.setText("Iniciar Ensaio");
        btnIniciar.setEnabled(false);
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        btnAdicionar.setText("[+]");
        btnAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdicionarActionPerformed(evt);
            }
        });

        btnExcluir.setText("[-]");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Fórmula para o cálculo de Cell Index:");

        buttonGroup1.add(rdbZ_Zo_15);
        rdbZ_Zo_15.setSelected(true);
        rdbZ_Zo_15.setText("CI = (Z-Zo)/15");
        rdbZ_Zo_15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbZ_Zo_15ActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbZ_Zo_17);
        rdbZ_Zo_17.setText("CI = (Z-Zo)/17");
        rdbZ_Zo_17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbZ_Zo_17ActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbZ_Zo);
        rdbZ_Zo.setText("CI = (Z-Zo)");
        rdbZ_Zo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbZ_ZoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdbZ);
        rdbZ.setText("CI = Z");
        rdbZ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbZActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(rdbZ_Zo_15)
                    .addComponent(rdbZ_Zo_17)
                    .addComponent(rdbZ_Zo)
                    .addComponent(rdbZ))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(rdbZ_Zo_15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbZ_Zo_17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbZ_Zo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rdbZ)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlParametrosLayout = new javax.swing.GroupLayout(pnlParametros);
        pnlParametros.setLayout(pnlParametrosLayout);
        pnlParametrosLayout.setHorizontalGroup(
            pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlParametrosLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlParametrosLayout.createSequentialGroup()
                        .addGroup(pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlParametrosLayout.createSequentialGroup()
                                .addComponent(btnAdicionar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnExcluir)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlParametrosLayout.createSequentialGroup()
                                .addComponent(btnIniciar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelar))
                            .addGroup(pnlParametrosLayout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)))))
                .addContainerGap())
        );
        pnlParametrosLayout.setVerticalGroup(
            pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlParametrosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlParametrosLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlParametrosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancelar)
                            .addComponent(btnIniciar)
                            .addComponent(btnAdicionar)
                            .addComponent(btnExcluir)))
                    .addGroup(pnlParametrosLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane.addTab("Parâmetros", pnlParametros);

        tblACTR.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Data", "Hora", "A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1", "A2", "B2", "C2", "D2", "E2", "F2", "G2", "H2"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblACTR.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblACTR.setShowGrid(true);
        tblACTR.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblACTR);

        javax.swing.GroupLayout pnlTabelaLayout = new javax.swing.GroupLayout(pnlTabela);
        pnlTabela.setLayout(pnlTabelaLayout);
        pnlTabelaLayout.setHorizontalGroup(
            pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 614, Short.MAX_VALUE)
        );
        pnlTabelaLayout.setVerticalGroup(
            pnlTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTabelaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
        );

        jTabbedPane.addTab("Tabela", pnlTabela);

        pnlGraficoLeft.setBackground(new java.awt.Color(0, 102, 102));
        pnlGraficoLeft.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout pnlGraficoLeftLayout = new javax.swing.GroupLayout(pnlGraficoLeft);
        pnlGraficoLeft.setLayout(pnlGraficoLeftLayout);
        pnlGraficoLeftLayout.setHorizontalGroup(
            pnlGraficoLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 448, Short.MAX_VALUE)
        );
        pnlGraficoLeftLayout.setVerticalGroup(
            pnlGraficoLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        pnlGraficoRight.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel3.setText("Mostrar gráfico:");

        chkA1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkA1.setText("A1");
        chkA1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkB1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkB1.setText("B1");
        chkB1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkC1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkC1.setText("C1");
        chkC1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkD1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkD1.setText("D1");
        chkD1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkE1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkE1.setText("E1");
        chkE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkF1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkF1.setText("F1");
        chkF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkG1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkG1.setText("G1");
        chkG1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkH1.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkH1.setText("H1");
        chkH1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkA2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkA2.setText("A2");
        chkA2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkB2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkB2.setText("B2");
        chkB2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkC2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkC2.setText("C2");
        chkC2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkD2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkD2.setText("D2");
        chkD2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkE2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkE2.setText("E2");
        chkE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkF2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkF2.setText("F2");
        chkF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkG2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkG2.setText("G2");
        chkG2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkH2.setFont(new java.awt.Font("Courier 10 Pitch", 0, 15)); // NOI18N
        chkH2.setText("H2");
        chkH2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkShowActionPerformed(evt);
            }
        });

        chkNormalizar.setText("Normalizar");
        chkNormalizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkNormalizarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGraficoRightLayout = new javax.swing.GroupLayout(pnlGraficoRight);
        pnlGraficoRight.setLayout(pnlGraficoRightLayout);
        pnlGraficoRightLayout.setHorizontalGroup(
            pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGraficoRightLayout.createSequentialGroup()
                        .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkD1)
                            .addComponent(chkE1)
                            .addComponent(chkF1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkD2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkE2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkF2, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGraficoRightLayout.createSequentialGroup()
                        .addComponent(chkH1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkH2))
                    .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                        .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(chkA1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chkB1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkA2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(chkB2, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                        .addComponent(chkC1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkC2))
                    .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                        .addComponent(chkG1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(chkG2))
                    .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                        .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(chkNormalizar))
                        .addGap(0, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlGraficoRightLayout.setVerticalGroup(
            pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoRightLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkA1)
                    .addComponent(chkA2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkB1)
                    .addComponent(chkB2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkC1)
                    .addComponent(chkC2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkD1)
                    .addComponent(chkD2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkE1)
                    .addComponent(chkE2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkF1)
                    .addComponent(chkF2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkG1)
                    .addComponent(chkG2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlGraficoRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkH1)
                    .addComponent(chkH2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                .addComponent(chkNormalizar)
                .addContainerGap())
        );

        javax.swing.GroupLayout pnlGraficoLayout = new javax.swing.GroupLayout(pnlGrafico);
        pnlGrafico.setLayout(pnlGraficoLayout);
        pnlGraficoLayout.setHorizontalGroup(
            pnlGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGraficoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlGraficoLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlGraficoRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlGraficoLayout.setVerticalGroup(
            pnlGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlGraficoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlGraficoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(pnlGraficoRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlGraficoLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane.addTab("Gráfico", pnlGrafico);

        pnlStatus.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatusEnsaio.setText("---");

        javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatusEnsaio)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblStatusEnsaio)
        );

        mnuArquivo.setText("Arquivo");

        mnuNovo.setText("Novo ensaio");
        mnuNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuNovoActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuNovo);

        mnuAbrir.setText("Abrir...");
        mnuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAbrirActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuAbrir);

        mnuSalvar.setText("Salvar...");
        mnuSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuSalvarActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuSalvar);
        mnuArquivo.add(jSeparator1);

        mnuExportarTabela.setText("Exportar tabela...");
        mnuExportarTabela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExportarTabelaActionPerformed(evt);
            }
        });
        mnuArquivo.add(mnuExportarTabela);

        jMenuBar.add(mnuArquivo);

        mnuEngenharia.setText("Engenharia");

        mnuImpedancias.setText("Medir Impedâncias...");
        mnuImpedancias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuImpedanciasActionPerformed(evt);
            }
        });
        mnuEngenharia.add(mnuImpedancias);

        jMenuBar.add(mnuEngenharia);

        mnuSobre.setText("Sobre");

        mnuACTRMigracao.setText("ACTR - Migração");
        mnuACTRMigracao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuACTRMigracaoActionPerformed(evt);
            }
        });
        mnuSobre.add(mnuACTRMigracao);

        jMenuBar.add(mnuSobre);

        setJMenuBar(jMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane)
                    .addComponent(pnlStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mnuImpedanciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuImpedanciasActionPerformed
        (new FrmImpedancias()).setVisible(true);
    }//GEN-LAST:event_mnuImpedanciasActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if(ensaio.getStatusEnsaio() == StatusEnsaio.EmAndamento)
        {
            int r = JOptionPane.showConfirmDialog(null, "Ensaio em andamento. Tem certeza que deseja sair do programa", "Sair", JOptionPane.YES_NO_OPTION);
            if(r == JOptionPane.NO_OPTION)
                return;
        }
        ensaio.save(INI_FILENAME);
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void btnAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdicionarActionPerformed
        DlgProgramacao dlgProgramacao = new DlgProgramacao(this, true);
        dlgProgramacao.setVisible(true);
        if(dlgProgramacao.ok_pressed)
        {
            int qtdVarreduras = (Integer)(dlgProgramacao.spnQtdVarreduras.getValue());
            int periodoMin = (Integer)(dlgProgramacao.spnPeriodoMin.getValue());
            Programacao programacao = new Programacao(ensaio.getAgenda(), qtdVarreduras, periodoMin);
            ensaio.getAgenda().AdicionarProgramacao(programacao);
            atualizarControles();
        }
        
    }//GEN-LAST:event_btnAdicionarActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
        int indices[] = lstAgenda.getSelectedIndices();
        for(int i=0; i<indices.length; i++)
        {
            DefaultListModel model = (DefaultListModel) lstAgenda.getModel();
            Programacao p = (Programacao) model.get(indices[i]);
            ensaio.getAgenda().removerProgramacao(p);
        }
        atualizarControles();
    }//GEN-LAST:event_btnExcluirActionPerformed


    
    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        ensaio.iniciarEnsaio();
        atualizarControles();
        

    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        if(ensaio.getStatusEnsaio() == StatusEnsaio.EmAndamento)
        {
            int r = JOptionPane.showConfirmDialog(this, "Deseja interromper e finalizar o ensaio em andamento?", "Cancelar", JOptionPane.YES_NO_OPTION);
            if(r==JOptionPane.OK_OPTION)
            {
                ensaio.finalizarEnsaio();
            }
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void mnuSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuSalvarActionPerformed
        FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.SAVE);
        fd.setVisible(true);
        if(fd.getFiles().length != 0)
        {
            String filename = fd.getFiles()[0].getAbsolutePath();
            ensaio.save(filename);
        }        
        
    }//GEN-LAST:event_mnuSalvarActionPerformed

    private void mnuNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuNovoActionPerformed
        
        switch (ensaio.getStatusEnsaio()) {
            case EmAndamento:
                JOptionPane.showMessageDialog(null, "Ensaio em andamento. Não é possível criar um novo neste momento.");
                break;
            case Finalizado:
                int r = JOptionPane.showConfirmDialog(null, "Deseja salvar o ensaio atual?", "Novo ensaio", JOptionPane.YES_NO_OPTION);
                if(r == JOptionPane.YES_OPTION)
                {
                    FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.SAVE);
                    fd.setVisible(true);
                    String filename = fd.getFile();
                    if (filename != null)
                    {
                        ensaio.save(filename);
                        ensaio = new Ensaio(this);
                    }
                }
                else
                {
                    ensaio = new Ensaio(this);
                }   break;
            default: //Em especificação
                ensaio = new Ensaio(this);
                break;
        }
        atualizarControles();
    }//GEN-LAST:event_mnuNovoActionPerformed

    private void mnuACTRMigracaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuACTRMigracaoActionPerformed
        (new DlgSobre(this, true)).setVisible(true);
    }//GEN-LAST:event_mnuACTRMigracaoActionPerformed

    private void mnuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuAbrirActionPerformed
        FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFiles().length != 0)
        {
            String filename = fd.getFiles()[0].getAbsolutePath();
            ensaio = Ensaio.load(filename, this);
        }
    }//GEN-LAST:event_mnuAbrirActionPerformed

    private void mnuExportarTabelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExportarTabelaActionPerformed

        FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.SAVE);
        fd.setVisible(true);
        
        if (fd.getFiles().length>0)
        {
            String filename = fd.getFiles()[0].getAbsolutePath();
            StringBuilder sb = new StringBuilder();
            for(int col=0; col<tblACTR.getColumnCount(); col++)
            {
                sb.append(tblACTR.getColumnName(col)+",");
            }
            sb.append("\n");
            
            for(int row = 0; row<tblACTR.getRowCount(); row++)
            {
                for(int col=0; col<tblACTR.getColumnCount(); col++)
                {
                    sb.append(tblACTR.getValueAt(row, col)+",");
                }
                sb.append("\n");
            }
            File file = new File(filename);

            BufferedWriter writer;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.append(sb.toString());
                writer.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, ex.toString());
            }
            

        }
        
    }//GEN-LAST:event_mnuExportarTabelaActionPerformed

    private void chkShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowActionPerformed
        ensaio.setVisibleGraph(Well.WELL_A1, chkA1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_B1, chkB1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_C1, chkC1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_D1, chkD1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_E1, chkE1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_F1, chkF1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_G1, chkG1.isSelected());
        ensaio.setVisibleGraph(Well.WELL_H1, chkH1.isSelected());

        ensaio.setVisibleGraph(Well.WELL_A2, chkA2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_B2, chkB2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_C2, chkC2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_D2, chkD2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_E2, chkE2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_F2, chkF2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_G2, chkG2.isSelected());
        ensaio.setVisibleGraph(Well.WELL_H2, chkH2.isSelected());
        
        atualizarControles();
    }//GEN-LAST:event_chkShowActionPerformed

    private void chkNormalizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkNormalizarActionPerformed
        ensaio.setNormalizedCi(chkNormalizar.isSelected());

        if(ensaio.getStatusEnsaio() != StatusEnsaio.EmEspecificacao && chkNormalizar.isSelected())
        {
            ensaio.updateVarreduraBase();
            
        }
        

        atualizarControles();
    }//GEN-LAST:event_chkNormalizarActionPerformed

    private void rdbZ_Zo_15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbZ_Zo_15ActionPerformed
        ensaio.setCIFormula(CIFormula.Z_Zo_15);
        atualizarControles();
    }//GEN-LAST:event_rdbZ_Zo_15ActionPerformed

    private void rdbZ_Zo_17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbZ_Zo_17ActionPerformed
        ensaio.setCIFormula(CIFormula.Z_Zo_17);
        atualizarControles();
    }//GEN-LAST:event_rdbZ_Zo_17ActionPerformed

    private void rdbZ_ZoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbZ_ZoActionPerformed
        ensaio.setCIFormula(CIFormula.Z_Zo);
        atualizarControles();
    }//GEN-LAST:event_rdbZ_ZoActionPerformed

    private void rdbZActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbZActionPerformed
        ensaio.setCIFormula(CIFormula.Z);
        atualizarControles();
    }//GEN-LAST:event_rdbZActionPerformed

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdicionar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnIniciar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox chkA1;
    private javax.swing.JCheckBox chkA2;
    private javax.swing.JCheckBox chkB1;
    private javax.swing.JCheckBox chkB2;
    private javax.swing.JCheckBox chkC1;
    private javax.swing.JCheckBox chkC2;
    private javax.swing.JCheckBox chkD1;
    private javax.swing.JCheckBox chkD2;
    private javax.swing.JCheckBox chkE1;
    private javax.swing.JCheckBox chkE2;
    private javax.swing.JCheckBox chkF1;
    private javax.swing.JCheckBox chkF2;
    private javax.swing.JCheckBox chkG1;
    private javax.swing.JCheckBox chkG2;
    private javax.swing.JCheckBox chkH1;
    private javax.swing.JCheckBox chkH2;
    private javax.swing.JCheckBox chkNormalizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JLabel lblStatusEnsaio;
    private javax.swing.JList<String> lstAgenda;
    private javax.swing.JMenuItem mnuACTRMigracao;
    private javax.swing.JMenuItem mnuAbrir;
    private javax.swing.JMenu mnuArquivo;
    private javax.swing.JMenu mnuEngenharia;
    private javax.swing.JMenuItem mnuExportarTabela;
    private javax.swing.JMenuItem mnuImpedancias;
    private javax.swing.JMenuItem mnuNovo;
    private javax.swing.JMenuItem mnuSalvar;
    private javax.swing.JMenu mnuSobre;
    private javax.swing.JPanel pnlGrafico;
    private javax.swing.JPanel pnlGraficoLeft;
    private javax.swing.JPanel pnlGraficoRight;
    private javax.swing.JPanel pnlParametros;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlTabela;
    private javax.swing.JRadioButton rdbZ;
    private javax.swing.JRadioButton rdbZ_Zo;
    private javax.swing.JRadioButton rdbZ_Zo_15;
    private javax.swing.JRadioButton rdbZ_Zo_17;
    private javax.swing.JTable tblACTR;
    // End of variables declaration//GEN-END:variables

    @Override
    public void varreduraRealizada(Ensaio ensaio) {
        this.ensaio = ensaio;
        ensaio.save(INI_FILENAME);
        atualizarControles();
    }

    @Override
    public void ensaioFinalizado(Ensaio ensaio) {
        this.ensaio = ensaio;
        atualizarControles();
    }

    @Override
    public void ensaioCriado(Ensaio ensaio) {
        this.ensaio = ensaio;
        atualizarControles();
    }
    
    public void atualizarGrafico()
    {
        while(pnlGraficoLeft.getComponentCount()>0)
            pnlGraficoLeft.remove(0);
        
        ACTRChartCI actrChart = new ACTRChartCI(ensaio);
        
        pnlGraficoLeft.setLayout(new BorderLayout());
        pnlGraficoLeft.add(actrChart,BorderLayout.CENTER);
        pnlGraficoLeft.validate();
    }

    @Override
    public void varreduraBaseAlterada(Ensaio ensaio) {
        this.ensaio = ensaio;
        atualizarControles();
    }
}
