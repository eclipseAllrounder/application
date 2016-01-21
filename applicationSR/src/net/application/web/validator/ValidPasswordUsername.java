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
//@FacesValidator("net.application.web.validator.validPasswordUsername")
public class ValidPasswordUsername implements javax.faces.validator.Validator {
	


	@Inject PartitionManager partitionManager;
	
	

		
	@SuppressWarnings("unchecked")
	 public void validate(FacesContext context, UIComponent component, Object value)
			 throws ValidatorException {
		
				
		//Partition defaultPartition = new Realm(Realm.DEFAULT_REALM);
		//ia.partitionManager.add(defaultPartition); // creates the default partition
		
		

		IdentityManager identityManager = partitionManager.createIdentityManager();
		
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		String bundleString = bundle.getString("wrongCredentials");
		ResourceBundle bundle2 = context.getApplication().getResourceBundle(context, "msg");
		String bundleString2 = bundle.getString("statusUnverfied");
		
		UIInput usernameField = (UIInput) findComponentInRoot("usernameLogin", context);
		String username = (String) usernameField.getValue();
		
		// Lookup the user by their username
		IdentityQueryBuilder queryBuilder = identityManager.getQueryBuilder();
		List<User> users = queryBuilder
			.createIdentityQuery(User.class)
			.where(
					queryBuilder.equal(User.LOGIN_NAME, username)
					)
		.getResultList();
		if(users.size()==1){
			Password plainTextPassword = new Password((String)value);
			UsernamePasswordCredentials credential = new UsernamePasswordCredentials();
			 credential.setUsername(username);
		     credential.setPassword(plainTextPassword);
		     if (users.get(0).getAttribute("status")!=null){
		    	 if (users.get(0).getAttribute("status").getValue().toString().matches(net.application.util.Status.UNVERIFIED.toString())) {
						FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString2, bundleString2); 
				        throw new ValidatorException(message); 
				}
		     }
		     
		     identityManager.validateCredentials(credential);
			if((credential.getStatus() != Status.VALID)&&(credential.getStatus() != Status.EXPIRED)){
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
		        throw new ValidatorException(message); 
			}
		} else {
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
