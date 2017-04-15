package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="NFE_CONFIG")
public class NfeConfig implements Persistente {

	@Id
	@Column(name="ID_EMPRESA")
	private Long id;

	@Column(name="NR_SERIE")
	private String nrSerie;

	@Column(name="NR_NOTA")
	private String nrNota;

	@Column(name="IND_AMBIENTE")
	private String indAmbiente;

	@Column(name="IND_MODELO")
	private String indModelo;
	
	@Column(name="IND_TIPO_IMPRESSAO")
	private String indTipoImpressao;
	
	@Column(name="NR_VERSAO_EMISSOR")
	private String nrVersaoEmissor;
	
	@OneToOne
	@PrimaryKeyJoinColumn
	private Vendedor vendedor;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNrSerie() {
		return nrSerie;
	}

	public void setNrSerie(String nrSerie) {
		this.nrSerie = nrSerie;
	}

	public String getNrNota() {
		return nrNota;
	}

	public void setNrNota(String nrNota) {
		this.nrNota = nrNota;
	}

	public String getIndAmbiente() {
		return indAmbiente;
	}

	public void setIndAmbiente(String indAmbiente) {
		this.indAmbiente = indAmbiente;
	}

	public String getIndModelo() {
		return indModelo;
	}

	public void setIndModelo(String indModelo) {
		this.indModelo = indModelo;
	}

	public String getIndTipoImpressao() {
		return indTipoImpressao;
	}

	public void setIndTipoImpressao(String indTipoImpressao) {
		this.indTipoImpressao = indTipoImpressao;
	}

	public String getNrVersaoEmissor() {
		return nrVersaoEmissor;
	}

	public void setNrVersaoEmissor(String nrVersaoEmissor) {
		this.nrVersaoEmissor = nrVersaoEmissor;
	}

	public Vendedor getVendedor() {
		return vendedor;
	}

	public void setVendedor(Vendedor vendedor) {
		this.vendedor = vendedor;
	}
}
