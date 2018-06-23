package br.com.mpconnect.holder;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.fincatto.documentofiscal.DFAmbiente;
import com.fincatto.documentofiscal.DFUnidadeFederativa;
import com.fincatto.documentofiscal.nfe.NFeConfig;

public class NfeConfigurationHolder extends NFeConfig
{	

	private KeyStore keyStoreCertificado;
	
    private KeyStore keyStoreCadeia;
    
    private String certificadoSenha;
    
    private String cadeiaCertificadosSenha;
	
	private static NfeConfigurationHolder instance;
	
	public NfeConfigurationHolder(String certificadoSenha, String cadeiaCertificadosSenha) {
		this.certificadoSenha = certificadoSenha;
		this.cadeiaCertificadosSenha = cadeiaCertificadosSenha;
	}
	
	public static NfeConfigurationHolder getInstance() {
		return instance;
	}

	public static NfeConfigurationHolder setInstance(String certificadoSenha, String cadeiaCertificadosSenha) {
		NfeConfigurationHolder.instance = new NfeConfigurationHolder(certificadoSenha,cadeiaCertificadosSenha);
		return NfeConfigurationHolder.instance;
	}
	
	@Override
	public DFUnidadeFederativa getCUF() {

		return DFUnidadeFederativa.RJ;
	}

	@Override
	public KeyStore getCadeiaCertificadosKeyStore() throws KeyStoreException {
		if (this.keyStoreCadeia == null) {
            this.keyStoreCadeia = KeyStore.getInstance("JKS");
            try (InputStream cadeia = getClass().getResourceAsStream("/nfe/certificado.jks")) {
                this.keyStoreCadeia.load(cadeia, this.getCadeiaCertificadosSenha().toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                this.keyStoreCadeia = null;
                throw new KeyStoreException("Nao foi possibel montar o KeyStore com o certificado", e);
            }
        }
        return this.keyStoreCadeia;
	}

	@Override
	public KeyStore getCertificadoKeyStore() throws KeyStoreException {
		if (this.keyStoreCertificado == null) {
            this.keyStoreCertificado = KeyStore.getInstance("PKCS12");
            try (InputStream certificadoStream = getClass().getResourceAsStream("/nfe/certificado.pfx")) {
                this.keyStoreCertificado.load(certificadoStream, this.getCertificadoSenha().toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                this.keyStoreCadeia = null;
                throw new KeyStoreException("Nao foi possibel montar o KeyStore com a cadeia de certificados", e);
            }
        }
        return this.keyStoreCertificado;
        
	}
	
	@Override
	public String getCertificadoSenha() {
		return certificadoSenha;
	}

	@Override
	public String getCadeiaCertificadosSenha() {
		return cadeiaCertificadosSenha;
	}
	
	public void setCertificadoSenha(String certificadoSenha) {
		this.certificadoSenha = certificadoSenha;
	}

	public void setCadeiaCertificadosSenha(String cadeiaCertificadosSenha) {
		this.cadeiaCertificadosSenha = cadeiaCertificadosSenha;
	}
	
	@Override
    public DFAmbiente getAmbiente() {
        return DFAmbiente.HOMOLOGACAO;
    }
	
}
