package br.com.mpconnect.ml.api.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;

import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;
import br.com.trendsoftware.markethub.repository.NfeRepository;
import br.com.trendsoftware.markethub.repository.OrderRepository;
import br.com.trendsoftware.markethub.utils.PdfUtils;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;

public class Teste_NFE {

	public static Logger logger = LogManager.getLogger(Teste_NFE.class);
	
	public static void main(String[] args) throws Exception {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		// OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
		OrderRepository vendaDao = (OrderRepository) ctx.getBean("orderRepository");
		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
		NfeRepository nfeConfidDao = (NfeRepository) ctx.getBean("nfeRepository");
		ShippingProvider shippingProvider = (ShippingProvider) ctx.getBean("shippingProvider");
		
		shippingProvider.setLogger(logger);

		// Venda venda = orderBusiness.recuperarVenda("1601889869");
		


		Venda venda1 = vendaDao.findById("350486514102").get();
//		Venda venda2 = vendaDao.findById("350484241401").get();
//		Venda venda3 = vendaDao.findById("268979119004").get();
//		Venda venda4 = vendaDao.findById("107245909201").get();
//		Venda venda5 = vendaDao.findById("268696061301").get();
//		Venda venda6 = vendaDao.findById("268690787801").get();
//		Venda venda7 = vendaDao.findById("268305472401").get();
//		Venda venda8 = vendaDao.findById("402606657").get();
//		Venda venda9 = vendaDao.findById("268249344301").get();
//		Venda venda10 = vendaDao.findById("350337771803").get();
//		Venda venda11 = vendaDao.findById("350335080602").get();
//		Venda venda12 = vendaDao.findById("268239534701").get();
//		Venda venda13 = vendaDao.findById("268237545201").get();
//		Venda venda14 = vendaDao.findById("350334155201").get();
//		Venda venda15 = vendaDao.findById("350333843301").get();

		List<Venda> vendas = new ArrayList<Venda>();
		vendas.add(venda1);
//		vendas.add(venda2);
//		vendas.add(venda3);
//		vendas.add(venda4);
//		vendas.add(venda5);
//		vendas.add(venda6);
//		vendas.add(venda7);
//		vendas.add(venda8);
//		vendas.add(venda9);
//		vendas.add(venda10);
//		vendas.add(venda11);
//		vendas.add(venda12);
//		vendas.add(venda13);
//		vendas.add(venda14);
//		vendas.add(venda15);
		
//		nfeProvider.faturaNotasB2w(vendas, ctx);


		for (Venda order : vendas) {
			Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
			order.getEnvio().setCodMunicipio(code);
		}

		NfeConfig nfeConfig = nfeConfidDao.findById(1L).get();

		List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(vendas, nfeConfig, vendaDao);

		//int index = 0;
		for (NFNotaProcessada notaProcessada : notasProcessadas) {
			//vendas.get(index).setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
			//index++;

			System.out.println(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota() + " "
					+ notaProcessada.getNota().getInfo().getChaveAcesso() + " "
					+ notaProcessada.getNota().getInfo().getDestinatario().getRazaoSocial() + " - "
					+ notaProcessada.getProtocolo().getProtocoloInfo().getMotivo());
		}

		List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);

		
		nfeConfig.setNrNota(new Integer(Integer.valueOf(nfeConfig.getNrNota()) + notasProcessadas.size()).toString());
		nfeConfig.setNrLote(new Integer(Integer.valueOf(nfeConfig.getNrLote()) + 1).toString());
		nfeConfidDao.save(nfeConfig);

		InputStream pdfInputStream = new ByteArrayInputStream(PdfUtils.merge(inputStreams).toByteArray());

		File f = new File("nfe.pdf");
		OutputStream out = new FileOutputStream(f);
		byte buf[] = new byte[1024];
		int len;
		while ((len = pdfInputStream.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		pdfInputStream.close();
		f.createNewFile();
		
		// return f;


		nfeProvider.faturaNotasB2w(vendas, ctx);
		

		System.out.println();
		System.exit(0);
	}

}
