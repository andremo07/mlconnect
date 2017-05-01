package br.com.mpconnect.ml.api;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.mercadolibre.sdk.Meli;

import br.com.mpconnect.manager.AcessoManagerBo;
import br.com.mpconnect.model.AcessoMl;

public class ApiMl {
	
	@Resource
	private AcessoManagerBo acessoManager;
	
	private Meli me;
	
	public static final int RESPONSE_STATUS_CODE_FAIL = 400;
	public static final int RESPONSE_STATUS_CODE_SUCCESS = 204;
	
	@PostConstruct
	public void init(){
		AcessoMl acessoMl = acessoManager.recuperarUltimoAcesso();
		me = new Meli(acessoMl.getClientId(),acessoMl.getClientSecret() , acessoMl.getAccessToken(),acessoMl.getRefreshToken());
	}

	public AcessoManagerBo getAcessoManager() {
		return acessoManager;
	}

	public void setAcessoManager(AcessoManagerBo acessoManager) {
		this.acessoManager = acessoManager;
	}

	public Meli getMe() {
		return me;
	}

	public void setMe(Meli me) {
		this.me = me;
	}

}
