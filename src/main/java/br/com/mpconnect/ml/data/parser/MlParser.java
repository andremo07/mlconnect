package br.com.mpconnect.ml.data.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mpconnect.ml.api.enums.TipoPessoaEnum;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.mpconnect.util.DateUtils;
import br.com.trendsoftware.mlProvider.dto.Buyer;
import br.com.trendsoftware.mlProvider.dto.ItemResponse;
import br.com.trendsoftware.mlProvider.dto.ListingType;
import br.com.trendsoftware.mlProvider.dto.Order;
import br.com.trendsoftware.mlProvider.dto.OrderItem;
import br.com.trendsoftware.mlProvider.dto.Payment;
import br.com.trendsoftware.mlProvider.dto.ReceiverAddress;
import br.com.trendsoftware.mlProvider.dto.Seller;
import br.com.trendsoftware.mlProvider.dto.Shipping;

public class MlParser {

	public static Pagamento parsePayment(Payment payment){

		Pagamento pagamento = new Pagamento();

		pagamento.setIdML(payment.getId().toString());
		pagamento.setTipo(payment.getPaymentType());
		pagamento.setTotalPago(payment.getTotalPaidAmount());
		pagamento.setValorTransacao(payment.getTransactionAmount());
		pagamento.setNumeroParcelas(payment.getInstallments());

		return pagamento;
	}

	public static Anuncio parseItem(ItemResponse item){

		Anuncio anuncio = new Anuncio();

		anuncio.setDescricao(item.getDescriptions() != null && !item.getDescriptions().isEmpty() ? item.getDescriptions().get(0).toString() : null);
		anuncio.setCategoria(item.getCategoryId());
		anuncio.setIdMl(item.getId());
		anuncio.setStatus(item.getStatus());
		anuncio.setTitulo(item.getTitle());
		anuncio.setTipo(item.getListingTypeId());
		anuncio.setValor(item.getPrice());

		return anuncio;

	}
	
	public static DetalheVenda parseOrderItem(OrderItem orderItem, Double valorTotalTransacao){

		DetalheVenda detalheVenda = new DetalheVenda();
		
		ItemResponse item = orderItem.getItem();
		
		Anuncio anuncio = parseItem(item);
		
		double comissão = 0.0;
		if(item.getListingTypeId().equals(ListingType.PREMIUM.getName()))
			comissão = Math.round(valorTotalTransacao * 16) / 100.0;	
		else
			comissão = Math.round(valorTotalTransacao * 11) / 100.0;
		
		Produto produto = new Produto();
		produto.setSku(item.getSellerCustomField());
		
		detalheVenda.setProduto(produto);
		detalheVenda.setTarifaVenda(comissão);
		detalheVenda.setAnuncio(anuncio);
		detalheVenda.setQuantidade(orderItem.getQuantity().intValue());
		
		return detalheVenda;
		
	}

	public static Cliente parseClient(Buyer buyer){

		Cliente cliente = new Cliente();
		cliente.setNome(buyer.getFirstName()+" "+buyer.getLastName());
		cliente.setNrDocumento(buyer.getBillingInfo().getDocNumber().toString());
		cliente.setApelido(buyer.getNickname());
		cliente.setIdMl(buyer.getId().toString());
		cliente.setEmail(buyer.getEmail());
		
		if(buyer.getPhone()!=null)
			if(buyer.getPhone().getNumber()!=null)
				if(buyer.getPhone().getAreaCode() != null && !buyer.getPhone().getAreaCode().equals("null"))
					cliente.setTelefone(buyer.getPhone().getAreaCode().trim()+" "+buyer.getPhone().getNumber().trim());
				else
					cliente.setTelefone(buyer.getPhone().getNumber().trim());
		if(buyer.getBillingInfo().getDocNumber()!=null)
			if(buyer.getBillingInfo().getDocNumber().toString().length()>11){
				cliente.setTipo(TipoPessoaEnum.JURIDICA.getValue());
				cliente.setTipoContribuinteIcms(9);
			}
			else{
				cliente.setTipo(TipoPessoaEnum.FISICA.getValue());
				cliente.setTipoContribuinteIcms(9);
			}
		else{
			cliente.setTipo(TipoPessoaEnum.FISICA.getValue());
			cliente.setTipoContribuinteIcms(9);
		}	

		return cliente;
	}

	public static Vendedor parseSeller(Seller seller){

		Vendedor vendedor = new Vendedor();
		vendedor.setIdMl(seller.getId().toString());
		vendedor.setApelido(seller.getNickname());

		return vendedor;
		
	}

	public static Envio parseShipping(Shipping shipping){

		Envio envio = new Envio();
		if(shipping.getShippingOption()!=null)
			if(shipping.getShippingOption().getCost()==0){
				envio.setCustoVendedor(shipping.getShippingOption().getListCost());
				envio.setCustoComprador(0.00);
			}
			else{
				envio.setCustoVendedor(0.00);
				envio.setCustoComprador(shipping.getShippingOption().getCost());
			}

		envio.setIdMl(shipping.getId() != null ? shipping.getId().toString() : null);
		envio.setMetodo(shipping.getShippingOption() != null ? shipping.getShippingOption().getName() : null);
		envio.setModo(shipping.getMode());
		envio.setTipo(shipping.getTrackingMethod());

		if(shipping.getReceiverAddress()!=null){
			ReceiverAddress address = shipping.getReceiverAddress();
			envio.setLogradouro(address.getStreetName());
			envio.setMunicipio(address.getCity() != null ? address.getCity().getName() : null);
			envio.setCep(Long.valueOf(address.getZipCode()));
			envio.setPais(address.getCountry() != null ? address.getCountry().getName() : null);
			envio.setNumero(address.getStreetNumber());
			envio.setComplemento(address.getComment());
			envio.setUf(address.getState() != null ? address.getState().getId().split("-")[1] : null);
			envio.setBairro(address.getNeighborhood() != null ? address.getNeighborhood().getName() : null);
		}	
		
		return envio;
		
	}

	public static Venda parseOrder(Order order){

		Venda venda = new Venda(); 
		
		venda.setId(order.getId().toString());
		venda.setStatus(order.getStatus());

		String dataVendaString = order.getDateCreated();
		Date dataVenda = DateUtils.getDataFormatada(dataVendaString, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		venda.setData(dataVenda);
		
		List<Pagamento> pagamentos = new ArrayList<Pagamento>();
		Double valorTotalTransacao = 0.0;
		for(Payment payment : order.getPayments()){
			Pagamento pagamento = parsePayment(payment);
			pagamentos.add(pagamento);
			valorTotalTransacao = valorTotalTransacao+payment.getTransactionAmount();
		}
		venda.setPagamentos(pagamentos);
		
		List<DetalheVenda> detalhesVenda = new ArrayList<DetalheVenda>();
		for(OrderItem orderItem : order.getOrderItems()){			
			DetalheVenda detalheVenda = parseOrderItem(orderItem, valorTotalTransacao);
			detalhesVenda.add(detalheVenda);	
		}
		venda.setDetalhesVenda(detalhesVenda);
		
		Envio envio = parseShipping(order.getShipping());
		venda.setEnvio(envio);
		
		Cliente cliente = parseClient(order.getBuyer());
		venda.setCliente(cliente);
		
		Vendedor vendedor = parseSeller(order.getSeller());
		venda.setVendedor(vendedor);
		
		return venda;
		
	}

}
