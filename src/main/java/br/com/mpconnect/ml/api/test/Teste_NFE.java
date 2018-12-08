package br.com.mpconnect.ml.api.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) throws Exception {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		// OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
		OrderRepository vendaDao = (OrderRepository) ctx.getBean("orderRepository");
		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
		NfeRepository nfeConfidDao = (NfeRepository) ctx.getBean("nfeRepository");
		ShippingProvider shippingProvider = new ShippingProvider();

		// Venda venda = orderBusiness.recuperarVenda("1601889869");
		
		Venda venda1 = vendaDao.findById("268415612202").get();
//		Venda venda2 = vendaDao.findById("268387663701").get();
//		Venda venda3 = vendaDao.findById("268347917502").get();
//		Venda venda4 = vendaDao.findById("350350030601").get();
//		Venda venda5 = vendaDao.findById("350349851701").get();
//		Venda venda6 = vendaDao.findById("268307529001").get();
//		Venda venda7 = vendaDao.findById("268305472401").get();
//		Venda venda8 = vendaDao.findById("402606657").get();
//		Venda venda9 = vendaDao.findById("268249344301").get();
//		Venda venda10 = vendaDao.findById("350337771803").get();
//		Venda venda11 = vendaDao.findById("350335080602").get();
//		Venda venda12 = vendaDao.findById("268239534701").get();
//		Venda venda13 = vendaDao.findById("268237545201").get();
//		Venda venda14 = vendaDao.findById("350334155201").get();
//		Venda venda15 = vendaDao.findById("350333843301").get();
//		Venda venda16 = vendaDao.findById("350332096702").get();
//		Venda venda17 = vendaDao.findById("268228877701").get();
//		Venda venda18 = vendaDao.findById("268222480001").get();
//		Venda venda19 = vendaDao.findById("268218722101").get();
//		Venda venda20 = vendaDao.findById("268213844503").get();
//		Venda venda21 = vendaDao.findById("350325241801").get();
//		Venda venda22 = vendaDao.findById("350325198701").get();
//		Venda venda23 = vendaDao.findById("350325171601").get();
//		Venda venda24 = vendaDao.findById("350325119702").get();
//		Venda venda25 = vendaDao.findById("350324816301").get();
//		Venda venda26 = vendaDao.findById("350324738801").get();
//		Venda venda27 = vendaDao.findById("268194464202").get();
//		Venda venda28 = vendaDao.findById("350320744101").get();
//		Venda venda29 = vendaDao.findById("268187247004").get();
//		Venda venda30 = vendaDao.findById("350316358702").get();
//		Venda venda31 = vendaDao.findById("350315956601").get();
//		Venda venda32 = vendaDao.findById("268175887901").get();
//		Venda venda33 = vendaDao.findById("268291913701").get();
//		Venda venda34 = vendaDao.findById("350348918701").get();

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
//		vendas.add(venda16);
//		vendas.add(venda17);
//		vendas.add(venda18);
//		vendas.add(venda19);
//		vendas.add(venda20);
//		vendas.add(venda21);
//		vendas.add(venda22);
//		vendas.add(venda23);
//		vendas.add(venda24);
//		vendas.add(venda25);
//		vendas.add(venda26);
//		vendas.add(venda27);
//		vendas.add(venda28);
//		vendas.add(venda29);
//		vendas.add(venda30);
//		vendas.add(venda31);
//		vendas.add(venda32);
//		vendas.add(venda33);
//		vendas.add(venda34);
		
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
