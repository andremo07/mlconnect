package br.com.mpconnect.ml.data;

import java.util.List;
import java.util.Map;

public class AnuncioML {

	private String id;
	private String titulo;
	private Double valor;
	private String idCategoria;
	private Integer quantidade;
	private String status;
	private String descricao;
	private TipoAnuncioML tipo;
	private MetodoEnvioML metodoEnvio;
	private List<PictureML> pictures;
	private List<VariacaoML> variacoes;
	private String html;
	
	
	public AnuncioML(){
		
	}
	
	public AnuncioML(String id, String titulo, Double valor,
			String idCategoria, Integer quantidade, String status, String descricao) {
		super();
		this.id = id;
		this.titulo = titulo;
		this.valor = valor;
		this.idCategoria = idCategoria;
		this.quantidade = quantidade;
		this.status = status;
		this.descricao = descricao;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(String idCategoria) {
		this.idCategoria = idCategoria;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
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

	public List<PictureML> getPictures() {
		return pictures;
	}

	public void setPictures(List<PictureML> pictures) {
		this.pictures = pictures;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public TipoAnuncioML getTipo() {
		return tipo;
	}

	public void setTipo(TipoAnuncioML tipo) {
		this.tipo = tipo;
	}

	public List<VariacaoML> getVariacoes() {
		return variacoes;
	}

	public void setVariacoes(List<VariacaoML> variacoes) {
		this.variacoes = variacoes;
	}

	public MetodoEnvioML getMetodoEnvio() {
		return metodoEnvio;
	}

	public void setMetodoEnvio(MetodoEnvioML metodoEnvio) {
		this.metodoEnvio = metodoEnvio;
	}	
}
