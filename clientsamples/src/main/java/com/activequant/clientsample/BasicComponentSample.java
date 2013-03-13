package com.activequant.clientsample;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.activequant.component.ComponentBase;
import com.activequant.interfaces.transport.ITransportFactory;

public class BasicComponentSample extends ComponentBase {

	public BasicComponentSample(ITransportFactory transFac)
			throws Exception {
		super("BasicComponentSample", transFac);
	}

	@Override
	public String getDescription() {
		return "The most basic component";
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
		new BasicComponentSample(appContext.getBean(ITransportFactory.class));		
	}
	
}
