package br.com.trendsoftware.markethub.business;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.AdRepository;
import br.com.trendsoftware.markethub.repository.ProductRepository;

public abstract class AdBusiness {

	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public AdRepository adRepository;

	public static int savedAds = 0;
	
	public abstract Origem getOrigem();

	public Produto createProduct(Anuncio anuncio, Produto produto)
	{
		List<Produto> produtos = productRepository.findDistinctBySku(produto.getSku());

		if(produtos!=null && !produtos.isEmpty())
			produto = produtos.get(0);

		if(produto.getAnuncios()==null)
		{
			produto.setAnuncios(new HashSet<Anuncio>());
			produto.getAnuncios().add(anuncio);
		}
		else if(!produto.getAnuncios().contains(anuncio))
			produto.getAnuncios().add(anuncio);		

		return produto;		
	}
	
	@Transactional
	public void syncronizeAd(List<Anuncio> anuncios)
	{
		List<String> notSaved = new ArrayList<String>();

		anuncios.stream().forEach(anuncio -> 
		{
			Anuncio anuncioExistente = adRepository.findByIdMlAndOrigem(anuncio.getIdMl(),getOrigem().getId());

			if(anuncioExistente==null || anuncioExistente.getOrigem()==null)
			{
				Set<Produto> produtosAssociados = new HashSet<Produto>();

				for(Produto produto : anuncio.getProdutos())
				{
					produto = createProduct(anuncio, produto);
					produtosAssociados.add(produto);
				}

				if(anuncio.getProdutos()==null)
					anuncio.setProdutos(produtosAssociados);
				else
					anuncio.getProdutos().addAll(produtosAssociados);

				adRepository.save(anuncio);

				savedAds++;			
			}
			else
				notSaved.add(anuncio.getIdMl());

		});

		System.out.println(notSaved);
		System.out.println("SAVED ADS "+savedAds);
	}
}
