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
@Table(name="PRODUTO")
public class Produto implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;
	
	@Column(name="NOME")
	private String nome;
	
	@Column(name="SKU")
	private String sku;
	
	@Column(name="QUANTIDADEDISPONIVEL")
	private Integer quantidadeDisponivel;
	
	@Column(name="CD_BARRAS")
	private Long codBarras;
	
	@Column(name="NCM")
	private Long ncm;
	
	@Column(name="UNIDADE_COMERCIAL")
	private String unidadeComercial;
	
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	@JoinTable(name = "ANUNCIO_PRODUTO", joinColumns = {
			@JoinColumn(name = "PRODUTO_ID", nullable = false, updatable = false) },
			inverseJoinColumns = { @JoinColumn(name = "ANUNCIO_ID",
					nullable = false, updatable = false) })
	private Set<Anuncio> anuncios;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Long getCodBarras() {
		return codBarras;
	}

	public void setCodBarras(Long codBarras) {
		this.codBarras = codBarras;
	}

	public Long getNcm() {
		return ncm;
	}

	public void setNcm(Long ncm) {
		this.ncm = ncm;
	}

	public String getUnidadeComercial() {
		return unidadeComercial;
	}

	public void setUnidadeComercial(String unidadeComercial) {
		this.unidadeComercial = unidadeComercial;
	}

	public Set<Anuncio> getAnuncios() {
		return anuncios;
	}

	public void setAnuncios(Set<Anuncio> anuncios) {
		this.anuncios = anuncios;
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
		Produto other = (Produto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
