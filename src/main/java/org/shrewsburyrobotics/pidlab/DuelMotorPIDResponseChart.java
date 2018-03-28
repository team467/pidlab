package org.shrewsburyrobotics.pidlab;

import javax.swing.SwingUtilities;

public class DuelMotorPIDResponseChart {
	private PIDResponseChart chart1;
	private PIDResponseChart chart2;

	public DuelMotorPIDResponseChart(String title1, String title2) {
		chart1 = new PIDResponseChart(title1);
		chart2 = new PIDResponseChart(title2);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DuelMotorPIDResponseChart example = new DuelMotorPIDResponseChart("Left", "Right");
			example.chart1.init();
			example.chart2.init();
		});
	}
}
