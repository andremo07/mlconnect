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
		Venda venda1 = vendaDao.findById("350204795201").get();
		Venda venda2 = vendaDao.findById("350204769503").get();
		Venda venda3 = vendaDao.findById("267729344501").get();
		Venda venda4 = vendaDao.findById("267729058001").get();
		Venda venda5 = vendaDao.findById("267724450301").get();
		// Venda venda6 = vendaDao.recuperaUm("350135911202");
		// Venda venda7 = vendaDao.recuperaUm("350135654103");
		// Venda venda8 = vendaDao.recuperaUm("350135604501");
		// Venda venda9 = vendaDao.recuperaUm("267469344701");
		// Venda venda10 = vendaDao.recuperaUm("350134941401");

		List<Venda> vendas = new ArrayList<Venda>();
		vendas.add(venda1);
		vendas.add(venda2);
		vendas.add(venda3);
		vendas.add(venda4);
		vendas.add(venda5);
		// vendas.add(venda6);
		// vendas.add(venda7);
		// vendas.add(venda8);
		// vendas.add(venda9);
		// vendas.add(venda10);
		// vendas.add(venda2);
		// vendas.add(venda3);

		for (Venda order : vendas) {
			Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
			order.getEnvio().setCodMunicipio(code);
		}

		NfeConfig nfeConfig = nfeConfidDao.findById(1L).get();

		List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(vendas, nfeConfig);

		int index = 0;
		for (NFNotaProcessada notaProcessada : notasProcessadas) {
			vendas.get(index)
					.setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
			index++;

			System.out.println(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota() + " "
					+ notaProcessada.getNota().getInfo().getChaveAcesso() + " "
					+ notaProcessada.getNota().getInfo().getDestinatario().getRazaoSocial() + " - "
					+ notaProcessada.getProtocolo().getProtocoloInfo().getMotivo());
		}

		List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);

		//nfeProvider.faturaNotasB2w(notasProcessadas);
		
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

		// vendas.add(venda2);
		// vendas.add(venda3);

		System.out.println();
		System.exit(0);
	}

}