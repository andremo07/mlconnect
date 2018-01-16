package br.com.mpconnect.ml.api.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fincatto.nfe310.classes.nota.NFNotaProcessada;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.file.utils.PdfUtils;
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
			
			//Venda venda = orderBusiness.recuperarVenda("1601889869");
			Venda venda1 = orderBusiness.recuperarVenda("1602982050");
			Venda venda2 = orderBusiness.recuperarVenda("1602818445");
			Venda venda3 = orderBusiness.recuperarVenda("1602789565");
			
			Municipio mun;
						
			mun = munDao.findMunicipioByNameAndUf(venda1.getEnvio().getMunicipio(), venda1.getEnvio().getUf());
			venda1.getEnvio().setCodMunicipio(mun.getId().intValue());
			
			mun = munDao.findMunicipioByNameAndUf(venda2.getEnvio().getMunicipio(), venda2.getEnvio().getUf());
			venda2.getEnvio().setCodMunicipio(mun.getId().intValue());
			
			mun = munDao.findMunicipioByNameAndUf(venda3.getEnvio().getMunicipio(), venda3.getEnvio().getUf());
			venda3.getEnvio().setCodMunicipio(mun.getId().intValue());
			
			List<Venda> vendas = new ArrayList<Venda>();
			vendas.add(venda1);
			vendas.add(venda2);
			vendas.add(venda3);
			
			
			//venda.getEnvio().setCodMunicipio(mun.getId().intValue());
			
			NfeConfig userNfeConfig = nfeConfidDao.recuperaUm(1L);
						
			List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(vendas,userNfeConfig);
			
			File nfeFile = new File("NFes.pdf");
			nfeFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(nfeFile);
			
			List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);
			
			PdfUtils pdfUtils = new PdfUtils();
			pdfUtils.merge(inputStreams,fos);
			
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
