package net.application.web.converter;



import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.web.controller.UserManagementController;

import org.picketlink.idm.model.basic.Role;

/**
* @author <a href="http://community.jboss.org/people/bleathem">Brian Leathem</a>
*/

//@FacesConverter("RolesConverter")
@FacesConverter(value = "RolesConverter")
public class RolesConverter implements Converter {
   
   @Inject
   private UserManagementController userManagementController;

   public Object getAsObject(FacesContext facesContext, UIComponent component, String s) {
	   System.out.println("converter String: " + s);
       for (CustomerIdmRolesCombination role : userManagementController.loadCustomerIdmRoles()) {
    	   System.out.println("converter Role: " + role.getIdmRoleName());
           if (role.getIdmRoleName().equals(s)) {
        	   System.out.println("converter Role: " + role.getIdmRoleName());
               return role;
           }
       }
       return null;
   }

   public String getAsString(FacesContext facesContext, UIComponent component, Object o) {
	   System.out.println("Object: " + o.getClass() + " " + ((CustomerIdmRolesCombination) o).getIdmRoleName());
       if (o == null) return null;
       if (!(o instanceof CustomerIdmRolesCombination)) {
           return null;
       }
       return ((CustomerIdmRolesCombination) o).getIdmRoleName();
   }

   
}
