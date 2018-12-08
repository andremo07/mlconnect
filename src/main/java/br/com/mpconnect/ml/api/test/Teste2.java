package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;

import br.com.mpconnect.holder.B2wConfigurationHolder;
<<<<<<< Updated upstream
import br.com.trendsoftware.b2wprovider.dataprovider.B2wItemProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItemList;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
=======
import br.com.mpconnect.model.Venda;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wOrderProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrder;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.markethub.data.parser.B2WParser;
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream

		Gson parser = null;
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");
		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(), B2wConfigurationHolder.getInstance().getApiKey(), B2wConfigurationHolder.getInstance().getAccountManagerKey());
		B2wItemProvider itemProvider = (B2wItemProvider) ctx.getBean("b2wItemProvider");
		
		RestResponse response = itemProvider.listProducts(userCredencials);
		SkyHubItemList skyHubItemList = itemProvider.getParser().fromJson(response.getBody(), SkyHubItemList.class);
		
=======
		B2wOrderProvider b2wOrderProvider = (B2wOrderProvider) ctx.getBean("b2wOrderProvider");
		
		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());
		
		RestResponse response = b2wOrderProvider.searchOrderById(userCredencials, "Lojas Americanas-268415612202");
		
		Gson parser = new Gson();
		
		SkyHubOrder skyHubOrder = parser.fromJson(response.getBody(), SkyHubOrder.class);
		
		Venda marketHubOrder = B2WParser.parseOrder(skyHubOrder);

>>>>>>> Stashed changes
		System.out.println();
		
		
	}

}
