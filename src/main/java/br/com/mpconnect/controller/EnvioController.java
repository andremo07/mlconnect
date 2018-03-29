package br.com.mpconnect.controller;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.manager.LogisticBusiness;
import br.com.mpconnect.manager.OrderBusiness;
import br.com.mpconnect.ml.api.ApiPerguntas;
import br.com.mpconnect.ml.api.ApiVendas;
import br.com.mpconnect.ml.dto.MensagemVendaML;
import br.com.mpconnect.ml.dto.PerguntaML;
import br.com.mpconnect.model.Usuario;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.util.DateUtils;
import br.com.mpconnect.utils.comparator.VendaComparator;
import br.com.trendsoftware.mlProvider.dto.ShippingStatus;
import br.com.trendsoftware.mlProvider.dto.ShippingSubStatus;

@Component
@Scope(value="view")
public class EnvioController extends GenericCrudController<Venda> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private List<Venda> vendas;

	@Autowired
	private VendaDao vendaDao;

	@Autowired
	private ApiVendas apiVendas;

	@Autowired
	private ApiPerguntas apiPerguntas;

	@Autowired
	private LogisticBusiness logisticBusiness;

	@Autowired
	private OrderBusiness orderBusiness;

	private List<Venda> vendasSelecionadas;

	private StreamedContent exportFile;

	private Venda vendaSelecionada;

	private List<MensagemVendaML> msgsVenda;

	private List<PerguntaML> perguntasVenda;

	public EnvioController(){
		vendas = new ArrayList<Venda>();
		vendasSelecionadas = new ArrayList<Venda>();
	}

	@PostConstruct
	public void init(){
		try{
			orderBusiness.loadOrdersByDate(DateUtils.adicionaDias(new Date(), -5), DateUtils.adicionaDias(new Date(), 1));
			vendas = orderBusiness.listOrdersByShippingStatus(ShippingStatus.READY_TO_SHIP, ShippingSubStatus.PRINTED);
			Collections.sort(vendas, new VendaComparator());
		} catch (BusinessException e) {
			addMessage("Erro!", "Problema no carregamento das vendas recentes");
		}
	}

	public void recuperaMensagensVenda(){
		String id = vendaSelecionada.getId();
		msgsVenda = apiVendas.obterMensagensPosVenda(id);
		perguntasVenda = apiPerguntas.recuperarPerguntasVenda(vendaSelecionada.getDetalhesVenda().get(0).getAnuncio().getIdMl(), vendaSelecionada.getCliente().getIdMl());
	}

	@Override
	public List<Venda> paginacao(int first, int pageSize, Map<String,Object> filters){
		return vendas;
	}

	public void gerarPlanilha(){

		try {
			//MUDAR DEPOIS
			Map<String,Object> map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
			Usuario usuario =  (Usuario) map.get("usuario");
			String accessToken = usuario.getAcessoMercadoLivre().getAccessToken();
			
			exportFile = logisticBusiness.generateShippingSheetAndTags(vendasSelecionadas, accessToken);
		} catch (BusinessException e) {
			addMessage("Erro!", "Problema na geração de etiqueta.");
		}
	}

	public void createPdFile(String path,InputStream is){

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

	public List<MensagemVendaML> getMsgsVenda() {
		return msgsVenda;
	}

	public void setMsgsVenda(List<MensagemVendaML> msgsVenda) {
		this.msgsVenda = msgsVenda;
	}

	public List<PerguntaML> getPerguntasVenda() {
		return perguntasVenda;
	}

	public void setPerguntasVenda(List<PerguntaML> perguntasVenda) {
		this.perguntasVenda = perguntasVenda;
	}
}
