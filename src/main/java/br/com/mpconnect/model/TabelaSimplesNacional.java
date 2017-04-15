package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name="SIMPLES_NACIONAL_TX")
public class TabelaSimplesNacional {
	
	@Id
	@Column(name="ID")
	private Long id;
	
	@Column(name="NR_ANEXO")
	private Integer nrAnexo;
	
	@Column(name="VL_INICIAL")
	private Double valorInicial;
	
	@Column(name="VL_FINAL")
	private Double valorFinal;
	
	@Column(name="ALIQUOTA")
	private Double aliquota;
	
	@Column(name="IRPJ")
	private Double irpj;
	
	@Column(name="CSLL")
	private Double csll;
	
	@Column(name="COFINS")
	private Double confins;
	
	@Column(name="PIS_PASEP")
	private Double pisPasep;
	
	@Column(name="CPP")
	private Double cpp;
	
	@Column(name="ICMS")
	private Double icms;
	
	@Column(name="ISS")
	private Double iss;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNrAnexo() {
		return nrAnexo;
	}

	public void setNrAnexo(Integer nrAnexo) {
		this.nrAnexo = nrAnexo;
	}

	public Double getValorInicial() {
		return valorInicial;
	}

	public void setValorInicial(Double valorInicial) {
		this.valorInicial = valorInicial;
	}

	public Double getValorFinal() {
		return valorFinal;
	}

	public void setValorFinal(Double valorFinal) {
		this.valorFinal = valorFinal;
	}

	public Double getAliquota() {
		return aliquota;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public Double getIrpj() {
		return irpj;
	}

	public void setIrpj(Double irpj) {
		this.irpj = irpj;
	}

	public Double getCsll() {
		return csll;
	}

	public void setCsll(Double csll) {
		this.csll = csll;
	}

	public Double getConfins() {
		return confins;
	}

	public void setConfins(Double confins) {
		this.confins = confins;
	}

	public Double getPisPasep() {
		return pisPasep;
	}

	public void setPisPasep(Double pisPasep) {
		this.pisPasep = pisPasep;
	}

	public Double getCpp() {
		return cpp;
	}

	public void setCpp(Double cpp) {
		this.cpp = cpp;
	}

	public Double getIcms() {
		return icms;
	}

	public void setIcms(Double icms) {
		this.icms = icms;
	}

	public Double getIss() {
		return iss;
	}

	public void setIss(Double iss) {
		this.iss = iss;
	}
}
