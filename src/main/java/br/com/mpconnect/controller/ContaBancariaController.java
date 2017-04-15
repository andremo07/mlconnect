package br.com.mpconnect.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.manager.ContaBancariaManagerBo;
import br.com.mpconnect.model.ContaBancaria;
import br.com.mpconnect.model.Saldo;
import br.com.mpconnect.model.Vendedor;

@Component
@Scope(value="view")
public class ContaBancariaController extends GenericCrudController<ContaBancaria>{

	private ContaBancaria conta;
	private Saldo saldo;

	@Autowired
	private ContaBancariaManagerBo contaBancariaManager;
	
	@Autowired
	private VendedorDao vendedorDao;

	public ContaBancariaController(){

		conta = new ContaBancaria();
		saldo = new Saldo();
	}

	@PostConstruct
	public void init(){
		
		try {
			Vendedor vendedor = vendedorDao.recuperaUm(new Long(1));
			conta.setVendedor(vendedor);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String salvar(){
		
		try {
			List<Saldo> saldos = new ArrayList<Saldo>();
			saldos.add(saldo);
			conta.setSaldos(saldos);
			contaBancariaManager.salvarConta(conta);
			addSessionAttribute("tipoOperacao", 0);
			return "paginaInicial";
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}

	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	@Override
	public List<ContaBancaria> paginacao(int first, int pageSize,
			Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	public ContaBancaria getConta() {
		return conta;
	}

	public void setConta(ContaBancaria conta) {
		this.conta = conta;
	}

	public Saldo getSaldo() {
		return saldo;
	}

	public void setSaldo(Saldo saldo) {
		this.saldo = saldo;
	}

}
