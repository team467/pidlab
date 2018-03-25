package org.shrewsburyrobotics.pidlab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.shrewsburyrobotics.pidlab.model.Constants;
import org.shrewsburyrobotics.pidlab.model.MotorModel;

class ImpulseResponseChart extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private ChartPanel chartPanel;

    private JTextField gainField = new JTextField("10", 6);
    private JTextField timeField = new JTextField("5", 4);
    private JTextField deadField = new JTextField("0.2", 4);
    private JTextField plotTimeField = new JTextField("10", 4);

    public ImpulseResponseChart(String title) {
		super(title);

        // Create viewer panel in which to display the chart.
        chartPanel = new ChartPanel(createChart());

        // Create controller panels where we read input values from.
        JPanel motorPanel = createMotorPanel(chartPanel);
        motorPanel.setMaximumSize(new Dimension(700, 200));

        // Create the main panel containing all of the other panels.
        JPanel mainPanel = new JPanel();
        mainPanel.add(chartPanel);
        mainPanel.add(motorPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);
	}

    private JPanel initTextFieldPanel(String name, JTextField field) {
        // Create a label, text field, and a panel to contain them.
        JPanel panel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(lineBorder));
        panel.add(new JLabel(name));
        panel.add(field);

        // If the text field changes, notify this class to re-render.
        field.addActionListener(this);

        return panel;
    }

    private JPanel createMotorPanel(ChartPanel chartPanel) {
        JPanel panel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

        JPanel gainPanel = initTextFieldPanel("Gain", gainField);
        JPanel timePanel = initTextFieldPanel("Time Constant", timeField);
        JPanel deadPanel = initTextFieldPanel("Dead Time", deadField);
        JPanel plotTimePanel = initTextFieldPanel("Plot Time (secs)", plotTimeField);

        panel.add(gainPanel);
        panel.add(timePanel);
        panel.add(deadPanel);
        panel.add(plotTimePanel);

        return panel;
    }

    private JFreeChart createChart() {
        // Get the data.
        XYSeriesCollection simulationData = createSimulationData();
        XYSeriesCollection recordedData = readRecordedData("test.data");

        // Create the renderers.
        StandardXYItemRenderer simulationDataRenderer = new StandardXYItemRenderer();
        XYLineAndShapeRenderer recordedDataRenderer = new XYLineAndShapeRenderer(false, true);   // Shapes only
        
        // Create the plot and axes.
        XYPlot plot = new XYPlot();
        plot.setDomainAxis(new NumberAxis("Time"));
        plot.setRangeAxis(new NumberAxis("Value"));

        // Add the simulated data to the plot.
        plot.setDataset(0, simulationData);
        plot.setRenderer(0, simulationDataRenderer);
        simulationDataRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        simulationDataRenderer.setSeriesStroke(1, new BasicStroke(2.0f));

        // Add the recorded data to the plot.
        plot.setDataset(1, recordedData);
        plot.setRenderer(1, recordedDataRenderer);

        // Return a complete chart created from the plot.
        JFreeChart chart = new JFreeChart("Impulse Response Simulation", plot);
        return chart;
    }

    private XYSeriesCollection createSimulationData() {
		double plotTimeSecs = Double.parseDouble(plotTimeField.getText());
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);

		// Create a motor model based on the current settings from the UI.
        MotorModel model = new MotorModel(Double.parseDouble(gainField.getText()),
                Double.parseDouble(timeField.getText()),
                Double.parseDouble(deadField.getText()));

        // Create the data series in which to store the data.
		XYSeries speedSeries = new XYSeries("Motor speed");
		XYSeries positionSeries = new XYSeries("Motor position");

		// Run the simulation.
		for (int i = 0; i < numTicks/2; i++) {
			model.step(1.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
			System.out.println(i * Constants.STEP_TIME_SEC + ",1.0," + model.getSpeed()
			        + "," + model.getPosition());
		}
		for (int i = numTicks/2; i < numTicks; i++) {
			model.step(0.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
            System.out.println(i * Constants.STEP_TIME_SEC + ",0.0," + model.getSpeed()
                    + "," + model.getPosition());
		}

		// Aggregate the data series into a single data set.
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(speedSeries);
		dataset.addSeries(positionSeries);

		return dataset;
	}

	private XYSeriesCollection readRecordedData(String fileName) {
        // Create the data series in which to store the data.
        final XYSeries inputSeries = new XYSeries("Actual Input");
        final XYSeries speedSeries = new XYSeries("Actual Speed");
        final XYSeries positionSeries = new XYSeries("Actual Position");

        // Read the data from the file.
        // The expected format for a line of data is:
        //     time,input,speed,position
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int lineNum = 0;;
            String line;
            while ((line = br.readLine()) != null) {
                // Parse the line.
                lineNum++;
                String[] values = line.split(",");
                if (values.length != 4) {
                    System.out.println("Wrong number of values line " + lineNum
                            + ", found " + values.length + " items, expected 4.");
                    continue;
                }

                // Gather the parsed data into the series.
                final double time = Double.parseDouble(values[0]);
                inputSeries.add(time, Double.parseDouble(values[1]));
                speedSeries.add(time, Double.parseDouble(values[2]));
                positionSeries.add(time, Double.parseDouble(values[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Aggregate whatever data we've successfully read.
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(inputSeries);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		chartPanel.setChart(createChart());
	}
}