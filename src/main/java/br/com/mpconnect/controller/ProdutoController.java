package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Produto;

@Component
@Scope(value="view")
public class ProdutoController extends GenericCrudController<Produto> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private Produto produto;
	private Anuncio anuncioSelecionado;
	private List<Anuncio> anuncios;
	private String teste;

	@Autowired
	private AnuncioDao anuncioDao;

	@Autowired
	private ProdutoDao produtoDao;

	public ProdutoController(){
		anuncios = new ArrayList<Anuncio>();		
	}

	@PostConstruct
	public void init(){
		try{
			//anuncioSelecionado = new Anuncio();
			produto = new Produto();
			if(tipoOperacao==0){
				this.getModel().setRowCount(produtoDao.recuperaTotalRegistros().intValue());
				this.getModel().setDatasource(produtoDao.recuperaTodosPorIntervalo(0, this.getModel().getPageSize(), new HashMap<String, Object>()));
			}
			else if(tipoOperacao==1){
				anuncios = anuncioDao.recuperaTodos();
				Set<Anuncio> anuncios = new HashSet<Anuncio>();
				produto.setAnuncios(anuncios);
			}
			else{
				anuncios = anuncioDao.recuperaTodos();
				Long idProduto = (Long) getSessionAttribute("idProduto");
				produto = produtoDao.recuperaUm(idProduto);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public String salvar(){
		try {
			if(tipoOperacao==1){
				produto.setUnidadeComercial("UN");
				produtoDao.gravar(produto);
			}
			else
				produtoDao.alterar(produto);

			addSessionAttribute("tipoOperacao", 0);
			return "listaProdutos";

		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String adicionaAnuncio(){
		produto.getAnuncios().add(anuncioSelecionado);
		return null;
	}

	public String removerAnuncio(){
		produto.getAnuncios().remove(anuncioSelecionado);
		return null;
	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "cadastroProdutos?faces-redirect=true";
	}

	public String editar(){

		addSessionAttribute("idProduto", produto.getId());
		addSessionAttribute("tipoOperacao", 2);
		return "cadastroProdutos?faces-redirect=true";
	}

	public void remover(){
		try{
			produtoDao.deletar(produto);
			addMessage("Sucesso!", "Produto removido com êxito.");
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public String getTeste() {
		return teste;
	}

	public String colorStatus(Integer qtd){
		if(qtd>=10)
			return "ESTOQUE_ALTO";
		else 
			if(qtd<10 && qtd>=5)
				return "ESTOQUE_MEDIO";
			else
				return "ESTOQUE_BAIXO";
	}

	public void setTeste(String teste) {
		this.teste = teste;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Anuncio getAnuncioSelecionado() {
		return anuncioSelecionado;
	}

	public void setAnuncioSelecionado(Anuncio anuncioSelecionado) {
		this.anuncioSelecionado = anuncioSelecionado;
	}

	public List<Anuncio> getAnuncios() {
		return anuncios;
	}

	public void setAnuncios(List<Anuncio> anuncios) {
		this.anuncios = anuncios;
	}

	public AnuncioDao getAnuncioDao() {
		return anuncioDao;
	}

	public void setAnuncioDao(AnuncioDao anuncioDao) {
		this.anuncioDao = anuncioDao;
	}

	@Override
	public List<Produto> paginacao(int first, int pageSize, Map<String,Object> filters){
		try{
			this.getModel().setRowCount(produtoDao.recuperaTotalRegistros(filters).intValue());
			return produtoDao.recuperaTodosPorIntervalo(first, pageSize, filters);
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

}
