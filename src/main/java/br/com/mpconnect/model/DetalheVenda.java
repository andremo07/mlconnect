package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="DETALHE_VENDA")
public class DetalheVenda {

	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ANUNCIO_ID", nullable=false)
	private Anuncio anuncio;

	@Column(name="TARIFAVENDA")
	private Double tarifaVenda;

	@Column(name="QUANTIDADE")
	private Integer quantidade;
	
	@Column(name="VL_ITEM")
	private Double valor;
	
	@Transient
	private Produto produto;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Anuncio getAnuncio() {
		return anuncio;
	}

	public void setAnuncio(Anuncio anuncio) {
		this.anuncio = anuncio;
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

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}	
}
