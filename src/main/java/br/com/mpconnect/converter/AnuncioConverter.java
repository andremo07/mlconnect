package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.AnuncioDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.Anuncio;

@ManagedBean(name="anuncioConverter")
@RequestScoped
@Component
public class AnuncioConverter implements Converter {
	
	@Autowired
	private AnuncioDao anuncioDao;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
				return (Anuncio) anuncioDao.recuperaUm(new Long(value));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return new Anuncio();
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof Anuncio) {
        	Anuncio anuncio= (Anuncio) value;
            if (anuncio != null && anuncio instanceof Anuncio && anuncio.getId() != null) {
                uiComponent.getAttributes().put( anuncio.getId().toString(), anuncio);
                return anuncio.getId().toString();
            }
        }
        return "";
    }

}
