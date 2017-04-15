package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.OrigemDao;
import br.com.mpconnect.model.Origem;
import br.com.mpconnect.model.Produto;

@ManagedBean(name="origemConverter")
@RequestScoped
@Component
public class OrigemConverter implements Converter {
	
	@Autowired
	private OrigemDao origemDao;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
				return (Origem) origemDao.recuperaUm(new Long(value));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return new Produto();
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof Origem) {
        	Origem origem= (Origem) value;
            if (origem != null && origem instanceof Origem && origem.getId() != null) {
                uiComponent.getAttributes().put( origem.getId().toString(), origem);
                return origem.getId().toString();
            }
        }
        return "";
    }

}
