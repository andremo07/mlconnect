package br.com.mpconnect.ml.api.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Municipio;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;
import br.com.mpconnect.provider.exception.NfeProviderException;

//@Service
public class Teste {

	public static void main(String[] args) throws Exception{

		try {
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

			OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
			NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
			NfeConfigDao nfeConfidDao = (NfeConfigDao) ctx.getBean("nfeConfidDao");
			MunicipioDao munDao = (MunicipioDao) ctx.getBean("municipioDao");
			
			Venda venda = orderBusiness.recuperarVenda("1601889869");
			
			Municipio mun = munDao.findMunicipioByNameAndUf(venda.getEnvio().getMunicipio(), venda.getEnvio().getUf());
			
			venda.getEnvio().setCodMunicipio(new Integer(mun.getCdUf()));
			
			NfeConfig userNfeConfig = nfeConfidDao.recuperaUm(1L);
			
			nfeProvider.gerarNFe(venda,userNfeConfig);
			
		} catch (NfeProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println();
		System.exit(0);

	}


}
