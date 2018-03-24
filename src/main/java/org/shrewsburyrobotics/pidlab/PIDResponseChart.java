package org.shrewsburyrobotics.pidlab;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class PIDResponseChart extends JFrame {
	private static final long serialVersionUID = 1L;

	private JTextField gainField = new JTextField("10", 6);
	private JTextField timeField = new JTextField("5", 4);
	private JTextField deadField = new JTextField("0.2", 4);
	private JTextField pField = new JTextField("0.0060", 6);
	private JTextField iField = new JTextField("0.0", 6);
	private JTextField dField = new JTextField("0.015", 6);
	private JTextField targetField = new JTextField("100", 4);

	private MotorModel motor = new MotorModel(Double.parseDouble(gainField.getText()),
											  Double.parseDouble(timeField.getText()),
											  Double.parseDouble(deadField.getText()));

	private PIDController controller = new PIDController(Double.parseDouble(pField.getText()),
														 Double.parseDouble(iField.getText()),
														 Double.parseDouble(dField.getText()));

	private XYDataset dataset = createDataset(motor, controller, Double.parseDouble(targetField.getText()));

	public PIDResponseChart(String title) {
		super(title);

		// Create viewer panel in which to display the chart.
		ChartPanel chartPanel = new ChartPanel(createPIDChart());

		// Create controller panels where we read input values from.
		JPanel motorPanel = createMotorPanel(); 
		JPanel pidPanel = createPidPanel();
		JPanel targetPanel = createTargetPanel(chartPanel);

		// Create the main panel containing all of the other panels.
		JPanel mainPanel = new JPanel();
		mainPanel.add(chartPanel);
		mainPanel.add(motorPanel);
		mainPanel.add(pidPanel);
		mainPanel.add(targetPanel);
		setContentPane(mainPanel);
	}

	private JPanel createMotorPanel() {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

		JPanel gainPanel = initTextFieldPanel("Gain", gainField);
		JPanel timePanel = initTextFieldPanel("Time Constant", timeField);
		JPanel deadPanel = initTextFieldPanel("Dead Time", deadField);

		panel.add(gainPanel);
		panel.add(timePanel);
		panel.add(deadPanel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		return panel;
	}

	private JPanel createPidPanel() {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "PID Constants"));

		JPanel pPanel = initTextFieldPanel("P", pField);
		JPanel iPanel = initTextFieldPanel("I", iField);
		JPanel dPanel = initTextFieldPanel("D", dField);

		panel.add(pPanel);
		panel.add(iPanel);
		panel.add(dPanel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 

		return panel;
	}

	private JPanel createTargetPanel(ChartPanel chartPanel) {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Target Panel"));

		JButton runButton = new JButton("Run");
		runButton.addActionListener((ActionEvent e) -> {
			chartPanel.setChart(createPIDChart());
		});

		panel.add(new JLabel("Target Distance"));
		panel.add(targetField);
		panel.add(runButton);

		return panel;
	}

	private XYDataset createDataset(MotorModel motor, PIDController controller, double targetDistance) {
		double plotTimeSecs = 12.0;
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);
	
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

	public JFreeChart createPIDChart() {
		motor = new MotorModel(query(gainField), query(timeField), query(deadField));
		controller = new PIDController(query(pField), query(iField), query(dField));
		dataset = createDataset(motor, controller, query(targetField));

		// Create chart.
		boolean wantLegend = true;
		boolean wantTooltips = true;
		boolean wantURLs = false;

		JFreeChart chart = ChartFactory.createXYLineChart(
				"PID Response Simulation", "Time (sec)", "", dataset,
				PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		return chart;
	}

	private JPanel initTextFieldPanel(String name, JTextField field) {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder));
		panel.add(new JLabel(name));
		panel.add(field);
		return panel;
	}

	public double query(JTextField field) {
		double result = 0.0;
		try {
			result =  Double.parseDouble(field.getText());
		} catch (NumberFormatException e) {
			field.setText("0.0");
			System.err.println(field.getName() + " is empty, defaulting to zero.");
		}
		return result;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			PIDResponseChart example = new PIDResponseChart("PID Lab");
			example.setSize(800, 600);
			example.setLocationRelativeTo(null);
			example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			example.setVisible(true);
		});
	}
}