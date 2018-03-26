package org.shrewsburyrobotics.pidlab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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

	private JTextField gainField = new JTextField("10", 6);
	private JTextField timeField = new JTextField("5", 4);
	private JTextField deadField = new JTextField("0.2", 4);
	private JTextField pField = new JTextField("0.0060", 6);
	private JTextField iField = new JTextField("0.0", 6);
	private JTextField dField = new JTextField("0.015", 6);
	private JTextField targetField = new JTextField("100", 4);
	private JTextField durationField = new JTextField("10", 4);

	private JRadioButton pButton = new JRadioButton("P");
	private JRadioButton iButton = new JRadioButton("I");
	private JRadioButton dButton = new JRadioButton("D");
	private ButtonGroup pidSelector = new ButtonGroup();

	private ChartPanel chartPanel = new ChartPanel(createPIDChart());

	private XYDataset dataset = createDataset(query(pField), query(iField), query(dField), query(targetField));

	public PIDResponseChart(String title) {
		super(title);

		// Create controller panels where we read input values from.
		JPanel UIPanel = new JPanel();
		UIPanel.add(createMotorPanel()); 
		UIPanel.add(createPidPanel());
		UIPanel.add(createPlotPanel());
		UIPanel.setMaximumSize(new Dimension(800, 100));

		// Create the main panel containing all of the other panels.
		JPanel mainPanel = new JPanel();
		mainPanel.add(chartPanel);
		mainPanel.add(UIPanel);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); 
		setContentPane(mainPanel);
}

	private JPanel createMotorPanel() {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

		gainField.setName("Gain");
		timeField.setName("Time Constant");
		deadField.setName("Dead Time");

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

		pField.setName("P");
		iField.setName("I");
		dField.setName("D");

		JPanel pPanel = initTextFieldPanel("P", pField);
		JPanel iPanel = initTextFieldPanel("I", iField);
		JPanel dPanel = initTextFieldPanel("D", dField);

		panel.add(pPanel);
		panel.add(iPanel);
		panel.add(dPanel);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); 

		return panel;
	}

	private JPanel createPlotPanel() {
		JPanel panel = new JPanel();
		Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
		panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Plot Settings"));

		targetField.setName("Target Distance");

		pidSelector.add(pButton);
		pidSelector.add(iButton);
		pidSelector.add(dButton);

		targetField.addActionListener(this);
		durationField.addActionListener(this);
		pButton.addActionListener(this);
		iButton.addActionListener(this);
		dButton.addActionListener(this);

		pButton.setSelected(true);

		JPanel pidSelectorPanel = new JPanel();
		pidSelectorPanel.add(pButton);
		pidSelectorPanel.add(iButton);
		pidSelectorPanel.add(dButton);
		pidSelectorPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Tune Parameter"));

		panel.add(new JLabel("Target Distance:"));
		panel.add(targetField);
		panel.add(new JLabel("Duration:"));
		panel.add(durationField);
		panel.add(pidSelectorPanel);

		return panel;
	}

	private MotorModel makeMotor() {
		return new MotorModel(Double.parseDouble(gainField.getText()),
			  Double.parseDouble(timeField.getText()),
			  Double.parseDouble(deadField.getText()));
	}

	private XYDataset createDataset(double kP, double kI, double kD, double targetDistance) {
		double plotTimeSecs = query(durationField);
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);

		PIDController mainController = new PIDController(kP, kI, kD);
		MotorModel mainMotor = makeMotor();
		XYSeries mainSeries = new XYSeries("Set");

		PIDController plusController;
		MotorModel plusMotor = makeMotor();
		XYSeries plusSeries = new XYSeries("Plus 50%");

		PIDController minusController;
		MotorModel minusMotor = makeMotor();
		XYSeries minusSeries = new XYSeries("Minus 50%");

		XYSeries targetSeries = new XYSeries("Target");

		if (pButton.isSelected()) {
			plusController = new PIDController(1.5*kP, kI, kD);
			minusController = new PIDController(0.5*kP, kI, kD);
		} else if (iButton.isSelected()) {
			plusController = new PIDController(kP, 1.5*kI, kD);
			minusController = new PIDController(kP, 0.5*kI, kD);
		} else { // Neither P nor I, must be D
			plusController = new PIDController(kP, kI, 1.5*kD);
			minusController = new PIDController(kP, kI, 0.5*kD);
		}

		mainController.setError(targetDistance);
		plusController.setError(targetDistance);
		minusController.setError(targetDistance);

		try (Formatter formatter = new Formatter(System.out)) {
			for (int i = 0; i < numTicks; i++) {
				final double time = i * Constants.STEP_TIME_SEC;

				iterate(mainMotor, mainController, mainSeries, targetDistance, time);
				iterate(plusMotor, plusController, plusSeries, targetDistance, time);
				iterate(minusMotor, minusController, minusSeries, targetDistance, time);
				targetSeries.add(time, targetDistance);

				formatter.format("%f,%f\n", time, mainMotor.getPosition());
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(mainSeries);
		dataset.addSeries(plusSeries);
		dataset.addSeries(minusSeries);
		dataset.addSeries(targetSeries);

		return dataset;
	}

	private void iterate(MotorModel motor, PIDController controller, XYSeries series, double targetDistance, double time) {
		double drive = controller.calculate(motor.getPosition(), motor.getSpeed(),
				Constants.STEP_TIME_SEC, targetDistance);
		motor.step(drive);
		series.add(time, motor.getPosition());
	}

	public JFreeChart createPIDChart() {
		dataset = createDataset(query(pField), query(iField), query(dField), query(targetField));

		// Create chart.
		boolean wantLegend = true;
		boolean wantTooltips = true;
		boolean wantURLs = false;

		JFreeChart chart = ChartFactory.createXYLineChart(
				"PID Response Simulation", "Time (sec)", "", dataset,
				PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(3.0F));
		chart.getXYPlot().getRenderer().setSeriesStroke(3, new BasicStroke(3.0F));
		return chart;
	}

	private JPanel initTextFieldPanel(String name, JTextField field) {
		JPanel panel = new JPanel();
		panel.add(new JLabel(name + ":"));
		field.addActionListener(this);
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
			example.setSize(800, 680);
			example.setLocationRelativeTo(null);
			example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			example.setVisible(true);
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Update chart when anything changes
		chartPanel.setChart(createPIDChart());
	}
}