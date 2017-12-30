package br.com.mpconnect.provider;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.assinatura.AssinaturaDigital;
import com.fincatto.nfe310.classes.NFModelo;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.NFNota;
import com.fincatto.nfe310.classes.nota.NFNotaInfo;
import com.fincatto.nfe310.parsers.NotaParser;
import com.fincatto.nfe310.utils.NFGeraChave;
import com.fincatto.nfe310.webservices.WSFacade;

import br.com.mpconnect.holder.NfeConfigurationHolder;
import br.com.mpconnect.model.Venda;
import br.com.mpconnect.provider.exception.NfeProviderException;

@Service
@DependsOn("nfeConfHolder")
public class NFeProvider {

	private NFeConfig config;

	@PostConstruct
	public void setUp(){		
		config = NfeConfigurationHolder.getInstance();		
	}	

	public String gerarNFe(Venda venda) throws NfeProviderException{
		NFNota nota = new NFNota();
		NFGeraChave ch = new NFGeraChave(nota);
		nota.setInfo(getNFeInfo(ch,venda));
		assinarNFe(nota);
		NFLoteConsultaRetorno retc = consultarLoteNFe(gerarLoteEnvioNfe(nota));
		return null;
	}

	public NFNotaInfo getNFeInfo(NFGeraChave ch,Venda venda){
		NFNotaInfo nfeInfo = new NFNotaInfo();
		nfeInfo.setIdentificador(ch.getChaveAcesso());
		//SET IDENTIFIÇÃO

		//
		nfeInfo.getIdentificacao().setDigitoVerificador(ch.getDV());
		return nfeInfo;
	}

	public NFLoteEnvioRetorno gerarLoteEnvioNfe(List<NFNota> notas) throws NfeProviderException{
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

	public NFLoteEnvioRetorno gerarLoteEnvioNfe(NFNota nota) throws NfeProviderException{
		try {
			final NFLoteEnvio loteEnvio = new NFLoteEnvio();
			loteEnvio.setIdLote("333972757970401");
			loteEnvio.setVersao("3.10");
			loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
			loteEnvio.setNotas(Collections.singletonList(nota));
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

	public void assinarNFe(NFNota nota) throws NfeProviderException{
		try {
			AssinaturaDigital asd = new AssinaturaDigital(config);
			String strgNotaAssinada = asd.assinarDocumento(nota.toString());
			NotaParser np = new NotaParser();
			nota = np.notaParaObjeto(strgNotaAssinada);
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

}
