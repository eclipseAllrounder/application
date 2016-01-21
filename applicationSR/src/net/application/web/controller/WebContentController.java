package net.application.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;

import net.application.authorization.util.SendMailTLS;
import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.PasswordTableDao;
import net.application.web.entity.ApplicationControl;
import net.application.web.entity.Content;
import net.application.web.entity.Mailserver;
import net.application.web.entity.Skins;
import net.application.web.util.ApplicationControlDao;
import net.application.web.util.ContentDao;
import net.application.web.util.MailserverDao;
import net.application.web.util.ManagedBeanMailserverDao;
import net.application.web.util.SkinBean;
import net.application.web.util.SkinDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;




@Named                                                         
@ApplicationScoped                                                     
public class WebContentController implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject private FacesContext facesContext;
    @Inject Logger log;
    @Inject private ContentDao contentDao;
    
    
    
    private List<Content> allContents;

	
   
    @PostConstruct
	public void init() {
	   log.info("init WebContentController");
	   allContents= null;
	   if(allContents==null)allContents = contentDao.getAllContents();
	}
     public String getContentTextByName(String name) {
    	try {     
    		for (Content content : allContents) {
    			if (content.getName().equals(name)){
    				return new String(content.getText(), "UTF-8");
    			}
    		}
    		
    		return " ";
    		
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during content deleting" + e);
    		return " ";
    	}
    }
  
     public List<Content> getAllContents() {
 		if(allContents==null)allContents=contentDao.getAllContents();
 		return allContents;
 	}

 	public void setAllContents(List<Content> allContents) {
 		this.allContents = allContents;
 	}
   
}