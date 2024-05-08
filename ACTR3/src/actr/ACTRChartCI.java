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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

public class ACTRChartCI extends JPanel implements ChartMouseListener {

    private final Ensaio ensaio;
    private ChartPanel chartPanel;
    
    public ACTRChartCI(Ensaio ensaio) {
        this.ensaio = ensaio;
        initUI();
    }

    private void initUI() {

        
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        

        chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        
        chartPanel.addChartMouseListener(this);

    }

    private XYDataset createDataset() {
        var dataset = new TimeSeriesCollection();
        double min_ci = Double.MAX_VALUE;
        double max_ci = Double.MIN_VALUE;
        
        for(Well well: Well.values())
        {
            if(ensaio.isVisibleGraph(well))
            {
                TimeSeries series = new TimeSeries(well.getShortName());
                for(int p=0; p<ensaio.getAgenda().conteProgramacoes(); p++)
                {
                    Programacao programacao = ensaio.getAgenda().getProgramacao(p);
                    for(int v=0; v<programacao.getQtdVarreruras(); v++)
                    {
                        Varredura varredura = programacao.getVarredura(v);
                        CellIndexCalculator.Response r;
                        if(ensaio.isNormalizedCi())
                            r = CellIndexCalculator.calcNormalizedCellIndex(varredura, well, ensaio.getVarreduraBase());
                        else
                            r = CellIndexCalculator.calcCellIndex(varredura, well);
                        if(r.statusLeitura !=StatusLeitura.AindaNaoRealizada)
                        {
                            series.add(new Millisecond(varredura.getScheduleTime()), r.ci);
                            if(r.ci > max_ci) max_ci=r.ci;
                            if(r.ci < min_ci) min_ci=r.ci;
                            
                        }
                    }
                }
                dataset.addSeries(series);
            }
        }
        
        if(ensaio.isNormalizedCi() && min_ci != Double.MAX_VALUE && max_ci != Double.MIN_VALUE)
        {
            Date d = ensaio.getVarreduraBase().getScheduleTime();
            Date d1 = new Date(d.getTime()+1);
            TimeSeries series = new TimeSeries("");
            series.add(new Millisecond(d), min_ci);
            series.add(new Millisecond(d1), max_ci);
            dataset.addSeries(series);
            
        }
        
        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String strCellIndex = ensaio.isNormalizedCi()?"Normalized Cell Index":"Cell Index";
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "",
                "Hora",
                strCellIndex,
                dataset,
                
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();

        for(int i=0; i<ensaio.countVisibleGraph(); i++)
        {
            renderer.setSeriesStroke(i, new BasicStroke(3.0f));
            renderer.setSeriesVisibleInLegend(i, true);
        }

        


        
        renderer.setDefaultShapesVisible(false);
        

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        
        DateAxis domain = (DateAxis) plot.getDomainAxis();
        domain.setVerticalTickLabels(true);        
        
        chart.setTitle(new TextTitle("",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        
        if(ensaio.isNormalizedCi())
        {
            int x = cme.getTrigger().getX(); 
            int y = cme.getTrigger().getY();
            Point2D p = chartPanel.translateScreenToJava2D(new Point(x, y));

            XYPlot plot = chartPanel.getChart().getXYPlot();

            Rectangle2D dataArea = chartPanel.getChartRenderingInfo().getPlotInfo().getDataArea();

            double xx = plot.getDomainAxis().java2DToValue(p.getX(), dataArea, plot.getDomainAxisEdge());
            Date d =new Date((long)xx);

            ensaio.updateVarreduraBase(d);

            
        }
   }

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        
    }


}