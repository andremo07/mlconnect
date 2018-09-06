package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.ContaReceber;
import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.StatusContaReceberEnum;
import br.com.mpconnect.model.Vendedor;
import br.com.trendsoftware.markethub.repository.PersonRepository;
import br.com.trendsoftware.markethub.repository.ReceivingBillCategoryRepository;
import br.com.trendsoftware.markethub.repository.ReceivingBillRepository;
import br.com.trendsoftware.markethub.repository.SellerRepository;

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
	private ReceivingBillCategoryRepository receivingBillCategoryRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private ReceivingBillRepository receivingBillRepository;

	@Resource
	private SellerRepository sellerRepository;

	public ContaReceberController(){
		contaReceber = new ContaReceber();
		pessoas = new ArrayList<Pessoa>();	
		categorias = new ArrayList<CategoriaContaReceber>();
	}

	@PostConstruct
	public void init()
	{
		if(tipoOperacao==null||tipoOperacao==0){
			this.getModel().setRowCount(new Long(receivingBillRepository.count()).intValue());
			this.getModel().setDatasource(receivingBillRepository.findAllByPagingAndFilters(0, this.getModel().getPageSize(), new HashMap<String, Object>()));
		}
		else if(tipoOperacao==1){
			categorias = receivingBillCategoryRepository.findAll();
			pessoas = personRepository.findAll();
		}
		else{
			categorias = receivingBillCategoryRepository.findAll();
			pessoas = personRepository.findAll();
			Long idConta = (Long) getSessionAttribute("idContaReceber");
			Optional<ContaReceber> result = receivingBillRepository.findById(idConta);
			if(result.isPresent())
				contaReceber = result.get();
		}
	}

	public String salvar()
	{
		if(tipoOperacao==1){
			Optional<Vendedor> result = sellerRepository.findById(1L);
			contaReceber.setStatus(StatusContaReceberEnum.A_RECEBER.getValue());
			contaReceber.setVendedor(result.get());
		}
		
		receivingBillRepository.save(contaReceber);

		addSessionAttribute("tipoOperacao", 0);
		return "listaContasReceber";
	}

	public void visualizar(ContaReceber contaPagar)
	{
		Optional<ContaReceber> result = receivingBillRepository.findById(contaPagar.getId());
		if(result.isPresent())
			contaReceber = result.get();
	}

	public void baixar()
	{
		contaReceber.setStatus(StatusContaReceberEnum.RECEBIDO.getValue());
		receivingBillRepository.save(contaReceber);
	}

	public void reverterBaixar(ContaReceber contaReceber)
	{
		contaReceber.setStatus(StatusContaReceberEnum.A_RECEBER.getValue());
		contaReceber.setDataBaixa(null);
		receivingBillRepository.save(contaReceber);
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

	public void remover()
	{
		receivingBillRepository.delete(contaReceber);
		addMessage("Sucesso!", "Conta removida com êxito.");
	}

	public void abrirPopupCadastro(){
		categoria = new CategoriaContaReceber();
	}

	public void cadastrarCategoria()
	{
		categorias.add(categoria);
		receivingBillCategoryRepository.save(categoria);
		addMessage("Sucesso!", "Gravação realizada com êxito.");
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
	public List<ContaReceber> paginacao(int first, int pageSize, Map<String,Object> filters)
	{
		this.getModel().setRowCount(receivingBillRepository.count(filters).intValue());
		return receivingBillRepository.findAllByPagingAndFilters(first, pageSize, filters);
	}

}
