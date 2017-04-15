package br.com.mpconnect.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="FORNECEDOR")
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
public class Fornecedor extends Pessoa implements Persistente{
	
	@Column(name="RAZAO_SOCIAL")
	private String razaoSocial;
	
	@Column(name="NOME_FANTASIA")
	private String nomeFantasia;
	
	@Column(name="IE")
	private String ie;

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public String getIe() {
		return ie;
	}

	public void setIe(String ie) {
		this.ie = ie;
	}
}
