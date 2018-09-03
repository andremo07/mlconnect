package br.com.trendsoftware.markethub.b2w.business.impl;

import java.io.Serializable;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Channel;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;

@Service("b2WOrderBusiness")
public class OrderBusinessImpl extends OrderBusiness implements Serializable {

	private static final long serialVersionUID = -6462524421141281130L;

	public Origem getChannel(){
		return Channel.B2W.getOrigem();
	}

	@Override
	@Transactional
	public void save(Venda order) throws BusinessException
	{
		Optional<Vendedor> result = sellerRepository.findById(1L);
		Vendedor vendedor = result.get();
		order.setVendedor(vendedor);
		saveOrder(order);
	}

	@Override
	public Venda searchPartnerOrder(String userId, String orderId) throws BusinessException {
		return null;
	}
}