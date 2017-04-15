package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import br.com.mpconnect.ml.api.enums.StatusEnvioMlEnum;
import br.com.mpconnect.ml.data.EnderecoML;
import br.com.mpconnect.ml.data.EnvioML;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.FluentStringsMap;
import com.ning.http.client.Response;

@Service("apiEnvios")
public class ApiEnvios extends ApiMl{

	private EnderecoML parseEndereco(JSONObject jsonObj){

		try {
			EnderecoML endereco = new EnderecoML();
			endereco.setId(jsonObj.getString("id"));
			endereco.setCep(jsonObj.getLong("zip_code"));
			endereco.setNumero(jsonObj.getString("street_number"));
			endereco.setNomeRua(jsonObj.getString("street_name"));
			endereco.setNomeEstado(jsonObj.getJSONObject("state").getString("name"));
			String idEstado = jsonObj.getJSONObject("state").getString("id");
			String ufEstado = idEstado.split("-")[1];
			endereco.setUfEstado(ufEstado);
			if(jsonObj.has("neighborhood")&&!jsonObj.getJSONObject("neighborhood").isNull("name"))
				endereco.setBairro(jsonObj.getJSONObject("neighborhood").getString("name"));
			else
				endereco.setBairro("");
			endereco.setCidade(jsonObj.getJSONObject("city").getString("name"));
			endereco.setPais(jsonObj.getJSONObject("country").getString("name"));
			endereco.setComentario(jsonObj.getString("comment"));
			return endereco;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public EnvioML parseJson(JSONObject jsonObj){

		try {
			EnvioML envio = new EnvioML();
			if(!jsonObj.getString("status").equals(StatusEnvioMlEnum.RETIRAR_EM_MAOS.getValue())){
				envio.setId(jsonObj.getString("id"));
				envio.setTipoEnvio(jsonObj.getString("shipment_type"));
				envio.setModoEnvio(jsonObj.getString("shipping_mode"));
				if(jsonObj.has("shipping_option")){
					envio.setMetodoEnvio(jsonObj.getJSONObject("shipping_option").getString("name"));
					EnderecoML endereco = parseEndereco(jsonObj.getJSONObject("receiver_address"));
					envio.setEndereco(endereco);
				}
			}
			return envio;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Double calculaValorEnvio(String idUsuario,String categoria){

		try {

			Response meliResponse = this.getMe().get("/categories/"+categoria+"/shipping");
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());

			String height = jsonObject.getString("height");
			String width = jsonObject.getString("width");
			String length = jsonObject.getString("length");
			String weight = jsonObject.getString("weight");

			FluentStringsMap params = new FluentStringsMap();
			params.add("dimensions", height+"x"+width+"x"+length+","+weight);

			meliResponse = this.getMe().get("/users/"+idUsuario+"/shipping_options/free",params);
			jsonObject = new JSONObject(meliResponse.getResponseBody());

			Double custoEnvio = jsonObject.getJSONObject("coverage").getJSONObject("all_country_except_exclusion_zone").getDouble("list_cost");
			return custoEnvio;

		}catch(MeliException e){
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

	public Double calculaValorEnvioPorCep(String idUsuario,String categoria, String cepComprador){

		try {

			Response meliResponse = this.getMe().get("/categories/"+categoria+"/shipping");
			JSONObject jsonObject = new JSONObject(meliResponse.getResponseBody());

			String height = jsonObject.getString("height");
			String width = jsonObject.getString("width");
			String length = jsonObject.getString("length");
			String weight = jsonObject.getString("weight");

			FluentStringsMap params = new FluentStringsMap();
			params.add("dimensions", height+"x"+width+"x"+length+","+weight);
			params.add("zip_code_from", "24210-145");
			params.add("zip_code_to", cepComprador);

			meliResponse = this.getMe().get("/sites/MLB/shipping_options",params);
			jsonObject = new JSONObject(meliResponse.getResponseBody());

			Double custoEnvio = jsonObject.getJSONObject("coverage").getJSONObject("all_country_except_exclusion_zone").getDouble("list_cost");
			return custoEnvio;

		}catch(MeliException e){
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

	public InputStream gerarEtiquetas(List<String> idsEnvios){

		try {

			FluentStringsMap params = new FluentStringsMap();
			params.add("shipment_ids", idsEnvios);
			//params.add("shipment_ids", "25836325361%2025820092634%2025836325223%2025832914335");
			params.add("response_type", "pdf");
			params.add("access_token", this.getMe().getAccessToken());
			JSONArray array = new JSONArray(idsEnvios);
			JSONObject jsonObj = new JSONObject();
			try {
				jsonObj.put("shipment_ids", array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Response meliResponse = this.getMe().get("/shipment_labels",params);
			//Response meliResponse = this.getMe().get("/shipment_labels?shipment_ids=25836325361,25820092634,25836325223,25832914335&savePdf=Y&access_token="+this.getMe().getAccessToken());
			InputStream input = meliResponse.getResponseBodyAsStream();
			return input;

		}catch(MeliException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public InputStream gerarEtiquetas2(List<String> idsEnvios){

		try {
			StringBuilder sb = new StringBuilder();
			if(!idsEnvios.isEmpty()){
				String prefix = "";
				for(String idEnvio: idsEnvios){
					sb.append(prefix);
					prefix = ",";
					sb.append(idEnvio);
				}
			}

			String apiUrl = "https://api.mercadolibre.com";
			String url = apiUrl+"/shipment_labels?shipment_ids="+sb+
					"&savePdf=Y&access_token="+this.getMe().getAccessToken();

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			if(con.getResponseMessage().equals("Not Found"))
				return null;
			return con.getInputStream();

		}catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
