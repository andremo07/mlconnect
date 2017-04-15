package br.com.mpconnect.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.codehaus.jettison.json.JSONException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.manager.AnuncioManagerBo;
import br.com.mpconnect.ml.api.ApiCategorias;
import br.com.mpconnect.ml.api.ApiProdutos;
import br.com.mpconnect.ml.api.enums.ModoEnvioFreteGratisMlEnum;
import br.com.mpconnect.ml.api.enums.ModoEnvioMlEnum;
import br.com.mpconnect.ml.api.enums.TipoAnuncioEnum;
import br.com.mpconnect.ml.data.AnuncioML;
import br.com.mpconnect.ml.data.CategoriaML;
import br.com.mpconnect.ml.data.MetodoEnvioML;
import br.com.mpconnect.ml.data.PictureML;
import br.com.mpconnect.ml.data.TipoAnuncioML;
import br.com.mpconnect.ml.data.ValorVariacaoML;
import br.com.mpconnect.ml.data.VariacaoML;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Produto;

import com.mercadolibre.sdk.MeliException;

@Component
@Scope(value="view")
public class AnuncioController extends GenericCrudController<Anuncio> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private Anuncio anuncio;
	private AnuncioML anuncioMl;
	private Produto produtoSelecionado;
	private List<Produto> produtos;
	private List<TipoAnuncioML> tiposAnuncio;
	private List<UploadedFile> files;
	private String tipoFrete;
	private boolean retirarEmMaos;
	private boolean possuiVariacao=false;
	private VariacaoML variacao;
	private ValorVariacaoML combinacao;
	private String nomeVariacao;
	private String pathCategorias;

	private StreamedContent imagem;

	private int imageIndex;
	private int variacaoIndex;

	private List<UploadedFile> listFiles;

	private List<List<UploadedFile>> listsFilesVariacao;
	
	private List<List<CategoriaML>> hierarquiaCategorias;

	@Autowired
	private ProdutoDao produtoDao;

	@Autowired
	private AnuncioDao anuncioDao;

	@Autowired
	private AnuncioManagerBo anuncioManager;

	@Autowired
	private ApiProdutos apiProduto;
	
	@Autowired
	private ApiCategorias apiCategorias;

	public AnuncioController(){
		produtos = new ArrayList<Produto>();		
		listFiles = new ArrayList<UploadedFile>();
	}

	@PostConstruct
	public void init(){
		try{

			anuncio = new Anuncio();

			if(tipoOperacao==0){
				anuncioManager.carregarAnunciosMl();
				this.getModel().setRowCount(anuncioDao.recuperaTotalRegistros().intValue());
				this.getModel().setDatasource(anuncioDao.recuperaTodosPorIntervalo(0, this.getModel().getPageSize(), new HashMap<String, Object>()));
			}
			else if(tipoOperacao==1){
				anuncioMl = new AnuncioML();
				TipoAnuncioML tipo = new TipoAnuncioML();
				tiposAnuncio = new ArrayList<TipoAnuncioML>();
				tiposAnuncio.add(TipoAnuncioEnum.ML_PREMIUM.getValue());
				tiposAnuncio.add(TipoAnuncioEnum.ML_CLASSICO.getValue());
				anuncioMl.setTipo(tipo);
				produtos = produtoDao.recuperaTodos();
				Set<Produto> produtos = new HashSet<Produto>();
				List<PictureML> imagens = new ArrayList<PictureML>();
				anuncioMl.setPictures(imagens);
				anuncio.setProdutos(produtos);
			}
			else{
				produtos = produtoDao.recuperaTodos();
				Long idAnuncio = (Long) getSessionAttribute("idAnuncio");
				anuncio = anuncioDao.recuperaUm(idAnuncio);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public String salvar(){
		System.out.println();

		MetodoEnvioML metodo = criarMetodoEnvio();
		anuncioMl.setMetodoEnvio(metodo);
		if(anuncioMl.getVariacoes()!=null && !anuncioMl.getVariacoes().isEmpty())
			completaCamposVariacoes(anuncioMl.getVariacoes());
		apiProduto.salvarAnuncio(anuncioMl);
		//			if(tipoOperacao==1)
		//				anuncioDao.gravar(anuncio);
		//			else
		//				anuncioDao.alterar(anuncio);
		//
		//			addSessionAttribute("tipoOperacao", 0);
		//			return "listaAnuncios";
		return null;
	}

	private MetodoEnvioML criarMetodoEnvio(){
		MetodoEnvioML metodo = new MetodoEnvioML();
		metodo.setModoEnvio(ModoEnvioMlEnum.MERCADO_ENVIOS_2.getValue());
		metodo.setMetodoGratisId("100009");
		if(tipoFrete.equals("tf1")){
			metodo.setFreteGratis(false);
		}
		if(tipoFrete.equals("tf2")){
			metodo.setFreteGratis(true);
			metodo.setMetodoGratisModo(ModoEnvioFreteGratisMlEnum.TODO_BRASIL.getValue());
		}
		else if(tipoFrete.equals("tf3")){
			metodo.setFreteGratis(true);
			metodo.setMetodoGratisModo(ModoEnvioFreteGratisMlEnum.EXCLUINDO_REGIOES.getValue());
			List<String> valoresExclusao = new ArrayList<String>();
			valoresExclusao.add("BR-NO");
			valoresExclusao.add("BR-NE");
			metodo.setMetodoGratisValores(valoresExclusao);
		}
		metodo.setRetiradaLocal(retirarEmMaos);
		return metodo;
	}

	private void completaCamposVariacoes(List<VariacaoML> variacoes){
		List<PictureML> picsAnuncios = anuncioMl.getPictures();
		for(VariacaoML variacao : variacoes){
			variacao.setPreco(anuncioMl.getValor());
			ValorVariacaoML valoVariacaoML = variacao.getValores().get(0);
			valoVariacaoML.setNomeVariacao(nomeVariacao);

			//MUDAR DEPOIS SÓ PARA TESTES
//			List<PictureML> pics = new ArrayList<PictureML>();
//			PictureML pic = new PictureML();
//			//pic.setId("934811-MLB20641269247_032016");
//			pic.setSource("http://imageshack.com/a/img540/2189/5tvYqo.jpg");
//			pics.add(pic);
//			picsAnuncios.add(pic);
//			variacao.setPictures(pics);
		}
	}

	public void habilitarVariacoes(){
		anuncioMl.setVariacoes(null);
		listsFilesVariacao=null;
		if(possuiVariacao){
			VariacaoML variacao = new VariacaoML();
			ValorVariacaoML combinacao = new ValorVariacaoML();
			List<ValorVariacaoML> combinacoes = new ArrayList<ValorVariacaoML>();
			List<PictureML> pictures = new ArrayList<PictureML>();
			combinacoes.add(combinacao);
			variacao.setValores(combinacoes);
			variacao.setPictures(pictures);
			List<VariacaoML> variacoes = new ArrayList<VariacaoML>();
			variacoes.add(variacao);
			listsFilesVariacao = new ArrayList<List<UploadedFile>>();
			List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
			listsFilesVariacao.add(uploadedFiles);
			anuncioMl.setVariacoes(variacoes);
		}
	}

	public void adicionaVariaveis(){
		VariacaoML variacao = new VariacaoML();
		ValorVariacaoML combinacao = new ValorVariacaoML();
		List<ValorVariacaoML> combinacoes = new ArrayList<ValorVariacaoML>();
		combinacoes.add(combinacao);
		variacao.setValores(combinacoes);
		anuncioMl.getVariacoes().add(variacao);
		List<PictureML> pictures = new ArrayList<PictureML>();
		variacao.setPictures(pictures);
		List uploadedFiles = new ArrayList<UploadedFile>();
		listsFilesVariacao.add(uploadedFiles);
		imageIndex=0;
		variacaoIndex=0;
	}

	public void removerVariavel(VariacaoML variacao){
		int index = anuncioMl.getVariacoes().indexOf(variacao);
		anuncioMl.getVariacoes().remove(variacao);
		listsFilesVariacao.remove(index);
		imageIndex=0;
		variacaoIndex=0;
	}
	
	public void carregarCategorias(){
		
		try {
			hierarquiaCategorias = new ArrayList<List<CategoriaML>>();
			List<CategoriaML> categoriasPrincipais = apiCategorias.retornaCategoriasPrincipais();
			hierarquiaCategorias.add(categoriasPrincipais);
			pathCategorias="";
		} catch (MeliException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void carregarSubCategorias(SelectEvent event){
		
		try {
			CategoriaML categoria = (CategoriaML) event.getObject();
			int sizeArvoreCategorias = hierarquiaCategorias.size();
			List<CategoriaML> subCategoria = apiCategorias.retornaSubCategorias(categoria.getId());
			if(subCategoria.isEmpty())
				anuncioMl.setIdCategoria(categoria.getId());
			else{
				if(categoria.getNivel()!=hierarquiaCategorias.size()){
					hierarquiaCategorias.removeAll(hierarquiaCategorias.subList(categoria.getNivel(),sizeArvoreCategorias));
					String[] arrayPathCategorias = pathCategorias.split(" ->");
					pathCategorias="";
					for(int index=0;index<hierarquiaCategorias.size()-1;index++){
						if(pathCategorias.equals(""))
							pathCategorias=arrayPathCategorias[index];
						else
							pathCategorias = pathCategorias + " -> " + arrayPathCategorias[index]; 
					}
				}
			}
			sizeArvoreCategorias = hierarquiaCategorias.size();
			if(!hierarquiaCategorias.get(sizeArvoreCategorias-1).isEmpty()){
				hierarquiaCategorias.add(subCategoria);
				if(pathCategorias.equals(""))
					pathCategorias=categoria.getName();
				else
					pathCategorias = pathCategorias + " -> " +categoria.getName() ;
			}
		} catch (MeliException | JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String adicionaProduto(){
		anuncio.getProdutos().add(produtoSelecionado);
		return null;
	}

	public String removerProduto(){
		anuncio.getProdutos().remove(produtoSelecionado);
		return null;
	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "cadastroAnuncios?faces-redirect=true";
	}

	public String editar(){

		addSessionAttribute("idAnuncio", anuncio.getId());
		addSessionAttribute("tipoOperacao", 2);
		return "cadastroAnuncios?faces-redirect=true";
	}

	public void remover(){
		try{
			anuncioDao.deletar(anuncio);
			addMessage("Sucesso!", "Anuncio removido com êxito.");
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public Anuncio getAnuncio() {
		return anuncio;
	}

	public void setAnuncio(Anuncio Anuncio) {
		this.anuncio = Anuncio;
	}

	public Produto getProdutoSelecionado() {
		return produtoSelecionado;
	}

	public void setProdutoSelecionado(Produto produtoSelecionado) {
		this.produtoSelecionado = produtoSelecionado;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public ProdutoDao getProdutoDao() {
		return produtoDao;
	}

	public void setAnuncioDao(ProdutoDao produtoDao) {
		this.produtoDao = produtoDao;
	}

	public List<UploadedFile> getFiles() {
		return files;
	}

	public void setFiles(List<UploadedFile> files) {
		this.files = files;
	}

	public void removerImagem(Integer index){
		listFiles.remove(index.intValue());
		anuncioMl.getPictures().remove(index.intValue());
		imageIndex=0;
	}

	public void removerImagemVariacao(Integer variacaoIndex,Integer index){
		List<UploadedFile> listFiles = listsFilesVariacao.get(variacaoIndex);
		listFiles.remove(index.intValue());
		VariacaoML variacao = anuncioMl.getVariacoes().get(variacaoIndex);
		variacao.getPictures().remove(index.intValue());
		this.variacaoIndex=variacaoIndex;
		imageIndex=0;
	}

	public void handleFileUpload(FileUploadEvent event) {

		try{
			UploadedFile file = event.getFile();
			listFiles.add(file);
			imageIndex=0;
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/tmp");
			File targetFile = new File(path+"/"+file.getFileName());
			OutputStream outStream = new FileOutputStream(targetFile);
			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			InputStream is = file.getInputstream();
			while ((bytesRead = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.close();
			targetFile.createNewFile();
			List<PictureML> imagens = anuncioMl.getPictures();
			String serverPath = "http://localhost:8080/ml-connect/tmp/";
			PictureML pic = new PictureML();
			pic.setSource(serverPath+file.getFileName());
			imagens.add(pic);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void handleFileUploadVariacoes(FileUploadEvent event) {

		try{
			Integer index = (Integer) event.getComponent().getAttributes().get("index");
			VariacaoML variacao = anuncioMl.getVariacoes().get(index);
			List<UploadedFile> uploadeFiles = listsFilesVariacao.get(index);
			UploadedFile file = event.getFile();
			uploadeFiles.add(file);
			imageIndex=0;
			variacaoIndex=index;
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/tmp");
			File targetFile = new File(path+"/"+file.getFileName());
			OutputStream outStream = new FileOutputStream(targetFile);
			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			InputStream is = file.getInputstream();
			while ((bytesRead = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			outStream.close();
			targetFile.createNewFile();
			List<PictureML> imagens = variacao.getPictures();
			String serverPath = "http://localhost:8080/ml-connect/tmp/";
			PictureML pic = new PictureML();
			pic.setSource(serverPath+file.getFileName());
			imagens.add(pic);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public AnuncioML getAnuncioMl() {
		return anuncioMl;
	}

	public void setAnuncioMl(AnuncioML anuncioMl) {
		this.anuncioMl = anuncioMl;
	}

	public List<TipoAnuncioML> getTiposAnuncio() {
		return tiposAnuncio;
	}

	public void setTiposAnuncio(List<TipoAnuncioML> tiposAnuncio) {
		this.tiposAnuncio = tiposAnuncio;
	}

	public String getTipoFrete() {
		return tipoFrete;
	}

	public void setTipoFrete(String tipoFrete) {
		this.tipoFrete = tipoFrete;
	}

	public boolean isRetirarEmMaos() {
		return retirarEmMaos;
	}

	public void setRetirarEmMaos(boolean retirarEmMaos) {
		this.retirarEmMaos = retirarEmMaos;
	}

	public boolean isPossuiVariacao() {
		return possuiVariacao;
	}

	public void setPossuiVariacao(boolean possuiVariacao) {
		this.possuiVariacao = possuiVariacao;
	}

	public VariacaoML getVariacao() {
		return variacao;
	}

	public void setVariacao(VariacaoML variacao) {
		this.variacao = variacao;
	}

	public ValorVariacaoML getCombinacao() {
		return combinacao;
	}

	public void setCombinacao(ValorVariacaoML combinacao) {
		this.combinacao = combinacao;
	}

	public String getNomeVariacao() {
		return nomeVariacao;
	}

	public void setNomeVariacao(String nomeVariacao) {
		this.nomeVariacao = nomeVariacao;
	}

	public StreamedContent getImagem() {
		try {
			if(imageIndex<listFiles.size()){
				UploadedFile file = listFiles.get(imageIndex);
				if(file!=null){

					imageIndex++;
					return new DefaultStreamedContent(file.getInputstream(), "image/jpeg");
				}
				else
					return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return null;

	}

	public StreamedContent getImagemVariacao() {
		try {
			if(imageIndex>=listsFilesVariacao.get(variacaoIndex).size()){
				variacaoIndex++;
				imageIndex=0;
				if(listsFilesVariacao.size()<=variacaoIndex||listsFilesVariacao.get(variacaoIndex)==null
						||listsFilesVariacao.get(variacaoIndex).isEmpty()){
					variacaoIndex=0;
				}
			}
			UploadedFile file = listsFilesVariacao.get(variacaoIndex).get(imageIndex);
			imageIndex++;
			return new DefaultStreamedContent(file.getInputstream(), "image/jpeg");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setImagem(StreamedContent imagem) {
		this.imagem = imagem;
	}

	public List<UploadedFile> getListFiles() {
		return listFiles;
	}

	public void setListFiles(List<UploadedFile> listFiles) {
		this.listFiles = listFiles;
	}
	
	public List<List<CategoriaML>> getHierarquiaCategorias() {
		return hierarquiaCategorias;
	}

	public void setHierarquiaCategorias(List<List<CategoriaML>> hierarquiaCategorias) {
		this.hierarquiaCategorias = hierarquiaCategorias;
	}
	
	public String getPathCategorias() {
		return pathCategorias;
	}

	public void setPathCategorias(String pathCategorias) {
		this.pathCategorias = pathCategorias;
	}

	@Override
	public List<Anuncio> paginacao(int first, int pageSize, Map<String,Object> filters){
		try{
			this.getModel().setRowCount(anuncioDao.recuperaTotalRegistros(filters).intValue());
			return anuncioDao.recuperaTodosPorIntervalo(first, pageSize, filters);
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

}
