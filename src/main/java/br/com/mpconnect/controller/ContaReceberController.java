package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.CategoriaContaReceberDao;
import br.com.mpconnect.dao.ContaReceberDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.PessoaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.ml.api.enums.StatusContaReceberEnum;
import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.ContaReceber;
import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.Vendedor;

@Component
@Scope(value="view")
public class ContaReceberController extends GenericCrudController<ContaReceber> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private ContaReceber contaReceber;
	private List<Pessoa> pessoas;
	private CategoriaContaReceber categoria;
	private List<CategoriaContaReceber> categorias;

	@Autowired
	private CategoriaContaReceberDao categoriaContaReceberDao;

	@Autowired
	private PessoaDao pessoaDao;

	@Autowired
	private ContaReceberDao contaReceberDao;

	@Autowired
	private VendedorDao vendedorDao;

	public ContaReceberController(){
		contaReceber = new ContaReceber();
		pessoas = new ArrayList<Pessoa>();	
		categorias = new ArrayList<CategoriaContaReceber>();
	}

	@PostConstruct
	public void init(){
		try{
			if(tipoOperacao==0){
				this.getModel().setRowCount(contaReceberDao.recuperaTotalRegistros().intValue());
				this.getModel().setDatasource(contaReceberDao.recuperaTodosPorIntervalo(0, this.getModel().getPageSize(), new HashMap<String, Object>()));
			}
			else if(tipoOperacao==1){
				categorias = categoriaContaReceberDao.recuperaTodos();
				pessoas = pessoaDao.recuperaTodos();
			}
			else{
				categorias = categoriaContaReceberDao.recuperaTodos();
				pessoas = pessoaDao.recuperaTodos();
				Long idConta = (Long) getSessionAttribute("idContaReceber");
				contaReceber = contaReceberDao.recuperaUm(idConta);
			}
			
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public String salvar(){
		try {
			if(tipoOperacao==1){
				Vendedor vendedor = vendedorDao.recuperaUm(new Long(1));
				contaReceber.setStatus(StatusContaReceberEnum.A_RECEBER.getValue());
				contaReceber.setVendedor(vendedor);
				contaReceberDao.gravar(contaReceber);
			}
			else
				contaReceberDao.alterar(contaReceber);
			

			addSessionAttribute("tipoOperacao", 0);
			return "listaContasReceber";

		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void visualizar(ContaReceber contaPagar){
		try {
			this.contaReceber = contaReceberDao.recuperaUm(contaPagar.getId());
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void baixar(){

		try {
			contaReceber.setStatus(StatusContaReceberEnum.RECEBIDO.getValue());
			contaReceberDao.alterar(contaReceber);
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void reverterBaixar(ContaReceber contaReceber){
		try {
			contaReceber.setStatus(StatusContaReceberEnum.RECEBIDO.getValue());
			contaReceber.setDataBaixa(null);
			contaReceberDao.alterar(contaReceber);
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "contasReceber?faces-redirect=true";
	}

	public String editar(){

		addSessionAttribute("idContaReceber", contaReceber.getId());
		addSessionAttribute("tipoOperacao", 2);
		return "contasReceber?faces-redirect=true";
	}

	public void remover(){
		try{
			contaReceberDao.deletar(contaReceber);
			addMessage("Sucesso!", "Conta removida com êxito.");
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void abrirPopupCadastro(){
		categoria = new CategoriaContaReceber();
	}

	public void cadastrarCategoria(){
		try {
			categorias.add(categoria);
			categoriaContaReceberDao.gravar(categoria);
			addMessage("Sucesso!", "Gravação realizada com êxito.");
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public ContaReceber getContaReceber() {
		return contaReceber;
	}

	public void setContaReceber(ContaReceber contaPagar) {
		this.contaReceber = contaPagar;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public List<CategoriaContaReceber> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<CategoriaContaReceber> categorias) {
		this.categorias = categorias;
	}

	public CategoriaContaReceber getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaContaReceber categoria) {
		this.categoria = categoria;
	}

	@Override
	public List<ContaReceber> paginacao(int first, int pageSize, Map<String,Object> filters){
		try{
			this.getModel().setRowCount(contaReceberDao.recuperaTotalRegistros(filters).intValue());
			return contaReceberDao.recuperaTodosPorIntervalo(first, pageSize, filters);
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

}
