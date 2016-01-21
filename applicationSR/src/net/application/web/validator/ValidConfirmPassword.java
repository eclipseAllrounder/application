package net.application.web.validator;




import java.util.Iterator;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import net.application.customer.entity.Customer;

import com.sun.faces.component.visit.FullVisitContext;

 
@FacesValidator("net.application.web.validator.ValidConfirmPassword")
public class ValidConfirmPassword implements Validator { 
	 
	
	

	public void validate(FacesContext context, UIComponent component, Object value)
	throws ValidatorException {
		
	
	
	ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
	String bundleString = bundle.getString("passwordConfirm");

	String confirmPassword = (String) value; 
	// Obtain the component and submitted value of the confirm password component.
    UIInput confirmComponent = (UIInput) component.getAttributes().get("password");
    if (confirmComponent == null) 
        throw new IllegalArgumentException(String.format("Unable to find component."));
    
    String password = (String) confirmComponent.getSubmittedValue();
	
    System.out.println(confirmPassword + password);
    if (!confirmPassword.equals(password)) { 
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
	      System.out.println(kid.getId());
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
	public UIComponent findComponent2(final String id){
	    FacesContext context = FacesContext.getCurrentInstance(); 
	    UIViewRoot root = context.getViewRoot();
	    final UIComponent[] found = new UIComponent[1];
	    root.visitTree(new FullVisitContext(context), new VisitCallback() {     
	        @Override
	        public VisitResult visit(VisitContext context, UIComponent component) {
	            if(component.getId().equals(id)){
	                found[0] = component;
	                return VisitResult.COMPLETE;
	            }
	            return VisitResult.ACCEPT;              
	        }
	    });
	    return found[0];
	}
	
} 
