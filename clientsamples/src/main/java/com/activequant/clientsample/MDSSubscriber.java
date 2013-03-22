package com.activequant.clientsample;

import java.text.DecimalFormat;

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
	private String mdiId = "PATS.MDI.SFE.S_IRM3";

	public MDSSubscriber(ITransportFactory transFac) throws Exception {
		super("MDSSubscriber", transFac);
		
		final DecimalFormat dcf = new DecimalFormat("#.#####"); 
		MarketDataFeedAdapter mdf = new MarketDataFeedAdapter(mdiId, transFac) {
			@Override
			public void processMarketDataSnapshot(
					MarketDataSnapshot mds) {
				if(mds.isResend()){
					System.out.println("Resent. Bailing out. ");
					return; 
				}
				System.out.println(mds + " // " + mds.getBidSizes()[0] + " " + mds.getAskSizes()[0]);
				double cumBidSize = 0.0 ;
				double cumAskSize = 0.0 ;
				double avgBidPrice = 0.0; 
				double avgAskPrice = 0.0;
				// 
				int askCount =0; 
				int bidCount =0;
				// 
				for(int i=0;i<mds.getAskSizes().length;i++){
					if(mds.getAskSizes()[i]!=Double.NaN){
						cumAskSize += mds.getAskSizes()[i];
						avgAskPrice += mds.getAskPrices()[i] * mds.getAskSizes()[i];
						askCount++; 
					}
				}
								
				for(int i=0;i<mds.getBidSizes().length;i++){
					if(mds.getBidSizes()[i]!=Double.NaN){
						cumBidSize += mds.getBidSizes()[i];
						avgBidPrice += mds.getBidPrices()[i] * mds.getBidSizes()[i];
						bidCount++; 
					}
				}
				
				avgAskPrice /=  cumAskSize;
				avgBidPrice /=  cumBidSize; 
				// 
				System.out.println("=============");
				System.out.println(dcf.format(cumBidSize)+" " + dcf.format(avgBidPrice) + " " + dcf.format(mds.getBidPrices()[0]));
				System.out.println(dcf.format(cumAskSize)+" " + dcf.format(avgAskPrice) + " " + dcf.format(mds.getAskPrices()[0]));
				System.out.println("=============");
				// 
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
