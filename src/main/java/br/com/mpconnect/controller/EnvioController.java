package br.com.mpconnect.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.file.utils.ExcelUtils;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.file.utils.ZipUtils;
import br.com.mpconnect.manager.FluxoDeCaixaManagerBo;
import br.com.mpconnect.manager.VendaManagerBo;
import br.com.mpconnect.ml.api.ApiEnvios;
import br.com.mpconnect.ml.api.ApiPerguntas;
import br.com.mpconnect.ml.api.ApiUsuario;
import br.com.mpconnect.ml.api.ApiVendas;
import br.com.mpconnect.ml.api.enums.StatusEnvioMlEnum;
import br.com.mpconnect.ml.api.enums.StatusEtiquetaMlEnum;
import br.com.mpconnect.ml.data.MensagemVendaML;
import br.com.mpconnect.ml.data.PerguntaML;
import br.com.mpconnect.ml.data.VendaML;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.utils.DateUtils;
import br.com.mpconnect.utils.comparator.VendaComparator;

@Component
@Scope(value="view")
public class EnvioController extends GenericCrudController<Venda> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private List<Venda> vendas;

	@Autowired
	private VendaDao vendaDao;

	@Autowired
	private ApiEnvios apiEnvios;

	@Autowired
	private ApiVendas apiVendas;

	@Autowired
	private ApiUsuario apiUsuario;

	@Autowired
	private ApiPerguntas apiPerguntas;

	@Autowired
	private VendaManagerBo vendasManager;
	
	@Autowired
	private FluxoDeCaixaManagerBo fluxoDeCaixaManager;

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
			vendasManager.carregaVendasRecentes();
			Set<VendaML> vendasMl = apiVendas.recuperaVendasPorStatusEnvio(apiUsuario.getIdUsuarioLogado(), StatusEnvioMlEnum.PRONTO_PARA_ENVIO, StatusEtiquetaMlEnum.PRONTO_PARA_IMPRIMIR);
			for(Iterator<VendaML> it = vendasMl.iterator();it.hasNext();){
				VendaML vendaMl = it.next();
				Venda venda = vendaDao.recuperaUm(vendaMl.getId());
				if(venda!=null)
					vendas.add(venda);
			}

			Collections.sort(vendas,new VendaComparator());

		} catch (DaoException e) {
			e.printStackTrace();
		}
	}

	public void recuperaMensagensVenda(){
		String id = vendaSelecionada.getId();
		msgsVenda = apiVendas.obterMensagensPosVenda(id);
		perguntasVenda = apiPerguntas.recuperarPerguntasVenda(vendaSelecionada.getDetalhesVenda().get(0).getAnuncio().getIdMl(), vendaSelecionada.getCliente().getIdMl());
	}

	@Override
	public List<Venda> paginacao(int first, int pageSize, Map<String,Object> filters){
		try{
			this.getModel().setRowCount(vendaDao.recuperaTotalRegistros(filters).intValue());
			return vendaDao.recuperaTodosPorIntervalo(first, pageSize, filters);
		} catch (DaoException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void gerarPlanilha(){

		try {
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/tmp");
			String data = DateUtils.getDataFormatada(new Date(), "dd-MM-YYYY");

			File zipFile = new File(path+"\\envio.zip");
			zipFile.createNewFile();
			ZipUtils zipUtils = new ZipUtils(zipFile);

			Collections.sort(vendasSelecionadas, new VendaComparator());
			//GERAÇÂO DO ARQUIVO PDF
			List<String> idsEnvios = getIdsEnvios();
			InputStream pdfInputStream = apiEnvios.gerarEtiquetas2(idsEnvios);
			//

			//LEITURA PDF
			if(pdfInputStream!=null){
				PdfUtils pdfFile = new PdfUtils(pdfInputStream);
				List<String> codigosNf = pdfFile.localizarString("(NF: )(\\d+)");
				File filePdf = new File(path+"\\etiquetas.pdf");
				filePdf.createNewFile();
				pdfFile.save(filePdf);
				pdfInputStream = new FileInputStream(filePdf);
				pdfFile.close();
				zipUtils.adicionarArquivo("Etiquetas "+data+".pdf", pdfInputStream);
				//

				//GERAÇAO PLANILHA EXCEL
				if(idsEnvios.size()==codigosNf.size()){
					XSSFWorkbook workbook = new XSSFWorkbook();
					criarPlanilhaExcelEnvio(workbook,idsEnvios,codigosNf);
					File fileExcel = new File(path+"\\planilhaTemp.xlsx");
					fileExcel.createNewFile();
					FileOutputStream fos = new FileOutputStream(fileExcel);
					workbook.write(fos);
					workbook.close();
					fos.flush();
					fos.close();
					InputStream excelInputStream = new BufferedInputStream(new FileInputStream(fileExcel));
					zipUtils.adicionarArquivo("Planilha envio "+data+".xlsx", excelInputStream);
				}
				//

				//COMPACTAR OS DOIS ARQUIVOS EM UM ZIP
				zipUtils.finalizarGravacao();

				//PREPARA DOWNLOAD
				InputStream zipInputStream = new BufferedInputStream(new FileInputStream(zipFile));
				exportFile = new DefaultStreamedContent(zipInputStream, "application/zip", "Envio "+data+".zip");
			}
			else{
				addMessage("Erro!", "Problema na geração de etiqueta.");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void criarPlanilhaExcelEnvio(XSSFWorkbook workbook,List<String> idsEnvios,List<String> codigosNf){

		ExcelUtils excelUtils = new ExcelUtils(workbook);
		XSSFSheet sheet = excelUtils.criarFolha("FirstSheet");  

		XSSFRow cabecalhoRow = sheet.createRow((short)0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "ID", 0);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "NF", 1);
		excelUtils.criarCelulaCabecalho(cabecalhoRow, "CODIGO DO PRODUTO", 2);

		int index =0;
		for(Iterator<Venda> i = vendasSelecionadas.iterator(); i.hasNext();){
			Venda venda = i.next();
			DetalheVenda dv = venda.getDetalhesVenda().get(0);
			Produto produto = dv.getProduto();
			if(produto==null||produto.getSku()==null){
				produto=new Produto();
				produto.setSku("");
			}
			System.out.println(produto.getSku());
			XSSFRow row = sheet.createRow((short)index+1);
			excelUtils.criarCelula(row, idsEnvios.get(index), 0, true);
			excelUtils.criarCelula(row, codigosNf.get(index), 1, true);
			excelUtils.criarCelula(row, produto.getSku(), 2, true);
			index++;
		}

	}

	public void adicionarVenda(Venda venda){
		if(!vendasSelecionadas.contains(venda))
			vendasSelecionadas.add(venda);
		else
			vendasSelecionadas.remove(venda);
	}

	private List<String> getIdsEnvios(){
		List<String> idsEnvios = new ArrayList<String>();
		try {
			for(Venda venda: vendasSelecionadas){
				venda = vendaDao.recuperaUm(venda.getId());
				idsEnvios.add(venda.getEnvio().getIdMl());
			}
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idsEnvios;
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
