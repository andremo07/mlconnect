package br.com.trendsoftware.markethub.business;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Usuario;

public abstract class MarketHubBusiness
{	
	public final Logger logger = LogManager.getLogger(this.getClass());
	
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
