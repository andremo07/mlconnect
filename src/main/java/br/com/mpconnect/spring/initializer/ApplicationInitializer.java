package br.com.mpconnect.spring.initializer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class ApplicationInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>
{
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext)
	{
		Properties properties = new Properties();
		
		try {
			properties.load(new FileInputStream(System.getProperty("mlconnect.path") + "/application.properties"));
		} catch (IOException ex) {
			try {
				properties.load(ApplicationInitializer.class.getResourceAsStream("/application.properties"));
			} catch (IOException e) {
				System.exit(1);
			}
		}
				
		String activeProfile = properties.getProperty("profile","dev");
		
		applicationContext.getEnvironment().setActiveProfiles(activeProfile);
	}
	
}
