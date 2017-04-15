package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import br.com.mpconnect.ml.data.AnuncioML;
import br.com.mpconnect.ml.data.TipoAnuncioML;
import br.com.mpconnect.ml.data.UsuarioML;
import br.com.mpconnect.utils.DateUtils;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

@Service
public class ApiUsuario extends ApiMl{

	public ApiUsuario(){


	}

	public String getIdUsuarioLogado(){

		try {
			////myfeeds?app_id={App_id}
			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", this.getMe().getAccessToken());
			Response meliResponse = this.getMe().get("/users/me",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			String id = jsonObject.getString("id");
			return id;	
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public String getFeeds(){

		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("app_id", "4013368235398167");
			Response meliResponse = this.getMe().get("/myfeeds",params);
			JSONArray jsonArray = new JSONArray(meliResponse.getResponseBody());
			for(int index=0; index<jsonArray.length(); index++){
				JSONObject jsonObject = jsonArray.getJSONObject(index);;
				System.out.println();
			}
			return "";	
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	public UsuarioML getInformacoesUsuario(String idUsuario){

		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("access_token", this.getMe().getAccessToken());
			Response meliResponse = this.getMe().get("/users/"+idUsuario,params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			UsuarioML usuario = parseJson(jsonObject);
			return usuario;	
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public Integer recuperaTotalVisitasPorPeriodo(String idUsuario,Date dataInicio, Date dataFim){

		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("date_from", DateUtils.getDataFormatada(dataInicio));
			params.add("date_to", DateUtils.getDataFormatada(dataFim));
			Response meliResponse = this.getMe().get("/users/"+idUsuario+"/items_visits",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			Integer totalVisitas = jsonObject.getInt("total_visits");
			return totalVisitas;	
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Integer recuperaTotalVisitas(String idUsuario, Integer dias){

		try {
			FluentStringsMap params = new FluentStringsMap();
			params.add("last", "1");
			params.add("unit", "day");
			params.add("ending", DateUtils.getDataAnteriorString(dias-1));
			Response meliResponse = this.getMe().get("/users/"+idUsuario+"/items_visits/time_window",params);
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());
			Integer totalVisitas = jsonObject.getInt("total_visits");
			return totalVisitas;	
		} catch (MeliException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public UsuarioML parseJson(JSONObject jsonObj){

		try {

			String id = jsonObj.getString("id");
			String apelido = jsonObj.getString("nickname");
			String dataRegistro = null;

			if(jsonObj.has("registration_date"))
				dataRegistro = jsonObj.getString("registration_date");

			UsuarioML usuario = new UsuarioML();
			usuario.setId(id);
			usuario.setApelido(apelido);
			if(dataRegistro!=null)
				usuario.setDataCadastro(DateUtils.getDataFormatada(dataRegistro));
			return usuario;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}