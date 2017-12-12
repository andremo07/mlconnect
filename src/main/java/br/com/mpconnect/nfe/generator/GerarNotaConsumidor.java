package br.com.mpconnect.nfe.generator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.fincatto.nfe310.classes.NFRegimeTributario;
import com.fincatto.nfe310.classes.NFTipo;
import com.fincatto.nfe310.classes.NFTipoEmissao;
import com.fincatto.nfe310.classes.NFTipoImpressao;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;
import com.fincatto.nfe310.classes.nota.NFFormaImportacaoIntermediacao;
import com.fincatto.nfe310.classes.nota.NFIdentificadorLocalDestinoOperacao;
import com.fincatto.nfe310.classes.nota.NFIndicadorIEDestinatario;
import com.fincatto.nfe310.classes.nota.NFIndicadorPresencaComprador;
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
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemProdutoDeclaracaoImportacao;
import com.fincatto.nfe310.classes.nota.NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTotal;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTransportador;
import com.fincatto.nfe310.classes.nota.NFNotaInfoTransporte;
import com.fincatto.nfe310.classes.nota.NFNotaInfoVolume;
import com.fincatto.nfe310.classes.nota.NFOperacaoConsumidorFinal;
import com.fincatto.nfe310.classes.nota.NFViaTransporteInternacional;

import br.com.mpconnect.manager.VendaManagerBo;

public class GerarNotaConsumidor {
	
	@Autowired
	private VendaManagerBo vendaManager;
	
	public NFNotaInfo getNFNotaInfo() {
        final NFNotaInfo info = new NFNotaInfo();
        
        //vendaManager.carregaVendasRecentes();
        
        //info.setIdentificador("89172658591754401086218048846976493475937081");
        info.setVersao(new BigDecimal("3.10"));
        
        info.setIdentificacao(GerarNotaConsumidor.getNFNotaInfoIdentificacao());
        info.setEmitente(GerarNotaConsumidor.getNFNotaInfoEmitente());
        info.setDestinatario(GerarNotaConsumidor.getNFNotaInfoDestinatario());
        info.setItens(Collections.singletonList(GerarNotaConsumidor.getNFNotaInfoItem()));
        info.setTotal(GerarNotaConsumidor.getNFNotaInfoTotal());
        info.setTransporte(GerarNotaConsumidor.getNFNotaInfoTransporte());
        
        info.setInformacoesAdicionais(GerarNotaConsumidor.getNFNotaInfoInformacoesAdicionais());
        
        
        
        return info;
    }
	
	// Registro <ide>
	public static NFNotaInfoIdentificacao getNFNotaInfoIdentificacao() {
        final NFNotaInfoIdentificacao identificacao = new NFNotaInfoIdentificacao();
        identificacao.setAmbiente(NFAmbiente.HOMOLOGACAO);
        identificacao.setCodigoMunicipio("3304557");
        identificacao.setCodigoRandomico("45000050");
        identificacao.setDataHoraEmissao(new DateTime());
        identificacao.setDataHoraSaidaOuEntrada(new DateTime());
        //identificacao.setDigitoVerificador(Integer.valueOf(8));
        identificacao.setFinalidade(NFFinalidade.NORMAL);
        identificacao.setFormaPagamento(NFFormaPagamentoPrazo.A_VISTA);
        identificacao.setModelo(NFModelo.NFE);
        identificacao.setNaturezaOperacao("Venda de Mercadoria");
        identificacao.setNumeroNota("38");
        identificacao.setProgramaEmissor(NFProcessoEmissor.CONTRIBUINTE);
        identificacao.setSerie("1");
        identificacao.setTipo(NFTipo.SAIDA);
        identificacao.setTipoEmissao(NFTipoEmissao.EMISSAO_NORMAL);
        identificacao.setTipoImpressao(NFTipoImpressao.DANFE_NORMAL_RETRATO);
        identificacao.setUf(NFUnidadeFederativa.RJ);
        identificacao.setVersaoEmissor("3.10");
        //identificacao.setDataHoraContigencia(new DateTime(2014, 10, 10, 10, 10, 10));
        //identificacao.setJustificativaEntradaContingencia("b1Aj7VBU5I0LDthlrWTk73otsFXSVbiNYyAgGZjLYT0pftpjhGzQEAtnolQoAEB3omnxNq8am4iMqwwviuaXRHjiYWY7YaPITlDN7cDN9obnhEqhDhkgKphRBY5frTfD6unwTB4w7j6hpY2zNNzWwbNJzPGgDmQ8WhBDnpq1fQOilrcDspY7SGkNDfjxpGTQyNSNsmF4B2uHHLhGhhxG2qVq2bFUvHFqSL8atQAuYpyn3wplW21v88N96PnF0MEV");
        identificacao.setIdentificadorLocalDestinoOperacao(NFIdentificadorLocalDestinoOperacao.OPERACAO_INTERNA);
        identificacao.setOperacaoConsumidorFinal(NFOperacaoConsumidorFinal.SIM);
        identificacao.setIndicadorPresencaComprador(NFIndicadorPresencaComprador.OPERACAO_NAO_PRESENCIAL_INTERNET);
        return identificacao;
    }
	
	// Registro <emit>
	public static NFNotaInfoEmitente getNFNotaInfoEmitente() {
        final NFNotaInfoEmitente emitente = new NFNotaInfoEmitente();
        emitente.setClassificacaoNacionalAtividadesEconomicas("4757100");
        emitente.setCnpj("10366939000128");
        emitente.setEndereco(GerarNotaConsumidor.getNFEndereco());
        emitente.setInscricaoEstadual("79089710");
        emitente.setInscricaoMunicipal("06215769");
        //emitente.setNomeFantasia("Trend Store");
        emitente.setRazaoSocial("TREND SOLU«’ES PARA TECNOLOGIA DA INFORMA«√O LTDA.");
        emitente.setRegimeTributario(NFRegimeTributario.SIMPLES_NACIONAL);
        return emitente;
    }
	
	// Registro <dest>
	public static NFNotaInfoDestinatario getNFNotaInfoDestinatario() {
        final NFNotaInfoDestinatario destinatario = new NFNotaInfoDestinatario();
        //destinatario.setIdEstrangeiro("00000000000000000000");
        destinatario.setCpf("09132027702");
        destinatario.setRazaoSocial("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        //destinatario.setEmail("ivU3ctXKzImStrYzRpDTXRyCfSzxlEe5GTbeyVZ1OlIvgKGLJJMJlaKtYj8K");
        destinatario.setEndereco(GerarNotaConsumidor.getNFEnderecoDest());
        //destinatario.setInscricaoEstadual("13245678901234");
        //destinatario.setInscricaoSuframa("999999999");
        //destinatario.setRazaoSocial("SHENZHEN TENG BIN INDUSTRIAL CO. LTD");
        destinatario.setIndicadorIEDestinatario(NFIndicadorIEDestinatario.NAO_CONTRIBUINTE);
        //destinatario.setInscricaoMunicipal("5ow5E1mZQPe1VUR");
        return destinatario;
    }
	
	// Registro <det>
	public static NFNotaInfoItem getNFNotaInfoItem() {
        final NFNotaInfoItem item = new NFNotaInfoItem();
        item.setImposto(GerarNotaConsumidor.getNFNotaInfoItemImposto());
        item.setNumeroItem(Integer.valueOf(990));
        item.setProduto(GerarNotaConsumidor.getNFNotaInfoItemProduto());
        return item;
    }
	
	// Registro <total>
	public static NFNotaInfoTotal getNFNotaInfoTotal() {
        final NFNotaInfoTotal total = new NFNotaInfoTotal();
        total.setIcmsTotal(GerarNotaConsumidor.getNFNotaInfoICMSTotal());
        return total;
    }
	
	// Registro <transp>
	public static NFNotaInfoTransporte getNFNotaInfoTransporte() {
        final NFNotaInfoTransporte transporte = new NFNotaInfoTransporte();
        //transporte.setIcmsTransporte(FabricaDeObjetosFake.getNFNotaInfoRetencaoICMSTransporte());
        transporte.setModalidadeFrete(NFModalidadeFrete.POR_CONTA_DO_EMITENTE);
        //transporte.setReboques(Collections.singletonList(FabricaDeObjetosFake.getNFNotaInfoReboque()));
        transporte.setTransportador(GerarNotaConsumidor.getNFNotaInfoTransportador());
        transporte.setVolumes(Collections.singletonList(GerarNotaConsumidor.getNFNotaInfoVolume()));
        return transporte;
    }
	
	// Registro <infAdic>
	public static NFNotaInfoInformacoesAdicionais getNFNotaInfoInformacoesAdicionais() {
        final NFNotaInfoInformacoesAdicionais infoAdicionais = new NFNotaInfoInformacoesAdicionais();
        //infoAdicionais.setInformacoesAdicionaisInteresseFisco("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");
        infoAdicionais.setInformacoesComplementaresInteresseContribuinte("I - DOCUMENTO EMITIDO POR ME OU EPP OPTANTE PELO SIMPLES NACIONAL; II - N√O GERA DIREITO A CR…DITO FISCAL DE ICMS, DE ISS E DE IPI.");

        return infoAdicionais;
    }
	
	public static NFNotaInfoTransportador getNFNotaInfoTransportador() {
        final NFNotaInfoTransportador transportador = new NFNotaInfoTransportador();
        transportador.setCnpj("34843274000164");
        transportador.setEnderecoComplemento("D8nOWsHxI5K4RgYTUGwWgIKajhiUf4Q7aOOmaTV2wnYV0kQ5MezOjqfoPcNY");
        transportador.setInscricaoEstadual("ISENTO");
        transportador.setNomeMunicipio("4lb4Qv5yi9oYq7s8fF98a0EEv98oAxl0CIs5gzyKNVp1skE3IHD9Z7JbjHCn");
        transportador.setRazaoSocial("4lb4Qv5yi9oYq7s8fF98a0EEv98oAxl0CIs5gzyKNVp1skE3IHD9Z7JbjHCn");
        transportador.setUf(NFUnidadeFederativa.SP);
        return transportador;
    }
	
	public static NFNotaInfoVolume getNFNotaInfoVolume() {
        final NFNotaInfoVolume volume = new NFNotaInfoVolume();
        volume.setEspecieVolumesTransportados("3Qf46HFs7FcWlhuQqLJ96vsrgJHu6B5ZXmmwMZ1RtvQVOV4Yp6M9VNqn5Ecb");
        //final NFNotaInfoLacre notaInfoLacre = new NFNotaInfoLacre();
        //notaInfoLacre.setNumeroLacre("gvmjb9BB2cmwsLbzeR3Bsk8QbA7b1XEgXUhKeS9QZGiwhFnqDtEzS3377MP2");
        //volume.setLacres(Collections.singletonList(notaInfoLacre));
        //volume.setMarca("lc0w13Xw2PxsSD4u4q3N6Qix9ZuCFm0HXo6BxBmKnjVbh9Xwy3k9UwBNfuYo");
        //volume.setNumeracaoVolumesTransportados("mcBUtZwnI5DKj2YZNAcLP7W9h6j1xKmF5SX1BTKmsvyg0H5xSrfVw8HGn8eb");
        volume.setPesoBruto(new BigDecimal("1.358"));
        volume.setPesoLiquido(new BigDecimal("1"));
        volume.setQuantidadeVolumesTransportados(new BigInteger("99999999999"));
        return volume;
    }
	
	public static NFNotaInfoICMSTotal getNFNotaInfoICMSTotal() {
        final NFNotaInfoICMSTotal icmsTotal = new NFNotaInfoICMSTotal();
        icmsTotal.setBaseCalculoICMS(new BigDecimal("0"));
        icmsTotal.setOutrasDespesasAcessorias(new BigDecimal("0"));
        icmsTotal.setBaseCalculoICMSST(new BigDecimal("0"));
        icmsTotal.setValorCOFINS(new BigDecimal("0"));
        icmsTotal.setValorPIS(new BigDecimal("0"));
        icmsTotal.setValorTotalDesconto(new BigDecimal("0"));
        icmsTotal.setValorTotalDosProdutosServicos(new BigDecimal("199.90"));
        icmsTotal.setValorTotalFrete(new BigDecimal("0"));
        icmsTotal.setValorTotalICMS(new BigDecimal("0"));
        icmsTotal.setValorTotalICMSST(new BigDecimal("0"));
        icmsTotal.setValorTotalII(new BigDecimal("0"));
        icmsTotal.setValorTotalIPI(new BigDecimal("0"));
        icmsTotal.setValorTotalNFe(new BigDecimal("199.90"));
        icmsTotal.setValorTotalSeguro(new BigDecimal("0"));
        icmsTotal.setValorICMSDesonerado(new BigDecimal("0"));
        icmsTotal.setValorICMSFundoCombatePobreza(new BigDecimal("0"));
        icmsTotal.setValorICMSPartilhaDestinatario(new BigDecimal("0"));
        icmsTotal.setValorICMSPartilhaRementente(new BigDecimal("0"));
        icmsTotal.setValorTotalTributos(new BigDecimal("10.93"));
        return icmsTotal;
    }
	
	public static NFNotaInfoItemProduto getNFNotaInfoItemProduto() {
        final NFNotaInfoItemProduto produto = new NFNotaInfoItemProduto();
        produto.setCfop("5104");
        produto.setCodigo("770310173");
        produto.setCodigoDeBarras("");
        produto.setCodigoDeBarrasTributavel("");
        produto.setCampoeValorNota(NFProdutoCompoeValorNota.SIM);
        //produto.setDeclaracoesImportacao(Collections.singletonList(GerarNotaConsumidor.getNFNotaInfoItemProdutoDeclaracaoImportacao()));
        produto.setDescricao("OBS0ztekCoG0DSSVcQwPKRV2fV842Pye7mED13P4zoDczcXi4AMNvQ7BKBLnHtLc2Z9fuIY1pcKmXSK1IJQSLEs5QWvVGyC74DyJuIM0X7L0cqWPZQii5JtP");
        //produto.setExtipi("999");
        //produto.setCodigoEspecificadorSituacaoTributaria("9999999");
        //produto.setMedicamentos(Collections.singletonList(FabricaDeObjetosFake.getNFNotaInfoItemProdutoMedicamento()));
        produto.setNcm("39269090");
        //produto.setNumeroPedidoCliente("NNxQ9nrQ3HCe5Mc");
        //produto.setNumeroPedidoItemCliente(999999);
        produto.setQuantidadeComercial(new BigDecimal("1.0000"));
        produto.setQuantidadeTributavel(new BigDecimal("1.0000"));
        produto.setUnidadeComercial("UN");
        produto.setUnidadeTributavel("UN");
        //produto.setValorDesconto(new BigDecimal("999999999999.99"));
        //produto.setValorFrete(new BigDecimal("999999999999.99"));
        //produto.setValorOutrasDespesasAcessorias(new BigDecimal("999999999999.99"));
        //produto.setValorSeguro(new BigDecimal("999999999999.99"));
        produto.setValorTotalBruto(new BigDecimal("199.90"));
        produto.setValorUnitario(new BigDecimal("199.9000000000"));
        //produto.setNomeclaturaValorAduaneiroEstatistica(Collections.singletonList("AZ0123"));
        produto.setValorUnitarioTributavel(new BigDecimal("199.9000000000"));
        return produto;
    }
	
	public static NFNotaInfoItemProdutoDeclaracaoImportacao getNFNotaInfoItemProdutoDeclaracaoImportacao() {
        final NFNotaInfoItemProdutoDeclaracaoImportacao declaraoImportacao = new NFNotaInfoItemProdutoDeclaracaoImportacao();
        declaraoImportacao.setAdicoes(Collections.singletonList(GerarNotaConsumidor.getNFNotaInfoItemProdutoDeclaracaoImportacaoAdicao()));
        declaraoImportacao.setCodigoExportador("SHENZHEN TENG BIN INDUSTRIAL CO. LTD");
        declaraoImportacao.setDataDesembaraco(new LocalDate(2017, 1, 2));
        declaraoImportacao.setDataRegistro(new LocalDate(2017, 1, 2));
        declaraoImportacao.setLocalDesembaraco("RIO DE JANEIRO/GALE√ÉO");
        declaraoImportacao.setNumeroRegistro("1619710783");
        declaraoImportacao.setUfDesembaraco(NFUnidadeFederativa.RJ);
        declaraoImportacao.setTransporteInternacional(NFViaTransporteInternacional.AEREA);
        //declaraoImportacao.setValorAFRMM(new BigDecimal("999999999999.99"));
        declaraoImportacao.setFormaImportacaoIntermediacao(NFFormaImportacaoIntermediacao.IMPORTACAO_CONTA_PROPRIA);
        //declaraoImportacao.setCnpj("12345678901234");
        //declaraoImportacao.setUfTerceiro(NFUnidadeFederativa.RS);
        return declaraoImportacao;
    }
	
	public static NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao getNFNotaInfoItemProdutoDeclaracaoImportacaoAdicao() {
        final NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao importacaoAdicao = new NFNotaInfoItemProdutoDeclaracaoImportacaoAdicao();
        importacaoAdicao.setCodigoFabricante("VIOFO LTD");
        //importacaoAdicao.setDesconto(new BigDecimal("999999999999.99"));
        importacaoAdicao.setNumero(Integer.valueOf(1));
        importacaoAdicao.setSequencial(Integer.valueOf(1));
        //importacaoAdicao.setNumeroAtoConcessorioDrawback(new BigInteger("99999999999"));
        return importacaoAdicao;
    }
	
	public static NFNotaInfoItemImposto getNFNotaInfoItemImposto() {
        final NFNotaInfoItemImposto imposto = new NFNotaInfoItemImposto();
        imposto.setCofins(GerarNotaConsumidor.getNFNotaInfoItemImpostoCOFINS());
        imposto.setIcms(GerarNotaConsumidor.getNFNotaInfoItemImpostoICMS());
        //imposto.setImpostoImportacao(GerarNotaConsumidor.getNFNotaInfoItemImpostoImportacao());
        //imposto.setIpi(GerarNotaConsumidor.getNFNotaInfoItemImpostoIPI());
        imposto.setPis(GerarNotaConsumidor.getNFNotaInfoItemImpostoPIS());
        imposto.setValorTotalTributos(new BigDecimal("10.93"));
        return imposto;
    }
	
	public static NFNotaInfoItemImpostoPIS getNFNotaInfoItemImpostoPIS() {
        final NFNotaInfoItemImpostoPIS pis = new NFNotaInfoItemImpostoPIS();
        //pis.setAliquota(GerarNotaConsumidor.getNFNotaInfoItemImpostoPISAliquota());
        pis.setOutrasOperacoes(getNFNotaInfoItemImpostoPISOutrasOperacoes());
        return pis;
    }
	
	public static NFNotaInfoItemImpostoPISOutrasOperacoes getNFNotaInfoItemImpostoPISOutrasOperacoes() {
        final NFNotaInfoItemImpostoPISOutrasOperacoes pisOutras = new NFNotaInfoItemImpostoPISOutrasOperacoes();
        
        pisOutras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaPIS.OUTRAS_OPERACOES);
        pisOutras.setQuantidadeVendida(new BigDecimal("0"));
        pisOutras.setValorAliquota(new BigDecimal("0.0000"));
        pisOutras.setValorTributo(new BigDecimal("0"));
        return pisOutras;
    }

	
	public static NFNotaInfoItemImpostoCOFINS getNFNotaInfoItemImpostoCOFINS() {
        final NFNotaInfoItemImpostoCOFINS cofins = new NFNotaInfoItemImpostoCOFINS();
        //cofins.setAliquota(GerarNotaConsumidor.getNFNotaInfoItemImpostoCOFINSAliquota());
        cofins.setOutrasOperacoes(GerarNotaConsumidor.getNFNotaInfoItemImpostoCOFINSOutrasOperacoes());
        return cofins;
    }
	
	public static NFNotaInfoItemImpostoCOFINSOutrasOperacoes getNFNotaInfoItemImpostoCOFINSOutrasOperacoes() {
        final NFNotaInfoItemImpostoCOFINSOutrasOperacoes cofinsOutras = new NFNotaInfoItemImpostoCOFINSOutrasOperacoes();
        cofinsOutras.setValorAliquota(new BigDecimal("0.0000"));
        cofinsOutras.setSituacaoTributaria(NFNotaInfoSituacaoTributariaCOFINS.OUTRAS_OPERACOES);
        cofinsOutras.setValorCOFINS(new BigDecimal("0"));
        cofinsOutras.setQuantidadeVendida(new BigDecimal("0"));
        return cofinsOutras;
    }
	
	public static NFNotaInfoItemImpostoICMS getNFNotaInfoItemImpostoICMS() {
        final NFNotaInfoItemImpostoICMS icms = new NFNotaInfoItemImpostoICMS();
        icms.setIcmssn102(GerarNotaConsumidor.getNFNotaInfoItemImpostoICMSSN102());
        return icms;
    }

    public static NFNotaInfoItemImpostoICMSSN102 getNFNotaInfoItemImpostoICMSSN102() {
        final NFNotaInfoItemImpostoICMSSN102 icmssn102 = new NFNotaInfoItemImpostoICMSSN102();
        icmssn102.setOrigem(NFOrigem.ESTRANGEIRA_IMPORTACAO_DIRETA);
        icmssn102.setSituacaoOperacaoSN(NFNotaSituacaoOperacionalSimplesNacional.TRIBUTADA_SEM_PERMISSAO_CREDITO);
        return icmssn102;
    }
	
	public static NFEndereco getNFEndereco() {
        final NFEndereco endereco = new NFEndereco();
        endereco.setBairro("Centro");
        endereco.setCep("20071004");
        endereco.setCodigoMunicipio("3304557");
        endereco.setCodigoPais("1058");
        endereco.setComplemento("sala 1312");
        endereco.setDescricaoMunicipio("Rio de Janeiro");
        endereco.setLogradouro("Av. Presidente Vargas");
        endereco.setNumero("633");
        endereco.setTelefone("2122242353");
        endereco.setUf(NFUnidadeFederativa.RJ);
        return endereco;
    }
	
	public static NFEndereco getNFEnderecoDest() {
		final NFEndereco endereco = new NFEndereco();
        endereco.setBairro("Pechincha");
        endereco.setCep("22740300");
        endereco.setCodigoMunicipio("3304557");
        endereco.setCodigoPais("1058");
        endereco.setComplemento("Ap 103 Bl 02");
        endereco.setDescricaoMunicipio("Rio de Janeiro");
        endereco.setLogradouro("Rua Ana Silva");
        endereco.setNumero("53");
        //endereco.setTelefone("12345678901324");
        endereco.setUf(NFUnidadeFederativa.RJ);
        return endereco;
    }
	
}
