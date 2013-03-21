package com.activequant.clientsample;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.component.ComponentBase;
import com.activequant.interfaces.transport.ITransportFactory;
import com.activequant.messages.AQMessages.ValueSet;

/**
 * @author GhostRider
 * 
 */
public class AQExcelIntercomExample extends ComponentBase {

	public AQExcelIntercomExample(ITransportFactory transFac) throws Exception {
		super("AQExcelIntercom", transFac);

	}

	@Override
	public String getDescription() {
		return "An AQExcel intercommunication example. ";
	}

	protected void handle(ValueSet valueSet){
		// value sets are a bit dumb. 
		String field = valueSet.getField();
		String id = valueSet.getId();
		String type = valueSet.getType();
		// ... they deliver everything as string. 
		String value = valueSet.getValue();
		// just a start ... 
		
		// 
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
		new AQExcelIntercomExample(appContext.getBean(ITransportFactory.class));
	}

}
