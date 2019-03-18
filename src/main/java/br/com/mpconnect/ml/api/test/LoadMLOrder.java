package br.com.mpconnect.ml.api.test;

import java.util.Date;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.trendsoftware.markethub.business.OrderBusiness;
import br.com.trendsoftware.markethub.ml.business.impl.OrderBusinessImpl;
import br.com.trendsoftware.markethub.utils.DateUtils;

public class LoadMLOrder {

	public static void main(String[] args) throws Exception{
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		
		OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("mlOrderBusiness");
		
		((OrderBusinessImpl) orderBusiness).loadOrdersByDateTest(DateUtils.adicionaDias(new Date(), -10), new Date());
		
		System.out.println();
	}


}
