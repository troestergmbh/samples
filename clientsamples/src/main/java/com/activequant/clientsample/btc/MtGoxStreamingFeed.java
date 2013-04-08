package com.activequant.clientsample.btc;

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
import com.activequant.utils.ArrayUtils;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.mtgox.v1.MtGoxExchange;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

/**
 * 
 * @author GhostRider
 *
 */
public class MtGoxStreamingFeed extends ComponentBase {

	// to construct byte messages. 
	private MessageFactory mf = new MessageFactory();
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
        List<Double> bidPxList = new ArrayList<Double>();
        List<Double> askPxList = new ArrayList<Double>();
        List<Double> bidQList = new ArrayList<Double>();
        List<Double> askQList = new ArrayList<Double>();
        
        while ( true ) {
        	// 
            Ticker ticker = marketDataService.getTicker(Currencies.BTC, Currencies.USD);
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
            Thread.sleep(10000);
            BaseMessage bm = mf.buildMds("MTGOX.BTC/USD", bidPxList, askPxList, bidQList, askQList, false);
            transFac.getPublisher(ETransportType.MARKET_DATA, "MTGOX.BTC/USD").send(bm.toByteArray());
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