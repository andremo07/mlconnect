package br.com.mpconnect.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ENVIO")
public class Envio implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;
	
	@Column(name="IDML")
	private String idMl;
	
	@Column(name="MODO")
	private String modo;
	
	@Column(name="TIPO")
	private String tipo;
	
	@Column(name="METODO")
	private String metodo;
	
	@Column(name="CUSTO")
	private Double custo;
	
	@Column(name="DATA")
	private Date data;
	
	@Column(name="LOGRADOURO")
	private String logradouro;
	
	@Column(name="NUMERO")
	private String numero;
	
	@Column(name="COMPLEMENTO")
	private String complemento;
	
	@Column(name="BAIRRO")
	private String bairro;
	
	@Column(name="CD_MUNICIPIO")
	private Integer codMunicipio;
	
	@Column(name="MUNICIPIO")
	private String municipio;
	
	@Column(name="UF")
	private String uf;
	
	@Column(name="CEP")
	private Long cep;
	
	@Column(name="CD_PAIS")
	private Integer codPais;
	
	@Column(name="PAIS")
	private String pais;

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

	public String getModo() {
		return modo;
	}

	public void setModo(String modo) {
		this.modo = modo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	public Double getCusto() {
		return custo;
	}

	public void setCusto(Double custo) {
		this.custo = custo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public Integer getCodMunicipio() {
		return codMunicipio;
	}

	public void setCodMunicipio(Integer codMunicipio) {
		this.codMunicipio = codMunicipio;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public Long getCep() {
		return cep;
	}

	public void setCep(Long cep) {
		this.cep = cep;
	}

	public Integer getCodPais() {
		return codPais;
	}

	public void setCodPais(Integer codPais) {
		this.codPais = codPais;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}
}
