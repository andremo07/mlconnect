package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.stereotype.Component;

import br.com.mpconnect.ml.api.enums.TipoAnuncioEnum;
import br.com.mpconnect.ml.dto.TipoAnuncioML;
import br.com.mpconnect.model.Produto;

@ManagedBean(name="tipoAnuncioConverter")
@RequestScoped
@Component
public class TipoAnuncioConverter implements Converter {
	
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
        		if(value.equals(TipoAnuncioEnum.ML_PREMIUM.getValue().getId()))
        			return TipoAnuncioEnum.ML_PREMIUM.getValue();
        		else
        			return TipoAnuncioEnum.ML_CLASSICO.getValue();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return new Produto();
    }

    @Override
	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof TipoAnuncioML) {
        	TipoAnuncioML tipo= (TipoAnuncioML) value;
            if (tipo != null && tipo instanceof TipoAnuncioML && tipo.getId() != null) {
                uiComponent.getAttributes().put( tipo.getId().toString(), tipo);
                return tipo.getId().toString();
            }
        }
        return "";
    }

}
