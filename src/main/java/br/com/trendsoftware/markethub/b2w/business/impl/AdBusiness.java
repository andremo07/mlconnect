package br.com.trendsoftware.markethub.b2w.business.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.b2wprovider.dto.SkyHubItem;
import br.com.trendsoftware.b2wprovider.dto.SkyHubVariation;
import br.com.trendsoftware.markethub.data.parser.B2WParser;
import br.com.trendsoftware.markethub.repository.AdRepository;
import br.com.trendsoftware.markethub.repository.ProductRepository;

@Service("b2WAdBusiness")
public class AdBusiness {
	
	@Autowired
	public ProductRepository productRepository;

	@Autowired
	public AdRepository adRepository;

	public static int savedAds = 0;

	@Transactional
	public void syncronizeAd(List<SkyHubItem> products)
	{
		List<String> notSaved = new ArrayList<String>();

		products.stream().forEach(skuHubAd -> 
		{
			Anuncio anuncio = adRepository.findByIdMl(skuHubAd.getSku());

			if(anuncio==null)
			{
				anuncio = B2WParser.parseAd(skuHubAd);

				Set<Produto> produtosAssociados = new HashSet<Produto>();

				if(skuHubAd.getVariations()==null || skuHubAd.getVariations().isEmpty())
				{
					Produto produto = createProduct(anuncio, skuHubAd.getSku(), skuHubAd.getName(), skuHubAd.getEan(), skuHubAd.getQty().intValue());
					produtosAssociados.add(produto);
				}
				else
					for(SkyHubVariation skyHubVariation: skuHubAd.getVariations())
					{
						Produto produto = createProduct(anuncio, skyHubVariation.getSku(), skuHubAd.getName(), skyHubVariation.getEan(), skyHubVariation.getQty().intValue());
						produtosAssociados.add(produto);
					}

				anuncio.setProdutos(produtosAssociados);
				adRepository.save(anuncio);

				savedAds++;			
			}
			else if(anuncio.getOrigem()==null)
			{								
				anuncio = B2WParser.parseAd(skuHubAd);

				Set<Produto> produtosAssociados = new HashSet<Produto>();

				if(skuHubAd.getVariations()==null || skuHubAd.getVariations().isEmpty())
				{
					Produto produto = createProduct(anuncio, skuHubAd.getSku(), skuHubAd.getName(), skuHubAd.getEan(), skuHubAd.getQty().intValue());
					produtosAssociados.add(produto);
				}
				else
					for(SkyHubVariation skyHubVariation: skuHubAd.getVariations())
					{
						Produto produto = createProduct(anuncio, skyHubVariation.getSku(), skuHubAd.getName(), skyHubVariation.getEan(), skyHubVariation.getQty().intValue());
						produtosAssociados.add(produto);
					}

				anuncio.getProdutos().addAll(produtosAssociados);
				adRepository.save(anuncio);
			}
			else if(anuncio.getOrigem()==3)
			{
				Long id = anuncio.getId();
				
				Set<Produto> produtosAssociados = new HashSet<Produto>(); 
				
				if(skuHubAd.getVariations()==null || skuHubAd.getVariations().isEmpty())
				{
					Produto produto = createProduct(anuncio, skuHubAd.getSku(), skuHubAd.getName(), skuHubAd.getEan(), skuHubAd.getQty().intValue());
					produtosAssociados.add(produto);
				}
				else
					for(SkyHubVariation skyHubVariation: skuHubAd.getVariations())
					{
						Produto produto = createProduct(anuncio, skyHubVariation.getSku(), skuHubAd.getName(), skyHubVariation.getEan(), skyHubVariation.getQty().intValue());
						produtosAssociados.add(produto);
					}

				anuncio = B2WParser.parseAd(skuHubAd);
				anuncio.setProdutos(produtosAssociados);
				anuncio.setId(id);
				anuncio.setProdutos(produtosAssociados);
				adRepository.save(anuncio);
				notSaved.add(anuncio.getIdMl());
			}
			else
				notSaved.add(anuncio.getIdMl());

		});

		System.out.println(notSaved);
		System.out.println("SAVED ADS "+savedAds);
	}


	public Produto createProduct(Anuncio anuncio,String sku, String name, String ean, Integer qty)
	{
		Produto produto = null;

		List<Produto> produtos = productRepository.findDistinctBySku(sku);

		if(produtos==null || produtos.isEmpty())
		{
			produto = new Produto();
			produto.setNome(name);
			produto.setCodBarras(ean!=null ? new Long(ean):null);
			produto.setSku(sku);
			produto.setQuantidadeDisponivel(qty);
		}
		else
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
}
