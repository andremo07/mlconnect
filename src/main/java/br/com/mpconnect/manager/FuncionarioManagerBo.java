package br.com.mpconnect.manager;

import java.util.List;

import br.com.mpconnect.model.Funcionario;

public interface FuncionarioManagerBo {
	
	public void inserirFuncionario(Funcionario funcionario);
	public void editarFuncionario(Funcionario funcionario);
	public void removerFuncionario(Funcionario funcionario);
	public List<Funcionario> listarFuncionarios();
	public List<Funcionario> listarFuncionariosPorIntervalo(int first,int max);
	public Long recuperaTotalFuncionarios();
}
