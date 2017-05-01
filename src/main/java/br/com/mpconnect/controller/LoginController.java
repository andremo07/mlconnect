package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mercadolibre.sdk.Meli;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.UsuarioDao;
import br.com.mpconnect.manager.AcessoManagerBo;
import br.com.mpconnect.model.Usuario;


@Component
@Scope(value="view")
public class LoginController implements Serializable{

	private String user;
	private String senha;

	@Autowired
	private AcessoManagerBo acessoManager;

	@Autowired
	private UsuarioDao usuarioDao;
	
	public LoginController(){

	}

	public String login(){

		try {
			
			Usuario usuario = usuarioDao.getUsuarioByLoginSenha("admin", "admin");

			if(usuario!=null){
				acessoManager.conectarMl();
				Map<String,Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
				map.put("usuario", usuario);
				if(map.containsKey("lastPage")){
					String lastPage = (String) map.get("lastPage");
					map.remove("lastPage");
					return lastPage+"?faces-redirect=true";
				}
				return "paginas/paginaInicial?faces-redirect=true";
			}
		

		} catch (DaoException e) {
			addMessage(FacesMessage.SEVERITY_INFO,"Ocorreu um erro!", "Erro na consulta");
		}

		addMessage(FacesMessage.SEVERITY_INFO,"Usuario inválido!", "Não existe o usuário no sistema");
		return "";
	}

    public void addMessage(Severity type, String summary, String detail) {
        FacesMessage message = new FacesMessage(type, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}

	public AcessoManagerBo getAcessoManager() {
		return acessoManager;
	}

	public void setAcessoManager(AcessoManagerBo acessoManager) {
		this.acessoManager = acessoManager;
	}

}
