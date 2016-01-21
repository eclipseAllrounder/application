package net.application.authorization.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.sun.xml.internal.ws.util.StringUtils;

import net.application.web.controller.WebContentController;
 
@Named
public class SendMailTLS implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    @Inject transient static Logger log;
    @Inject private WebContentController webContentController;
    
	public void sendMail(String mailAdress, String text, String subject){
		final String username = "christian.lewer@letterbee.de";
		final String password = "Oper!2003";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.letterbee.de");
		props.put("mail.smtp.port", "25");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@letterbee.de"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(mailAdress));
			message.setSubject(subject);
			message.setSentDate(new Date());
			message.setContent(text, "text/html");
 
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			//throw new RuntimeException(e);
			 if(e.getMessage()!= null){
		            log.info(e.getMessage()); 
		            log.info(e.getLocalizedMessage());
		            log.info(getExceptionDump(e));
	        	}
		}	
		
	}
 
	public void test(){
 
		final String username = "christian.lewer@letterbee.de";
		final String password = "Oper!2003";
 
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.letterbee.de");
		props.put("mail.smtp.port", "25");
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("info@letterbee.de"));
			message.setRecipients(Message.RecipientType.TO,
		    InternetAddress.parse("christian.lewer@gmx.de"));
			message.setSubject("Account-Activierung");
			message.setSentDate(new Date());
		
			String htmlTable= "<table><tr><td>Hello World!</td></tr></table>";
			
			Document doc = Jsoup.parse(webContentController.getContentTextByName("accountActivationTextGerman"));

			// then use something like this to get your element:
			Elements tds = doc.getElementsByTag("td");
			// set plain text message
						message.setContent(message, "text/html");
			// tds will contain this one element: <td>Hello World!</td>
						
		    // set plain text message
			message.setContent(webContentController.getContentTextByName("accountActivationTextGerman2"), "text/html");
			
			
			Transport.send(message);
 
			System.out.println("Done");
 
		} catch (MessagingException e) {
			//throw new RuntimeException(e);
			 if(e.getMessage()!= null){
		            log.info(e.getMessage()); 
		            log.info(e.getLocalizedMessage());
		            log.info(getExceptionDump(e));
	        	}
			
		}
	}
	
	
	private static String getExceptionDump(Exception ex) {
        StringBuilder result = new StringBuilder();
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (result.length() > 0)
                result.append("Caused by: ");
            result.append(cause.getClass().getName());
            result.append(": ");
            result.append(cause.getMessage());
            result.append("\n");
            for (StackTraceElement element: cause.getStackTrace()) {
                result.append("\tat ");
                result.append(element.getMethodName());
                result.append("(");
                result.append(element.getFileName());
                result.append(":");
                result.append(element.getLineNumber());
                result.append(")\n");
            }
        }
        return result.toString();
    }

	public WebContentController getWebContentController() {
		return webContentController;
	}

	public void setWebContentController(WebContentController webContentController) {
		this.webContentController = webContentController;
	}
}
