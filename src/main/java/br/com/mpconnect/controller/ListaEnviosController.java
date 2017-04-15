package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.EnvioDao;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.utils.DateUtils;

@Component
@Scope(value="view")
public class ListaEnviosController extends GenericCrudController<Venda> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	
	@Autowired
	private EnvioDao envioDao;
	
	private List<Envio> enviosRealizados;

	private Date dtInicio;
	
	private Date dtFim;
	

	public ListaEnviosController(){

		enviosRealizados = new ArrayList<Envio>();
	}

	public List<Envio> getEnviosRealizados() {
		return enviosRealizados;
	}

	public void setEnviosRealizados(List<Envio> enviosRealizados) {
		this.enviosRealizados = enviosRealizados;
	}
	
	public void listarEnvios(){
		dtFim = DateUtils.setHoras(dtFim, 24);
		enviosRealizados = envioDao.listarEnvios(dtInicio, dtFim);
	}
	
	public int getTotalRegistros(){
		
		return enviosRealizados.size();
	}

	@Override
	public List<Venda> paginacao(int first, int pageSize,
			Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

}
