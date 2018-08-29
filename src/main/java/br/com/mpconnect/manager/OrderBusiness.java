package br.com.mpconnect.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.NfeConfigDao;
import br.com.mpconnect.dao.VendaDao;
import br.com.mpconnect.dao.VendedorDao;
import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.manager.impl.MarketHubBusiness;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.model.Vendedor;
import br.com.mpconnect.provider.NFeProvider;
import br.com.mpconnect.provider.exception.NfeProviderException;
import br.com.mpconnect.util.ExceptionUtil;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.restProvider.exception.ProviderException;

public abstract class OrderBusiness extends MarketHubBusiness {
	
	@Resource
	public AnuncioDao anuncioDao;

	@Resource
	public NfeConfigDao nfeConfidDao;

	@Resource
	public VendedorDao vendedorDao;

	@Resource
	public AcessoMlDao acessoDao;

	@Resource
	public VendaDao vendaDao;

	@Autowired
	private ShippingProvider shippingProvider;

	@Autowired
	private NFeProvider nfeProvider;
		
	@PostConstruct
	public void init(){
		getShippingProvider().setLogger(logger);
		getNfeProvider().setLogger(logger);
	}
	
	public InputStream generateNfeFileStream(List<Venda> vendas, String pathName) throws BusinessException
	{
		getLogger().debug(String.format("iniciando geração de nfe"));
		
		try {
			List<InputStream> nfesInputStreams = generateOrderNfes(vendas);
			InputStream pdfInputStream = new ByteArrayInputStream(PdfUtils.merge(nfesInputStreams).toByteArray());
			
			getLogger().debug(String.format("geração de nfe finalizada")); 
			
			return pdfInputStream;
		} catch (IOException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			String exception = String.format("FILE_MANIPULATION_ERROR");
			throw new BusinessException(exception);
		}
	}
	
	@Transactional
	public List<InputStream> generateOrderNfes(List<Venda> orders) throws BusinessException{

		try {

			Vendedor vendedor = vendedorDao.recuperarVendedorPorIdMl(orders.get(0).getVendedor().getIdMl());

			for(Venda order: orders){
				Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
				order.getEnvio().setCodMunicipio(code);
				order.setVendedor(vendedor);
				Anuncio anuncio = anuncioDao.recuperarAnuncioPorIdMl(order.getDetalhesVenda().get(0).getAnuncio().getIdMl());
				order.getDetalhesVenda().get(0).setAnuncio(anuncio);
			};

			NfeConfig userNfeConfig = nfeConfidDao.recuperaUm(1L);

			List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(orders,userNfeConfig);

			int index=0;
			for(NFNotaProcessada notaProcessada: notasProcessadas){
				orders.get(index).setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
				index++;
			}

			List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);

			userNfeConfig.setNrNota(new Integer(Integer.valueOf(userNfeConfig.getNrNota())+notasProcessadas.size()).toString());
			userNfeConfig.setNrLote(new Integer(Integer.valueOf(userNfeConfig.getNrLote())+1).toString());
			nfeConfidDao.alterar(userNfeConfig);

			return inputStreams;
		} catch (DaoException e) {
			String exception = String.format("Error generating Nfes - Database error");
			throw new BusinessException(exception);
		} catch (NfeProviderException e) {
			String exception = String.format("Error generating Nfes - Provider error");
			throw new BusinessProviderException(exception);
		} catch (ProviderException e) {
			String exception = String.format("Error generating Nfes - Querying municipy error");
			throw new BusinessProviderException(exception);
		}
	}
	
	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	public NFeProvider getNfeProvider() {
		return nfeProvider;
	}
	
	public abstract void save(String userId, String orderId) throws BusinessException;
	public abstract void salvarVenda(Venda venda);
	public abstract void loadOrdersByDate(Date fromDate, Date toDate) throws BusinessException;
	public abstract void atualizarVenda(Venda venda);
	
}
