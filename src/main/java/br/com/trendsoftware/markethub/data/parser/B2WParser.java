package br.com.trendsoftware.markethub.data.parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.TipoPessoaEnum;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.trendsoftware.b2wprovider.dto.SkyHubCategory;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrder;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrderAddress;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrderCustomer;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrderItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubOrderPayment;
import br.com.trendsoftware.b2wprovider.dto.SkyHubVariation;
import br.com.trendsoftware.markethub.utils.DateUtils;
import br.com.trendsoftware.mlProvider.dto.Seller;

public class B2WParser {

	public static Pagamento parsePayment(SkyHubOrderPayment payment){

		Pagamento pagamento = new Pagamento();

		pagamento.setTipo(payment.getMethod());
		pagamento.setTotalPago(payment.getValue());
		pagamento.setValorTransacao(payment.getValue());
		pagamento.setNumeroParcelas(payment.getParcels());

		return pagamento;
	}
	
	public static Anuncio parseAd(SkyHubItem item)
	{
		Anuncio anuncio = new Anuncio();

		anuncio.setIdMl(item.getSku());
		anuncio.setTitulo(item.getName());
		anuncio.setValor(item.getPrice());
		anuncio.setOrigem(Channel.B2W.getOrigem().getId());
		anuncio.setDescricao(item.getDescription());
		anuncio.setStatus(item.getStatus());
		
		String caterory="";
		for (SkyHubCategory skyHubCategory : item.getCategories())
			caterory = caterory.concat(skyHubCategory.getName()).concat(";");
		anuncio.setCategoria(caterory);
				
		return anuncio;
	}
	
	public static Anuncio parseAd(SkyHubItem item, String sku)
	{
		Anuncio anuncio = new Anuncio();

		anuncio.setIdMl(sku);
		anuncio.setTitulo(item.getName());
		anuncio.setValor(item.getPrice());
		anuncio.setOrigem(Channel.B2W.getOrigem().getId());
		anuncio.setDescricao(item.getDescription());
		anuncio.setStatus(item.getStatus());
		
		String caterory="";
		for (SkyHubCategory skyHubCategory : item.getCategories())
			caterory = caterory.concat(skyHubCategory.getName()).concat(";");
		anuncio.setCategoria(caterory);
				
		return anuncio;
	}

	public static DetalheVenda parseOrderItem(SkyHubOrderItem orderItem){

		DetalheVenda detalheVenda = new DetalheVenda();

		Anuncio anuncio = new Anuncio();

		anuncio.setIdMl(orderItem.getProductId());
		anuncio.setTitulo(orderItem.getName());
		anuncio.setValor(orderItem.getOriginalPrice());
		anuncio.setOrigem(Channel.B2W.getOrigem().getId());

		BigDecimal comissao = new BigDecimal(orderItem.getSpecialPrice()*0.16);
		comissao = comissao.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		
		detalheVenda.setProdutoSku(orderItem.getId());
		detalheVenda.setTarifaVenda(comissao.doubleValue());
		detalheVenda.setAnuncio(anuncio);
		detalheVenda.setValor(orderItem.getSpecialPrice());
		detalheVenda.setQuantidade(orderItem.getQty().intValue());

		return detalheVenda;
	}

	public static Cliente parseClient(SkyHubOrderCustomer buyer){

		Cliente cliente = new Cliente();
		cliente.setNome(buyer.getName());
		cliente.setNrDocumento(buyer.getVatNumber());
		cliente.setEmail(buyer.getEmail());

		if(buyer.getPhones()!=null)
			cliente.setTelefone(buyer.getPhones().get(0));

		cliente.setTipo(TipoPessoaEnum.FISICA.getValue());
		cliente.setTipoContribuinteIcms(9);

		return cliente;
	}

	public static Vendedor parseSeller(Seller seller){

		Vendedor vendedor = new Vendedor();
		vendedor.setIdMl(seller.getId().toString());
		vendedor.setApelido(seller.getNickname());

		return vendedor;

	}

	public static Envio parseShipping(SkyHubOrderAddress shipping,Double shippingCost, String shippingMethod, String shippingMode){

		Envio envio = new Envio();
		if(shippingCost==0){
			envio.setCustoVendedor(shippingCost);
			envio.setCustoComprador(0.00);
		}
		else{
			envio.setCustoVendedor(0.00);
			envio.setCustoComprador(shippingCost);
		}

		envio.setMetodo(shippingMethod);
		envio.setModo(shippingMode);
		envio.setTipo("shipping");
		envio.setLogradouro(shipping.getStreet());
		envio.setMunicipio(shipping.getCity());
		envio.setCep(shipping.getPostcode());
		envio.setPais(shipping.getCountry());
		envio.setNumero(shipping.getNumber());
		envio.setComplemento(shipping.getDetail());
		envio.setUf(shipping.getRegion());
		envio.setBairro(shipping.getNeighborhood());

		return envio;
	}

	public static Venda parseOrder(SkyHubOrder order){

		Venda venda = new Venda(); 

		venda.setId(order.getCode().split("-")[1]);
		venda.setStatus(order.getStatus().getCode());

		String dataVendaString = order.getPlacedAt();
		Date dataVenda = DateUtils.getDataFormatada(dataVendaString, "yyyy-MM-dd'T'HH:mm:ssX");
		venda.setData(dataVenda);

		List<Pagamento> pagamentos = new ArrayList<Pagamento>();
		Double valorTotalTransacao = 0.0;
		for(SkyHubOrderPayment payment : order.getPayments()){
			Pagamento pagamento = parsePayment(payment);
			pagamentos.add(pagamento);
			valorTotalTransacao = valorTotalTransacao+payment.getValue();
		}

		venda.setPagamentos(pagamentos);

		List<DetalheVenda> detalhesVenda = new ArrayList<DetalheVenda>();
		for(SkyHubOrderItem orderItem : order.getItems()){			
			DetalheVenda detalheVenda = parseOrderItem(orderItem);
			detalhesVenda.add(detalheVenda);	
		}
		venda.setDetalhesVenda(detalhesVenda);

		Envio envio = parseShipping(order.getShippingAddress(),order.getShippingCost(),order.getShippingMethod(),order.getCalculationType());
		venda.setEnvio(envio);

		Cliente cliente = parseClient(order.getCustomer());
		venda.setCliente(cliente);

		return venda;
	}

}
