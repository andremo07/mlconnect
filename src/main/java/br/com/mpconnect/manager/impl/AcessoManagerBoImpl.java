package br.com.mpconnect.manager.impl;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONException;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.manager.AcessoManagerBo;
import br.com.mpconnect.model.AcessoMl;

import com.mercadolibre.sdk.Meli;

@Service("acessoManager")
//@Scope("session")
public class AcessoManagerBoImpl implements AcessoManagerBo, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7996634029900417026L;

	@Resource
	public AcessoMlDao acessoDao;
	
    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager txManager;
	
	private Meli meli;
	
	@PostConstruct
	public void init(){
		
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
            	conectarMl();
            }
        });
	}
	
	@Transactional
	private void conectarMl() {
		
		try {
			
			AcessoMl acessoMl = acessoDao.recuperarUltimo();
			String urlFinal ="https://api.mercadolibre.com/oauth/token?grant_type=authorization_code&client_id="+acessoMl.getClientId()+"&client_secret="+acessoMl.getClientSecret()+"&code=TG-580e8032e4b05f92b0244a15-146216892&redirect_uri=https://gestao.trendstore.com.br";
			CloseableHttpClient client = HttpClients.createDefault();
			HttpPost post = new HttpPost(urlFinal);

			post.setHeader("User-Agent", "Mozilla/5.0");
			post.setHeader("Accept",
					"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			post.setHeader("Accept-Language", "en-US,en;q=0.5");

			HttpResponse response = client.execute(post);
			//int responseCode = response.getStatusLine().getStatusCode();
			//System.out.println("\nSending 'POST' request to URL : " + url);
			//System.out.println("Response Code : " + responseCode);
					
			String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
			
			JSONObject jsonObject = new JSONObject(jsonResponse);
			
			if(jsonObject.has("status") && jsonObject.getInt("status")==400){
				String refreshTokenUrl = "https://api.mercadolibre.com/oauth/token?grant_type=refresh_token&client_id=4013368235398167&client_secret="+acessoMl.getClientSecret()+"&refresh_token="+acessoMl.getRefreshToken();
				post = new HttpPost(refreshTokenUrl);
				response = client.execute(post);
				//responseCode = response.getStatusLine().getStatusCode();				
				jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
				jsonObject = new JSONObject(jsonResponse);
			}
					
			String accessToken = jsonObject.getString("access_token");
			String refreshToken = jsonObject.getString("refresh_token");
			acessoMl.setAccessToken(accessToken);
			acessoMl.setRefreshToken(refreshToken);
			acessoDao.alterar(acessoMl);
			
			meli = new Meli(acessoMl.getClientId(),acessoMl.getClientSecret() , acessoMl.getAccessToken());
		
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Meli getAcessoMl() {
		return meli;
	}

	public AcessoMlDao getAcessoDao() {
		return acessoDao;
	}

	public void setAcessoDao(AcessoMlDao acessoDao) {
		this.acessoDao = acessoDao;
	}

}
