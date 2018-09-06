package br.com.mpconnect.converter;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.Produto;
import br.com.trendsoftware.markethub.repository.ProductRepository;

@ManagedBean(name="produtoConverter")
@RequestScoped
@Component
public class ProdutoConverter implements Converter {
	
	@Autowired
	private ProductRepository productRepository;
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {
        		Optional<Produto> result = productRepository.findById(new Long(value));
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
