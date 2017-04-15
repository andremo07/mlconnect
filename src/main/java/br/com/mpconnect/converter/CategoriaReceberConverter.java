package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.CategoriaContaReceberDao;
import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.Produto;

@ManagedBean(name="categoriaReceberConverter")
@RequestScoped
@Component
public class CategoriaReceberConverter implements Converter {
	
	@Autowired
	private CategoriaContaReceberDao categoriaContaReceberDao;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
				return (CategoriaContaReceber) categoriaContaReceberDao.recuperaUm(new Long(value));
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
        if (value instanceof CategoriaContaReceber) {
        	CategoriaContaReceber categoriaContaReceber= (CategoriaContaReceber) value;
            if (categoriaContaReceber != null && categoriaContaReceber instanceof CategoriaContaReceber && categoriaContaReceber.getId() != null) {
                uiComponent.getAttributes().put( categoriaContaReceber.getId().toString(), categoriaContaReceber);
                return categoriaContaReceber.getId().toString();
            }
        }
        return "";
    }

}
