package br.com.mpconnect.manager;

import com.mercadolibre.sdk.Meli;

import br.com.mpconnect.model.AcessoMl;


public interface AcessoManagerBo {
	
	public Meli conectarMl();
	public AcessoMl recuperarUltimoAcesso();
	
}
