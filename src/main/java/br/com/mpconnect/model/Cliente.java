package br.com.mpconnect.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name="CLIENTE")
@PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
public class Cliente extends Pessoa implements Persistente{
	
	@Column(name="IDML")
	private String idMl;
	
	@Column(name="APELIDO")
	private String apelido;
	
	@Column(name="TP_CONTRIB_ICMS")
	private Integer tipoContribuinteIcms;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="CLIENTE_ID")
	private List<Venda> vendas;
	
	public String getIdMl() {
		return idMl;
	}

	public void setIdMl(String idMl) {
		this.idMl = idMl;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public Integer getTipoContribuinteIcms() {
		return tipoContribuinteIcms;
	}

	public void setTipoContribuinteIcms(Integer tipoContribuinteIcms) {
		this.tipoContribuinteIcms = tipoContribuinteIcms;
	}

	public List<Venda> getVendas() {
		return vendas;
	}

	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}
}
