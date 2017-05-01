package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

import br.com.mpconnect.ml.api.enums.StatusEnvioMlEnum;
import br.com.mpconnect.ml.api.enums.StatusEtiquetaMlEnum;
import br.com.mpconnect.ml.data.ClienteML;
import br.com.mpconnect.ml.data.DetalheVendaML;
import br.com.mpconnect.ml.data.EnvioML;
import br.com.mpconnect.ml.data.MensagemVendaML;
import br.com.mpconnect.ml.data.PagamentoML;
import br.com.mpconnect.ml.data.UsuarioML;
import br.com.mpconnect.ml.data.VendaML;
import br.com.mpconnect.utils.DateUtils;
import br.com.mpconnect.utils.comparator.MensagemMLComparator;

@Component
public class ApiVendas{

	@Autowired
	private ApiMl apiMl;
	
	@Autowired
	private ApiEnvios apiEnvios;

	@Autowired
	private ApiCliente apiCliente;
	
	@Autowired
	private ApiUsuario apiUsuario;

	public Set<VendaML> recuperaVendasDia(String idVendedor){

		Date dataAtual = new Date();
		Date dataFinal = DateUtils.adicionaHoras(dataAtual, 2); 
		Date dataInicial = DateUtils.criarData(DateUtils.getDia(dataAtual), DateUtils.getMes(dataAtual), DateUtils.getAno(dataAtual),2,0);
		FluentStringsMap params = new FluentStringsMap();
		params.add("order.date_created.from", DateUtils.getDataFormatada(dataInicial));
		params.add("order.date_created.to", DateUtils.getDataFormatada(dataFinal));
		Set<VendaML> listaVendas = retornaListaVendas(idVendedor,params);

		return listaVendas;
	}

	public Set<VendaML> recuperaVendasMes(String idVendedor){

		Date dataAtual = new Date();
		Date dataFinal = DateUtils.adicionaHoras(dataAtual, 2); 
		Date dataInicial = DateUtils.criarData(1, DateUtils.getMes(dataAtual), DateUtils.getAno(dataAtual),2,0);
		FluentStringsMap params = new FluentStringsMap();
		params.add("order.date_created.from", DateUtils.getDataFormatada(dataInicial));
		params.add("order.date_created.to", DateUtils.getDataFormatada(dataFinal));
		Set<VendaML> listaVendas = retornaListaVendas(idVendedor,params);

		return listaVendas;
	}
	
	public Set<VendaML> recuperaVendasSemana(String idVendedor){

		Date dataAtual = new Date();
		Date dataFinal = DateUtils.adicionaHoras(dataAtual, 2); 
		Date dataInicial = DateUtils.getPrimeiroDiaSemana();
		FluentStringsMap params = new FluentStringsMap();
		params.add("order.date_created.from", DateUtils.getDataFormatada(dataInicial));
		params.add("order.date_created.to", DateUtils.getDataFormatada(dataFinal));
		Set<VendaML> listaVendas = retornaListaVendas(idVendedor,params);
		return listaVendas;
	}
	
	public Set<VendaML> recuperaVendasPeriodo(String idVendedor, Date dataInicial, Date dataFinal){

		dataFinal = DateUtils.adicionaHoras(dataFinal, 2); 
		dataInicial = DateUtils.adicionaHoras(dataInicial, 2); 
		FluentStringsMap params = new FluentStringsMap();
		params.add("order.date_created.from", DateUtils.getDataFormatada(dataInicial));
		params.add("order.date_created.to", DateUtils.getDataFormatada(dataFinal));
		Set<VendaML> listaVendas = retornaListaVendas(idVendedor,params);

		return listaVendas;
	}

	public Set<VendaML> recuperaVendasPorStatusEnvio(String idVendedor, StatusEnvioMlEnum statusEnvio, StatusEtiquetaMlEnum statusEtiqueta){
 
		FluentStringsMap params = new FluentStringsMap();
		params.add("shipping.status", statusEnvio.getValue());
		params.add("shipping.substatus", statusEtiqueta.getValue());
		Set<VendaML> listaVendas = retornaListaVendas(idVendedor,params);

		return listaVendas;
	}
	
	public Set<VendaML> retornaListaVendas(String idVendedor,FluentStringsMap params){
		adicionaParametros(params, idVendedor);
		Set<VendaML> listaVendas = new HashSet<VendaML>();
		listaVendas = populaListaVendas(listaVendas,params);
		return listaVendas;
	}
	
	private void adicionaParametros(FluentStringsMap params, String idVendedor){
		params.add("seller", idVendedor);
		params.add("access_token", apiMl.getMe().getAccessToken());
		params.add("offset", "0");
	}
	
	public VendaML retornaVendaPorId(String id,String idVendedor){
		
		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("seller", idVendedor);
			params.add("access_token", apiMl.getMe().getAccessToken());
			Response meliResponse = apiMl.getMe().get("/orders/"+id,params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			return parseJson(jsonObject);
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	
	public Set<VendaML> populaListaVendas(Set<VendaML> listaVendas,FluentStringsMap params){

		try {

			Response meliResponse = apiMl.getMe().get("/orders/search",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			JSONArray vendas = jsonObject.getJSONArray("results");

			for (int index=0;index < vendas.length();index++){
				JSONObject jsonObj = vendas.getJSONObject(index);
				VendaML venda = parseJson(jsonObj);
				listaVendas.add(venda);
			}			
			int totalVendas = jsonObject.getJSONObject("paging").getInt("total");
			Integer offset = jsonObject.getJSONObject("paging").getInt("offset");

			if(totalVendas > listaVendas.size()){
				offset = offset+50;
				params.replace("offset", offset.toString());
				listaVendas.addAll(populaListaVendas(listaVendas,params));
			}

			return listaVendas;

		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<MensagemVendaML> obterMensagensPosVenda(String resourceId){

		try {

			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", apiMl.getMe().getAccessToken());
			Response meliResponse = apiMl.getMe().get("/messages/orders/"+resourceId,params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			JSONArray mensagens = jsonObject.getJSONArray("results");

			List<MensagemVendaML> mensagensMl = new ArrayList<MensagemVendaML>();
			for (int index=0;index < mensagens.length();index++){
				JSONObject jsonObj = mensagens.getJSONObject(index);
				MensagemVendaML mensagem = parseJsonToMensageML(jsonObj);
				mensagensMl.add(mensagem);
			}			
			Collections.sort(mensagensMl,new MensagemMLComparator());
			return mensagensMl;

		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	public VendaML parseJson(JSONObject jsonObj){

		try {
			String idVenda = jsonObj.getString("id");
			String status = jsonObj.getString("status");
			String data = jsonObj.getString("date_closed");

			//DETALHES PEDIDO
			JSONArray detalhesVendasJS = jsonObj.getJSONArray("order_items");
			List<DetalheVendaML> listaDetalhesVenda = new ArrayList<DetalheVendaML>();
			for (int index=0;index < detalhesVendasJS.length();index++){
				JSONObject obj = detalhesVendasJS.getJSONObject(index);
				String idAnuncio = obj.getJSONObject("item").getString("id");
				//Double tarifaVenda = obj.getDouble("sale_fee");
				Integer quantidade = obj.getInt("quantity");
				DetalheVendaML detalheVenda = new DetalheVendaML();
				detalheVenda.setIdAnuncio(idAnuncio);
				//detalheVenda.setTarifaVenda(tarifaVenda);
				detalheVenda.setQuantidade(quantidade);
				listaDetalhesVenda.add(detalheVenda);
			}

			//PAGAMENTOS
			JSONArray pagamentosJS = jsonObj.getJSONArray("payments");
			List<PagamentoML> pagamentos = new ArrayList<PagamentoML>();
			for (int index=0;index < pagamentosJS.length();index++){
				JSONObject obj = pagamentosJS.getJSONObject(index);
				if(obj.get("status").equals("approved")){
					String idPagamento = obj.getString("id");
					Double valorTransacao = obj.getDouble("transaction_amount");
					Double custoEnvio = obj.getDouble("shipping_cost");
					Double totalPago = obj.getDouble("total_paid_amount");
					String tipoPagamento = obj.getString("payment_type");
					String numeroParcelas = obj.getString("installments");
					int numParcelas=0;
					if(!numeroParcelas.equals("null"))
						numParcelas=new Integer(numeroParcelas).intValue();
					PagamentoML pagamento = new PagamentoML();
					pagamento.setId(idPagamento);
					pagamento.setValorTransacao(valorTransacao);
					pagamento.setCustoEnvio(custoEnvio);
					pagamento.setTotalPago(totalPago);
					pagamento.setTipoPagamento(tipoPagamento);
					pagamento.setNumeroParcelas(numParcelas);
					pagamentos.add(pagamento);
				}
			}

			//ENVIO
			EnvioML envio = apiEnvios.parseJson(jsonObj.getJSONObject("shipping"));

			//CLIENTE
			ClienteML cliente = apiCliente.parseJson(jsonObj.getJSONObject("buyer"));
			
			//VENDEDOR
			UsuarioML usuario = apiUsuario.parseJson(jsonObj.getJSONObject("seller"));

			VendaML venda = new VendaML();
			venda.setId(idVenda);
			venda.setStatus(status);
			venda.setData(data);
			venda.setDetalhesVenda(listaDetalhesVenda);
			venda.setPagamentos(pagamentos);
			venda.setEnvio(envio);
			venda.setCliente(cliente);
			venda.setUsuario(usuario);

			return venda;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public MensagemVendaML parseJsonToMensageML(JSONObject jsonObj){

		try {
			
			String dataString = jsonObj.getString("date_available");
			Date data = DateUtils.getDataFormatada(dataString,"yyyy-MM-dd'T'HH:mm:ss");
			Long idRemetente = jsonObj.getJSONObject("from").getLong("user_id");
			UsuarioML usuarioRemetente = apiUsuario.getInformacoesUsuario(idRemetente.toString());
			JSONArray toArray = jsonObj.getJSONArray("to");
			JSONObject to = toArray.getJSONObject(0);
			Long idDestinatario = to.getLong("user_id");
			UsuarioML usuarioDestinatario = apiUsuario.getInformacoesUsuario(idDestinatario.toString());
			String texto = jsonObj.getJSONObject("text").getString("plain");
			MensagemVendaML mensagem = new MensagemVendaML(data, usuarioRemetente.getApelido(), usuarioDestinatario.getApelido(), texto);
			return mensagem;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
