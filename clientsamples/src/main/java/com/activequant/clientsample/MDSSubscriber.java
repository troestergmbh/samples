package com.activequant.clientsample;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.component.ComponentBase;
import com.activequant.domainmodel.streaming.MarketDataSnapshot;
import com.activequant.interfaces.transport.ITransportFactory;
import com.activequant.trading.MarketDataFeedAdapter;

/**
 * 
 * @author GhostRider
 * 
 */
public class MDSSubscriber extends ComponentBase {

	// which instrument this one will run on.
	private String mdiId = "PATS.MDI.CME.GEM3";

	public MDSSubscriber(ITransportFactory transFac) throws Exception {
		super("MDSSubscriber", transFac);
		MarketDataFeedAdapter mdf = new MarketDataFeedAdapter(mdiId, transFac) {
			@Override
			public void processMarketDataSnapshot(
					MarketDataSnapshot mds) {
				System.out.println(mds);
				
			}
		};
		mdf.start();

	}

	@Override
	public String getDescription() {
		return "Very generic market data snapshot subscriber.";
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
		new MDSSubscriber(appContext.getBean(ITransportFactory.class));
	}

}
