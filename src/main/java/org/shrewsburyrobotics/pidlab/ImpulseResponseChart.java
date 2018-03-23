package org.shrewsburyrobotics.pidlab;

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
import org.shrewsburyrobotics.pidlab.model.Constants;
import org.shrewsburyrobotics.pidlab.model.MotorModel;

public class ImpulseResponseChart extends JFrame {
	private static final long serialVersionUID = 1L;

	public ImpulseResponseChart(String title) {
		super(title);

		// Create dataset.
		XYDataset dataset = createDataset();

		// Create chart.
		boolean wantLegend = true;
		boolean wantTooltips = true;
		boolean wantURLs = false;
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Motor Simulation", "Time (sec)", "", dataset,
				PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);

		// Create panel in which to display the chart.
		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);
	}

	private XYDataset createDataset() {
        MotorModel model = new MotorModel(1000, 1.0, 1.0);

		XYSeries speedSeries = new XYSeries("Motor speed");
		XYSeries positionSeries = new XYSeries("Motor position");
		for (int i = 0; i < 100; i++) {
			model.step(1.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
		}
		for (int i = 100; i < 200; i++) {
			model.step(0.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(speedSeries);
		dataset.addSeries(positionSeries);

		return dataset;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			ImpulseResponseChart example = new ImpulseResponseChart("PID Lab");
			example.setSize(1200, 800);
			example.setLocationRelativeTo(null);
			example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			example.setVisible(true);
		});
	}
}