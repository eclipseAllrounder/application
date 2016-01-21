package net.application.web.validator;


import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
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
import javax.persistence.EntityManager;





import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;



@ManagedBean
@RequestScoped
//@FacesValidator("net.application.web.validator.validUsername")
public class ValidUsername implements javax.faces.validator.Validator {
	
	@Inject
	private FacesContext facesContext;
	    
	@Inject
	private Identity identity;
	    
	@Inject 
	DefaultLoginCredentials credentials;
	       
	@Inject 
	IdentityManager identityManager;
	
	@Inject 
	Logger log;
	

		
	@SuppressWarnings("unchecked")
	 public void validate(FacesContext context, UIComponent component, Object value)
			 throws ValidatorException {
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		String bundleString = bundle.getString("userExist");
		
		IdentityQuery<User> query = identityManager.createIdentityQuery(User.class);
		query.setParameter(User.LOGIN_NAME, (String) value);
		 
		// find only by the user (login) name
		List<User> result = query.getResultList();
		if(result.size()>0){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
	        throw new ValidatorException(message); 
		}
	 
	}



	
} 
