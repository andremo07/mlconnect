package br.com.mpconnect.ml.api.test;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wItemProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.markethub.b2w.business.impl.AdBusiness;
import br.com.trendsoftware.markethub.repository.AdRepository;
import br.com.trendsoftware.markethub.repository.ProductRepository;

public class B2WAddSyncroziner {

	public static ProductRepository productRepository;

	public static AdRepository adRepository;

	public static int savedAds = 0;

	public static void main(String[] args) throws Exception{

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		B2wItemProvider b2wItemProvider = (B2wItemProvider) ctx.getBean("b2wItemProvider");
		
		AdBusiness adBusiness = (AdBusiness) ctx.getBean("b2WAdBusiness");

		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());

		List<SkyHubItem> products = b2wItemProvider.listItens(userCredencials);
		
		adBusiness.syncronizeAd(products);
		
		System.out.println();
	}
}
