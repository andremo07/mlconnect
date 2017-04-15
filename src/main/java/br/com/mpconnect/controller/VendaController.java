package br.com.mpconnect.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.ClienteDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.manager.VendaManagerBo;
import br.com.mpconnect.ml.api.enums.TipoPagamentoEnum;
import br.com.mpconnect.ml.api.enums.TipoPessoaEnum;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.Envio;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.Produto;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;

@Component
@Scope(value="view")
public class VendaController extends GenericCrudController<Venda> implements Serializable
{

	private static final long serialVersionUID = -244605228849576075L;
	private Venda venda;
	private DetalheVenda detalheVenda;
	private Pagamento pagamento;
	private Produto produto;
	private List<Anuncio> anuncios;
	private List<String> metodosEnvios;
	private List<String> modosEnvios;
	private List<String> tiposPagamento;
	private List<String> tiposPessoa;

	@Autowired
	private AnuncioDao anuncioDao;

	@Autowired
	private OrigemDao origemDao;

	@Autowired
	private VendedorDao vendedorDao;

	@Autowired
	private VendaDao vendaDao;

	@Autowired
	private ClienteDao clienteDao;

	@Autowired
	private VendaManagerBo vendasManager;

	public VendaController(){
		limparCampos();
		anuncios = new ArrayList<Anuncio>();
		metodosEnvios = new ArrayList<String>();
		modosEnvios = new ArrayList<String>();
		tiposPagamento = new ArrayList<String>();
		tiposPessoa = new ArrayList<String>();
	}

	@PostConstruct
	public void init(){

		try {

			anuncios = anuncioDao.recuperaTodos();

			metodosEnvios.add("Expresso");
			metodosEnvios.add("Normal");

			modosEnvios.add("me2");
			modosEnvios.add("pagseguro");
			modosEnvios.add("contrato");

			tiposPagamento.add(TipoPagamentoEnum.BOLETO.getValue());
			tiposPagamento.add(TipoPagamentoEnum.CARTAO.getValue());

			tiposPessoa.add(TipoPessoaEnum.FISICA.getValue());
			tiposPessoa.add(TipoPessoaEnum.JURIDICA.getValue());

			Vendedor vendedor = vendedorDao.recuperaUm(new Long(1));
			venda.setVendedor(vendedor);

		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String salvar(){
		try {
			Origem origem = origemDao.recuperaUm(new Long(2));
			venda.setOrigem(origem);
			venda.getEnvio().setTipo("shipping");
			Double tarifa = 0.05*pagamento.getValorTransacao();
			detalheVenda.setTarifaVenda(tarifa);
			pagamento.setTotalPago(pagamento.getValorTransacao());
			venda.getDetalhesVenda().add(detalheVenda);
			venda.getPagamentos().add(pagamento);
			venda.setStatus("paid");
			Cliente cliente = venda.getCliente();
			if(cliente.getTipo().equals(TipoPessoaEnum.FISICA.getValue()))
				cliente.setTipoContribuinteIcms(9);
			else
				cliente.setTipoContribuinteIcms(9);
			Long id = vendasManager.getMaxIdVenda();
			if (id!=null){
				id = id+1;
				venda.setId(id.toString());
				vendasManager.cadastrarVendaUnitaria(venda, produto); 
				limparCampos();
			}
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return null;

	}

	public void limparCampos(){
		venda = new Venda();
		detalheVenda = new DetalheVenda();
		pagamento = new Pagamento();
		produto = new Produto();
		Cliente cliente = new Cliente();
		Envio envio = new Envio();
		venda.setCliente(cliente);
		venda.setEnvio(envio);
		venda.setDetalhesVenda(new ArrayList<DetalheVenda>());
		venda.setPagamentos(new ArrayList<Pagamento>());
	}

	public String incluir(){
		addSessionAttribute("tipoOperacao", 1);
		return "cadastroVendas";
	}

	public String editar(){

		addSessionAttribute("funcionario", venda);
		addSessionAttribute("tipoOperacao", 2);
		return "cadastroVendas";
	}

	public void confirmaGravacao() {
		addMessage("Sucesso!", "Gravação realizada com êxito.");
	}

	public boolean habilitaCampoParcelas(){

		if(pagamento.getTipo() == null ||
				pagamento.getTipo().equals(TipoPagamentoEnum.BOLETO.getValue()))
			return true;
		else
			return false;
	}

	@Override
	public List<Venda> paginacao(int first, int pageSize, Map<String,Object> filters){	
		return null;
	}

	public DetalheVenda getDetalheVenda() {
		return detalheVenda;
	}

	public void setDetalheVenda(DetalheVenda detalheVenda) {
		this.detalheVenda = detalheVenda;
	}

	public Pagamento getPagamento() {
		return pagamento;
	}

	public void setPagamento(Pagamento pagamento) {
		this.pagamento = pagamento;
	}

	public List<Anuncio> getAnuncios() {
		return anuncios;
	}

	public void setAnuncios(List<Anuncio> anuncios) {
		this.anuncios = anuncios;
	}

	public List<String> getMetodosEnvios() {
		return metodosEnvios;
	}

	public void setMetodosEnvios(List<String> metodosEnvios) {
		this.metodosEnvios = metodosEnvios;
	}

	public List<String> getModosEnvios() {
		return modosEnvios;
	}

	public void setModosEnvios(List<String> modosEnvios) {
		this.modosEnvios = modosEnvios;
	}

	public Venda getVenda() {
		return venda;
	}

	public void setVenda(Venda funcionario) {
		this.venda = funcionario;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public List<String> getTiposPagamento() {
		return tiposPagamento;
	}

	public List<String> getTiposPessoa() {
		return tiposPessoa;
	}

	public void setTiposPessoa(List<String> tiposPessoa) {
		this.tiposPessoa = tiposPessoa;
	}

	public void setTiposPagamento(List<String> tiposPagamento) {
		this.tiposPagamento = tiposPagamento;
	}

}
