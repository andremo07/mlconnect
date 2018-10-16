package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wItemProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItemList;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.restProvider.response.RestResponse;

public class Teste2 {

	public static void main(String[] args) throws Exception {

//		try {
//			FileUtils.writeByteArrayToFile(new File("producao.cacerts"),
//					GeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.PRODUCAO, "123456"));
//			FileUtils.writeByteArrayToFile(new File("homologacao.cacerts"),
//					GeraCadeiaCertificados.geraCadeiaCertificados(DFAmbiente.HOMOLOGACAO, "123456"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
//		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
//		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
//		nfeProvider.testaServico();


		Gson parser = null;
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(), B2wConfigurationHolder.getInstance().getApiKey(), B2wConfigurationHolder.getInstance().getAccountManagerKey());
		B2wItemProvider itemProvider = (B2wItemProvider) ctx.getBean("b2wItemProvider");
		
		RestResponse response = itemProvider.listProducts(userCredencials);
		SkyHubItemList skyHubItemList = itemProvider.getParser().fromJson(response.getBody(), SkyHubItemList.class);
		
		System.out.println();
		
		
	}

}
