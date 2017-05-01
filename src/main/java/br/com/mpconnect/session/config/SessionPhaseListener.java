package br.com.mpconnect.session.config;

import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.com.mpconnect.model.Usuario;

public class SessionPhaseListener implements PhaseListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5928849396046589752L;
	private static final String LOGIN_PAGE = "/login.xhtml";
	private static final String HOME_PATH = "/ml-connect";

    @Override
    public void afterPhase(PhaseEvent event) {
        //Do anything
    }

    @Override
    public void beforePhase(PhaseEvent event) {
    	
        FacesContext context = event.getFacesContext();
        ExternalContext ext = context.getExternalContext();
        HttpSession session = (HttpSession) ext.getSession(false);

        boolean newSession = (session == null) || (session.isNew());
        boolean postback = !ext.getRequestParameterMap().isEmpty();
        boolean timedout = postback && newSession;
        
    	HttpServletRequest request = (HttpServletRequest) ext.getRequest();
    	String uri = request.getRequestURI();
    	Map<String,Object> map = ext.getSessionMap();
    	boolean usuarioAutenticado=true;
        if(!timedout){
        	if(!uri.equals(HOME_PATH+"/")&&!uri.equals(HOME_PATH+LOGIN_PAGE)){
        		Usuario usuario = (Usuario) map.get("usuario");
        		usuarioAutenticado=(usuario!=null);
        	}
        }
        
        if (timedout||!usuarioAutenticado) {
        	uri = uri.replaceAll(HOME_PATH, "");
        	map.put("lastPage", uri);
            Application app = context.getApplication();
            ViewHandler viewHandler = app.getViewHandler();
            UIViewRoot view = viewHandler.createView(context,LOGIN_PAGE);
            context.setViewRoot(view);
            context.renderResponse();
            try {
                viewHandler.renderView(context, view);
                context.responseComplete();
            } catch (Throwable t) {
                throw new FacesException("Session timed out", t);
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}