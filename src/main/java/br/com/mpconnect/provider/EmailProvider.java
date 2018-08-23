package br.com.mpconnect.provider;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public abstract class EmailProvider {

	private static Session session;

	public abstract String getUser();
	public abstract String getPassword();
	public abstract String getSenderEmail();
	public abstract String getSenderAlias();
	public abstract String getReceiverEmail();
	
	private Logger logger;

	public Logger getLogger() {
		return logger;
	}

	public void setUp()
	{		
		if ( session == null )
		{
			Properties properties = new Properties();

			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.socketFactory.port", "465");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.port", "465");

			session = Session.getDefaultInstance(properties, new Authenticator() 
			{
				protected PasswordAuthentication getPasswordAuthentication() 
				{
					return new PasswordAuthentication(getUser(), getPassword());
				}
			}); 
		}
	}

	public void sendEmail(String subject, String text) throws Exception 
	{						
		Address[] toUser = InternetAddress.parse(getReceiverEmail());  

		MimeMessage message = new MimeMessage(session);

		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setFrom(new InternetAddress(getSenderEmail(), getSenderAlias()));
		message.setSubject(subject);
		message.setText(text, "utf-8", "html");

		getLogger().info(String.format("Sending email %s to %s", text, toUser.toString()));

		Transport.send(message);
	}

	public void sendEmail(String subject, String text, String receiver) throws Exception 
	{						
		Address[] toUser = InternetAddress.parse(receiver);  

		MimeMessage message = new MimeMessage(session);

		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setFrom(new InternetAddress(getSenderEmail(), getSenderAlias()));
		message.setSubject(subject);
		message.setContent(text, "text/plain; charset=utf-8");

		getLogger().info(String.format("Sending email %s to %s", text, toUser.toString()));

		Transport.send(message);
	}
}
