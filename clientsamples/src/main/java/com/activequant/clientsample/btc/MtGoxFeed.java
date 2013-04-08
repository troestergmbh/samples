package com.activequant.clientsample.btc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.component.ComponentBase;
import com.activequant.domainmodel.ETransportType;
import com.activequant.interfaces.dao.IDaoFactory;
import com.activequant.interfaces.transport.ITransportFactory;
import com.activequant.messages.AQMessages.BaseMessage;
import com.activequant.messages.MessageFactory;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v1.MtGoxExchange;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

/**
 * @author GhostRider, phaze9
 */
public class MtGoxFeed extends ComponentBase {

	// to construct byte messages.
	private MessageFactory mf = new MessageFactory();

	public MtGoxFeed(ITransportFactory transFac, IDaoFactory daoFactory)
			throws Exception {
		super("MtGoxFeed", transFac);
		init(daoFactory);
	}

	public void init(IDaoFactory daoFactory) throws Exception {
		Exchange mtGox = ExchangeFactory.INSTANCE
				.createExchange(MtGoxExchange.class.getName());
		PollingMarketDataService marketDataService = mtGox
				.getPollingMarketDataService();

		

		while (true) {

			List<CurrencyPair> pairs = marketDataService.getExchangeSymbols();

			for (CurrencyPair pair : pairs) {
				if(pair.counterCurrency.equals("BTC"))continue; 
				//
				System.out.println(pair);
				//
				List<Double> bidPxList = new ArrayList<Double>();
				List<Double> askPxList = new ArrayList<Double>();
				List<Double> bidQList = new ArrayList<Double>();
				List<Double> askQList = new ArrayList<Double>();
				//
				Ticker ticker = marketDataService.getTicker(pair.baseCurrency,
                        pair.counterCurrency);
				Double bid = ticker.getBid().getAmount().doubleValue();
				Double ask = ticker.getAsk().getAmount().doubleValue();
				//
				bidPxList.clear();
				askPxList.clear();
				bidQList.clear();
				askQList.clear();
				//
				bidPxList.add(bid);
				askPxList.add(ask);
				bidQList.add(100000.0);
				askQList.add(100000.0);
				//
				System.out.println(ticker);
				BaseMessage bm = mf.buildMds("MTGOX." + pair.baseCurrency + "/"
						+ pair.counterCurrency, bidPxList, askPxList, bidQList,
						askQList, false);
				transFac.getPublisher(
						ETransportType.MARKET_DATA,
						"MTGOX." + pair.baseCurrency + "/"
								+ pair.counterCurrency).send(bm.toByteArray());
			}
			Thread.sleep(20000);

		}
		//

	}

	/**
	 * 
	 */
	@Override
	public String getDescription() {
		return "Initializes BTC MDIs";
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
		new MtGoxFeed(appContext.getBean(ITransportFactory.class),
				appContext.getBean(IDaoFactory.class));
	}
}