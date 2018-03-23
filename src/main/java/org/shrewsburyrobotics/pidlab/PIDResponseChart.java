package org.shrewsburyrobotics.pidlab;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

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

public class PIDResponseChart extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel mainPanel;
	private JPanel pidPanel;
	private JPanel pPanel;
	private JPanel iPanel;
	private JPanel dPanel;

	private Border lineBorder;

	private JTextField pField;
	private JTextField iField;
	private JTextField dField;
	private JButton runButton;

	public PIDResponseChart(String title) {
		super(title);

		// Create dataset.
		XYDataset dataset = createDataset();

		lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		mainPanel = new JPanel();
		pidPanel = new JPanel();
		pidPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "PID Controls"));

		pField = new JTextField(4);
		pField.setName("P");

		iField = new JTextField(4);
		iField.setName("I");

		dField = new JTextField(4);
		dField.setName("D");

		pPanel = initPIDPanel("P", pField);
		iPanel = initPIDPanel("I", iField);
		dPanel = initPIDPanel("D", dField);

		runButton = new JButton("Run");
		runButton.addActionListener(this);

		pidPanel.add(pPanel);
		pidPanel.add(iPanel);
		pidPanel.add(dPanel);
		pidPanel.add(runButton);

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
		mainPanel.add(pidPanel);
		setContentPane(mainPanel);
	}

	private JPanel initPIDPanel(String name, JTextField field) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, name));
		panel.add(field);
		return panel;
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
		try (Formatter formatter = new Formatter(System.out)) {
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == runButton) {
			System.out.println("Running Simulation");
			// TODO Run the simulation here
		}
	}
}