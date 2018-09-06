package br.com.mpconnect.converter;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.CategoriaContaReceber;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.ReceivingBillCategoryRepository;

@ManagedBean(name="categoriaReceberConverter")
@RequestScoped
@Component
public class CategoriaReceberConverter implements Converter {
	
	@Autowired
	private ReceivingBillCategoryRepository receivingBillCategoryRepository;
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
        		Optional<CategoriaContaReceber> result = receivingBillCategoryRepository.findById(new Long(value));
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
