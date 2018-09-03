package br.com.mpconnect.manager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;

import br.com.mpconnect.exception.BusinessException;
import br.com.mpconnect.exception.BusinessProviderException;
import br.com.mpconnect.file.utils.PdfUtils;
import br.com.mpconnect.model.Anuncio;
import br.com.mpconnect.model.Cliente;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.NFeProvider;
import br.com.mpconnect.provider.exception.NfeProviderException;
import br.com.mpconnect.util.ExceptionUtil;
import br.com.trendsoftware.markethub.repository.AdRepository;
import br.com.trendsoftware.markethub.repository.ChannelRepository;
import br.com.trendsoftware.markethub.repository.ClientRepository;
import br.com.trendsoftware.markethub.repository.NfeRepository;
import br.com.trendsoftware.markethub.repository.OrderRepository;
import br.com.trendsoftware.markethub.repository.SellerRepository;
import br.com.trendsoftware.mlProvider.dataprovider.ShippingProvider;
import br.com.trendsoftware.mlProvider.dto.Order;
import br.com.trendsoftware.restProvider.exception.ProviderException;

public abstract class OrderBusiness extends MarketHubBusiness {

	@Resource
	private AdRepository adRepository;

	@Resource
	protected OrderRepository orderRepository;

	@Resource
	protected ClientRepository clientRepository;

	@Resource
	protected SellerRepository sellerRepository;

	@Resource
	protected ChannelRepository channelRepository;

	@Resource
	protected NfeRepository nfeRepository;

	@Autowired
	protected ShippingProvider shippingProvider;

	@Autowired
	private NFeProvider nfeProvider;

	public abstract void save(Venda venda) throws BusinessException;
	public abstract Venda searchPartnerOrder(String userId, String orderId) throws BusinessException;

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

			for(Venda order: orders){
				Integer code = shippingProvider.searchMunicipyCodeByCep(order.getEnvio().getCep());
				order.getEnvio().setCodMunicipio(code);
				Anuncio anuncio =  adRepository.findByIdMl(order.getDetalhesVenda().get(0).getAnuncio().getIdMl());
				order.getDetalhesVenda().get(0).setAnuncio(anuncio);
			};

			Optional<NfeConfig> result = nfeRepository.findById(1L);

			if(result.isPresent()){
				NfeConfig userNfeConfig = result.get();
				List<NFNotaProcessada> notasProcessadas = nfeProvider.generateNFes(orders,userNfeConfig);

				int index=0;
				for(NFNotaProcessada notaProcessada: notasProcessadas){
					orders.get(index).setNrNfe(Long.valueOf(notaProcessada.getNota().getInfo().getIdentificacao().getNumeroNota()));
					index++;
				}

				List<InputStream> inputStreams = nfeProvider.generateNFePdf(notasProcessadas);

				userNfeConfig.setNrNota(new Integer(Integer.valueOf(userNfeConfig.getNrNota())+notasProcessadas.size()).toString());
				userNfeConfig.setNrLote(new Integer(Integer.valueOf(userNfeConfig.getNrLote())+1).toString());
				nfeRepository.save(userNfeConfig);

				return inputStreams;
			}
			else
				throw new BusinessException("Missing Nfe Configuration");
		} catch (NfeProviderException e) {
			String exception = String.format("Error generating Nfes - Provider error");
			throw new BusinessProviderException(exception);
		} catch (ProviderException e) {
			String exception = String.format("Error generating Nfes - Querying municipy error");
			throw new BusinessProviderException(exception);
		}
	}

	@Transactional
	public Venda saveOrder(Venda order)
	{
		if(!orderRepository.existsById(order.getId()))
		{
			order.getDetalhesVenda().forEach(orderDetail ->{
				Anuncio anuncio = adRepository.findByIdMl(orderDetail.getAnuncio().getIdMl());
				if(anuncio!=null){
					orderDetail.getAnuncio().setId(anuncio.getId());
					orderDetail.getAnuncio().setProdutos(anuncio.getProdutos());
				}
				adRepository.save(orderDetail.getAnuncio());
			});

			Cliente cliente = clientRepository.findByNrDocumento(order.getCliente().getNrDocumento());
			if(cliente==null)
				cliente = clientRepository.save(order.getCliente());			
			order.setCliente(cliente);

			Optional<Origem> result  = channelRepository.findById(getChannel().getId());
			if(result.isPresent())
				order.setOrigem(result.get());

			return orderRepository.save(order);
		}

		return null;
	}

	public List<Order> retornaVendasNaoCadastradas(List<Order> vendas){

		List<String> idsVendas = new ArrayList<String>();
		for(Order vendaMl : vendas)
			idsVendas.add(vendaMl.getId().toString());
		
		List<String> idsVendasCadastradas = 
				orderRepository.findAllById(idsVendas).stream()
			              .map(Venda::getId)
			              .collect(Collectors.toList());
		
		idsVendas.removeAll(idsVendasCadastradas);

		List<Order> vendasNaoExistentes = new ArrayList<Order>();
		for(Order venda : vendas){
			String id = venda.getId().toString();
			if(idsVendas.contains(id))
				vendasNaoExistentes.add(venda);
		}

		return vendasNaoExistentes;
	}

	public ShippingProvider getShippingProvider() {
		return shippingProvider;
	}

	public NFeProvider getNfeProvider() {
		return nfeProvider;
	}	
}
