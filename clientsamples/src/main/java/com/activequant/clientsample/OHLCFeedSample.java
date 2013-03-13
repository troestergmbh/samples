package com.activequant.clientsample;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.clientsample.utils.EMACalculator;
import com.activequant.component.ComponentBase;
import com.activequant.domainmodel.OHLCV;
import com.activequant.domainmodel.TimeFrame;
import com.activequant.domainmodel.exceptions.IncompleteOrderInstructions;
import com.activequant.domainmodel.exceptions.UnsupportedOrderType;
import com.activequant.domainmodel.trade.order.MarketOrder;
import com.activequant.domainmodel.trade.order.OrderSide;
import com.activequant.interfaces.transport.ITransportFactory;
import com.activequant.trading.DefaultTransportExchange;
import com.activequant.trading.OHLCDataFeedAdapter;

/**
 * This is an example of an OHLC Feed subscriber that calculates a FAST and a
 * SLOW EMA to derive crossover signals and to place simple market orders.
 * 
 * @author GhostRider
 * 
 */
public class OHLCFeedSample extends ComponentBase {

	// which instrument this one will run on.
	private String mdiId = "PATS.MDI.SFE.S_IRM3";

	// initialize two ema calculators
	EMACalculator fastEma = new EMACalculator(20, TimeFrame.MINUTES_1);
	EMACalculator slowEma = new EMACalculator(50, TimeFrame.MINUTES_1);

	// where we will send our orders to, in this case an anonymous transport
	// exchange.
	DefaultTransportExchange dex;

	public OHLCFeedSample(ITransportFactory transFac) throws Exception {
		super("OHLCFeedSample", transFac);
		// initialize a transport exchange
		dex = new DefaultTransportExchange(transFac);
		//
		OHLCDataFeedAdapter o = new OHLCDataFeedAdapter(mdiId,
				TimeFrame.MINUTES_1, transFac) {
			@Override
			public void processOHLCV(OHLCV ohlc) {
				// let's delegate it on.
				try {
					process(ohlc);
				} catch (UnsupportedOrderType e) {
					e.printStackTrace();
				} catch (IncompleteOrderInstructions e) {
					e.printStackTrace();
				}
			}
		};

	}

	/**
	 * process incoming ohlc data
	 * 
	 * @param ohlcv
	 * @throws IncompleteOrderInstructions
	 * @throws UnsupportedOrderType
	 */
	private void process(OHLCV ohlcv) throws UnsupportedOrderType,
			IncompleteOrderInstructions {
		//
		double formerFastEma = fastEma.getLastEma();
		double formerSlowEma = slowEma.getLastEma();
		// let's push in our close price.
		fastEma.update(ohlcv.getTimeStamp(), ohlcv.getClose());
		slowEma.update(ohlcv.getTimeStamp(), ohlcv.getClose());
		//
		double newFastEma = fastEma.getLastEma();
		double newSlowEma = slowEma.getLastEma();
		//
		if (formerFastEma > formerSlowEma && newFastEma < newSlowEma) {
			// short crossing.
			MarketOrder mo = new MarketOrder();
			mo.setOrderSide(OrderSide.SELL);
			mo.setQuantity(1.0);
			mo.setTradInstId(mdiId);
			dex.prepareOrder(mo).submit();
		} else if (formerFastEma < formerSlowEma && newFastEma > newSlowEma) {
			// long crossing.
			MarketOrder mo = new MarketOrder();
			mo.setOrderSide(OrderSide.BUY);
			mo.setQuantity(1.0);
			mo.setTradInstId(mdiId);
			dex.prepareOrder(mo).submit();
		}

	}

	@Override
	public String getDescription() {
		return "The most basic component";
	}

	/**
	 * It is possible to run components from within a ComponentServer or from a
	 * main.
	 * 
	 * @param args
	 * @throws Exception
	 * @throws BeansException
	 */
	public static void main(String[] args) throws BeansException, Exception {
		// first, let's get the spring context.
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "fwspring.xml" });
		new OHLCFeedSample(appContext.getBean(ITransportFactory.class));
	}

}
