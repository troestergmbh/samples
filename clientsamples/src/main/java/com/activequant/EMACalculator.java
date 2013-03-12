package com.activequant;

import com.activequant.domainmodel.TimeFrame;
import com.activequant.domainmodel.TimeStamp;
import com.activequant.timeseries.TSContainer2;

/**

 * @author GhostRider
 * 
 */
public class EMACalculator {

	private int period = 0;
	private TSContainer2 d;
	private double lambda = 1.0;
	private double lastEma = 0.0;

	public double getLastEma() {
		return lastEma;
	}

	
	//
	public EMACalculator(int period, TimeFrame timeFrame) {
		d = new TSContainer2("");
		lambda = 2.0 / ((double) period + 1.0);
		this.period = period;
		d.setResolutionInNanoseconds(timeFrame.getNanoseconds());
	}

	public void update(TimeStamp ts, Double val) {
		boolean newVal = false;
		int cn = d.getNumRows();
		d.setValue("VAL", ts, val);
		if (d.getNumRows() > period * 2) {
			d.delete(0);
			newVal = true;
		}

		if (cn != d.getNumRows())
			newVal = true;
		// calculate ...
		if (d.getNumRows() <= period) {
			// let's calculate the moving average.
			// ...
			double sma = 0.0;
			for (int i = 0; i < d.getNumRows(); i++) {
				sma += (Double) d.getColumns().get(0).get(i);
			}
			sma /= d.getNumRows();
			lastEma = sma;
			d.setValue("EMA", ts, sma);
		} else {
			// take the last EMA value and add the current weighted value ...
			double ema = lambda * val + (1 - lambda) * lastEma;
			d.setValue("EMA", ts, ema);
			if (newVal)
				lastEma = ema;
		}
		// if (newVal)
		// 	System.out.println(lastEma);
	}
	
	
}
