package br.com.mpconnect.dao;

import java.util.Date;
import java.util.List;

import br.com.mpconnect.model.Envio;

public interface EnvioDao extends DaoCrud<Envio>{
	
	public Envio recuperaEnvioPorIdMl(String idMl);
	public List<Envio> listarEnvios(Date dtInicio, Date dtFim);
}
