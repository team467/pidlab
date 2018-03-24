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
	private JPanel mainPanel;
	private ChartPanel chartPanel;
	private JPanel motorPanel;
	private JPanel pidPanel;

	private JPanel gainPanel;
	private JTextField gainField;

	private JPanel timePanel;
	private JTextField timeField;

	private JPanel deadPanel;
	private JTextField deadField;

	private JPanel pPanel;
	private JTextField pField;

	private JPanel iPanel;
	private JTextField iField;

	private JPanel dPanel;
	private JTextField dField;

	private Border lineBorder;

	private JPanel targetPanel;
	private JTextField targetField;
	private JButton runButton;

	private MotorModel motor;
	private PIDController controller;
	private XYDataset dataset;

	public PIDResponseChart(String title) {
		super(title);

		// TODO Better initialized defaults?
		motor = new MotorModel(0.0, 0.0, 0.0);
		controller = new PIDController(0.0, 0.0, 0.0);
		dataset = createDataset(motor, controller, 0.0);

		lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		mainPanel = new JPanel();

		motorPanel = new JPanel();
		motorPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

		pidPanel = new JPanel();
		pidPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "PID Constants"));

		targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Target Panel"));

		gainField = new JTextField(4);
		gainField.setName("Gain");
		gainPanel = initTextFieldPanel("Gain", gainField);

		timeField = new JTextField(4);
		timeField.setName("Time Constant");
		timePanel = initTextFieldPanel("Time Constant", timeField);

		deadField = new JTextField(4);
		deadField.setName("Dead Time");
		deadPanel = initTextFieldPanel("Dead Time", deadField);

		motorPanel.add(gainPanel);
		motorPanel.add(timePanel);
		motorPanel.add(deadPanel);
		motorPanel.setLayout(new BoxLayout(motorPanel, BoxLayout.Y_AXIS)); 

		pField = new JTextField(4);
		pField.setName("P");
		pPanel = initTextFieldPanel("P", pField);

		iField = new JTextField(4);
		iField.setName("I");
		iPanel = initTextFieldPanel("I", iField);

		dField = new JTextField(4);
		dField.setName("D");
		dPanel = initTextFieldPanel("D", dField);

		targetField = new JTextField(4);
		targetField.setName("Target Distance");

		runButton = new JButton("Run");
		runButton.addActionListener((ActionEvent e) -> {
			chartPanel.setChart(createPIDChart());
		});

		targetPanel.add(new JLabel("Target Distance"));
		targetPanel.add(targetField);
		targetPanel.add(runButton);

		pidPanel.add(pPanel);
		pidPanel.add(iPanel);
		pidPanel.add(dPanel);
		pidPanel.setLayout(new BoxLayout(pidPanel, BoxLayout.Y_AXIS)); 

		// Create panel in which to display the chart.
		chartPanel = new ChartPanel(createPIDChart());
		mainPanel.add(chartPanel);
		mainPanel.add(motorPanel);
		mainPanel.add(pidPanel);
		mainPanel.add(targetPanel);
		setContentPane(mainPanel);
	}

	public JFreeChart createPIDChart() {
		try {
			motor = new MotorModel(Double.parseDouble(gainField.getText()),
								   Double.parseDouble(timeField.getText()),
								   Double.parseDouble(deadField.getText()));

			controller = new PIDController(Double.parseDouble(pField.getText()),
										   Double.parseDouble(iField.getText()),
										   Double.parseDouble(dField.getText()));

			dataset = createDataset(motor, controller, Double.parseDouble(targetField.getText()));

		} catch (NumberFormatException e) {
			System.err.println("Invalid data in text fields");
		}

		// Create chart.
		boolean wantLegend = true;
		boolean wantTooltips = true;
		boolean wantURLs = false;

		return ChartFactory.createXYLineChart(
				"Motor Simulation", "Time (sec)", "", dataset,
				PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);
	}

	private JPanel initTextFieldPanel(String name, JTextField field) {
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder));
		panel.add(new JLabel(name));
		panel.add(field);
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