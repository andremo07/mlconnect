package br.com.mpconnect.ml.api.test;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wItemProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.b2wprovider.util.B2WItemSheetLoader;
import br.com.trendsoftware.markethub.utils.ExcelUtils;

public class Teste_PlanilhaB2W {

	public static void main(String[] args) throws Exception{

//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
//
//		B2wOrderProvider b2wOrderProvider = (B2wOrderProvider) ctx.getBean("b2wOrderProvider");
//		
//		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());
//		
//		RestResponse response = b2wOrderProvider.searchOrderById(userCredencials, "Submarino-350197814101");
//		
//		Gson parser = new Gson();
//		
//		SkyHubOrder skyHubOrder = parser.fromJson(response.getBody(), SkyHubOrder.class);
//
//		System.out.println();
//		
		
		String fileName = "C:/Wanderson/TrendStore/itens_amazfit_stratus.xlsx";	
		List<SkyHubItem> itens = B2WItemSheetLoader.load(ExcelUtils.read(fileName));
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		B2wItemProvider itemProvider = (B2wItemProvider) ctx.getBean("b2wItemProvider");

		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(), B2wConfigurationHolder.getInstance().getApiKey(), B2wConfigurationHolder.getInstance().getAccountManagerKey());
		
		for(SkyHubItem item:itens)
			itemProvider.addItem(userCredencials, item);
		
		System.out.println();
	}


}
