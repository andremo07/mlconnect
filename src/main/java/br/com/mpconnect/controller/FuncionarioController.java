package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.manager.CargoManagerBo;
import br.com.mpconnect.manager.FuncionarioManagerBo;
import br.com.mpconnect.model.Cargo;
import br.com.mpconnect.model.Funcionario;
import br.com.mpconnect.model.Produto;

@Component
@Scope(value="view")
public class FuncionarioController extends GenericCrudController<Funcionario> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private Funcionario funcionario;
	private List<Cargo> cargos;

	@Autowired
	private CargoManagerBo cargoManager;

	@Autowired
	private FuncionarioManagerBo funcionarioManager ;

	public FuncionarioController(){
		cargos = new ArrayList<Cargo>();		
	}

	@PostConstruct
	public void init(){

		funcionario = new Funcionario();
		if(tipoOperacao==0){
			this.getModel().setRowCount(funcionarioManager.recuperaTotalFuncionarios().intValue());
			this.getModel().setDatasource(funcionarioManager.listarFuncionariosPorIntervalo(0, this.getModel().getPageSize()));
		}
		else if(tipoOperacao==1)
			cargos = cargoManager.listarCargos();
		else{
			cargos = cargoManager.listarCargos();
			funcionario = (Funcionario) getSessionAttribute("funcionario");
		}	
	}

	public String salvar(){

		if(tipoOperacao==1)
			funcionarioManager.inserirFuncionario(funcionario);
		else
			funcionarioManager.editarFuncionario(funcionario);
		
		addSessionAttribute("tipoOperacao", 0);
		return "listaFuncionarios";

	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "cadastroFuncionarios";
	}

	public String editar(){

		addSessionAttribute("funcionario", funcionario);
		addSessionAttribute("tipoOperacao", 2);
		return "cadastroFuncionarios";
	}
	
	public void remover(){
		funcionarioManager.removerFuncionario(funcionario);
		addMessage("Sucesso!", "Funcionário removido com êxito.");
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public Funcionario getFuncionario() {
		return funcionario;
	}

	public void setFuncionario(Funcionario funcionario) {
		this.funcionario = funcionario;
	}

	public List<Cargo> getCargos() {
		return cargos;
	}

	public void setCargos(List<Cargo> cargos) {
		this.cargos = cargos;
	}

	public CargoManagerBo getCargoManager() {
		return cargoManager;
	}

	public void setCargoManager(CargoManagerBo cargoManager) {
		this.cargoManager = cargoManager;
	}

	@Override
	public List<Funcionario> paginacao(int first, int pageSize, Map<String,Object> filters){	
		return null;
	}

}
