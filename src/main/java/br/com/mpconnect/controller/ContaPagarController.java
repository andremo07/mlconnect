package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.CategoriaContaPagar;
import br.com.mpconnect.model.ContaPagar;
import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.StatusContaPagarEnum;
import br.com.mpconnect.model.Vendedor;
import br.com.trendsoftware.markethub.repository.PayingBillCategoryRepository;
import br.com.trendsoftware.markethub.repository.PayingBillRepository;
import br.com.trendsoftware.markethub.repository.PersonRepository;
import br.com.trendsoftware.markethub.repository.SellerRepository;

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
	private PayingBillCategoryRepository payingBillCategoryRepository;

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PayingBillRepository payingBillRepository;

	@Autowired
	private SellerRepository sellerRepository;

	public ContaPagarController(){
		contaPagar = new ContaPagar();
		pessoas = new ArrayList<Pessoa>();	
		categorias = new ArrayList<CategoriaContaPagar>();
		status = new ArrayList<String>();
		statusInicial = StatusContaPagarEnum.A_PAGAR.getValue();
	}

	@PostConstruct
	public void init()
	{
		if(tipoOperacao==0){
			for(StatusContaPagarEnum statusEnum:StatusContaPagarEnum.values())
				status.add(statusEnum.getValue());
			status.remove(statusInicial);
			
			Map<String,Object> filters = new HashMap<String, Object>();
			filters.put("status", statusInicial);
			
			this.getModel().setRowCount(new Long(payingBillRepository.count()).intValue());
			this.getModel().setDatasource(payingBillRepository.findAllByPagingAndFilters(0, this.getModel().getPageSize(), filters));
		}
		else if(tipoOperacao==1){
			categorias = payingBillCategoryRepository.findAll();
			pessoas = personRepository.findAll();
		}
		else{
			categorias = payingBillCategoryRepository.findAll();
			pessoas = personRepository.findAll();
			Long idConta = (Long) getSessionAttribute("idContaPagar");
			Optional<ContaPagar> result = payingBillRepository.findById(idConta);
			contaPagar = (result.isPresent() ? result.get():null);
		}
	}

	public String salvar()
	{
		Optional<Vendedor> result = sellerRepository.findById(1L);
		Vendedor vendedor = (result.isPresent() ? result.get() : null);
		contaPagar.setVendedor(vendedor);
		if(tipoOperacao==1){
			contaPagar.setStatus(StatusContaPagarEnum.A_PAGAR.getValue());
		}

		payingBillRepository.save(contaPagar);
		
		addSessionAttribute("tipoOperacao", 0);
		return "listaContasPagar";
	}

	public void visualizar(ContaPagar contaPagar)
	{
		Optional<ContaPagar> result = payingBillRepository.findById(contaPagar.getId());
		contaPagar = (result.isPresent() ? result.get():null);
	}

	public void baixar()
	{
		contaPagar.setStatus(StatusContaPagarEnum.PAGO.getValue());
		payingBillRepository.save(contaPagar);
	}

	public void reverterBaixar(ContaPagar contaPagar)
	{
		contaPagar.setDataBaixa(null);
		contaPagar.setStatus(StatusContaPagarEnum.A_PAGAR.getValue());
		payingBillRepository.save(contaPagar);
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

	public void remover()
	{
		payingBillRepository.delete(contaPagar);
		addMessage("Sucesso!", "Conta removida com êxito.");
	}

	public void abrirPopupCadastro(){
		categoria = new CategoriaContaPagar();
	}

	public void cadastrarCategoria()
	{
		categorias.add(categoria);
		payingBillCategoryRepository.save(categoria);
		addMessage("Sucesso!", "Gravação realizada com êxito.");
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
	public List<ContaPagar> paginacao(int first, int pageSize, Map<String,Object> filters)
	{
		if(filters.isEmpty())
			filters.put("status", statusInicial);
		
		this.getModel().setRowCount(payingBillRepository.count(filters).intValue());
		return payingBillRepository.findAllByPagingAndFilters(first, pageSize, filters);
	}
}
