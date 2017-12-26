package br.com.mpconnect.manager.impl;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mpconnect.model.Usuario;

public class MarketHubBusiness
{	
	final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public Logger getLogger() {
		return logger;
	}
	
	public Usuario getSessionUserLogin(){
		
		Map<String,Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Usuario usuario =  (Usuario) map.get("usuario");
		return usuario;

	}
	
}
