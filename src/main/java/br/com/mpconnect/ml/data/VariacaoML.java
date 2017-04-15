package br.com.mpconnect.ml.data;

import java.util.List;

public class VariacaoML {
	
	private String id;
	private Integer qtdDisponivel;
	private Integer qtdVendida;
	private String codRefProduto;
	private Double preco;
	private List<ValorVariacaoML> valores;
	private List<PictureML> pictures;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public List<ValorVariacaoML> getValores() {
		return valores;
	}
	public void setValores(List<ValorVariacaoML> valores) {
		this.valores = valores;
	}
	public Integer getQtdDisponivel() {
		return qtdDisponivel;
	}
	public void setQtdDisponivel(Integer qtdDisponivel) {
		this.qtdDisponivel = qtdDisponivel;
	}
	public Integer getQtdVendida() {
		return qtdVendida;
	}
	public void setQtdVendida(Integer qtdVendida) {
		this.qtdVendida = qtdVendida;
	}
	public List<PictureML> getPictures() {
		return pictures;
	}
	public void setPictures(List<PictureML> pictures) {
		this.pictures = pictures;
	}
	public String getCodRefProduto() {
		return codRefProduto;
	}
	public void setCodRefProduto(String codRefProduto) {
		this.codRefProduto = codRefProduto;
	}
	public Double getPreco() {
		return preco;
	}
	public void setPreco(Double preco) {
		this.preco = preco;
	}
}
