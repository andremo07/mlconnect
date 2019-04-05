package br.com.trendsoftware.markethub.b2w.business.impl;

import org.springframework.stereotype.Service;

import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Origem;
import br.com.trendsoftware.markethub.business.AdBusiness;

@Service("b2WAdBusiness")
public class AdBusinessImpl extends AdBusiness 
{
	public static int savedAds = 0;
	
	public Origem getOrigem()
	{
		return Channel.B2W.getOrigem();
	}
}
