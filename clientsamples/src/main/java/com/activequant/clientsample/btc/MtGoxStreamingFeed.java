package com.activequant.clientsample.btc;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v1.MtGoxExchange;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;
import com.xeiam.xchange.service.streaming.ExchangeEvent;
import com.xeiam.xchange.service.streaming.StreamingExchangeService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.component.ComponentBase;
import com.activequant.domainmodel.MarketDataInstrument;
import com.activequant.domainmodel.TradeableInstrument;
import com.activequant.interfaces.dao.IDaoFactory;
import com.activequant.interfaces.transport.ITransportFactory;

/**
 * 
 * @author GhostRider
 *
 */
public class MtGoxStreamingFeed extends ComponentBase {

	/**
	 * 
	 * @param transFac
	 * @throws Exception
	 */
	public MtGoxStreamingFeed(ITransportFactory transFac, IDaoFactory daoFactory)
			throws Exception {
		super("MtGoxStreamingFeed", transFac);
		// 
		init(daoFactory);
	}
	
	public void init(IDaoFactory daoFactory) throws Exception {
        Exchange mtGox = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());
        //StreamingExchangeService exchangeService = mtGox.getStreamingExchangeService();
        PollingMarketDataService marketDataService = mtGox.getPollingMarketDataService();
        while ( true ) {
            Ticker ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
            System.out.println(ticker);
            Thread.sleep(10000);
        }
    }

	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "Initializes BTC MDIs";
	}
	
	/**
	 * It is possible to run components from within a ComponentServer or from a main. 
	 * @param args
	 * @throws Exception 
	 * @throws BeansException 
	 */
	public static void main(String[] args) throws BeansException, Exception{
		// first, let's get the spring context. 
		ApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { "fwspring.xml" });
		new MtGoxStreamingFeed(appContext.getBean(ITransportFactory.class), appContext.getBean(IDaoFactory.class));
	}
}