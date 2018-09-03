package br.com.mpconnect.manager;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Usuario;

public abstract class MarketHubBusiness
{	
	public final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public abstract Origem getChannel();
	
	public Logger getLogger() {
		return logger;
	}
	
	public Usuario getSessionUserLogin(){
		
		Map<String,Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		Usuario usuario =  (Usuario) map.get("usuario");
		return usuario;

	}
	
}