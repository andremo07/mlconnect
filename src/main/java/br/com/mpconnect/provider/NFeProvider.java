package br.com.mpconnect.provider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.fincatto.nfe310.assinatura.AssinaturaDigital;
import com.fincatto.nfe310.classes.NFAmbiente;
import com.fincatto.nfe310.classes.NFEndereco;
import com.fincatto.nfe310.classes.NFFinalidade;
import com.fincatto.nfe310.classes.NFFormaPagamentoPrazo;
import com.fincatto.nfe310.classes.NFModalidadeFrete;
import com.fincatto.nfe310.classes.NFModelo;
import com.fincatto.nfe310.classes.NFNotaInfoSituacaoTributariaCOFINS;
import com.fincatto.nfe310.classes.NFNotaInfoSituacaoTributariaPIS;
import com.fincatto.nfe310.classes.NFNotaSituacaoOperacionalSimplesNacional;
import com.fincatto.nfe310.classes.NFOrigem;
import com.fincatto.nfe310.classes.NFProcessoEmissor;
import com.fincatto.nfe310.classes.NFProdutoCompoeValorNota;
import com.fincatto.nfe310.classes.NFProtocolo;
import com.fincatto.nfe310.classes.NFProtocoloInfo;
import com.fincatto.nfe310.classes.NFRegimeTributario;
import com.fincatto.nfe310.classes.NFTipo;
import com.fincatto.nfe310.classes.NFTipoEmissao;
import com.fincatto.nfe310.classes.NFTipoImpressao;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.nfe310.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.nfe310.classes.nota.NFIndicadorPresencaComprador;
import com.fincatto.nfe310.classes.nota.NFNota;
import com.fincatto.nfe310.classes.nota.NFNotaInfo;
import com.fincatto.nfe310.classes.nota.NFNotaInfoDestinatario;
import com.fincatto.nfe310.classes.nota.NFNotaInfoEmitente;
import com.fincatto.nfe310.classes.nota.NFNotaInfoICMSTotal;
import com.fincatto.nfe310.classes.nota.NFNotaInfoIdentificacao;
import com.fincatto.nfe310.classes.nota.NFNotaInfoInformacoesAdicionais;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItem;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImposto;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoCOFINS;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoCOFINSOutrasOperacoes;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoICMS;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoICMSSN102;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoPIS;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemImpostoPISOutrasOperacoes;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemProduto;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTotal;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTransportador;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTransporte;
import com.fincatto.nfe310.classes.nota.NFNotaInfoVolume;
import com.fincatto.nfe310.classes.nota.NFNotaProcessada;
import com.fincatto.nfe310.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.nfe310.danfe.NFDanfeReport;
import com.fincatto.nfe310.parsers.NotaParser;
import com.fincatto.nfe310.utils.NFGeraChave;
import com.fincatto.nfe310.webservices.WSFacade;

import br.com.mpconnect.dao.MunicipioDao;
import br.com.mpconnect.dao.TabelaSimplesNacionalDao;
import br.com.mpconnect.holder.NfeConfigurationHolder;
import br.com.mpconnect.model.NfeConfig;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.nfe.generator.GerarNotaConsumidor;
import br.com.mpconnect.provider.exception.NfeProviderException;


@Service(value="nfeProvider")
@DependsOn("nfeConfHolder")
public class NFeProvider {

	private NfeConfigurationHolder config;

	@Autowired
	private TabelaSimplesNacionalDao tabSimplesDao;

	@Autowired
	private MunicipioDao municipioDao;

	@PostConstruct
	public void setUp(){		
		config = NfeConfigurationHolder.getInstance();		
	}	

	public String gerarNFe(List<Venda> vendas, NfeConfig userNfeConfig) throws Exception{

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
		}

		NFLoteConsultaRetorno retc = consultarLoteNFe(gerarLoteEnvioNfe(notas,userNfeConfig));

		List<NFNotaProcessada> notasProcessadas = new ArrayList<NFNotaProcessada>();

		for(NFProtocolo protocolo: retc.getProtocolos()) {

			final NFNotaProcessada notaProcessada = new NFNotaProcessada();

			notaProcessada.setNota(notas.iterator().next());
			//notaProcessada.setProtocolo(getNotaProt(retc, userNfeConfig));
			protocolo.getProtocoloInfo().setAmbiente(NFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));
			protocolo.getProtocoloInfo().setVersaoAplicacao("1.0");
			notaProcessada.setProtocolo(protocolo);
			notaProcessada.setVersao(new BigDecimal("3.10"));		
			notasProcessadas.add(notaProcessada);
		}

		
		BufferedImage originalImage = ImageIO.read(new File("trend-store.png"));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(originalImage, "png", baos);
		baos.flush();
		byte[] imageInByte = baos.toByteArray();
		baos.close();
		
		NFDanfeReport danfe;
		for (NFNotaProcessada notaProc : notasProcessadas) {

			// String notaProc = notaProcessada.toString();
			// System.out.println(notaProcessada.toString());
			danfe = new NFDanfeReport(notaProc);
			final byte[] fileByte = danfe.gerarDanfeNFe(imageInByte);
//			Assert.assertTrue(fileByte.length > 0);
			OutputStream out = new FileOutputStream("NFe_" + notaProc.getNota().getInfo().getIdentificacao().getNumeroNota() + ".pdf");
			out.write(fileByte);
			out.close();
		}
		
		return null;
	}

	public NFNotaInfo getNFeInfo(Venda venda, NfeConfig userNfeConfig) throws NfeProviderException{
		NFNotaInfo nfeInfo = new NFNotaInfo();

		//SET IDENTIFIÇÃO

		//TabelaSimplesNacional tbaSimplesNacionanl = tabSimplesDao.recuperaUm(0);
		//Municipio muni = municipioDao.recuperaUm(120010);

		nfeInfo.setVersao(new BigDecimal("3.10"));

		nfeInfo.setIdentificacao(getNFNotaInfoIdentificacao(venda,userNfeConfig));
		nfeInfo.setEmitente(getNFNotaInfoEmitente(venda));
		nfeInfo.setDestinatario(getNFNotaInfoDestinatario(venda));
		nfeInfo.setItens(Collections.singletonList(getNFNotaInfoItem(venda,userNfeConfig)));
		nfeInfo.setTotal(getNFNotaInfoTotal(venda,userNfeConfig));
		nfeInfo.setTransporte(getNFNotaInfoTransporte());
		nfeInfo.setInformacoesAdicionais(getNFNotaInfoInformacoesAdicionais());

		return nfeInfo;
	}

	public NFLoteEnvioRetorno gerarLoteEnvioNfe(List<NFNota> notas, NfeConfig userNfeConfig) throws NfeProviderException{
		try{
			final NFLoteEnvio loteEnvio = new NFLoteEnvio();
			loteEnvio.setIdLote("333972757970401");
			loteEnvio.setVersao("3.10");
			loteEnvio.setNotas(notas);
			loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
			NFLoteEnvioRetorno loteEnvioRetorno = new WSFacade(config).enviaLoteAssinado(loteEnvio.toString(), NFModelo.NFE);
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
			// VERIFICAR COMO GERAR O SEQUENCIAL DO LOTE
			Long nrLote = new Long(userNfeConfig.getNrLote());
			nrLote++;
			loteEnvio.setIdLote(nrLote.toString());
			loteEnvio.setVersao("3.10");
			loteEnvio.setNotas(Collections.singletonList(nota));
			loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
			NFLoteEnvioRetorno loteEnvioRetorno = new WSFacade(config).enviaLoteAssinado(loteEnvio.toString(), NFModelo.NFE);

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
			String strgNotaAssinada = asd.assinarDocumento(nota.toString());
			NotaParser np = new NotaParser();
			return np.notaParaObjeto(strgNotaAssinada);
		} catch (Exception e) {
			throw new NfeProviderException();
		}
	}

	public NFLoteConsultaRetorno consultarLoteNFe(NFLoteEnvioRetorno loteEnvioRetorno) throws NfeProviderException{

		try {
			NFLoteConsultaRetorno retc = new WSFacade(config).consultaLote(loteEnvioRetorno.getInfoRecebimento().getRecibo(), NFModelo.NFE);
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
		identificacao.setAmbiente(NFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));

		identificacao.setCodigoMunicipio(venda.getVendedor().getCodMunicipio().toString());
		//identificacao.setCodigoRandomico("45000050");
		identificacao.setDataHoraEmissao(new DateTime());
		identificacao.setDataHoraSaidaOuEntrada(new DateTime());
		identificacao.setFinalidade(NFFinalidade.NORMAL);

		if (venda.getPagamentos().get(0).getNumeroParcelas() <= 1)
			identificacao.setFormaPagamento(NFFormaPagamentoPrazo.A_VISTA);
		else
			identificacao.setFormaPagamento(NFFormaPagamentoPrazo.A_PRAZO);

		identificacao.setModelo(NFModelo.valueOfCodigo(userNfeConfig.getIndModelo()));
		identificacao.setNaturezaOperacao("Venda de Mercadoria");

		// Criar sequence para numero da nota.... dado armazenado na tabela NFE_CONFIG
		Long nrNote = new Long(userNfeConfig.getNrNota());
		nrNote++;
		identificacao.setNumeroNota(nrNote.toString());

		identificacao.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);

		// Dado armazenado na tabela NFE_CONFIG 
		identificacao.setSerie(userNfeConfig.getNrSerie());

		identificacao.setTipo(NFTipo.SAIDA);
		identificacao.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL);
		identificacao.setTipoImpressao(NFTipoImpressao.DANFE_NORMAL_RETRATO);
		identificacao.setUf(NFUnidadeFederativa.RJ);

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
		endereco.setUf(NFUnidadeFederativa.valueOfCodigo(venda.getVendedor().getUf()));
		return endereco;
	}

	public NFNotaInfoDestinatario getNFNotaInfoDestinatario(Venda venda) {
		final NFNotaInfoDestinatario destinatario = new NFNotaInfoDestinatario();
		destinatario.setCpf(venda.getCliente().getNrDocumento());
//		destinatario.setRazaoSocial(venda.getCliente().getNome());
		destinatario.setRazaoSocial("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
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
		// Criar rotina para carregar o código do município na tabela de envio no momento da importação da venda. O cadastro dos códigos estão na tabela MUNICIPIO.
		endereco.setCodigoMunicipio(venda.getEnvio().getCodMunicipio()==null?"9999999":venda.getEnvio().getCodMunicipio().toString());  
		// Criar rotina para carregar o código do país na tabela de envio no momento da importação da venda. Como só temos venda dentro do Brasil o código é 1058. 
		//endereco.setCodigoPais(venda.getEnvio().getCodPais().toString());
		endereco.setCodigoPais("1058");
		endereco.setDescricaoPais("Brasil");
		endereco.setComplemento(venda.getEnvio().getComplemento());
		endereco.setDescricaoMunicipio(venda.getEnvio().getMunicipio());
		endereco.setLogradouro(venda.getEnvio().getLogradouro());
		endereco.setNumero(venda.getEnvio().getNumero());
		endereco.setUf(NFUnidadeFederativa.valueOfCodigo(venda.getEnvio().getUf()));
		return endereco;
	}

	public NFNotaInfoItem getNFNotaInfoItem(Venda venda, NfeConfig userNfeConfig) {
		final NFNotaInfoItem item = new NFNotaInfoItem();
		item.setImposto(getNFNotaInfoItemImposto(venda, userNfeConfig));
		item.setNumeroItem(Integer.valueOf(1));
		item.setProduto(getNFNotaInfoItemProduto(venda));
		return item;
	}

	public NFNotaInfoItemImposto getNFNotaInfoItemImposto(Venda venda, NfeConfig userNfeConfig) {
		final NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
		imposto.setCofins(getNFNotaInfoItemImpostoCOFINS());
		imposto.setIcms(getNFNotaInfoItemImpostoICMS());
		imposto.setPis(getNFNotaInfoItemImpostoPIS());

		// Criar método para retornar a alíquota de imposto do Simples Nacional
		imposto.setValorTotalTributos(new BigDecimal((venda.getPagamentos().get(0).getValorTransacao() + venda.getEnvio().getCustoComprador() 
				- venda.getDetalhesVenda().get(0).getTarifaVenda()
				- venda.getEnvio().getCustoVendedor() - venda.getEnvio().getCustoComprador())
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
		cofins.setOutrasOperacoes(GerarNotaConsumidor.getNFNotaInfoItemImpostoCOFINSOutrasOperacoes());
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
		icms.setIcmssn102(GerarNotaConsumidor.getNFNotaInfoItemImpostoICMSSN102());
		return icms;
	}

	public NFNotaInfoItemImpostoICMSSN102 getNFNotaInfoItemImpostoICMSSN102() {
		final NFNotaInfoItemImpostoICMSSN102 icmssn102 = new NFNotaInfoItemImpostoICMSSN102();
		icmssn102.setOrigem(NFOrigem.ESTRANGEIRA_IMPORTACAO_DIRETA);
		icmssn102.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.TRIBUTADA_SEM_PERMISSAO_CREDITO);
		return icmssn102;
	}

	public NFNotaInfoItemProduto getNFNotaInfoItemProduto(Venda venda) {
		final NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();

		if (venda.getVendedor().getUf().equals(venda.getEnvio().getUf()))
			produto.setCfop("5104");
		else
			produto.setCfop("6104");
		produto.setCodigo(venda.getDetalhesVenda().get(0).getAnuncio().getId().toString());
		produto.setCodigoDeBarras("");
		produto.setCodigoDeBarrasTributavel("");
		produto.setCampoeValorNota(NFProdutoCompoeValorNota.SIM);
		produto.setDescricao(venda.getDetalhesVenda().get(0).getAnuncio().getTitulo());
		// POPULAR NCM DOS PRODUTOS NA BASE DE DADOS
		produto.setNcm(venda.getDetalhesVenda().get(0).getAnuncio().getProdutos().iterator().next().getNcm().toString());
		produto.setQuantidadeComercial(new BigDecimal(venda.getDetalhesVenda().get(0).getQuantidade()));
		produto.setQuantidadeTributavel(new BigDecimal(venda.getDetalhesVenda().get(0).getQuantidade()));
		produto.setUnidadeComercial("UN");
		produto.setUnidadeTributavel("UN");

		produto.setValorDesconto(new BigDecimal(venda.getDetalhesVenda().get(0).getTarifaVenda()
				+ venda.getEnvio().getCustoVendedor() + venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));

		produto.setValorFrete(venda.getEnvio().getCustoComprador()==0?null:new BigDecimal(venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//produto.setValorOutrasDespesasAcessorias(new BigDecimal("999999999999.99"));
		//produto.setValorSeguro(new BigDecimal("999999999999.99"));

		produto.setValorTotalBruto(new BigDecimal(venda.getPagamentos().get(0).getValorTransacao()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		produto.setValorUnitario(new BigDecimal(venda.getDetalhesVenda().get(0).getAnuncio().getValor()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		//produto.setNomeclaturaValorAduaneiroEstatistica(Collections.singletonList("AZ0123"));
		produto.setValorUnitarioTributavel(new BigDecimal(venda.getDetalhesVenda().get(0).getAnuncio().getValor()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		return produto;
	}

	public NFNotaInfoTotal getNFNotaInfoTotal(Venda venda, NfeConfig userNfeConfig) {
		final NFNotaInfoTotal total = new NFNotaInfoTotal();
		total.setIcmsTotal(getNFNotaInfoICMSTotal(venda,userNfeConfig));
		return total;
	}

	public NFNotaInfoICMSTotal getNFNotaInfoICMSTotal(Venda venda, NfeConfig userNfeConfig) {
		final NFNotaInfoICMSTotal icmsTotal = new NFNotaInfoICMSTotal();
		icmsTotal.setBaseCalculoICMS(new BigDecimal("0"));
		icmsTotal.setOutrasDespesasAcessorias(new BigDecimal("0"));
		icmsTotal.setBaseCalculoICMSST(new BigDecimal("0"));
		icmsTotal.setValorCOFINS(new BigDecimal("0"));
		icmsTotal.setValorPIS(new BigDecimal("0"));
		icmsTotal.setValorTotalDesconto(new BigDecimal(venda.getDetalhesVenda().get(0).getTarifaVenda()
				+ venda.getEnvio().getCustoVendedor() + venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalDosProdutosServicos(new BigDecimal(venda.getPagamentos().get(0).getValorTransacao()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalFrete(new BigDecimal(venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalICMS(new BigDecimal("0"));
		icmsTotal.setValorTotalICMSST(new BigDecimal("0"));
		icmsTotal.setValorTotalII(new BigDecimal("0"));
		icmsTotal.setValorTotalIPI(new BigDecimal("0"));
		icmsTotal.setValorTotalNFe(new BigDecimal(venda.getPagamentos().get(0).getValorTransacao() + venda.getEnvio().getCustoComprador() 
				- venda.getDetalhesVenda().get(0).getTarifaVenda()
				- venda.getEnvio().getCustoVendedor() - venda.getEnvio().getCustoComprador()).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		icmsTotal.setValorTotalSeguro(new BigDecimal("0"));
		icmsTotal.setValorICMSDesonerado(new BigDecimal("0"));
		icmsTotal.setValorICMSFundoCombatePobreza(new BigDecimal("0"));
		icmsTotal.setValorICMSPartilhaDestinatario(new BigDecimal("0"));
		icmsTotal.setValorICMSPartilhaRementente(new BigDecimal("0"));
		icmsTotal.setValorTotalTributos(new BigDecimal((venda.getPagamentos().get(0).getValorTransacao() + venda.getEnvio().getCustoComprador() 
				- venda.getDetalhesVenda().get(0).getTarifaVenda()
				- venda.getEnvio().getCustoVendedor() - venda.getEnvio().getCustoComprador()) 
				* Double.parseDouble(userNfeConfig.getTxSimplesNacional())).setScale(2, BigDecimal.ROUND_HALF_EVEN));
		return icmsTotal;
	}

	public NFNotaInfoTransporte getNFNotaInfoTransporte() {
		final NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();
		//transporte.setIcmsTransporte(FabricaDeObjetosFake.getNFNotaInfoRetencaoICMSTransporte());
		transporte.setModalidadeFrete(NFModalidadeFrete.POR_CONTA_DE_TERCEIROS);
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
		transportador.setUf(NFUnidadeFederativa.SP);
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

	public NFNotaInfoInformacoesAdicionais getNFNotaInfoInformacoesAdicionais() {
		final NFNotaInfoInformacoesAdicionais infoAdicionais = new NFNotaInfoInformacoesAdicionais();
		infoAdicionais.setInformacoesComplementaresInteresseContribuinte("I - DOCUMENTO EMITIDO POR ME OU EPP OPTANTE PELO SIMPLES NACIONAL; II - NÃO GERA DIREITO A CRÉDITO FISCAL DE ICMS, DE ISS E DE IPI.");

		return infoAdicionais;
	}

	public NFProtocolo getNotaProt(NFLoteConsultaRetorno retc, NfeConfig userNfeConfig) {

		final NFProtocolo protocolo = new NFProtocolo();

		protocolo.setProtocoloInfo(getNFProtocoloInfo(retc, userNfeConfig));
		protocolo.setVersao("3.10");
		return protocolo;
	}

	public NFProtocoloInfo getNFProtocoloInfo(NFLoteConsultaRetorno retc, NfeConfig userNfeConfig) {
		final NFProtocoloInfo info = new NFProtocoloInfo();
		info.setAmbiente(NFAmbiente.valueOfCodigo(userNfeConfig.getIndAmbiente()));

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
