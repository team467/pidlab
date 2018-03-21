package org.petrangelo.pidlab;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author imssbora
 */
public class ScatterPlotExample extends JFrame {
  private static final long serialVersionUID = 6294689542092367723L;

  public ScatterPlotExample(String title) {
    super(title);

    // Create dataset
    XYDataset dataset = createDataset();

    // Create chart
    boolean wantLegend = false;
    boolean wantTooltips = false;
    boolean wantURLs = false;    
    JFreeChart chart = ChartFactory.createScatterPlot(
        "Motor Speed", "X-Axis", "Y-Axis", dataset,
        PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);
    
    // Create Panel
    ChartPanel panel = new ChartPanel(chart);
    setContentPane(panel);
  }

  private XYDataset createDataset() {
    XYSeriesCollection dataset = new XYSeriesCollection();

    XYSeries series1 = new XYSeries("Motor speed");
    MotorModel model = new MotorModel(100, 15);
    for (int i = 0; i < 100; i++) {
    	model.step(1.0);
		System.out.println(i + "," + model.getSpeed());
    	series1.add(i, model.getSpeed());
    }
    for (int i = 100; i < 200; i++) {
    	model.step(0.5);
		System.out.println(i + "," + model.getSpeed());
    	series1.add(i, model.getSpeed());
    }

    dataset.addSeries(series1);
    
    return dataset;
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      ScatterPlotExample example = new ScatterPlotExample("Motor Stuff");
      example.setSize(800, 400);
      example.setLocationRelativeTo(null);
      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      example.setVisible(true);
    });
  }
}