package net.application.web.validator;


import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 












import javax.faces.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import net.application.web.controller.WebAdminController;
import net.application.web.entity.Mailserver;
import net.application.web.util.ContentDao;
import net.application.web.util.MailserverDao;

import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

@ManagedBean
@RequestScoped
//@FacesValidator("net.application.web.validator.ValidMailserverApplicationName")
public class ValidContentName implements javax.faces.validator.Validator {
	
	@Inject
	private FacesContext facesContext;	    
    @Inject 
    WebAdminController webAdminController;
	@Inject 
	private ContentDao contentDao;

		
	@Override
	 public void validate(FacesContext context, UIComponent component, Object value)
			 throws ValidatorException {
		
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		String bundleString = bundle.getString("applicationExist");
	
		if(contentDao.getByName((String) value)!=null && !webAdminController.getContentLoaded()){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
	        throw new ValidatorException(message); 
		}
	 
	}



	
} 
