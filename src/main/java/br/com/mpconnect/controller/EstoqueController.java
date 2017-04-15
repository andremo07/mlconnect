package br.com.mpconnect.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.manager.ProdutoManagerBo;
import br.com.mpconnect.model.Produto;

@Component
@Scope(value="view")
public class EstoqueController implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;

	@Autowired
	private ProdutoManagerBo produtoManager;
	
	public List<Produto> produtosAtualizadosList;
	public List<Produto> produtosCadastradosList;


	public EstoqueController(){
		produtosAtualizadosList = new ArrayList<Produto>();
		produtosCadastradosList = new ArrayList<Produto>();
	}

	public void baixarEstoque(FileUploadEvent e){

		try {
			produtosAtualizadosList.clear();
			produtoManager.baixarEstoqueProdutos(e.getFile().getInputstream(), produtosAtualizadosList);
			finalizaUpload();
		} catch (IOException expt) {
			expt.printStackTrace();
		} 
	}

	public void reporEstoque(FileUploadEvent e){

		try {
			produtosAtualizadosList.clear();
			produtosCadastradosList.clear();
			produtoManager.reporEstoqueProdutos(e.getFile().getInputstream(), produtosAtualizadosList,produtosCadastradosList);
			finalizaUpload();
		} catch (IOException expt) {
			expt.printStackTrace();
		}
	}

	private void finalizaUpload() throws IOException{
		
		String quebraLinha = System.getProperty("line.separator");
		FacesMessage message = new FacesMessage("Total de produtos atualizadas: " +produtosAtualizadosList.size()
												+quebraLinha+"Total de produtos cadastrados: "+produtosCadastradosList.size());
		message.setDetail(null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public List<Produto> getProdutosAtualizadosList() {
		return produtosAtualizadosList;
	}

	public void setProdutosAtualizadosList(List<Produto> produtosAtualizadosList) {
		this.produtosAtualizadosList = produtosAtualizadosList;
	}

	public List<Produto> getProdutosCadastradosList() {
		return produtosCadastradosList;
	}

	public void setProdutosCadastradosList(List<Produto> produtosCadastradosList) {
		this.produtosCadastradosList = produtosCadastradosList;
	}
	
}
