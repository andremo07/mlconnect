package br.com.mpconnect.nfe.generator;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Collections;

import com.fincatto.nfe310.assinatura.AssinaturaDigital;
import com.fincatto.nfe310.classes.NFModelo;
import com.fincatto.nfe310.classes.NFProtocolo;
import com.fincatto.nfe310.classes.lote.consulta.NFLoteConsultaRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvio;
import com.fincatto.nfe310.classes.lote.envio.NFLoteEnvioRetorno;
import com.fincatto.nfe310.classes.lote.envio.NFLoteIndicadorProcessamento;
import com.fincatto.nfe310.classes.nota.NFNota;
import com.fincatto.nfe310.parsers.NotaParser;
import com.fincatto.nfe310.utils.NFGeraChave;
import com.fincatto.nfe310.webservices.WSFacade;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws KeyManagementException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, Exception
    {
        //System.out.println( "Hello World!" );
    	
    	
//    	try {
//            FileUtils.writeByteArrayToFile(new File("producao.cacerts"), NFGeraCadeiaCertificados.geraCadeiaCertificados(NFAmbiente.PRODUCAO, "123456"));
//            FileUtils.writeByteArrayToFile(new File("homologacao.cacerts"), NFGeraCadeiaCertificados.geraCadeiaCertificados(NFAmbiente.HOMOLOGACAO, "123456"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    	
      
    	//final byte[] cadeia = NFGeraCadeiaCertificados.geraCadeiaCertificados(NFAmbiente.HOMOLOGACAO, "123456");
    	//Assert.assertTrue(cadeia.length > 0);
    	
    	// Gerar XML da nota de entrada.
    	/*
    	NFNota nota = new NFNota();
    	nota.setInfo(GerarNotaEntrada.getNFNotaInfo());
    	
    	final NFSignature assinatura = new NFSignature();
        nota.setAssinatura(assinatura);
        
    	
    	//nota.setAssinatura(new NFSignature());
    	//nota.getAssinatura().setSignedInfo(new NFSignedInfo());
    	//nota.getAssinatura().getSignedInfo().setCanonicalizationMethod(new NFCanonicalizationMethod());
    	
        //nota.getAssinatura().getSignedInfo().setReference(new NFReference());
        //nota.getAssinatura().getSignedInfo().getReference().setDigestValue("yzGYhUx1/XYYzksWB+fPR3Qc50c=");
    	
    	System.out.println(nota.toString());
    	*/
    	// fim XML da nota de entrada
    	
    	
    	
    	NFeConfigTeste config = new NFeConfigTeste();
//    	
//    	NFStatusServicoConsultaRetorno retorno2 = new WSFacade(config).consultaStatus(NFUnidadeFederativa.RJ, NFModelo.NFE);
//        System.out.println(retorno2.getStatus());
//        System.out.println(retorno2.getMotivo());
    	
    	NFNota nota = new NFNota();
    	
//    	NFNotaInfo nfNotaInfo = new NFNotaInfo();
    	
    	
//    	nota.setInfo(nfNotaInfo);
    	nota.setInfo(GerarNotaConsumidor.getNFNotaInfo());
    	
    	NFGeraChave ch = new NFGeraChave(nota);
    	
    	nota.getInfo().setIdentificador(ch.getChaveAcesso());
    	nota.getInfo().getIdentificacao().setDigitoVerificador(ch.getDV());
    	
    	AssinaturaDigital asd = new AssinaturaDigital(config);
    	
    	String notaAss = asd.assinarDocumento(nota.toString());
    	
    	System.out.println(notaAss);
    	
    	NotaParser np = new NotaParser();
    	nota = np.notaParaObjeto(notaAss);
    	
    	final NFLoteEnvio loteEnvio = new NFLoteEnvio();
        loteEnvio.setIdLote("333972757970401");
        loteEnvio.setVersao("3.10");
        loteEnvio.setNotas(Collections.singletonList(nota));
        loteEnvio.setIndicadorProcessamento(NFLoteIndicadorProcessamento.PROCESSAMENTO_ASSINCRONO);
    	
        //JasperPrint js = new 



        
        
    	NFLoteEnvioRetorno retorno = new WSFacade(config).enviaLoteAssinado(loteEnvio.toString(), NFModelo.NFE);
    	//NFStatusServicoConsultaRetorno retorno = new WSFacade(config).consultaStatus(NFUnidadeFederativa.RJ, NFModelo.NFE);
    	System.out.println(retorno.getStatus());
    	System.out.println(retorno.getMotivo());
    	System.out.println(retorno.getInfoRecebimento());
    	System.out.println(retorno.toString());
    	
    	System.out.println();System.out.println();
    	
    	NFLoteConsultaRetorno retc = new WSFacade(config).consultaLote(retorno.getInfoRecebimento().getRecibo(), NFModelo.NFE);
    	for (NFProtocolo prot : retc.getProtocolos()) {
    	    System.out.println("Chave: "+prot.getProtocoloInfo().getChave());
    	    System.out.println("Número Protocolo: "+prot.getProtocoloInfo().getNumeroProtocolo());
    	    System.out.println("Status: "+prot.getProtocoloInfo().getStatus());
    	    System.out.println("Motivo: "+prot.getProtocoloInfo().getMotivo());
    	}
    	
    }
}
