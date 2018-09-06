package br.com.mpconnect.converter;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.CategoriaContaPagar;
import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.PayingBillCategoryRepository;

@ManagedBean(name="categoriaPagarConverter")
@RequestScoped
@Component
public class CategoriaPagarConverter implements Converter {
	
	@Autowired
	private PayingBillCategoryRepository payingBillCategoryRepository;
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
        		Optional<CategoriaContaPagar> result = payingBillCategoryRepository.findById(new Long(value));
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
        if (value instanceof CategoriaContaPagar) {
        	CategoriaContaPagar categoriaContaPagar= (CategoriaContaPagar) value;
            if (categoriaContaPagar != null && categoriaContaPagar instanceof CategoriaContaPagar && categoriaContaPagar.getId() != null) {
                uiComponent.getAttributes().put( categoriaContaPagar.getId().toString(), categoriaContaPagar);
                return categoriaContaPagar.getId().toString();
            }
        }
        return "";
    }

}
