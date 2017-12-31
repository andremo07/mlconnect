package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;
import br.com.mpconnect.provider.exception.NfeProviderException;

//@Service
public class Teste {

	public static void main(String[] args){

		try {
			ClassPathXmlApplicationContext ctx = 
					new ClassPathXmlApplicationContext("spring.xml");

			OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
			NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");

			Venda venda = orderBusiness.recuperarVenda("1572003724");
			nfeProvider.gerarNFe(venda);
			
		} catch (NfeProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println();
		System.exit(0);

	}


}
