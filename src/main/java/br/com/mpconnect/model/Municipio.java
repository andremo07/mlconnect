package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MUNICIPIO")
public class Municipio implements Persistente{
	
	@Id
	@Column(name="CD_MUNICIPO")
	private Long id;
	
	@Column(name="NM_MUNICIPIO")
	private String nmMunicipio;
	
	@Column(name="COD_UF")
	private String cdUf;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNmMunicipio() {
		return nmMunicipio;
	}

	public void setNmMunicipio(String nmMunicipio) {
		this.nmMunicipio = nmMunicipio;
	}

	public String getCdUf() {
		return cdUf;
	}

	public void setCdUf(String cdUf) {
		this.cdUf = cdUf;
	}

}
