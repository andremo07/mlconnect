package br.com.mpconnect.manager.impl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.manager.LogisticBusiness;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.util.ExceptionUtil;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.restProvider.exception.ProviderException;

@Service("logisticBusiness")
public class LogisticBusinessImpl extends MarketHubBusiness implements LogisticBusiness, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6462524421141281130L;
	
	@Autowired
	private ShippingProvider shippingProvider;

	@PostConstruct
	public void init(){
		getShippingProvider().setLogger(logger);
	}

	@Override
	public InputStream printShippingTags(List<Venda> vendas) throws BusinessException{

		try {
			Usuario usuario = getSessionUserLogin();
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();

			List<String> shippingIds = new ArrayList<String>();

			for(Venda venda: vendas)
				shippingIds.add(venda.getEnvio().getIdMl());

			return shippingProvider.printTags(shippingIds, accessToken);
		} catch (ProviderException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("Error printing ML shipping tags", e.getCode(), e.getCodeMessage());
			throw new BusinessProviderException(exception);
		}
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}
	
}
