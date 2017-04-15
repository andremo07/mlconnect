package br.com.mpconnect.converter;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.mpconnect.manager.CargoManagerBo;
import br.com.mpconnect.model.Cargo;

@ManagedBean(name="cargoConverter")
@RequestScoped
@Component
public class CargoConverter implements Converter {
	
	@Autowired
	private CargoManagerBo cargoManager;
	
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {
        if (value != null && !value.isEmpty()) {
        	return (Cargo) cargoManager.findCargoById(new Long(value));
        }
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object value) {
        if (value instanceof Cargo) {
            Cargo cargo= (Cargo) value;
            if (cargo != null && cargo instanceof Cargo && cargo.getId() != null) {
                uiComponent.getAttributes().put( cargo.getId().toString(), cargo);
                return cargo.getId().toString();
            }
        }
        return "";
    }

}
