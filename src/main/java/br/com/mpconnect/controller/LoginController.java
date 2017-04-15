package br.com.mpconnect.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.manager.AcessoManagerBo;

import com.mercadolibre.sdk.Meli;


@Component
@Scope(value="view")
public class LoginController implements Serializable{
	
	private String user;
	private String senha;
		
	@Autowired
	private AcessoManagerBo acessoManager;
	
	public LoginController(){
		System.out.println();
	}
	
	public String login(){
		
		if(user.equals("admin") && senha.equals("admin")){
			
			 Meli meli = acessoManager.getAcessoMl();
			
			return "paginas/paginaInicial?faces-redirect=true";
		}
		
		return "";
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
