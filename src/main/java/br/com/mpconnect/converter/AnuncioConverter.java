package br.com.mpconnect.converter;

import java.util.Optional;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.model.Anuncio;
import br.com.trendsoftware.markethub.repository.AdRepository;

@ManagedBean(name="anuncioConverter")
@RequestScoped
@Component
public class AnuncioConverter implements Converter {
	
	@Autowired
	private AdRepository adRepository;
	
    @Override
	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	try {     		
        		Optional<Anuncio> result = adRepository.findById(new Long(value));
				return result.isPresent()? result.get():null;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return new Anuncio();
    }

    @Override
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
