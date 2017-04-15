package br.com.mpconnect.ml.data;

import java.util.List;

public class VendaML {

	//id
	private String id;
	
	//status
	private String status;
	
	//date_closed
	private String data;
	
	//order_items
	private List<DetalheVendaML> detalhesVenda;
	
	//payments
	private List<PagamentoML> pagamentos;
	
	//shipping
	private EnvioML envio;
	
	//buyer
	private ClienteML cliente;
	
	//seller
	private UsuarioML usuario;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<DetalheVendaML> getDetalhesVenda() {
		return detalhesVenda;
	}

	public void setDetalhesVenda(List<DetalheVendaML> detalhesVenda) {
		this.detalhesVenda = detalhesVenda;
	}

	public List<PagamentoML> getPagamentos() {
		return pagamentos;
	}

	public void setPagamentos(List<PagamentoML> pagamentos) {
		this.pagamentos = pagamentos;
	}

	public EnvioML getEnvio() {
		return envio;
	}

	public void setEnvio(EnvioML envio) {
		this.envio = envio;
	}

	public ClienteML getCliente() {
		return cliente;
	}

	public void setCliente(ClienteML cliente) {
		this.cliente = cliente;
	}

	public UsuarioML getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioML usuario) {
		this.usuario = usuario;
	}
	
}
