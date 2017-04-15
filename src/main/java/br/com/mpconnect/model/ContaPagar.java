package br.com.mpconnect.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="CONTA_PAGAR")
public class ContaPagar implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;

	@Column(name="NR_TRANSACAO")
	private String nrTransacao;
	
	@Column(name="DATA_EMISSAO",nullable=false)
	private Date dataEmissao;
	
	@Column(name="DATA_VENCIMENTO",nullable=false)
	private Date dataVencimento;
	
	@Column(name="DATA_BAIXA")
	private Date dataBaixa;
	
	@Column(name="VALOR",nullable=false)
	private Double valor;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="ANOTACAO")
	private String anotacao;
	
	@Column(name="FORMA_PAGAMENTO")
	private String formaPagamento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CATEGORIA_ID" , nullable=false)
	private CategoriaContaPagar categoria;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="VENDEDOR_ID" , nullable=false)
	private Vendedor vendedor;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PESSOA_ID")
	private Pessoa beneficiario;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNrTransacao() {
		return nrTransacao;
	}

	public void setNrTransacao(String nrTransacao) {
		this.nrTransacao = nrTransacao;
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAnotacao() {
		return anotacao;
	}

	public void setAnotacao(String anotacao) {
		this.anotacao = anotacao;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public CategoriaContaPagar getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaContaPagar categoria) {
		this.categoria = categoria;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}

	public Pessoa getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(Pessoa beneficiario) {
		this.beneficiario = beneficiario;
	}

	public Date getDataBaixa() {
		return dataBaixa;
	}

	public void setDataBaixa(Date dataBaixa) {
		this.dataBaixa = dataBaixa;
	}
}
