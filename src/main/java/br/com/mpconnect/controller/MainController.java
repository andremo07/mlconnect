package br.com.mpconnect.controller;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value="paginaInicial")
@Scope(value="request")
public class MainController {

	@PostConstruct
	public void initMyBean(){
		Map<String,String> requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		requestParams.get("reportKey");
	}

}
