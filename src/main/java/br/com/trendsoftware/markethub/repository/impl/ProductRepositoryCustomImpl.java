package br.com.trendsoftware.markethub.repository.impl;

import br.com.mpconnect.model.Persistente;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.ProductRepositoryCustom;

public class ProductRepositoryCustomImpl<P extends Persistente> extends JpaRepositoryCustomImpl<Produto> implements ProductRepositoryCustom{

	@Override
	public Class<Produto> getParameterizedType() {
		return Produto.class;
	}
}
