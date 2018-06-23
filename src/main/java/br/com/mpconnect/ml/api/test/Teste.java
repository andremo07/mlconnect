package br.com.mpconnect.ml.api.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;

//@Service
public class Teste {

	public static void main(String[] args) throws Exception, DaoException{

		
		//ZonedDateTime z = ZonedDateTime.of(LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(new DateTime().toString())), ZoneId.systemDefault()); 
				//ZonedDateTime.now(ZoneId.systemDefault());
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		OrderBusiness orderBusiness = (OrderBusiness) ctx.getBean("orderBusiness");
		NFeProvider nfeProvider = (NFeProvider) ctx.getBean("nfeProvider");
		NfeConfigDao nfeConfidDao = (NfeConfigDao) ctx.getBean("nfeConfidDao");
		MunicipioDao munDao = (MunicipioDao) ctx.getBean("municipioDao");
		ShippingProvider shippingProvider = new ShippingProvider();
		
		//Venda venda = orderBusiness.recuperarVenda("1601889869");
		Venda venda1 = orderBusiness.recuperarVenda("1736010583");
		Venda venda2 = orderBusiness.recuperarVenda("1735999862");
		Venda venda3 = orderBusiness.recuperarVenda("1735943111");

		List<Venda> vendas = new ArrayList<Venda>();
		vendas.add(venda1);
		vendas.add(venda2);
		vendas.add(venda3);
		
		for(Venda order: vendas){
			Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
			order.getEnvio().setCodMunicipio(code);
		};
		
		
		NfeConfig nfeConfig = nfeConfidDao.recuperaUm(1L);
		
		List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(vendas, nfeConfig);
		
		int index=0;
		for(NFNotaProcessada notaProcessada: notasProcessadas){
			vendas.get(index).setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
			index++;
		}
		
		List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);
		
		nfeConfig.setNrNota(new Integer(Integer.valueOf(nfeConfig.getNrNota())+notasProcessadas.size()).toString());
		nfeConfig.setNrLote(new Integer(Integer.valueOf(nfeConfig.getNrLote())+1).toString());
		nfeConfidDao.alterar(nfeConfig);
		
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
			//return f;
		
		
		//vendas.add(venda2);
		//vendas.add(venda3);

		System.out.println();
		System.exit(0);

	}


}
