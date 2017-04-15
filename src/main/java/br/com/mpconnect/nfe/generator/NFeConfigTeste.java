package br.com.mpconnect.nfe.generator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.fincatto.nfe310.NFeConfig;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;

public class NFeConfigTeste extends NFeConfig {

	private KeyStore keyStoreCertificado = null;
    private KeyStore keyStoreCadeia = null;
	
	@Override
	public NFUnidadeFederativa getCUF() {

		return NFUnidadeFederativa.RJ;
	}

	@Override
	public KeyStore getCadeiaCertificadosKeyStore() throws KeyStoreException {

		if (this.keyStoreCadeia == null) {
            this.keyStoreCadeia = KeyStore.getInstance("JKS");
            try (InputStream cadeia = new FileInputStream("certificado.jks")) {
                this.keyStoreCadeia.load(cadeia, this.getCadeiaCertificadosSenha().toCharArray());
            } catch (CertificateException | NoSuchAlgorithmException | IOException e) {
                this.keyStoreCadeia = null;
                throw new KeyStoreException("Nao foi possibel montar o KeyStore com o certificado", e);
            }
        }
        return this.keyStoreCadeia;
	}

	@Override
	public String getCadeiaCertificadosSenha() {
		
		return "123456";
	}

	@Override
	public KeyStore getCertificadoKeyStore() throws KeyStoreException {
		
		if (this.keyStoreCertificado == null) {
            this.keyStoreCertificado = KeyStore.getInstance("PKCS12");
            try (InputStream certificadoStream = new FileInputStream("/resources/certificado.pfx")) {
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

		return "FPhone@2013soci";
	}

}
