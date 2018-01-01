package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.UsuarioDao;
import br.com.mpconnect.holder.MeliConfigurationHolder;
import br.com.mpconnect.manager.AcessoManagerBo;
import br.com.mpconnect.ml.api.ApiMl;
import br.com.mpconnect.model.AcessoMl;
import br.com.mpconnect.model.Usuario;
import br.com.trendsoftware.mlProvider.dataprovider.UserProvider;
import br.com.trendsoftware.mlProvider.dto.User;
import br.com.trendsoftware.mlProvider.dto.UserCredencials;
import br.com.trendsoftware.mlProvider.response.Response;
import br.com.trendsoftware.restProvider.exception.ProviderException;


@Component
@Scope(value="view")
public class LoginController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4099469083045992073L;
	private String user;
	private String senha;

	@Autowired
	private AcessoManagerBo acessoManager;

	@Autowired
	private UsuarioDao usuarioDao;
	
	@Resource
	public AcessoMlDao acessoDao;
	
	@Autowired
	public UserProvider userProvider;
		
	@Autowired
	private ApiMl apiMl;
	
	final Logger logger = Logger.getLogger(this.getClass().getName());
	
	public LoginController(){

	}
	
	@PostConstruct
	public void init(){
		getUserProvider().setLogger(logger);
	}

	public String login(){

		try {
			
			Usuario usuario = usuarioDao.getUsuarioByLoginSenha(user, senha);

			if(usuario!=null){
				
				AcessoMl acessoMl = acessoDao.recuperarUltimo();
				
				Response response = userProvider.login(MeliConfigurationHolder.getInstance().getClientId().toString(), MeliConfigurationHolder.getInstance().getClientSecret(), acessoMl.getRefreshToken());
				UserCredencials token = (UserCredencials) response.getData(); 
				acessoMl.setAccessToken(token.getAccessToken());
				acessoMl.setRefreshToken(token.getRefreshToken());
				
				response = userProvider.getUserInfo(token.getAccessToken());
				User user = (User) response.getData();
				acessoMl.setIdMl(user.getId());
				usuario.setAcessoMercadoLivre(acessoMl);
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
		} catch (ProviderException e) {
			
			addMessage(FacesMessage.SEVERITY_INFO,"Ocorreu um erro!", "Erro Mercado Livre");
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

	public UserProvider getUserProvider() {
		return userProvider;
	}
	
}
