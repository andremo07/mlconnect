package br.com.mpconnect.ml.data;

public class PagamentoML {
	
	//payments -> id
	public String id;
	
	//payments -> transaction_amount
	public Double valorTransacao;
	
	//payments -> shipping_cost
	public Double custoEnvio;
	
	//payments -> total_paid_amount
	public Double totalPago;
	
	//payments -> payment_type
	public String tipoPagamento;
	
	//payments -> installments
	public Integer numeroParcelas;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getValorTransacao() {
		return valorTransacao;
	}

	public void setValorTransacao(Double valorTransacao) {
		this.valorTransacao = valorTransacao;
	}

	public Double getCustoEnvio() {
		return custoEnvio;
	}

	public void setCustoEnvio(Double custoEnvio) {
		this.custoEnvio = custoEnvio;
	}

	public Double getTotalPago() {
		return totalPago;
	}

	public void setTotalPago(Double totalPago) {
		this.totalPago = totalPago;
	}

	public String getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public Integer getNumeroParcelas() {
		return numeroParcelas;
	}

	public void setNumeroParcelas(Integer numeroParcelas) {
		this.numeroParcelas = numeroParcelas;
	}
	
}
