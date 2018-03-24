package org.shrewsburyrobotics.pidlab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;

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

public class ImpulseResponseChart extends JFrame {
	private static final long serialVersionUID = 1L;

    private JTextField gainField = new JTextField("10", 6);
    private JTextField timeField = new JTextField("5", 4);
    private JTextField deadField = new JTextField("0.2", 4);
    private JTextField plotTimeField = new JTextField("10", 4);

    private MotorModel motor = new MotorModel(Double.parseDouble(gainField.getText()),
            Double.parseDouble(timeField.getText()),
            Double.parseDouble(deadField.getText()));

    private XYDataset dataset = createDataset(motor);

    public ImpulseResponseChart(String title) {
		super(title);

        // Create viewer panel in which to display the chart.
        ChartPanel chartPanel = new ChartPanel(createChart());

        // Create controller panels where we read input values from.
        JPanel motorPanel = createMotorPanel(chartPanel); 

        // Create the main panel containing all of the other panels.
        JPanel mainPanel = new JPanel();
        mainPanel.add(chartPanel);
        mainPanel.add(motorPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);
	}

    private JPanel createMotorPanel(ChartPanel chartPanel) {
        JPanel panel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

        JPanel gainPanel = initTextFieldPanel("Gain", gainField);
        JPanel timePanel = initTextFieldPanel("Time Constant", timeField);
        JPanel deadPanel = initTextFieldPanel("Dead Time", deadField);
        JPanel plotTimePanel = initTextFieldPanel("Plot Time (secs)", plotTimeField);

        JButton runButton = new JButton("Run");
        runButton.addActionListener((ActionEvent e) -> {
            chartPanel.setChart(createChart());
        });

        panel.add(gainPanel);
        panel.add(timePanel);
        panel.add(deadPanel);
        panel.add(plotTimePanel);
        panel.add(runButton);

        return panel;
    }

    public JFreeChart createChart() {
        try {
            motor = new MotorModel(Double.parseDouble(gainField.getText()),
                                   Double.parseDouble(timeField.getText()),
                                   Double.parseDouble(deadField.getText()));

            dataset = createDataset(motor);
        } catch (NumberFormatException e) {
            System.err.println("Invalid data in text fields");
        }

        // Create chart.
        boolean wantLegend = true;
        boolean wantTooltips = true;
        boolean wantURLs = false;

        JFreeChart chart = ChartFactory.createXYLineChart(
                "PID Response Simulation", "Time (sec)", "", dataset,
                PlotOrientation.VERTICAL, wantLegend, wantTooltips, wantURLs);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(2.0f));
        chart.getXYPlot().getRenderer().setSeriesStroke(1, new BasicStroke(2.0f));
        return chart;
    }

    private XYDataset createDataset(MotorModel model) {
		double plotTimeSecs = Double.parseDouble(plotTimeField.getText());
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);

		XYSeries speedSeries = new XYSeries("Motor speed");
		XYSeries positionSeries = new XYSeries("Motor position");
		for (int i = 0; i < numTicks/2; i++) {
			model.step(1.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
		}
		for (int i = numTicks/2; i < numTicks; i++) {
			model.step(0.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(speedSeries);
		dataset.addSeries(positionSeries);

		return dataset;
	}

	private JPanel initTextFieldPanel(String name, JTextField field) {
	    JPanel panel = new JPanel();
	    Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
	    panel.setBorder(BorderFactory.createTitledBorder(lineBorder));
	    panel.add(new JLabel(name));
	    panel.add(field);
	    return panel;
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