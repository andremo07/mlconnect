package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.dao.DaoException;
import br.com.mpconnect.dao.PessoaDao;
import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.Produto;

@ManagedBean(name="pessoaConverter")
@RequestScoped
@Component
public class PessoaConverter implements Converter {
	
	@Autowired
	private PessoaDao pessoaDao;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
				return (Pessoa) pessoaDao.recuperaUm(new Long(value));
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
        if (value instanceof Pessoa) {
        	Pessoa pessoa= (Pessoa) value;
            if (pessoa != null && pessoa instanceof Pessoa && pessoa.getId() != null) {
                uiComponent.getAttributes().put( pessoa.getId().toString(), pessoa);
                return pessoa.getId().toString();
            }
        }
        return "";
    }

}
