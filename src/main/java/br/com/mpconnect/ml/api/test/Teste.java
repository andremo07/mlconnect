package br.com.mpconnect.ml.api.test;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wItemProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.b2wprovider.util.B2WItemSheetLoader;
import br.com.trendsoftware.markethub.utils.ExcelUtils;

public class Teste {

	public static void main(String[] args) throws Exception{

		String fileName = "C:/Wanderson/TrendStore/itens_b2w.xlsx";	
		List<SkyHubItem> itens = B2WItemSheetLoader.load(ExcelUtils.read(fileName));
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		B2wItemProvider itemProvider = (B2wItemProvider) ctx.getBean("b2wItemProvider");

		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(), B2wConfigurationHolder.getInstance().getApiKey(), B2wConfigurationHolder.getInstance().getAccountManagerKey());
		
		for(SkyHubItem item:itens)
			itemProvider.addItem(userCredencials, item);
		
		System.out.println();
	}

}
