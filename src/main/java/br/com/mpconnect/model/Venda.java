package br.com.mpconnect.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="VENDA")
public class Venda implements Persistente{

	@Id
	@Column(name="ID")
	private String id;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DATA")
	private Date data;
	
	@OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="VENDA_ID", nullable=false)
	private List<DetalheVenda> detalhesVenda;
	
	@OneToMany(fetch = FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="VENDA_ID", nullable=false)
	private List<Pagamento> pagamentos;
	
	@OneToOne(fetch = FetchType.LAZY,cascade={CascadeType.ALL})
	@JoinColumn(name="ENVIO_ID" , nullable=false)
	private Envio envio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Cliente cliente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Vendedor vendedor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Origem origem;

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

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public List<DetalheVenda> getDetalhesVenda() {
		return detalhesVenda;
	}

	public void setDetalhesVenda(List<DetalheVenda> detalhesVenda) {
		this.detalhesVenda = detalhesVenda;
	}

	public List<Pagamento> getPagamentos() {
		return pagamentos;
	}

	public void setPagamentos(List<Pagamento> pagamentos) {
		this.pagamentos = pagamentos;
	}

	public Envio getEnvio() {
		return envio;
	}

	public void setEnvio(Envio envio) {
		this.envio = envio;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public Origem getOrigem() {
		return origem;
	}

	public void setOrigem(Origem origem) {
		this.origem = origem;
	}
	
}
