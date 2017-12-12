package br.com.mpconnect.ml.dto;

public class DetalheVendaML {
	
	//order_items -> item -> id
	public String idAnuncio;
	
	//order_items -> sale_fee
	public Double tarifaVenda;
	
	//order_items -> quantity
	public Integer quantidade;

	public String getIdAnuncio() {
		return idAnuncio;
	}

	public void setIdAnuncio(String idAnuncio) {
		this.idAnuncio = idAnuncio;
	}

	public Double getTarifaVenda() {
		return tarifaVenda;
	}

	public void setTarifaVenda(Double tarifaVenda) {
		this.tarifaVenda = tarifaVenda;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
