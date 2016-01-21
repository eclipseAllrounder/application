package net.application.web.validator;


import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.SessionScoped;
import javax.faces.*;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import net.application.authorization.util.IDMUtil;
import net.application.web.controller.IdmUtilCreateController;

import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.Identity;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.Authenticator;
import org.picketlink.authentication.Authenticator.AuthenticationStatus;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.credential.UsernamePasswordCredentials;
import org.picketlink.idm.model.Partition;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;


@Named
@RequestScoped
@FacesValidator("net.application.web.validator.validRegisterCheckbox")
public class ValidRegisterCheckbox implements javax.faces.validator.Validator {
	
	
	@SuppressWarnings("unchecked")
	 public void validate(FacesContext context, UIComponent component, Object value)
			 throws ValidatorException {
		
				
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		String bundleString = bundle.getString("registerCheckbox");
		
		UIInput usernameField = (UIInput) findComponentInRoot("confirmAgb", context);
		boolean username = (Boolean) usernameField.getSubmittedValue();
		
		// Lookup the user by their username
		if(!username){
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
		        throw new ValidatorException(message); 
		}
		
		
	 
	}

	public static UIComponent findComponentInRoot(String id, FacesContext context) {
	    UIComponent component = null;

	    context = FacesContext.getCurrentInstance();
	    if (context != null) {
	      UIComponent root = context.getViewRoot();
	      component = findComponent(root, id);
	    }

	    return component;
	}
	    
	public static UIComponent findComponent(UIComponent base, String id) {
	    if (id.equals(base.getId()))
	      return base;
	  
	    UIComponent kid = null;
	    UIComponent result = null;
	    Iterator kids = base.getFacetsAndChildren();
	    while (kids.hasNext() && (result == null)) {
	      kid = (UIComponent) kids.next();
	      //System.out.println(kid.getId());
	      if (id.equals(kid.getId())) {
	        result = kid;
	        break;
	      }
	      result = findComponent(kid, id);
	      if (result != null) {
	        break;
	      }
	    }
	    return result;
	}

	
} 
