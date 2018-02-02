package br.com.mpconnect.ml.api.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;

//@Service
public class Teste {

	public static void main(String[] args) throws Exception{

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
		NfeConfigDao nfeConfidDao = (NfeConfigDao) ctx.getBean("nfeConfidDao");
		MunicipioDao munDao = (MunicipioDao) ctx.getBean("municipioDao");

		//Venda venda = orderBusiness.recuperarVenda("1601889869");
		Venda venda1 = orderBusiness.recuperarVenda("1602982050");
		Venda venda2 = orderBusiness.recuperarVenda("1602818445");
		Venda venda3 = orderBusiness.recuperarVenda("1602789565");

		List<Venda> vendas = new ArrayList<Venda>();
		vendas.add(venda1);
		vendas.add(venda2);
		vendas.add(venda3);

		List<InputStream> nfesInputStreams= orderBusiness.generateOrderNfes(vendas);

		File nfeFilePdf = new File("NFes.pdf");
		nfeFilePdf.createNewFile();
		PdfUtils.merge(nfesInputStreams,nfeFilePdf);
		FileInputStream nfesInputStream = new FileInputStream(nfeFilePdf);
		nfesInputStream.close();

		System.out.println();
		System.exit(0);

	}


}
