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

import br.com.mpconnect.dao.CategoriaContaPagarDao;
import br.com.mpconnect.dao.ContaPagarDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.PessoaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.ml.api.enums.StatusContaPagarEnum;
import br.com.mpconnect.model.CategoriaContaPagar;
import br.com.mpconnect.model.ContaPagar;
import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.Vendedor;

@Component
@Scope(value="view")
public class ContaPagarController extends GenericCrudController<ContaPagar> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private ContaPagar contaPagar;
	private List<Pessoa> pessoas;
	private List<String> status;
	private String statusInicial;
	private List<CategoriaContaPagar> categorias;
	private CategoriaContaPagar categoria;

	@Autowired
	private CategoriaContaPagarDao categoriaContaPagarDao;

	@Autowired
	private PessoaDao pessoaDao;

	@Autowired
	private ContaPagarDao contaPagarDao;

	@Autowired
	private VendedorDao vendedorDao;

	public ContaPagarController(){
		contaPagar = new ContaPagar();
		pessoas = new ArrayList<Pessoa>();	
		categorias = new ArrayList<CategoriaContaPagar>();
		status = new ArrayList<String>();
		statusInicial = StatusContaPagarEnum.A_PAGAR.getValue();
	}

	@PostConstruct
	public void init(){
		try{
			if(tipoOperacao==0){
				for(StatusContaPagarEnum statusEnum:StatusContaPagarEnum.values())
					status.add(statusEnum.getValue());
				status.remove(statusInicial);
				
				Map<String,Object> filters = new HashMap<String, Object>();
				filters.put("status", statusInicial);
				
				this.getModel().setRowCount(contaPagarDao.recuperaTotalRegistros().intValue());
				this.getModel().setDatasource(contaPagarDao.recuperaTodosPorIntervalo(0, this.getModel().getPageSize(), filters));
			}
			else if(tipoOperacao==1){
				categorias = categoriaContaPagarDao.recuperaTodos();
				pessoas = pessoaDao.recuperaTodos();
			}
			else{
				categorias = categoriaContaPagarDao.recuperaTodos();
				pessoas = pessoaDao.recuperaTodos();
				Long idConta = (Long) getSessionAttribute("idContaPagar");
				contaPagar = contaPagarDao.recuperaUm(idConta);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public String salvar(){
		try {
			Vendedor vendedor = vendedorDao.recuperaUm(new Long(1));
			contaPagar.setVendedor(vendedor);
			if(tipoOperacao==1){
				contaPagar.setStatus(StatusContaPagarEnum.A_PAGAR.getValue());
				contaPagarDao.gravar(contaPagar);
			}
			else
				contaPagarDao.alterar(contaPagar);

			addSessionAttribute("tipoOperacao", 0);
			return "listaContasPagar";

		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void visualizar(ContaPagar contaPagar){
		try {
			this.contaPagar = contaPagarDao.recuperaUm(contaPagar.getId());
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void baixar(){

		try {
			contaPagar.setStatus(StatusContaPagarEnum.PAGO.getValue());
			contaPagarDao.alterar(contaPagar);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reverterBaixar(ContaPagar contaPagar){

		try {
			contaPagar.setDataBaixa(null);
			contaPagar.setStatus(StatusContaPagarEnum.A_PAGAR.getValue());
			contaPagarDao.alterar(contaPagar);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "contasPagar?faces-redirect=true";
	}

	public String editar(){

		addSessionAttribute("idContaPagar", contaPagar.getId());
		addSessionAttribute("tipoOperacao", 2);
		return "contasPagar?faces-redirect=true";
	}

	public void remover(){
		try{
			contaPagarDao.deletar(contaPagar);
			addMessage("Sucesso!", "Conta removida com êxito.");
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void abrirPopupCadastro(){
		categoria = new CategoriaContaPagar();
	}

	public void cadastrarCategoria(){
		try {
			categorias.add(categoria);
			categoriaContaPagarDao.gravar(categoria);
			addMessage("Sucesso!", "Gravação realizada com êxito.");
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public ContaPagar getContaPagar() {
		return contaPagar;
	}

	public void setContaPagar(ContaPagar contaPagar) {
		this.contaPagar = contaPagar;
	}

	public List<Pessoa> getPessoas() {
		return pessoas;
	}

	public void setPessoas(List<Pessoa> pessoas) {
		this.pessoas = pessoas;
	}

	public List<CategoriaContaPagar> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<CategoriaContaPagar> categorias) {
		this.categorias = categorias;
	}

	public CategoriaContaPagar getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaContaPagar categoria) {
		this.categoria = categoria;
	}

	public List<String> getStatus() {
		return status;
	}

	public void setStatus(List<String> status) {
		this.status = status;
	}

	public String getStatusInicial() {
		return statusInicial;
	}

	public void setStatusInicial(String statusInicial) {
		this.statusInicial = statusInicial;
	}

	@Override
	public List<ContaPagar> paginacao(int first, int pageSize, Map<String,Object> filters){
		try{
			if(filters.isEmpty())
				filters.put("status", statusInicial);
			
			this.getModel().setRowCount(contaPagarDao.recuperaTotalRegistros(filters).intValue());
			return contaPagarDao.recuperaTodosPorIntervalo(first, pageSize, filters);
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

}
