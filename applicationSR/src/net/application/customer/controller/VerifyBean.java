package net.application.customer.controller;

import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowScoped;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.IdentityType;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;

import net.application.authorization.util.CreateUser;
import net.application.authorization.util.IDMInitializer;
import net.application.authorization.util.IDMUtil;
import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;
import net.application.customer.entity.PasswordTable;
import net.application.customer.util.CountryDao;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.PasswordTableDao;
import net.application.util.Status;
import net.application.web.util.CaptchaAction;
import net.application.web.util.ManagedBeanCustomerDao;


@Named
@FlowScoped("verification")
public class VerifyBean implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject Logger log;
	@Inject IDMUtil iu;   
	@Inject AccountVerifyBean accountVerifyBean;
	@Inject PartitionManager partitionManager;
	@Inject IdentityManager identityManager;
	@Inject RelationshipManager relationshipManager;
	@Inject ManagedBeanCustomerDao managedBeanCustomerDao;
	@Inject CaptchaAction captchaAction;

    
    private Customer customer;
    private Boolean userFound; 
    private Boolean wrongStatus; 
    private User user;
	
    String validate;
    
    public String getValidate() {
        return validate;
    }
 
    public void setValidate(String validate) {
        this.validate = validate;
    }
	
	public void initialize() {
	 Flow flow = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlow();
	 Map<Object, Object> flowScope = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlowScope();
		 
     log.info("Flow A initialized");
    // if (!userFound)redirect();
     if (userFound) log.info("User: " + customer.getLastName());
    }
	
	 public void finalize() {
	     log.info("Flow finalized");
	     log.info("Url-ID: " + accountVerifyBean.getId());
	 }  
	  
	@PostConstruct
	public void init() {
	   userFound=false;
	   wrongStatus=false;
	   user=new User();
	   log.info("Verification Flow created");
	   log.info("call verification init...");
	   log.info("Url-ID: " + accountVerifyBean.getId());
	   //load customer by verify-id
	   user=findUserByVerifyToken(accountVerifyBean.getId(), identityManager, relationshipManager);
		 
		 if (user!=null){
			 setCustomer(managedBeanCustomerDao.getCustomerByMail(user.getEmail()));
			//Status of user
			 log.info("UserStatus: " + user.getAttribute("status").getValue().toString());
			 if (!user.getAttribute("status").getValue().toString().matches(Status.UNVERIFIED.toString()))wrongStatus=true;
			 userFound=true;
		 } else {
			 
			 FacesContext context = FacesContext.getCurrentInstance();
			 ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
			 String bundleString = bundle.getString("somthimesWentWrongWithUser");
			 FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
			 context.addMessage(null, message);
			
		 }
	   // System.out.println(id); // 9099 as in your example.
	}	
	 public User findUserByVerifyToken(String id, IdentityManager identityManager, RelationshipManager relationshipManager) {
	    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
	    	IdentityQuery<User> queryu = builder.createIdentityQuery(User.class);
	    	queryu.where(builder.equal(IdentityType.QUERY_ATTRIBUTE.byName("verifyToken"), id));
			// find only by the first name
			List<User> resultUser = queryu.getResultList();
			if(!resultUser.isEmpty()) {return resultUser.get(0);} else {return null;}		
	 }
	 @Transactional
	 public void updateStatusForUser(User user){
		 log.info("update UserStatus");
		 user = BasicModel.getUser(identityManager, user.getLoginName());
		 iu.updateUserAttribute(user.getLoginName(), "status", Status.VERIFIED.toString(), identityManager, relationshipManager);
	 }
	 public String redirect(){
			log.info("redirect");
	        //if (validateId(id)) return "/homeCdi.jsf?faces-redirect=true";
	       // return "/error.jsf?faces-redirect=true";
			ExternalContext ec=FacesContext.getCurrentInstance().getExternalContext();
			log.info(ec.getRequestContextPath());
			
			try {
				ec.redirect(ec.getRequestContextPath() + "/homeCdi2.jsf?faces-redirect=true");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			  return "/error.jsf?faces-redirect=true";
	        
		}
	 public String execute() {
		 try {
			 captchaAction.setValidate(getValidate());
			return captchaAction.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	 }
	 
	 public String exitVerification() {
	     log.info("exit verification");
	     if (!userFound){		
	    	 log.info("exit verification with error*");
			 return "verification-errorNoUser";
		 }
	     if (userFound && wrongStatus){		
	    	 log.info("exit verification with error");
			 return "verification-errorUserWithWrongStatus";
		 }
	     //normal exit
	     if (userFound && !wrongStatus){	
	    	 updateStatusForUser(user);
			 return "verification-exit";
		 }
	     
	     return "verification-exit";
	 }
	 
	 
	 @PreDestroy 
	 public void onDestruction() {
		 log.info("Flowdestroyed");
	 }

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public String onload() {
	    // ...
		log.info("onload Url-ID: " + accountVerifyBean.getId());
		 if ((!userFound) ||(userFound && wrongStatus)) return "verification-end";
	    return "verification-confirm";
	}


	
}
