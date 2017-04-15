package br.com.mpconnect.controller;

import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import br.com.mpconnect.controller.datamodel.GenericDataModel;

@Component
public abstract class GenericCrudController<T> {
	
	private FacesContext facesContext;
	private HttpSession session;
	private GenericDataModel<T> model;
	protected Integer tipoOperacao;
	
	public GenericCrudController(){	
		facesContext = FacesContext.getCurrentInstance();
	    session = (HttpSession) facesContext.getExternalContext().getSession(true);
	    model = new GenericDataModel<T>(this);
		tipoOperacao = (Integer) getSessionAttribute("tipoOperacao");
	}
	
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }
    
    public void addSessionAttribute(String key, Object obj){
    	session.setAttribute(key, obj);
    }
    
    public void removeSessionAttribute(String key){
    	session.removeAttribute(key);
    }
    
    public Object getSessionAttribute(String key){
    	return session.getAttribute(key);
    }

	public GenericDataModel<T> getModel() {
		return model;
	}

	public void setModel(GenericDataModel<T> model) {
		this.model = model;
	}
	
	public Integer getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(Integer tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	public void voltar(){
		addSessionAttribute("tipoOperacao", 0);
	}
	
	public abstract List<T> paginacao(int first, int pageSize,Map<String,Object> filters);
}
