package br.com.mpconnect.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.FuncionarioDao;
import br.com.mpconnect.manager.FuncionarioManagerBo;
import br.com.mpconnect.model.Funcionario;

@Service("funcionarioManager")
public class FuncionarioManagerBoImpl implements FuncionarioManagerBo{

	@Resource
	public FuncionarioDao funcionarioDao;

	@Override
	@Transactional
	public void inserirFuncionario(Funcionario funcionario) {
		
		try {
			funcionarioDao.gravar(funcionario);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	@Transactional
	public void editarFuncionario(Funcionario funcionario) {
		
		try {
			funcionarioDao.alterar(funcionario);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	@Transactional
	public void removerFuncionario(Funcionario funcionario) {
		
		try {
			funcionarioDao.deletar(funcionario);
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public List<Funcionario> listarFuncionarios() {
		
		try {
			return funcionarioDao.recuperaTodos();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public List<Funcionario> listarFuncionariosPorIntervalo(int first,int max) {
		
		try {
			return funcionarioDao.recuperaTodosPorIntervalo(first, max, null);
		} catch (DaoException e) {

			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public Long recuperaTotalFuncionarios() {
		
		try {
			return funcionarioDao.recuperaTotalRegistros();
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Long(0);
	}

}
