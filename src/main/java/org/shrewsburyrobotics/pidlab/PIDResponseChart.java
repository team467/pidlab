package org.shrewsburyrobotics.pidlab;

import java.util.Formatter;

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

public class PIDResponseChart extends JFrame {
	private static final long serialVersionUID = 1L;

	public PIDResponseChart(String title) {
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
		final double targetDistance = 100.0;
		MotorModel motor = new MotorModel(1000, 2);
		PIDController controller = new PIDController(0.0060, 0.0, 0.015);

		XYSeries speedSeries = new XYSeries("Motor speed");
		XYSeries positionSeries = new XYSeries("Motor position");
		XYSeries driveSeries = new XYSeries("Drive");
		
		controller.setError(targetDistance);
		try (Formatter formatter = new Formatter(System.out)) {;
			for (int i = 0; i < 600; i++) {
				double drive = controller.calculate(motor.getPosition(), motor.getSpeed(),
						Constants.STEP_TIME_SEC, targetDistance);
				motor.step(drive);

				final double time = i * Constants.STEP_TIME_SEC;
				speedSeries.add(time, motor.getSpeed());
				positionSeries.add(time, motor.getPosition());
				driveSeries.add(time, drive*100);

				formatter.format("%f,%f,%f,%f\n", time, drive, motor.getSpeed(), motor.getPosition());
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(speedSeries);
		dataset.addSeries(positionSeries);
		dataset.addSeries(driveSeries);

		return dataset;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			PIDResponseChart example = new PIDResponseChart("PID Lab");
			example.setSize(1200, 800);
			example.setLocationRelativeTo(null);
			example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			example.setVisible(true);
		});
	}
}