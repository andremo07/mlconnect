package br.com.mpconnect.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="ANUNCIO")
public class Anuncio implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;
	
	@Column(name="IDML")
	private String idMl;
	
	@Column(name="TITULO")
	private String titulo;
	
	@Column(name="VALOR")
	private Double valor;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="DESCRICAO")
	private String descricao;
	
	@Column(name="TIPO")
	private String tipo;
	
	@JoinColumn(nullable=false)
	@ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.MERGE})
	@JoinTable(name = "ANUNCIO_PRODUTO", joinColumns = {
			@JoinColumn(name = "ANUNCIO_ID", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "PRODUTO_ID",
					nullable = false, updatable = false) })
	private Set<Produto> produtos;
	
	@Column(name="CATEGORIA")
	private String categoria;
	
	@Column(name="ORIGEM_ID")
	private Long origem;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdMl() {
		return idMl;
	}

	public void setIdMl(String idMl) {
		this.idMl = idMl;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Set<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(Set<Produto> produtos) {
		this.produtos = produtos;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Long getOrigem() {
		return origem;
	}

	public void setOrigem(Long origem) {
		this.origem = origem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anuncio other = (Anuncio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
