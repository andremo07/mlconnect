package br.com.mpconnect.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.push.EventBus;
import org.primefaces.push.EventBusFactory;

public class TesteFilter implements Filter{  

	public void destroy() {  
	}  

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {  

			HttpServletRequest req = (HttpServletRequest) request;  
			//HttpServletResponse res = (HttpServletResponse) response; 

	        EventBus eventBus = EventBusFactory.getDefault().eventBus();
	        eventBus.publish("/notifications",req);
			
	        chain.doFilter(request, response);			
	}  

	public void init(FilterConfig arg0) throws ServletException {  
	}  
}  