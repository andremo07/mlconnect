package br.com.mpconnect.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="VENDEDOR")
public class Vendedor implements Persistente{
	
	@Id
	@Column(name="ID")
	@GeneratedValue
	private Long id;
	
	@Column(name="IDML")
	private String idMl;
	
	@Column(name="APELIDO")
	private String apelido;
	
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
	
	@Column(name="CNAE")
	private Integer cnae;
	
	@Column(name="CNPJ")
	private String cnpj;
	
	@Column(name="INSCRICAO_ESTADUAL")
	private String incriEstadual;
	
	@Column(name="INSCRICAO_MUNICIPAL")
	private String incriMunicipal;
	
	@Column(name="RAZAO_SOCIAL")
	private String razaoSocial;
	
	@Column(name="REGIME_TRIBUTARIO")
	private Integer regimeTributario;
	
	@Column(name="FAT_ULT_12_MES")
	private Double faturamento;
	
	@OneToMany
	@JoinColumn(name="VENDEDOR_ID")
	private List<Venda> vendas;

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

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
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

	public Integer getCnae() {
		return cnae;
	}

	public void setCnae(Integer cnae) {
		this.cnae = cnae;
	}

	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public String getIncriEstadual() {
		return incriEstadual;
	}

	public void setIncriEstadual(String incriEstadual) {
		this.incriEstadual = incriEstadual;
	}

	public String getIncriMunicipal() {
		return incriMunicipal;
	}

	public void setIncriMunicipal(String incriMunicipal) {
		this.incriMunicipal = incriMunicipal;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Integer getRegimeTributario() {
		return regimeTributario;
	}

	public void setRegimeTributario(Integer regimeTributario) {
		this.regimeTributario = regimeTributario;
	}

	public Double getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(Double faturamento) {
		this.faturamento = faturamento;
	}

	public List<Venda> getVendas() {
		return vendas;
	}

	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}
}
