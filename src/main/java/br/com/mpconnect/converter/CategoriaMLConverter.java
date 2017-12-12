package br.com.mpconnect.converter;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.codehaus.jettison.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.ml.api.ApiCategorias;
import br.com.mpconnect.ml.dto.CategoriaML;

import com.mercadolibre.sdk.MeliException;

@ManagedBean(name="categoriaMLConverter")
@RequestScoped
@Component
public class CategoriaMLConverter implements Converter {

	@Autowired
	private ApiCategorias apiCategorias;

	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
		if (value != null && !value.isEmpty()) {
			try {
				return (CategoriaML) apiCategorias.retornaCategoria(value);
			} catch (MeliException | JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new CategoriaML();
	}

	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
		if (value instanceof CategoriaML) {
			CategoriaML categoriaMl= (CategoriaML) value;
			if (categoriaMl != null && categoriaMl instanceof CategoriaML && categoriaMl.getId() != null) {
				uiComponent.getAttributes().put( categoriaMl.getId().toString(), categoriaMl);
				return categoriaMl.getId().toString();
			}
		}
		return "";
	}

}
