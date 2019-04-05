package br.com.trendsoftware.markethub.ml.business.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Origem;
import br.com.trendsoftware.markethub.business.AdBusiness;

@Service("mlAdBusiness")
public class AdBusinessImpl extends AdBusiness 
{
	public static int savedAds = 0;
	
	public Origem getOrigem()
	{
		return Channel.ML.getOrigem();
	}
}
