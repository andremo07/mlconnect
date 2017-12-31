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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mercadolibre.sdk.Meli;

import br.com.mpconnect.dao.AcessoMlDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.manager.AcessoManagerBo;
import br.com.mpconnect.model.AcessoMl;

@Service("acessoManager")
//@Scope("session")
public class AcessoManagerBoImpl implements AcessoManagerBo, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7996634029900417026L;

	@Resource
	public AcessoMlDao acessoDao;

	@PostConstruct
	public void init(){

//		TransactionTemplate tmpl = new TransactionTemplate(txManager);
//		tmpl.execute(new TransactionCallbackWithoutResult() {
//			@Override
//			protected void doInTransactionWithoutResult(TransactionStatus status) {
//				conectarMl();
//			}
//		});
	}

	@Override
	@Transactional
	public Meli conectarMl() {

		try {

			AcessoMl acessoMl = acessoDao.recuperarUltimo();
			CloseableHttpClient client = HttpClients.createDefault();
			String refreshTokenUrl = "https://api.mercadolibre.com/oauth/token?grant_type=refresh_token&client_id=4013368235398167&client_secret="+acessoMl.getClientSecret()+"&refresh_token="+acessoMl.getRefreshToken();
			HttpPost post = new HttpPost(refreshTokenUrl);
			HttpResponse response = client.execute(post);
			String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
			JSONObject jsonObject = new JSONObject(jsonResponse);

			String accessToken = jsonObject.getString("access_token");
			String refreshToken = jsonObject.getString("refresh_token");
			acessoMl.setAccessToken(accessToken);
			acessoMl.setRefreshToken(refreshToken);
			acessoDao.alterar(acessoMl);
			Meli meli = new Meli(acessoMl.getClientId(),acessoMl.getClientSecret() , acessoMl.getAccessToken(),acessoMl.getRefreshToken());
			return meli;
		} catch (DaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	@Transactional
	public AcessoMl recuperarUltimoAcesso(){
		try {
			return acessoDao.recuperarUltimo();
		} catch (DaoException e) {
			return null;
		}
	}

	public AcessoMlDao getAcessoDao() {
		return acessoDao;
	}

	public void setAcessoDao(AcessoMlDao acessoDao) {
		this.acessoDao = acessoDao;
	}

}
