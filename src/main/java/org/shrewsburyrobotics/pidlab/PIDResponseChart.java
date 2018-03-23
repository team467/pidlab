package org.shrewsburyrobotics.pidlab;

import java.util.Formatter;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
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
import org.shrewsburyrobotics.pidlab.model.PIDController;

public class PIDResponseChart extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JPanel UIPanel;

	private JSlider pSlider;
	private JSlider iSlider;
	private JSlider dSlider;

	public PIDResponseChart(String title) {
		super(title);

		// Create dataset.
		XYDataset dataset = createDataset();

		mainPanel = new JPanel();
		UIPanel = new JPanel();

		// Create chart.
		boolean wantLegend = true;
		boolean wantTooltips = true;
		boolean wantURLs = false;
		JFreeChart chart = ChartFactory.createScatterPlot(
				"Motor Simulation", "Time (sec)", "", dataset,
				PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);

		// Create panel in which to display the chart.
		ChartPanel chartPanel = new ChartPanel(chart);
		mainPanel.add(chartPanel);
		setContentPane(mainPanel);
	}

	private XYDataset createDataset() {
		double plotTimeSecs = 12.0;
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);

		final double targetDistance = 100.0;
		MotorModel motor = new MotorModel(1000, 2, 0.0);
		PIDController controller = new PIDController(0.0060, 0.0, 0.015);

		XYSeries speedSeries = new XYSeries("Motor speed");
		XYSeries positionSeries = new XYSeries("Motor position");
		XYSeries driveSeries = new XYSeries("Drive");

		controller.setError(targetDistance);
		try (Formatter formatter = new Formatter(System.out)) {;
		for (int i = 0; i < numTicks; i++) {
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