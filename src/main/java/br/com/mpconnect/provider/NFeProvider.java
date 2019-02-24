package br.com.mpconnect.provider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFModelo;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.assinatura.AssinaturaDigital;
import com.fincatto.documentofiscal.nfe.NFTipoEmissao;
import com.fincatto.documentofiscal.nfe400.classes.NFEndereco;
import com.fincatto.documentofiscal.nfe400.classes.NFFinalidade;
import com.fincatto.documentofiscal.nfe400.classes.NFIndicadorFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.NFModalidadeFrete;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.documentofiscal.nfe400.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.documentofiscal.nfe400.classes.NFOrigem;
import com.fincatto.documentofiscal.nfe400.classes.NFProcessoEmissor;
import com.fincatto.documentofiscal.nfe400.classes.NFProdutoCompoeValorNota;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocolo;
import com.fincatto.documentofiscal.nfe400.classes.NFProtocoloInfo;
import com.fincatto.documentofiscal.nfe400.classes.NFRegimeTributario;
import com.fincatto.documentofiscal.nfe400.classes.NFTipo;
import com.fincatto.documentofiscal.nfe400.classes.NFTipoImpressao;
import com.fincatto.documentofiscal.nfe400.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvio;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.documentofiscal.nfe400.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFFormaPagamentoMoeda;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNota;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfo;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoEmitente;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoFormaPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoInformacoesAdicionais;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItem;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoCOFINSOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoICMSSN102;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPIS;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemImpostoPISOutrasOperacoes;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoPagamento;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTotal;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTransportador;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoTransporte;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaInfoVolume;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFNotaProcessada;
import com.fincatto.documentofiscal.nfe400.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.documentofiscal.nfe400.classes.statusservico.consulta.NFStatusServicoConsultaRetorno;
import com.fincatto.documentofiscal.nfe400.danfe.NFDanfeReport;
import com.fincatto.documentofiscal.nfe400.parsers.DFParser;
import com.fincatto.documentofiscal.nfe400.utils.NFGeraChave;
import com.fincatto.documentofiscal.nfe400.webservices.WSFacade;

import br.com.mpconnect.holder.B2wConfigurationHolder;
import br.com.mpconnect.holder.NfeConfigurationHolder;
import br.com.mpconnect.model.DetalheVenda;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Pagamento;
import br.com.mpconnect.model.TipoPessoaEnum;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.exception.NfeProviderException;
import br.com.trendsoftware.b2wprovider.dataprovider.B2wOrderProvider;
import br.com.trendsoftware.b2wprovider.dto.SkyHubUserCredencials;
import br.com.trendsoftware.markethub.repository.OrderRepository;
import br.com.trendsoftware.restProvider.exception.ProviderException;
import br.com.trendsoftware.restProvider.util.ExceptionUtil;

@Service(value="nfeProvider")
@DependsOn("nfeConfHolder")
public class NFeProvider {

	private NfeConfigurationHolder config;

	private Logger logger;

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	@PostConstruct
	public void setUp(){		
		config = NfeConfigurationHolder.getInstance();		
	}	

	public void testaServico() throws KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, Exception {
		
		
		NFStatusServicoConsultaRetorno retorno = new WSFacade(config).consultaStatus(DFUnidadeFederativa.RJ, DFModelo.NFE);
		System.out.println(retorno.getStatus());
		System.out.println(retorno.getMotivo());
		
	}
	
	public List<NFNotaProcessada> generateNFes(List<Venda> vendas, NfeConfig userNfeConfig, OrderRepository vendaDao) throws NfeProviderException{

//		getLogger().trace("Iniciando geraï¿½ï¿½o das NFes");
		
		List<NFNota> notas = new ArrayList<NFNota>();

		Long nrUltimaNota = new Long(userNfeConfig.getNrNota());

		for(Venda venda : vendas) {

			NFNota nota = new NFNota();
			nota.setInfo(getNFeInfo(venda,userNfeConfig));
			nrUltimaNota++;
			nota.getInfo().getIdentificacao().setNumeroNota(nrUltimaNota.toString());
			NFGeraChave ch = new NFGeraChave(nota);
			nota.getInfo().getIdentificacao().setCodigoRandomico(ch.geraCodigoRandomico());
			nota.getInfo().setIdentificador(ch.getChaveAcesso());
			nota.getInfo().getIdentificacao().setDigitoVerificador(ch.getDV());
			nota = assinarNFe(nota);
			notas.add(nota);
			
			venda.setNrNfe(nota.getInfo().getChaveAcesso());
			vendaDao.save(venda);
		}

		NFLoteConsultaRetorno retc = consultarLoteNFe(gerarLoteEnvioNfe(notas,userNfeConfig));

		List<NFNotaProcessada> notasProcessadas = new ArrayList<NFNotaProcessada>();
		Iterator<NFNota> it = notas.iterator();

		for(NFProtocolo protocolo: retc.getProtocolos()) {

			final NFNotaProcessada notaProcessada = new NFNotaProcessada();

			notaProcessada.setNota(it.next());
			protocolo.getProtocoloInfo().setAmbiente(DFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));
			protocolo.getProtocoloInfo().setVersaoAplicacao("1.0");
			notaProcessada.setProtocolo(protocolo);
			notaProcessada.setVersao(new BigDecimal("4.00"));		
			notasProcessadas.add(notaProcessada);
		}

		return notasProcessadas;
	}

	public List<InputStream> generateNFePdf(List<NFNotaProcessada> notasProcessadas) throws NfeProviderException{

		try {
			
			BufferedImage originalImage = ImageIO.read(getClass().getResourceAsStream("/images/trend-store.png"));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(originalImage, "png", baos);
			baos.flush();
			byte[] imageInByte = baos.toByteArray();
			baos.close();

			NFDanfeReport danfe;
			List<InputStream> inputStreams = new ArrayList<InputStream>();
			for (NFNotaProcessada notaProc : notasProcessadas) {
				danfe = new NFDanfeReport(notaProc);
				final byte[] fileByte = danfe.gerarDanfeNFe(imageInByte);
				InputStream is = new ByteArrayInputStream(fileByte);
				inputStreams.add(is);
			}
			
			//getLogger().trace("Geraï¿½ï¿½o das NFes finalizado");
			
			return inputStreams;
			
		} catch (IOException e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			throw new NfeProviderException();
		} catch (Exception e) {
			getLogger().error(ExceptionUtil.getStackTrace(e));
			throw new NfeProviderException();
		}
	}

	public void faturaNotasB2w(List<Venda> vendas, ClassPathXmlApplicationContext ctx) throws ProviderException {

		//ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring.xml");

		B2wOrderProvider b2wOrderProvider = (B2wOrderProvider) ctx.getBean("b2wOrderProvider");
		
		SkyHubUserCredencials userCredencials = new SkyHubUserCredencials(B2wConfigurationHolder.getInstance().getUserEmail(),B2wConfigurationHolder.getInstance().getApiKey(),B2wConfigurationHolder.getInstance().getAccountManagerKey());
		
		for (Venda venda : vendas) {
			b2wOrderProvider.invoiceOrder(userCredencials, venda.getOrigem().getNome() + '-' + venda.getId().toString(), venda.getNrNfe());
		}
		
		//b2wOrderProvider.invoiceOrder(userCredencials, )
		//b2wOrderProvider.searchOrderById(userCredencials, "Submarino-350197814101");
		
	}
	
	public NFNotaInfo getNFeInfo(Venda venda, NfeConfig userNfeConfig) throws NfeProviderException{
		
		NFNotaInfo nfeInfo = new NFNotaInfo();
		nfeInfo.setVersao(new BigDecimal("4.00"));
		nfeInfo.setIdentificacao(getNFNotaInfoIdentificacao(venda,userNfeConfig));
		nfeInfo.setEmitente(getNFNotaInfoEmitente(venda));
		nfeInfo.setDestinatario(getNFNotaInfoDestinatario(venda));
		//nfeInfo.setItens(Collections.singletonList(getNFNotaInfoItem(venda,userNfeConfig)));
		nfeInfo.setItens(getNFNotaInfoItem(venda,userNfeConfig));
		nfeInfo.setTotal(getNFNotaInfoTotal(nfeInfo.getItens(),userNfeConfig));
		nfeInfo.setPagamentos(Collections.singletonList(getNFNotaInfoPagamento(nfeInfo.getItens(), venda)));
		nfeInfo.setTransporte(getNFNotaInfoTransporte());
		//inf pagto
		nfeInfo.setInformacoesAdicionais(getNFNotaInfoInformacoesAdicionais());

		return nfeInfo;
	}

	public NFLoteEnvioRetorno gerarLoteEnvioNfe(List<NFNota> notas, NfeConfig userNfeConfig) throws NfeProviderException{
		
		try{
			
			final NFLoteEnvio loteEnvio = new NFLoteEnvio();
			loteEnvio.setIdLote(userNfeConfig.getNrLote());
			loteEnvio.setVersao("4.00");
			loteEnvio.setNotas(notas);
			loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
			NFLoteEnvioRetorno loteEnvioRetorno = new WSFacade(config).enviaLoteAssinado(loteEnvio.toString(), DFModelo.NFE);
			//NFLoteEnvioRetornoDados loteEnvioRetorno = new WSFacade(config).enviaLote(loteEnvio);
			return loteEnvioRetorno;
			
		} catch (KeyManagementException e) {
			throw new NfeProviderException();
		} catch (UnrecoverableKeyException e) {
			throw new NfeProviderException();
		} catch (KeyStoreException e) {
			throw new NfeProviderException();
		} catch (NoSuchAlgorithmException e) {
			throw new NfeProviderException();
		} catch (CertificateException e) {
			throw new NfeProviderException();
		} catch (IOException e) {
			throw new NfeProviderException();
		} catch (Exception e) {
			throw new NfeProviderException();
		}
	}

	public NFLoteEnvioRetorno gerarLoteEnvioNfe(NFNota nota, NfeConfig userNfeConfig) throws NfeProviderException{
		
		try {
			
			final NFLoteEnvio loteEnvio = new NFLoteEnvio();
			Long nrLote = new Long(userNfeConfig.getNrLote());
			loteEnvio.setIdLote(Long.toString(nrLote++));
			loteEnvio.setVersao("4.00");
			loteEnvio.setNotas(Collections.singletonList(nota));
			loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
			NFLoteEnvioRetorno loteEnvioRetorno = new WSFacade(config).enviaLoteAssinado(loteEnvio.toString(), DFModelo.NFE);

			return loteEnvioRetorno;
			
		} catch (KeyManagementException e) {
			throw new NfeProviderException();
		} catch (UnrecoverableKeyException e) {
			throw new NfeProviderException();
		} catch (KeyStoreException e) {
			throw new NfeProviderException();
		} catch (NoSuchAlgorithmException e) {
			throw new NfeProviderException();
		} catch (CertificateException e) {
			throw new NfeProviderException();
		} catch (IOException e) {
			throw new NfeProviderException();
		} catch (Exception e) {
			throw new NfeProviderException();
		}
	}

	public NFNota assinarNFe(NFNota nota) throws NfeProviderException{
		
		try {
			
			AssinaturaDigital asd = new AssinaturaDigital(config);
			String n = nota.toString();
			String strgNotaAssinada = asd.assinarDocumento(n);
			DFParser np = new DFParser();
			
			return np.notaParaObjeto(strgNotaAssinada);
			
		} catch (Exception e) {
			throw new NfeProviderException();
		}
	}

	public NFLoteConsultaRetorno consultarLoteNFe(NFLoteEnvioRetorno loteEnvioRetorno) throws NfeProviderException{

		try {
			
			NFLoteConsultaRetorno retc = new WSFacade(config).consultaLote(loteEnvioRetorno.getInfoRecebimento().getRecibo(), DFModelo.NFE);
			
			return retc;
			
		} catch (KeyManagementException e) {
			throw new NfeProviderException();
		} catch (UnrecoverableKeyException e) {
			throw new NfeProviderException();
		} catch (KeyStoreException e) {
			throw new NfeProviderException();
		} catch (NoSuchAlgorithmException e) {
			throw new NfeProviderException();
		} catch (CertificateException e) {
			throw new NfeProviderException();
		} catch (IOException e) {
			throw new NfeProviderException();
		} catch (Exception e) {
			throw new NfeProviderException();
		}
	}


	public NFNotaInfoIdentificacao getNFNotaInfoIdentificacao(Venda venda, NfeConfig userNfeConfig) {
		
		final NFNotaInfoIdentificacao identificacao = new NFNotaInfoIdentificacao();
		identificacao.setAmbiente(DFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));
		identificacao.setCodigoMunicipio(venda.getVendedor().getCodMunicipio().toString());
		
		Instant instant = Instant.now();
		identificacao.setDataHoraEmissao(instant.atZone(ZoneId.of("America/Sao_Paulo")));
				//ZonedDateTime.of(LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(new DateTime().toString())), ZoneId.systemDefault()));
        identificacao.setDataHoraSaidaOuEntrada(instant.atZone(ZoneId.of("America/Sao_Paulo")));
        		//ZonedDateTime.of(LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(new DateTime().toString())), ZoneId.systemDefault()));
		
		//identificacao.setCodigoRandomico("45000050");
		//identificacao.setDataHoraEmissao(ZonedDateTime.now(ZoneId.systemDefault()));
		//ZonedDateTime.of(LocalDateTime.from(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").parse(new DateTime().toString())), ZoneId.systemDefault()));
		//identificacao.setDataHoraSaidaOuEntrada(ZonedDateTime.now(ZoneId.systemDefault()));
		identificacao.setFinalidade(NFFinalidade.NORMAL);

		identificacao.setModelo(DFModelo.valueOfCodigo(userNfeConfig.getIndModelo()));
		identificacao.setNaturezaOperacao("Venda de Mercadoria");

		// Criar sequence para numero da nota.... dado armazenado na tabela NFE_CONFIG
		Long nrNote = new Long(userNfeConfig.getNrNota());
		identificacao.setNumeroNota(Long.toString(nrNote++));
		identificacao.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);

		// Dado armazenado na tabela NFE_CONFIG 
		identificacao.setSerie(userNfeConfig.getNrSerie());

		identificacao.setTipo(NFTipo.SAIDA);
		identificacao.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL);
		identificacao.setTipoImpressao(NFTipoImpressao.DANFE_NORMAL_RETRATO);
		identificacao.setUf(DFUnidadeFederativa.RJ);

		// Dado armazenado na tabela NFE_CONFIG
		identificacao.setVersaoEmissor(userNfeConfig.getNrVersaoEmissor());

		if (venda.getVendedor().getUf().equals(venda.getEnvio().getUf()))
			identificacao.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
		else
			identificacao.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERESTADUAL);
		identificacao.setOperacaoConsumidorFinal(NFOperacaoConsumidorFinal.SIM);
		identificacao.setIndicadorPresencaComprador(NFIndicadorPresencaComprador.OPERACAO_NAO_PRESENCIAL_INTERNET);
		return identificacao;
	}

	public NFNotaInfoEmitente getNFNotaInfoEmitente(Venda venda) {
		
		final NFNotaInfoEmitente emitente = new NFNotaInfoEmitente();
		emitente.setClassificacaoNacionalAtividadesEconomicas(venda.getVendedor().getCnae().toString());
		emitente.setCnpj(venda.getVendedor().getCnpj());
		emitente.setEndereco(getNFEndereco(venda));
		emitente.setInscricaoEstadual(venda.getVendedor().getIncriEstadual());
		emitente.setInscricaoMunicipal(venda.getVendedor().getIncriMunicipal());
		//emitente.setNomeFantasia("Trend Store");
		emitente.setRazaoSocial(venda.getVendedor().getRazaoSocial());
		emitente.setRegimeTributario(NFRegimeTributario.valueOfCodigo(venda.getVendedor().getRegimeTributario().toString()));
		
		return emitente;
	}

	public NFEndereco getNFEndereco(Venda venda) {
		
		final NFEndereco endereco = new NFEndereco();
		endereco.setBairro(venda.getVendedor().getBairro());
		endereco.setCep(venda.getVendedor().getCep().toString());
		endereco.setCodigoMunicipio(venda.getVendedor().getCodMunicipio().toString());
		endereco.setCodigoPais(venda.getVendedor().getCodPais().toString());
		endereco.setComplemento(venda.getVendedor().getComplemento());
		endereco.setDescricaoMunicipio(venda.getVendedor().getMunicipio());
		endereco.setLogradouro(venda.getVendedor().getLogradouro());
		endereco.setNumero(venda.getVendedor().getNumero());
		//        endereco.setTelefone("");
		endereco.setUf(DFUnidadeFederativa.valueOfCodigo(venda.getVendedor().getUf()));
		
		return endereco;
	}

	public NFNotaInfoDestinatario getNFNotaInfoDestinatario(Venda venda) {
		
		final NFNotaInfoDestinatario destinatario = new NFNotaInfoDestinatario();
		
		if(venda.getCliente().getTipo().equals(TipoPessoaEnum.FISICA.getValue()))
			destinatario.setCpf(venda.getCliente().getNrDocumento());
		else{
			destinatario.setCnpj(venda.getCliente().getNrDocumento());
		//		destinatario.setRazaoSocial(venda.getCliente().getNome());
		}
		destinatario.setRazaoSocial(venda.getCliente().getNome());
		//destinatario.setRazaoSocial("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
		destinatario.setEndereco(getNFEnderecoDest(venda));


		//destinatario.setInscricaoEstadual("13245678901234");
		//destinatario.setInscricaoSuframa("999999999");
		//destinatario.setRazaoSocial("SHENZHEN TENG BIN INDUSTRIAL CO. LTD");
		destinatario.setIndicadorIEDestinatario(NFIndicadorIEDestinatario.NAO_CONTRIBUINTE);
		//destinatario.setInscricaoMunicipal("5ow5E1mZQPe1VUR");
		return destinatario;
	}

	public NFEndereco getNFEnderecoDest(Venda venda) {
		
		final NFEndereco endereco = new NFEndereco();
		endereco.setBairro(venda.getEnvio().getBairro()==null||venda.getEnvio().getBairro().length()<2? "NI":venda.getEnvio().getBairro());
		endereco.setCep(String.format("%08d", Integer.parseInt(venda.getEnvio().getCep().toString())));
		//Criar rotina para carregar o cï¿½digo do municï¿½pio na tabela de envio no momento da importaï¿½ï¿½o da venda. O cadastro dos cï¿½digos estï¿½o na tabela MUNICIPIO.
		endereco.setCodigoMunicipio(venda.getEnvio().getCodMunicipio()==null?"9999999":venda.getEnvio().getCodMunicipio().toString());
		//endereco.setCodigoMunicipio("3550308");
		// Criar rotina para carregar o cï¿½digo do paï¿½s na tabela de envio no momento da importaï¿½ï¿½o da venda. Como sï¿½ temos venda dentro do Brasil o cï¿½digo ï¿½ 1058. 
		//endereco.setCodigoPais(venda.getEnvio().getCodPais().toString());
		endereco.setCodigoPais("1058");
		endereco.setDescricaoPais("Brasil");
		if(venda.getEnvio().getComplemento().length()>0) 
			endereco.setComplemento((venda.getEnvio().getComplemento().length()<=59)?venda.getEnvio().getComplemento():venda.getEnvio().getComplemento().substring(0, 58).trim());
		endereco.setDescricaoMunicipio(venda.getEnvio().getMunicipio());
		endereco.setLogradouro(venda.getEnvio().getLogradouro());
		endereco.setNumero(venda.getEnvio().getNumero());
		endereco.setUf(DFUnidadeFederativa.valueOfCodigo(venda.getEnvio().getUf()));
		
		return endereco;
	}

	public List<NFNotaInfoItem> getNFNotaInfoItem(Venda venda, NfeConfig userNfeConfig) {
		
		NFNotaInfoItem item;
		List<NFNotaInfoItem> listItem = new ArrayList<NFNotaInfoItem>();
		
		int x = 0;
		for (DetalheVenda dv : venda.getDetalhesVenda()) {
			
			item = new NFNotaInfoItem();
			item.setImposto(getNFNotaInfoItemImposto(venda, userNfeConfig, x));
			item.setNumeroItem(Integer.valueOf(x+1));
			item.setProduto(getNFNotaInfoItemProduto(venda, x));
			
			listItem.add(item);
			x++;
		}
		
		return listItem;
	}

	public NFNotaInfoItemImposto getNFNotaInfoItemImposto(Venda venda, NfeConfig userNfeConfig, int id) {
		
		final NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
		imposto.setCofins(getNFNotaInfoItemImpostoCOFINS());
		imposto.setIcms(getNFNotaInfoItemImpostoICMS());
		imposto.setPis(getNFNotaInfoItemImpostoPIS());

		BigDecimal freteItem = new BigDecimal(venda.getEnvio().getCustoComprador() / venda.getDetalhesVenda().size());
		
		double vlPag = 0;
		for (Pagamento pag : venda.getPagamentos()) {
			vlPag += pag.getValorTransacao();
		}
		
		double tarifaVenda = 0;
		for (DetalheVenda dv : venda.getDetalhesVenda()) {
			tarifaVenda += dv.getTarifaVenda() * dv.getQuantidade();
		}
		
		//BigDecimal comissaoVenda = new BigDecimal((vlPag + venda.getEnvio().getCustoComprador()) * 0.16).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		//BigDecimal comissaoVenda = new BigDecimal(vlPag * 0.16).setScale(2, BigDecimal.ROUND_HALF_EVEN);
		
		// Criar mï¿½todo para retornar a alï¿½quota de imposto do Simples Nacional
		imposto.setValorTotalTributos(new BigDecimal((venda.getPagamentos().get(id).getValorTransacao()  
				- venda.getDetalhesVenda().get(id).getTarifaVenda() - venda.getEnvio().getCustoVendedor() - freteItem.doubleValue())
				* Double.parseDouble(userNfeConfig.getTxSimplesNacional())).setScale(2, BigDecimal.ROUND_HALF_EVEN));

		//imposto.setValorTotalTributos(new BigDecimal("10.93"));
		return imposto;
	}

	public NFNotaInfoItemImpostoPIS getNFNotaInfoItemImpostoPIS() {
		
		final NFNotaInfoItemImpostoPIS pis = new NFNotaInfoItemImpostoPIS();
		//pis.setAliquota(GerarNotaConsumidor.getNFNotaInfoItemImpostoPISAliquota());
		pis.setOutrasOperacoes(getNFNotaInfoItemImpostoPISOutrasOperacoes());
		
		return pis;
	}

	public NFNotaInfoItemImpostoPISOutrasOperacoes getNFNotaInfoItemImpostoPISOutrasOperacoes() {
		
		final NFNotaInfoItemImpostoPISOutrasOperacoes pisOutras = new NFNotaInfoItemImpostoPISOutrasOperacoes();
		pisOutras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.OUTRAS_OPERACOES);
		pisOutras.setQuantidadeVendida(new BigDecimal("0"));
		pisOutras.setValorAliquota(new BigDecimal("0.0000"));
		pisOutras.setValorTributo(new BigDecimal("0"));
		
		return pisOutras;
	}


	public NFNotaInfoItemImpostoCOFINS getNFNotaInfoItemImpostoCOFINS() {
		
		final NFNotaInfoItemImpostoCOFINS cofins = new NFNotaInfoItemImpostoCOFINS();
		//cofins.setAliquota(GerarNotaConsumidor.getNFNotaInfoItemImpostoCOFINSAliquota());
		cofins.setOutrasOperacoes(getNFNotaInfoItemImpostoCOFINSOutrasOperacoes());
		
		return cofins;
	}

	public NFNotaInfoItemImpostoCOFINSOutrasOperacoes getNFNotaInfoItemImpostoCOFINSOutrasOperacoes() {
		
		final NFNotaInfoItemImpostoCOFINSOutrasOperacoes cofinsOutras = new NFNotaInfoItemImpostoCOFINSOutrasOperacoes();
		cofinsOutras.setValorAliquota(new BigDecimal("0.0000"));
		cofinsOutras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.OUTRAS_OPERACOES);
		cofinsOutras.setValorCOFINS(new BigDecimal("0"));
		cofinsOutras.setQuantidadeVendida(new BigDecimal("0"));
		
		return cofinsOutras;
	}

	public NFNotaInfoItemImpostoICMS getNFNotaInfoItemImpostoICMS() {
		
		final NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
		icms.setIcmssn102(getNFNotaInfoItemImpostoICMSSN102());
		
		return icms;
	}

	public NFNotaInfoItemImpostoICMSSN102 getNFNotaInfoItemImpostoICMSSN102() {
		
		final NFNotaInfoItemImpostoICMSSN102 icmssn102 = new NFNotaInfoItemImpostoICMSSN102();
		icmssn102.setOrigem(NFOrigem.ESTRANGEIRA_IMPORTACAO_DIRETA);
		icmssn102.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.TRIBUTADA_SEM_PERMISSAO_CREDITO);
		
		return icmssn102;
	}

	public NFNotaInfoItemProduto getNFNotaInfoItemProduto(Venda venda, int id) {
		
		final NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();

		BigDecimal freteItem = new BigDecimal(venda.getEnvio().getCustoComprador() / venda.getDetalhesVenda().size());  
		
		if (venda.getVendedor().getUf().equals(venda.getEnvio().getUf()))
			produto.setCfop("5104");
		else
			produto.setCfop("6104");
		produto.setCodigo(venda.getDetalhesVenda().get(id)!=null?venda.getDetalhesVenda().get(id).getProdutoSku().trim():venda.getDetalhesVenda().get(id).getAnuncio().getIdMl().trim());
		produto.setCodigoDeBarras("");
		produto.setCodigoDeBarrasTributavel("");
		produto.setCampoeValorNota(NFProdutoCompoeValorNota.SIM);
		produto.setDescricao(venda.getDetalhesVenda().get(id).getAnuncio().getTitulo());
		// POPULAR NCM DOS PRODUTOS NA BASE DE DADOS
		try{
			produto.setNcm("39269090");	
//		produto.setNcm(!venda.getDetalhesVenda().get(0).getAnuncio().getProdutos().isEmpty()?
//				venda.getDetalhesVenda().get(0).getAnuncio().getProdutos().iterator().next().getNcm().toString():null);
		}
		catch(NoSuchElementException e){
			produto.setNcm(null);
		}
		produto.setQuantidadeComercial(new BigDecimal(venda.getDetalhesVenda().get(id).getQuantidade()));
		produto.setQuantidadeTributavel(new BigDecimal(venda.getDetalhesVenda().get(id).getQuantidade()));
		produto.setUnidadeComercial("UN");
		produto.setUnidadeTributavel("UN");

		produto.setValorDesconto(new BigDecimal((venda.getDetalhesVenda().get(id).getTarifaVenda() * venda.getDetalhesVenda().get(id).getQuantidade())
				+ venda.getEnvio().getCustoVendedor() + freteItem.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN));

		produto.setValorFrete(venda.getEnvio().getCustoComprador()==0?null:freteItem.setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//produto.setValorOutrasDespesasAcessorias(new BigDecimal("999999999999.99"));
		//produto.setValorSeguro(new BigDecimal("999999999999.99"));

		//produto.setValorTotalBruto(new BigDecimal(venda.getPagamentos().get(0).getValorTransacao()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		produto.setValorTotalBruto(new BigDecimal(venda.getDetalhesVenda().get(id).getValor() * venda.getDetalhesVenda().get(id).getQuantidade()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		produto.setValorUnitario(new BigDecimal(venda.getDetalhesVenda().get(id).getValor()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//produto.setNomeclaturaValorAduaneiroEstatistica(Collections.singletonList("AZ0123"));
		produto.setValorUnitarioTributavel(new BigDecimal(venda.getDetalhesVenda().get(id).getValor()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		
		return produto;
	}

	public NFNotaInfoTotal getNFNotaInfoTotal(List<NFNotaInfoItem> listItem, NfeConfig userNfeConfig) {
		
		final NFNotaInfoTotal total = new NFNotaInfoTotal();
		total.setIcmsTotal(getNFNotaInfoICMSTotal(listItem,userNfeConfig));
		
		return total;
	}

	public NFNotaInfoICMSTotal getNFNotaInfoICMSTotal(List<NFNotaInfoItem> listItem, NfeConfig userNfeConfig) {
		
		double vlFrete = 0;
		double vlProd = 0;
		double vlDesc = 0;
		double vlTrib = 0;
		for (NFNotaInfoItem item : listItem) {
			vlProd += Double.valueOf(item.getProduto().getValorTotalBruto());
			vlDesc += Double.valueOf(item.getProduto().getValorDesconto());
			//if(item.getProduto().getValorFrete().equals("0"))
			vlFrete += Double.valueOf(item.getProduto().getValorFrete()==null?"0":item.getProduto().getValorFrete());
			vlTrib += Double.valueOf(item.getImposto().getValorTotalTributos());
		}
		
//		double vlPagto = 0;
//		for (Pagamento pag : venda.getPagamentos()) {
//			vlPagto = pag.getValorTransacao();
//		}
		
		final NFNotaInfoICMSTotal icmsTotal = new NFNotaInfoICMSTotal();
		icmsTotal.setBaseCalculoICMS(new BigDecimal("0"));
		icmsTotal.setOutrasDespesasAcessorias(new BigDecimal("0"));
		icmsTotal.setBaseCalculoICMSST(new BigDecimal("0"));
		icmsTotal.setValorCOFINS(new BigDecimal("0"));
		icmsTotal.setValorPIS(new BigDecimal("0"));
		icmsTotal.setValorTotalDesconto(new BigDecimal(vlDesc).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//		icmsTotal.setValorTotalDesconto(new BigDecimal(tarifaVenda
//				+ venda.getEnvio().getCustoVendedor() + venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalDosProdutosServicos(new BigDecimal(vlProd).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalFrete(new BigDecimal(vlFrete).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//		icmsTotal.setValorTotalFrete(new BigDecimal(venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalICMS(new BigDecimal("0"));
		icmsTotal.setValorTotalICMSST(new BigDecimal("0"));
		icmsTotal.setValorTotalII(new BigDecimal("0"));
		icmsTotal.setValorTotalIPI(new BigDecimal("0"));
		icmsTotal.setValorTotalNFe(new BigDecimal(vlProd + vlFrete - vlDesc).setScale(2, BigDecimal.ROUND_HALF_EVEN));
//		icmsTotal.setValorTotalNFe(new BigDecimal(vlPagto - tarifaVenda - venda.getEnvio().getCustoVendedor() 
//				- venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalSeguro(new BigDecimal("0"));
		icmsTotal.setValorICMSDesonerado(new BigDecimal("0"));
		icmsTotal.setValorICMSFundoCombatePobreza(new BigDecimal("0"));
		icmsTotal.setValorTotalFundoCombatePobreza(new BigDecimal("0"));
		icmsTotal.setValorTotalFundoCombatePobrezaST(new BigDecimal("0"));
		icmsTotal.setValorTotalFundoCombatePobrezaSTRetido(new BigDecimal("0"));
		icmsTotal.setValorTotalIPIDevolvido(new BigDecimal("0"));
		icmsTotal.setValorICMSPartilhaDestinatario(new BigDecimal("0"));
		icmsTotal.setValorICMSPartilhaRementente(new BigDecimal("0"));
		icmsTotal.setValorTotalTributos(new BigDecimal(vlTrib).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		
		return icmsTotal;
	}

	public NFNotaInfoTransporte getNFNotaInfoTransporte() {
		
		final NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();
		//transporte.setIcmsTransporte(FabricaDeObjetosFake.getNFNotaInfoRetencaoICMSTransporte());
		transporte.setModalidadeFrete(NFModalidadeFrete.CONTRATACAO_POR_CONTA_DO_EMITENTE);
		//transporte.setReboques(Collections.singletonList(FabricaDeObjetosFake.getNFNotaInfoReboque()));
		transporte.setTransportador(getNFNotaInfoTransportador());
		transporte.setVolumes(Collections.singletonList(getNFNotaInfoVolume()));
		
		return transporte;
	}


	// CRIAR TABELA COM OS DADOS DOS TRANSPORTADORES (MERCADO ENVIOS, B2W ENTREGAS, CORREIOS, ETC.)
	public NFNotaInfoTransportador getNFNotaInfoTransportador() {
		
		final NFNotaInfoTransportador transportador = new NFNotaInfoTransportador();
		transportador.setCnpj("20121850000155");
		transportador.setEnderecoComplemento("AV MARTE,489 ANDAR TERREO PARTE B");
		transportador.setInscricaoEstadual("ISENTO");
		transportador.setNomeMunicipio("Sao Paulo");
		transportador.setRazaoSocial("Mercado Envios Serviços de Logística Ltda");
		transportador.setUf(DFUnidadeFederativa.SP);
		
		return transportador;
	}

	public NFNotaInfoVolume getNFNotaInfoVolume() {
		
		final NFNotaInfoVolume volume = new NFNotaInfoVolume();
		volume.setEspecieVolumesTransportados("Caixa de papelão");
		volume.setPesoBruto(new BigDecimal("0.50"));
		volume.setPesoLiquido(new BigDecimal("0.30"));
		volume.setQuantidadeVolumesTransportados(new BigInteger("1"));
		
		return volume;
	}
	
	public static NFNotaInfoPagamento getNFNotaInfoPagamento(List<NFNotaInfoItem> listItem, Venda venda) {
        final NFNotaInfoPagamento pagamento = new NFNotaInfoPagamento();
        pagamento.setDetalhamentoFormasPagamento(Collections.singletonList(getNFNotaInfoFormaPagamento(listItem, venda)));
        return pagamento;
    }

    public static NFNotaInfoFormaPagamento getNFNotaInfoFormaPagamento(List<NFNotaInfoItem> listItem, Venda venda) {
        final NFNotaInfoFormaPagamento formaPagamento = new NFNotaInfoFormaPagamento();
        
		double vlProd = 0;
		double vlDesc = 0;
		double vlFrete = 0;
		for (NFNotaInfoItem item : listItem) {
			vlProd += Double.valueOf(item.getProduto().getValorUnitario());
			vlDesc += Double.valueOf(item.getProduto().getValorDesconto());
			vlFrete += Double.valueOf(item.getProduto().getValorFrete()==null?"0":item.getProduto().getValorFrete());
		}
        
        if (venda.getPagamentos().get(0).getNumeroParcelas() <= 1)
        	formaPagamento.setIndicadorFormaPagamento(NFIndicadorFormaPagamento.A_VISTA);
		else
			formaPagamento.setIndicadorFormaPagamento(NFIndicadorFormaPagamento.A_PRAZO);

        //formaPagamento.setCartao(FabricaDeObjetosFake.getNFNotaInfoCartao());
        
        formaPagamento.setValorPagamento(new BigDecimal(vlProd + vlFrete - vlDesc).setScale(2, BigDecimal.ROUND_HALF_EVEN));
        if (venda.getPagamentos().get(0).getTipo().equals("credit_card"))
        	formaPagamento.setFormaPagamentoMoeda(NFFormaPagamentoMoeda.CARTAO_CREDITO);
        else
        	formaPagamento.setFormaPagamentoMoeda(NFFormaPagamentoMoeda.BOLETO_BANCARIO);
        return formaPagamento;
    }

	public NFNotaInfoInformacoesAdicionais getNFNotaInfoInformacoesAdicionais() {
		
		final NFNotaInfoInformacoesAdicionais infoAdicionais = new NFNotaInfoInformacoesAdicionais();
		infoAdicionais.setInformacoesComplementaresInteresseContribuinte("I - DOCUMENTO EMITIDO POR ME OU EPP OPTANTE PELO SIMPLES NACIONAL; II - NÃO GERA DIREITO A CRÉDITO FISCAL DE ICMS, DE ISS E DE IPI.");

		return infoAdicionais;
	}

	public NFProtocolo getNotaProt(NFLoteConsultaRetorno retc, NfeConfig userNfeConfig) {

		final NFProtocolo protocolo = new NFProtocolo();
		protocolo.setProtocoloInfo(getNFProtocoloInfo(retc, userNfeConfig));
		protocolo.setVersao("4.00");
		
		return protocolo;
	}

	public NFProtocoloInfo getNFProtocoloInfo(NFLoteConsultaRetorno retc, NfeConfig userNfeConfig) {
		
		final NFProtocoloInfo info = new NFProtocoloInfo();
		info.setAmbiente(DFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));

		for (NFProtocolo prot : retc.getProtocolos()) {
			info.setChave(prot.getProtocoloInfo().getChave());
			try {
				info.setDataRecebimento(prot.getProtocoloInfo().getDataRecebimento().toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			info.setMotivo(prot.getProtocoloInfo().getMotivo());
			info.setNumeroProtocolo(prot.getProtocoloInfo().getNumeroProtocolo());
			info.setStatus(prot.getProtocoloInfo().getStatus());
			info.setValidador(prot.getProtocoloInfo().getValidador());
			info.setVersaoAplicacao("1.0");
			info.setIdentificador(prot.getProtocoloInfo().getIdentificador());
		}
		
		return info;
	}

}
