package com.activequant.clientsample.btc;

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
public class MDIInitializer extends ComponentBase {

	/**
	 * 
	 * @param transFac
	 * @throws Exception
	 */
	public MDIInitializer(ITransportFactory transFac, IDaoFactory daoFactory)
			throws Exception {
		super("BTCMdiInit", transFac);
		// 
		init(daoFactory);
	}
	
	public void init(IDaoFactory daoFactory) throws Exception {
		// 
		MarketDataInstrument mdi = new MarketDataInstrument("MTGOX", "BTC/USD");
		daoFactory.mdiDao().delete(mdi);
		daoFactory.mdiDao().create(mdi);
		TradeableInstrument tdi = new TradeableInstrument("MTGOX", "BTC/USD");
		daoFactory.tradeableDao().delete(tdi);
		daoFactory.tradeableDao().create(tdi);
		System.exit(0);
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
		new MDIInitializer(appContext.getBean(ITransportFactory.class), appContext.getBean(IDaoFactory.class));		
	}
}