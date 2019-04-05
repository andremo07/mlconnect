package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.holder.MeliConfigurationHolder;
import br.com.mpconnect.model.AcessoMl;
import br.com.trendsoftware.markethub.business.AdBusiness;
import br.com.trendsoftware.markethub.repository.AccessRepository;
import br.com.trendsoftware.mlProvider.dataprovider.ItemProvider;
import br.com.trendsoftware.mlProvider.dataprovider.UserProvider;
import br.com.trendsoftware.mlProvider.dto.ItemStatus;
import br.com.trendsoftware.mlProvider.dto.UserCredencials;
import br.com.trendsoftware.mlProvider.response.Response;

public class MLAddSyncroziner 
{
	
	public static void main(String[] args) throws Exception{

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		AccessRepository accessRepository = (AccessRepository) ctx.getBean("accessRepository");
		
		ItemProvider itemProvider = (ItemProvider) ctx.getBean("itemProvider");
		
		AdBusiness dBusiness = (AdBusiness) ctx.getBean("mlAdBusiness");
		
		UserProvider userProvider = (UserProvider) ctx.getBean("userProvider");
		
		AcessoMl acessoMl = accessRepository.findByClientId(MeliConfigurationHolder.getInstance().getClientId());
		
		Response response = userProvider.login(MeliConfigurationHolder.getInstance().getClientId().toString(), MeliConfigurationHolder.getInstance().getClientSecret(), acessoMl.getRefreshToken());
		
		UserCredencials token = (UserCredencials) response.getData(); 
		acessoMl.setAccessToken(token.getAccessToken());
		acessoMl.setRefreshToken(token.getRefreshToken());
		accessRepository.saveAndFlush(acessoMl);

		itemProvider.listUserProducts("146216892", ItemStatus.ACTIVE, 0, acessoMl.getAccessToken());
		
		/*
		 * List<Anuncio> anuncios = new ArrayList<Anuncio>();
		 * 
		 * products.forEach(anuncio -> { anuncios.add(B2WParser.parseAd(anuncio)); });
		 * 
		 * adBusiness.syncronizeAd(anuncios);
		 */
		
		System.out.println();
	}
}
