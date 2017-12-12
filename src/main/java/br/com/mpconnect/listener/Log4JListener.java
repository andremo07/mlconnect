package br.com.mpconnect.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

public class Log4JListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce) 
	{
		if ( System.getProperty("mlconnect.path") != null )
			PropertyConfigurator.configure(System.getProperty("mlconnect.path") + "/log4j.properties");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {}
}
