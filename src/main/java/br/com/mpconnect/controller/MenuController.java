package br.com.mpconnect.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


@ManagedBean
@ViewScoped
public class MenuController implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1081908830912548655L;
	private FacesContext facesContext;
	private HttpSession session;
	
	public MenuController(){

	}
	
	public void consultar(){
		facesContext = FacesContext.getCurrentInstance();
	    session = (HttpSession) facesContext.getExternalContext().getSession(true);
		session.setAttribute("tipoOperacao", 0);
	}

}
