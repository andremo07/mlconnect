package br.com.mpconnect.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.file.utils.ZipUtils;
import br.com.mpconnect.manager.LogisticBusiness;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.util.DateUtils;
import br.com.mpconnect.utils.comparator.VendaComparator;
import br.com.trendsoftware.markethub.ml.business.impl.OrderBusinessImpl;
import br.com.trendsoftware.mlProvider.dto.ShippingStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingSubStatus;

@Component
@Scope(value="view")
public class EnvioController extends GenericCrudController<Venda> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	
	private static final String NFE_FILE_NAME_PREFIX = "Nfes";
	private static final String ENVIOS_FILE_NAME_PREFIX = "Envio";
	private static final String ETIQUETAS_FILE_NAME_PREFIX = "Etiquetas";
	private static final String PLANILHA_FILE_NAME_PREFIX = "Planilha envio";
	
	private String data;
	private String path;
	
	private List<Venda> vendas;

	@Autowired
	private VendaDao vendaDao;

	@Autowired
	private LogisticBusiness logisticBusiness;

	@Autowired
	@Qualifier("mlOrderBusiness")
	private OrderBusiness orderBusiness;

	private List<Venda> vendasSelecionadas;

	private StreamedContent exportFile;

	private Venda vendaSelecionada;

	public EnvioController(){
		vendas = new ArrayList<Venda>();
		vendasSelecionadas = new ArrayList<Venda>();
	}

	@PostConstruct
	public void init(){
		try{
			path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/tmp");
			data = DateUtils.getDataFormatada(new Date(), "dd-MM-YYYY");
			getOrderBusiness().loadOrdersByDate(DateUtils.adicionaDias(new Date(), -5), DateUtils.adicionaDias(new Date(), 1));
			vendas = getOrderBusiness().listOrdersByShippingStatus(ShippingStatus.READY_TO_SHIP, ShippingSubStatus.READY_TO_PRINT);
			Collections.sort(vendas, new VendaComparator());
		} catch (BusinessException e) {
			addMessage("Erro!", "Problema no carregamento das vendas recentes");
		}
	}

	@Override
	public List<Venda> paginacao(int first, int pageSize, Map<String,Object> filters){
		return vendas;
	}

	public void gerarPlanilha()
	{
		try {
			//MUDAR DEPOIS
			Map<String,Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
			Usuario usuario =  (Usuario) map.get("usuario");
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();
			
			List<String> fileNames = new ArrayList<String>();
			fileNames.add(String.format("%s %s.pdf",ETIQUETAS_FILE_NAME_PREFIX,data));
			fileNames.add(String.format("%s %s.xlsx",PLANILHA_FILE_NAME_PREFIX,data));
			List<InputStream> inputStreams = getLogisticBusiness().generateShippingSheetAndTags(vendasSelecionadas,accessToken);
			exportFile = buildZipFile(inputStreams,fileNames,path,String.format("%s %s.zip",ENVIOS_FILE_NAME_PREFIX,data));
		} catch (BusinessException e) {
			addMessage("Erro!", "Problema na geração de etiqueta");
		} catch (IOException e) {
			addMessage("Erro!", "Problema na geração de etiqueta");
		}
	}
	
	public void gerarNfe()
	{
		try {
			List<InputStream> inputStreams = Collections.singletonList(getOrderBusiness().generateNfeFileStream(vendasSelecionadas,String.format("%s/%s.pdf",path,NFE_FILE_NAME_PREFIX)));
			List<String> fileNames = Collections.singletonList(String.format("%s %s.pdf",NFE_FILE_NAME_PREFIX,data));
			exportFile = buildZipFile(inputStreams,fileNames,path,String.format("%s %s.zip",NFE_FILE_NAME_PREFIX,data));
		} catch (BusinessException e) {
			addMessage("Erro!", "Problema na geração de etiqueta.");
		} catch (IOException e) {
			addMessage("Erro!", "Problema na geração de etiqueta.");
		}
	}
	
	private DefaultStreamedContent buildZipFile(List<InputStream> inputStreams,List<String> fileNames,String path, String zipFileName) throws IOException
	{
		File zipFile = new File(String.format("%s/%s",path,zipFileName));
		if(!zipFile.exists())
			zipFile.getParentFile().mkdirs();
		zipFile.createNewFile();
		ZipUtils zipUtils = new ZipUtils(zipFile);
		
		int index=0;
		for(InputStream is: inputStreams){
			zipUtils.adicionarArquivo(fileNames.get(index), is);
			index++;
		}
		
		zipUtils.finalizarGravacao();
		InputStream zipInputStream = new BufferedInputStream(new FileInputStream(zipFile));
		return new DefaultStreamedContent(zipInputStream, "application/zip", zipFileName);
	}

	public void adicionarVenda(Venda venda){
		if(!vendasSelecionadas.contains(venda))
			vendasSelecionadas.add(venda);
		else
			vendasSelecionadas.remove(venda);
	}

	public List<Venda> getVendas() {
		return vendas;
	}

	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}

	public VendaDao getVendaDao() {
		return vendaDao;
	}

	public void setVendaDao(VendaDao vendaDao) {
		this.vendaDao = vendaDao;
	}

	public StreamedContent getExportFile() {
		return exportFile;
	}

	public void setExportFile(StreamedContent exportFile) {
		this.exportFile = exportFile;
	}

	public List<Venda> getVendasSelecionadas() {
		return vendasSelecionadas;
	}

	public void setVendasSelecionadas(List<Venda> vendasSelecionadas) {
		this.vendasSelecionadas = vendasSelecionadas;
	}

	public Venda getVendaSelecionada() {
		return vendaSelecionada;
	}

	public void setVendaSelecionada(Venda vendaSelecionada) {
		this.vendaSelecionada = vendaSelecionada;
	}

	public OrderBusinessImpl getOrderBusiness() {
		return (OrderBusinessImpl) orderBusiness;
	}

	public LogisticBusiness getLogisticBusiness() {
		return logisticBusiness;
	}
}
