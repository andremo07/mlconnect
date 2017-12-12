package br.com.mpconnect.manager.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.manager.AnuncioManagerBo;
import br.com.mpconnect.ml.api.ApiProdutos;
import br.com.mpconnect.ml.api.ApiUsuario;
import br.com.mpconnect.ml.api.enums.StatusAnuncioEnum;
import br.com.mpconnect.ml.dto.AnuncioML;
import br.com.mpconnect.model.Anuncio;

@Service("anuncioManager")
public class AnuncioManagerBoImpl implements AnuncioManagerBo{

	@Autowired
	public ApiProdutos apiProdutos;

	@Autowired
	public ApiUsuario apiUsuarios;

	@Resource
	public AnuncioDao anuncioDao;

	@Transactional
	public void carregarAnunciosMl() throws DaoException {

		List<String> anuncios = apiProdutos.recuperaIdsAnunciosCadastrados(apiUsuarios.getIdUsuarioLogado(), StatusAnuncioEnum.ATIVO);
		List<AnuncioML> anunciosMl = retornaAnunciosNaoExistentes(anuncios);

		for(Iterator<AnuncioML> iterVendas = anunciosMl.iterator();iterVendas.hasNext();){
			AnuncioML anuncioML = iterVendas.next();
			Anuncio anuncio = parseAnuncioMlToAnuncio(anuncioML);
			anuncioDao.gravar(anuncio);
		}
	}

	public List<AnuncioML> retornaAnunciosNaoExistentes(List<String> idsAnuncios){

		List<String> idsAnunciosExistentes  = anuncioDao.recuperaIdsAnunciosExistentes(idsAnuncios);
		idsAnuncios.removeAll(idsAnunciosExistentes);

		List<AnuncioML> anunciosNaoExistentes = new ArrayList<AnuncioML>();
		for(String idAnuncio : idsAnuncios){
			AnuncioML anuncioMl = apiProdutos.getAnuncioPorId(idAnuncio);
			anunciosNaoExistentes.add(anuncioMl);	
		}
		
		return anunciosNaoExistentes;

	}

	public Anuncio parseAnuncioMlToAnuncio(AnuncioML anuncioMl){
		Anuncio anuncio = new Anuncio();
		anuncio.setDescricao(anuncioMl.getDescricao());
		anuncio.setCategoria(anuncioMl.getIdCategoria());
		anuncio.setIdMl(anuncioMl.getId());
		anuncio.setStatus(anuncioMl.getStatus());
		anuncio.setTitulo(anuncioMl.getTitulo());
		anuncio.setTipo(anuncioMl.getTipo().getNome());
		anuncio.setValor(anuncioMl.getValor());
		return anuncio;	
	}

}
