package br.com.mpconnect.converter;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.Pessoa;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.PersonRepository;

@ManagedBean(name="pessoaConverter")
@RequestScoped
@Component
public class PessoaConverter implements Converter {
	
	@Autowired
	private PersonRepository personRepository;
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
        		Optional<Pessoa> result = personRepository.findById(new Long(value));
				return result.isPresent()? result.get():null;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return new Produto();
    }

    @Override
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
