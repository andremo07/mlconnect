package br.com.mpconnect.ml.api.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.mercadolibre.sdk.MeliException;

import br.com.mpconnect.manager.VendaManagerBo;
import br.com.mpconnect.ml.api.ApiCategorias;
import br.com.mpconnect.ml.api.ApiEnvios;
import br.com.mpconnect.ml.api.ApiPerguntas;
import br.com.mpconnect.ml.api.ApiProdutos;
import br.com.mpconnect.ml.api.ApiUsuario;
import br.com.mpconnect.ml.api.ApiVendas;
import br.com.mpconnect.ml.data.AnuncioML;
import br.com.mpconnect.ml.data.CategoriaML;
import br.com.mpconnect.ml.data.MensagemVendaML;
import br.com.mpconnect.ml.data.TipoAnuncioML;
import br.com.mpconnect.ml.data.VendaML;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.utils.DateUtils;
import br.com.mpconnect.utils.FileUtils;

@Service
public class Teste {

	public static void main(String[] args){

		ClassPathXmlApplicationContext ctx = 
				new ClassPathXmlApplicationContext("applicationContext.xml");

		ApiProdutos apiProdutos = (ApiProdutos) ctx.getBean("apiProdutos");
		ApiUsuario apiUsuario = (ApiUsuario) ctx.getBean("apiUsuario");
		ApiVendas apiVendas = (ApiVendas) ctx.getBean("apiVendas");
		ApiCategorias apiCategorias = (ApiCategorias) ctx.getBean("apiCategorias");
		ApiPerguntas apiPerguntas = (ApiPerguntas)ctx.getBean("apiPerguntas");
		VendaManagerBo vendasManager = (VendaManagerBo) ctx.getBean("vendasManager");
		
		VendaML venda2 = apiVendas.retornaVendaPorId("1321321828", apiUsuario.getIdUsuarioLogado());
					
		List<MensagemVendaML> msgs = apiVendas.obterMensagensPosVenda("1263510385");
		//List<ProdutoML> produtos = apiProdutos.recuperaProdutos(apiUsuario.getIdUsuarioLogado());
		//1169385168
		//Set<VendaML> vendasMl = apiVendas.recuperaVendasSemana(apiUsuario.getIdUsuarioLogado());
		//Set<VendaML> vendasMl = apiVendas.recuperaVendasPorStatusEnvio(apiUsuario.getIdUsuarioLogado(), "ready_to_ship");
		
		Set<VendaML> vendasMl = apiVendas.recuperaVendasPeriodo(apiUsuario.getIdUsuarioLogado(), DateUtils.adicionaDias(new Date(), -10), new Date());
		List<Object> vendasMlList = Arrays.asList(vendasMl.toArray());
		for(Object vendaMl: vendasMlList){
			Venda venda = vendasManager.parseVendaMltoVenda((VendaML) vendaMl);
			vendasManager.cadastrarVenda(venda);
		}

		AnuncioML produto = new AnuncioML();
		produto.setTitulo("Capa Spigen Neo Hybrid Galaxy Note 7 100% Original");
		produto.setIdCategoria("MLB75189");
		produto.setValor(new Double(29.9));
		produto.setQuantidade(new Integer(16));
		TipoAnuncioML tipoAnuncio = new TipoAnuncioML();
		tipoAnuncio.setId("gold_pro");
		produto.setTipo(tipoAnuncio);
		List<String> picturesUrls = new ArrayList<String>();
		picturesUrls.add("https://upload.wikimedia.org/wikipedia/commons/f/fd/Ray_Ban_Original_Wayfarer.jpg");
		picturesUrls.add("https://upload.wikimedia.org/wikipedia/commons/f/fd/Ray_Ban_Original_Wayfarer.jpg");

		//SÓ PARA TESTES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		FileUtils fileUtils = new FileUtils();
		String html = fileUtils.lerArquivoTexto("C:\\Users\\Public\\descricao.txt");
		produto.setHtml(html);

		//apiProdutos.salvarProduto(produto);
		Date data = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(year, month, day, 23, 59, 59);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(data);
		cal2.set(year, month, day, 0, 0, 0);
		//apiProdutos.getProdutoPorId("MLB735098419");
		apiUsuario.getFeeds();
		System.out.println();
		System.exit(0);

	}


}
