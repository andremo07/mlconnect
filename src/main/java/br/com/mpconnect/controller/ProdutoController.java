package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.AdRepository;
import br.com.trendsoftware.markethub.repository.ProductRepository;

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
	private AdRepository adRepository;
	
	@Autowired
	private ProductRepository productRepository;

	public ProdutoController(){
		anuncios = new ArrayList<Anuncio>();		
	}

	@PostConstruct
	public void init()
	{
		//anuncioSelecionado = new Anuncio();
		produto = new Produto();
		if(tipoOperacao==0){
			Map<String,Object> filters = new HashMap<String, Object>();
			this.getModel().setRowCount(new Long(productRepository.count()).intValue());
			this.getModel().setDatasource(productRepository.findAllByPagingAndFilters(0, this.getModel().getPageSize(), filters));
		}
		else if(tipoOperacao==1){
			anuncios = adRepository.findAll();
			Set<Anuncio> anuncios = new HashSet<Anuncio>();
			produto.setAnuncios(anuncios);
		}
		else{
			adRepository.findAll();
			Long idProduto = (Long) getSessionAttribute("idProduto");
			Optional<Produto> result = productRepository.findById(idProduto);
			produto = (result.isPresent() ? result.get() : null);
		}
	}

	public String salvar()
	{
		if(tipoOperacao==1)
			produto.setUnidadeComercial("UN");
		
		productRepository.save(produto);

		addSessionAttribute("tipoOperacao", 0);
		return "listaProdutos";
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

	public String editar()
	{
		addSessionAttribute("idProduto", produto.getId());
		addSessionAttribute("tipoOperacao", 2);
		return "cadastroProdutos?faces-redirect=true";
	}

	public void remover()
	{
		productRepository.delete(produto);
		addMessage("Sucesso!", "Produto removido com êxito.");
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

	@Override
	public List<Produto> paginacao(int first, int pageSize, Map<String,Object> filters)
	{
		this.getModel().setRowCount(productRepository.count(filters).intValue());
		return productRepository.findAllByPagingAndFilters(first, pageSize, filters);
	}

}
