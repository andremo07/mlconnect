package br.com.mpconnect.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope(value="view")
public class MenuController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1081908830912548655L;
	private FacesContext facesContext;
	private HttpSession session;
	
	public MenuController(){
		facesContext = FacesContext.getCurrentInstance();
	    session = (HttpSession) facesContext.getExternalContext().getSession(true);
	}
	
	public void consultar(){
		session.setAttribute("tipoOperacao", 0);
	}

}
