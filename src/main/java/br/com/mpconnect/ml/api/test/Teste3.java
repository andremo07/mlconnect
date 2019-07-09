package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wOrderProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrder;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.markethub.b2w.business.impl.OrderBusinessImpl;
import br.com.trendsoftware.markethub.business.OrderBusiness;
import br.com.trendsoftware.markethub.data.parser.B2WParser;
import br.com.trendsoftware.restProvider.response.RestResponse;

public class Teste3 {

	public static void main(String[] args) throws Exception{
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		B2wOrderProvider b2wOrderProvider = (B2wOrderProvider) ctx.getBean("b2wOrderProvider");
		
		OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("b2WOrderBusiness");
		
		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());
		
		RestResponse response = b2wOrderProvider.searchOrderById(userCredencials, "Lojas Americanas-350380874501");
		
		Gson parser = new Gson();
		
		SkyHubOrder skyHubOrder = parser.fromJson(response.getBody(), SkyHubOrder.class);
		
		Venda marketHubOrder = B2WParser.parseOrder(skyHubOrder);
		
		((OrderBusinessImpl) orderBusiness).setChannelName(skyHubOrder.getCode().split("-")[0]);
		orderBusiness.save(marketHubOrder);

		System.out.println();
	}


}
