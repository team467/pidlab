package org.shrewsburyrobotics.pidlab;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.shrewsburyrobotics.pidlab.model.Constants;
import org.shrewsburyrobotics.pidlab.model.MotorModel;
import org.shrewsburyrobotics.pidlab.model.Robot2018Model;

class ImpulseResponseChart extends JFrame { //implements ActionListener {
	private static final long serialVersionUID = 1L;

	// Data set indices.
    static final int SIMULATION_SPEED = 0;
    static final int SIMULATION_POSITION = 2;
    static final int RECORDED_SPEED = 1;
    static final int RECORDED_POSITION = 3;

    // Axes indices.
    static final int TIME_AXIS = 0;
    static final int SPEED_AXIS = 0;
    static final int POSITION_AXIS = 1;
    
    private JTextField gainField = new JTextField("10", 6);
    private JTextField timeField = new JTextField("5", 4);
    private JTextField deadField = new JTextField("0.2", 4);
    private JTextField zoneField = new JTextField("0.1", 4);
    private JTextField plotTimeField = new JTextField("10", 4);

    public ImpulseResponseChart(String title) {
		super(title);

        // Create viewer panel in which to display the chart.
        ChartPanel chartPanel = new ChartPanel(createChart());

        // Create controller panels where we read input values from.
        JPanel motorPanel = createMotorPanel(chartPanel);
        motorPanel.setMaximumSize(new Dimension(2000, 200));

        // Create the main panel containing all of the other panels.
        JPanel mainPanel = new JPanel();
        mainPanel.add(chartPanel);
        mainPanel.add(motorPanel);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);
	}

    private JPanel initTextFieldPanel(String name, JTextField field, ChartPanel chartPanel) {
        // Create a label, text field, and a panel to contain them.
        JPanel panel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(lineBorder));
        panel.add(new JLabel(name));
        panel.add(field);

        // If the text field changes, notify this class to re-render.
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setChart(createChart());
            }
        });

        return panel;
    }

    private JPanel createMotorPanel(ChartPanel chartPanel) {
        JPanel panel = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.BLACK);
        panel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Motor Properties"));

        JPanel gainPanel = initTextFieldPanel("Gain (ft/sec)", gainField, chartPanel);
        JPanel timePanel = initTextFieldPanel("Time Constant (sec)", timeField, chartPanel);
        JPanel deadPanel = initTextFieldPanel("Dead Time (sec)", deadField, chartPanel);
        JPanel zonePanel = initTextFieldPanel("Dead Zone (%V)", zoneField, chartPanel);
        JPanel plotTimePanel = initTextFieldPanel("Plot Time (sec)", plotTimeField, chartPanel);

        panel.add(gainPanel);
        panel.add(timePanel);
        panel.add(deadPanel);
        panel.add(zonePanel);
        panel.add(plotTimePanel);

        return panel;
    }

    private JFreeChart createChart() {
        // Get the data.
        final XYSeriesCollection simulationData = createSimulationData();
        final XYSeriesCollection recordedData = readRecordedData("Robot467.log");
        
        // Create the plot.
        final XYPlot plot = new XYPlot();

        // Setup a renderer for each data series.
        addRendererToSeries(plot, SIMULATION_SPEED, Color.RED);
        addRendererToSeries(plot, SIMULATION_POSITION, Color.BLUE);
        addRendererToSeries(plot, RECORDED_SPEED, Color.PINK);
        addRendererToSeries(plot, RECORDED_POSITION, Color.CYAN);
        
        // Set the axes.
        plot.setDomainAxis(new NumberAxis("Time (sec)"));
        plot.setRangeAxis(SPEED_AXIS, new NumberAxis("Speed (ft/sec)"));
        plot.setRangeAxis(POSITION_AXIS, new NumberAxis("Position (ft)"));

        // Add the data to the plot.
        plot.setDataset(SIMULATION_SPEED, new XYSeriesCollection(simulationData.getSeries(0)));
        plot.setDataset(SIMULATION_POSITION, new XYSeriesCollection(simulationData.getSeries(1)));
        plot.setDataset(RECORDED_SPEED, new XYSeriesCollection(recordedData.getSeries(0)));
        plot.setDataset(RECORDED_POSITION, new XYSeriesCollection(recordedData.getSeries(1)));

        // Apply same X-axis to all data sets.
        plot.mapDatasetToDomainAxis(SIMULATION_SPEED, TIME_AXIS);
        plot.mapDatasetToDomainAxis(SIMULATION_POSITION, TIME_AXIS);
        plot.mapDatasetToDomainAxis(RECORDED_SPEED, TIME_AXIS);
        plot.mapDatasetToDomainAxis(RECORDED_POSITION, TIME_AXIS);

        // Each data set gets its own Y-axis.
        plot.mapDatasetToRangeAxis(SIMULATION_SPEED, SPEED_AXIS);
        plot.mapDatasetToRangeAxis(SIMULATION_POSITION, POSITION_AXIS);
        plot.mapDatasetToRangeAxis(RECORDED_SPEED, SPEED_AXIS);
        plot.mapDatasetToRangeAxis(RECORDED_POSITION, POSITION_AXIS);

        // Return a complete chart created from the plot.
        final JFreeChart chart = new JFreeChart("Impulse Response Simulation", plot);
        return chart;
    }

    private void addRendererToSeries(final XYPlot plot, int seriesIndex, Color color) {
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        plot.setRenderer(seriesIndex, renderer);
        renderer.setSeriesStroke(0, new BasicStroke(2.0F));
        renderer.setSeriesPaint(0, color);
    }

    private XYSeriesCollection createSimulationData() {
		double plotTimeSecs = Double.parseDouble(plotTimeField.getText());
		int numTicks = (int)(plotTimeSecs / Constants.STEP_TIME_SEC);

		// Create a motor model based on the current settings from the UI.
        MotorModel model = new Robot2018Model(
        		query(gainField), query(timeField), query(deadField), query(zoneField));

        // Create the data series in which to store the data.
		XYSeries speedSeries = new XYSeries("Simulated speed");
		XYSeries positionSeries = new XYSeries("Simulated position");

		// Run the simulation.
		for (int i = 0; i < numTicks; i++) {
			model.step(1.0);
			speedSeries.add(i * Constants.STEP_TIME_SEC, model.getSpeed());
			positionSeries.add(i * Constants.STEP_TIME_SEC, model.getPosition());
//			System.out.println(i * Constants.STEP_TIME_SEC + ",1.0," + model.getSpeed()
//			        + "," + model.getPosition() + "," + model.getAcceleration());
		}

		// Aggregate the data series into a single data set.
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(speedSeries);
		dataset.addSeries(positionSeries);

		return dataset;
	}

	private XYSeriesCollection readRecordedData(String fileName) {
        // Create the data series in which to store the data.
        final XYSeries speedSeries = new XYSeries("Recorded speed");
        final XYSeries positionSeries = new XYSeries("Recorded position");

        double timeCorrection = Double.MAX_VALUE;
        double posCorrection = Double.MAX_VALUE;
        
        // Read the data from the file.
        // The expected format for a line of data is:
        //     time,input,speed,position
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            int lineNum = 0;
            String line;
            while ((line = br.readLine()) != null) {
                // Parse the line.
                lineNum++;

                String[] tokens = line.split(" ");                
                if (tokens.length != 5) {
                    System.out.println("Wrong number of tokens line " + lineNum
                            + ", found " + tokens.length + " items, expected 5.");
                    continue;
                }

                // Gather the parsed data into the series.
                String timeStr = tokens[0].replaceAll("ms", "");
                double time = Double.parseDouble(timeStr)/1000.0;
                
                if (time < timeCorrection) {
                	timeCorrection = time;
                }
                time -= timeCorrection;

                String[] values = tokens[4].split(",");
                if (values.length != 6) {
                    System.out.println("Wrong number of values line " + lineNum
                            + ", found " + values.length + " items, expected 6.");
                    continue;
                }

                double position = -Double.parseDouble(values[5]);
                if (position < posCorrection) {
                	posCorrection = position;
                }
                position -= posCorrection;

                // Skipping the "input" value at position 1.
                speedSeries.add(time, -Double.parseDouble(values[4]));
                positionSeries.add(time, position);
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Aggregate whatever data we've successfully read.
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(speedSeries);
        dataset.addSeries(positionSeries);
        return dataset;
    }

	public double query(JTextField field) {
		double result = 0.0;
		try {
			result = Double.parseDouble(field.getText());
		} catch (NumberFormatException e) {
			field.setText("0.0");
			System.err.println(field.getName() + " is invalid, defaulting to zero.");
		}
		return result;
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
