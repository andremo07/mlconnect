package br.com.mpconnect.ml.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import br.com.mpconnect.ml.data.CategoriaML;

import com.mercadolibre.sdk.MeliException;
import com.ning.http.client.Response;

@Service
public class ApiCategorias extends ApiMl {
	
	public List<CategoriaML> retornaCategoriasPrincipais() throws MeliException, JSONException, IOException{
		
		Response meliResponse = this.getMe().get("/sites/MLB/categories");
		JSONArray arrayCategorias = new JSONArray(meliResponse.getResponseBody());
		List<CategoriaML> categorias = parseJsonArrayToArrayCategorias(arrayCategorias);
		return categorias;
	}
	
	
	public List<CategoriaML> retornaSubCategorias(String idCategoria) throws MeliException, JSONException, IOException{
		
		Response meliResponse = this.getMe().get("/categories/"+idCategoria);
		JSONObject jsonObj = new JSONObject(meliResponse.getResponseBody());
		JSONArray arrayCategorias = jsonObj.getJSONArray("children_categories");
		List<CategoriaML> categorias = parseJsonArrayToArrayCategorias(arrayCategorias);
		return categorias;
	}
	
	
	public CategoriaML retornaCategoria(String idCategoria) throws MeliException, JSONException, IOException{
		
		Response meliResponse = this.getMe().get("/categories/"+idCategoria);
		JSONObject jsonObj = new JSONObject(meliResponse.getResponseBody());
		CategoriaML categoria = parseJsonToCategoriaML(jsonObj);
		return categoria;
		
	}
	
	public CategoriaML parseJsonToCategoriaML(JSONObject jo) throws JSONException{
		CategoriaML categoria = new CategoriaML();
		String id = jo.getString("id");
		String nome = jo.getString("name");
		categoria.setId(id);
		categoria.setName(nome);
		if(jo.has("path_from_root")){
			JSONArray niveis = jo.getJSONArray("path_from_root");
			categoria.setNivel(niveis.length());
		}
		return categoria;
	}
	
	private List<CategoriaML> parseJsonArrayToArrayCategorias(JSONArray array) throws JSONException{
		List<CategoriaML> categorias = new ArrayList<CategoriaML>();
		for (int index=0;index < array.length();index++){
			JSONObject jsonObj = array.getJSONObject(index);
			CategoriaML categoria = parseJsonToCategoriaML(jsonObj);
			categorias.add(categoria);
		}	
		return categorias;
	}
	
}
