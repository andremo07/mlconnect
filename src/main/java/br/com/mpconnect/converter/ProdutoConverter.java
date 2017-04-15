package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.ProdutoDao;
import br.com.mpconnect.model.Produto;

@ManagedBean(name="produtoConverter")
@RequestScoped
@Component
public class ProdutoConverter implements Converter {
	
	@Autowired
	private ProdutoDao produtoDao;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
				return (Produto) produtoDao.recuperaUm(new Long(value));
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
        if (value instanceof Produto) {
        	Produto produto= (Produto) value;
            if (produto != null && produto instanceof Produto && produto.getId() != null) {
                uiComponent.getAttributes().put( produto.getId().toString(), produto);
                return produto.getId().toString();
            }
        }
        return "";
    }

}
