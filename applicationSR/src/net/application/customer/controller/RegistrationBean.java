package net.application.customer.controller;

import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.ocpsoft.rewrite.annotation.Join;
import org.ocpsoft.rewrite.annotation.Parameter;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

import net.application.authorization.util.CreateUser;
import net.application.authorization.util.SendMailTLS;
import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;
import net.application.customer.entity.PasswordTable;
import net.application.customer.util.CountryDao;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.PasswordTableDao;
import net.application.util.CreateMailAdress;
import net.application.util.Status;
import net.application.web.controller.WebContentController;



@Named
@FlowScoped("registration")
public class RegistrationBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, List<FacesMessage>> messages;

	@Inject private FacesContext facesContext;
    
    @Inject PartitionManager partitionManager;
    @Inject IdentityManager identityManager;
    @Inject RelationshipManager relationshipManager;
    
    @Inject transient Logger log;

    @Inject private CustomerDao customerDao;
    @Inject private CountryDao countryDao;
    @Inject private PasswordTableDao PasswordTableDao;
    @Inject private SendMailTLS sendmail;
    @Inject private WebContentController webContentController;
    @Inject private CreateMailAdress createMailAdress;
    
    
    private String password;
    private String country;
    private String gender;
    private String passwordConfirm;
    private String userName;
    private String secureLevel;
    private Boolean confirmAgb;
    private UIInput passwordInput = null;
    private UIInput passwordConfirmInput = null;
    private String customerCategory;
    private Integer secureLvl;
   
   @Parameter
   String jfwid;

	@Named                                                           
    @Produces
    private Customer newCustomer;

	public RegistrationBean(){
	    messages = new HashMap<String, List<FacesMessage>>();
	}
	public void initialize() {
	   Flow flow = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlow();
	   Map<Object, Object> flowScope = FacesContext.getCurrentInstance().getApplication().getFlowHandler().getCurrentFlowScope();
       log.info("Flow for registration initialized");
    }
	   
	   
	@PostConstruct
	public void init() {
	   log.info("call init, registration flow started...");
	   newCustomer = new Customer();
	   secureLvl = 0;
	}
	
	
	 public String nextSessionId() {
		 SecureRandom random = new SecureRandom();
	    return new BigInteger(210, random).toString(32);
	 }
	 public String randomString() {
		 char[] chars = "abcdefghijklmnopqrstuvwxyzQWERTZUIOPASDFGHJKLYXCVBNM0123456789".toCharArray();
		 StringBuilder sb = new StringBuilder();
		 Random random = new Random();
		 for (int i = 0; i < 30; i++) {
		     char c = chars[random.nextInt(chars.length)];
		     sb.append(c);
		 }
		 String output = sb.toString();
		 System.out.println(output);
		 return output;
	 }
	
    public void create() {
    	log.info("begin creation");
        try {
        	
        	// IDM Acccount
        	String tokenString = nextSessionId();
        	String internalMailAccount = randomString();
        	internalMailAccount = internalMailAccount + "@letterbee.de";
        	System.out.println("tokenSTring: " + tokenString);
        	//newCustomer.setCountry(countryDao.getForCountryName(country));
        	newCustomer.setUserName(newCustomer.getEmail());
        	newCustomer.setAccountActivationString(tokenString);
        	newCustomer.setInternalEmail(internalMailAccount);
            User newUser = new User(newCustomer.getEmail());
            newUser.setEmail(newCustomer.getEmail());
            newUser.setFirstName(newCustomer.getFirstName());
            newUser.setLastName(newCustomer.getLastName());
            newUser.setEnabled(true);
            newUser.setAttribute( new Attribute<String>("verifyToken", tokenString));
            newUser.setAttribute( new Attribute<String>("status", Status.UNVERIFIED.toString()));
            newUser.setAttribute( new Attribute<String>("internalMailAccount", internalMailAccount));
            
            this.identityManager.add(newUser);
            this.identityManager.updateCredential(newUser, new Password(password));
                       
            
            // Assign Group(s)
            Group group = new Group();
            Group subgroup = new Group();
            RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
            
            // Grant the "customer" group
            //if(customerCategory.equals("customer")){
	            group = BasicModel.getGroup(identityManager, "customer"); 
	            addToGroup(relationshipManager, newUser, group);
	            //subgroup = BasicModel.getGroup(identityManager, "europe", group);
	            //addToGroup(relationshipManager, newUser, subgroup);
           // }
            // Grant the "author" group
          //  if(customerCategory.equals("author")){
          //  	group = BasicModel.getGroup(identityManager, "author");  
	      //      subgroup = BasicModel.getGroup(identityManager, "europe", group);
	     //       addToGroup(relationshipManager, newUser, subgroup);
         //   }
            // Grant the "customer" and "author" group
         //   if(customerCategory.equals("both")){
        //    	group = BasicModel.getGroup(identityManager, "customer");  
	    //        subgroup = BasicModel.getGroup(identityManager, "europe", group);
	    //        addToGroup(relationshipManager, newUser, subgroup);
	   //         group = BasicModel.getGroup(identityManager, "author");  
	   //         subgroup = BasicModel.getGroup(identityManager, "europe", group);
	   //         addToGroup(relationshipManager, newUser, subgroup);
        //    }
            
            //persist Customer 
            customerDao.createCustomer(newCustomer);
            log.info("account saved"); 
            //Create MailAccount            
            //Check for existence
     	    Integer exitStatus=createMailAdress.checkAccount(newCustomer.getEmail());
     	    if (exitStatus==1) {
     	    	log.info("mail forwarding in configuration");
     	    	//internalMailAccount = internalMailAccount + "@letterbee.de";
     	    	exitStatus=createMailAdress.createAccount(internalMailAccount, newCustomer.getEmail());
     	    } else {
     	    	log.info("external Mail-Address already exist");
     	    }
     	    String mailText=webContentController.getContentTextByName("accountActivationMailTextGerman");
     	    //geehrter Kunde
     	    if (newCustomer.getGender().matches("male")){
     	    	mailText=ersetze("geehrter Kunde", "geehrter Herr " + newCustomer.getLastName(), mailText);
     	    }
     	   if (newCustomer.getGender().matches("female")){
    	    	mailText=ersetze("geehrter Kunde", "geehrte Frau " + newCustomer.getLastName(), mailText);
    	    }
     	    //www.staffScout24.de/aktivierung
     	    mailText=ersetze("www.staffScout24.de/aktivierung", "http://localhost:8080/aktivierung.html?id=" + newCustomer.getAccountActivationString(), mailText);
            //generate Mail to User for AccountVerification
            sendmail.sendMail(newCustomer.getEmail(),mailText, webContentController.getContentTextByName("accountActivationMailSubjectGerman"));
            log.info("mail sent");          
           
        	
        } catch (Exception e) {
            String message = "An error has occured while creating" +
                             " the user (see log for details)";
            facesContext.addMessage(null, new FacesMessage(message));
            //if(e.getMessage()!= null){
	            log.info(e.getMessage()); 
	            log.info(e.getLocalizedMessage());
	            log.info(getExceptionDump(e));
        	//}
            
        }
    }
    public static String ersetze(String suche, String ersatz, String str) { 
        int start = str.indexOf(suche); 

        while (start != -1) { 
            str = str.substring(0, start) + ersatz + str.substring(start + suche.length(), str.length()); 
            start = str.indexOf(suche, start + ersatz.length()); 
        } 
        return (str); 
    } 

    private String getExceptionDump(Exception ex) {
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
    @Named                                                           
    @Produces
    List<SelectItem> allCountries;
    
    public List<SelectItem> getAllCountries(){
    	   List<SelectItem> items = new ArrayList<SelectItem>();
    	   List<Country> countryList = customerDao.getAllCountries();
    	    for(Country country: countryList){
    	       items.add(new SelectItem(country.getName_de(), country.getName_de()));
    	   }
    	   return items;
    }
    
    

	public void postValidatePassword(ComponentSystemEvent ev){
		this.passwordInput=(UIInput)ev.getComponent();
		
		System.out.println("Password Event!!! ");
	}
	public void postValidatePasswordConfirm(ComponentSystemEvent ev){
		this.passwordConfirmInput=(UIInput)ev.getComponent();
		System.out.println("password Confirm Event!!! ");
	}
	
	public void validatePassword(FacesContext context, UIComponent component,
	        Object value) throws ValidatorException {
	
		if (passwordConfirmInput != null ) {
			//passwordConfirm = null;
			passwordConfirm = (String)passwordConfirmInput.getValue();
			password = (String)value;
		    System.out.println("validatePassword confirm: " + passwordConfirm + " password: " + password);
		}
	    
	}
	public void validate(FacesContext context, UIComponent component,
	        Object value) throws ValidatorException {
	
		if (passwordConfirmInput != null ) {
			//passwordConfirm = null;
			passwordConfirm = (String)passwordConfirmInput.getValue();
			password = (String)value;
		    System.out.println("validatePassword confirm: " + passwordConfirm + " password: " + password);
		}
	    
	}
	public void validatePasswordConfirm(FacesContext context, UIComponent component,
	        Object value) throws ValidatorException {
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
		String bundleString = bundle.getString("passwordConfirm");
		if (passwordInput != null ) {
			//password = null;
		    password = (String)passwordInput.getValue();
		    passwordConfirm = (String)value;
		    System.out.println("validatePasswordConfirm confirm: " + passwordConfirm + " password: " + password);
		    if (password != null && !passwordConfirm.equals(password)) {
		    	System.out.println(passwordConfirm + password);
	            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, bundleString, bundleString); 
	            throw new ValidatorException(message); 
	        }
		 }
	    
	}
	
    @NotEmpty(message="{register.password.notEmpty}")
    @NotNull(message="{register.password.notNull}")
    @Length(max = 40, message="{register.password.length}")
    @Size(max = 40, message="{register.password.size}")
    
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@NotEmpty(message="{register.password.notEmpty}")  
	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getSecureLevel() {
		return secureLevel;
	}
	public void setSecureLevel(String secureLevel) {
		this.secureLevel = secureLevel;
	}
	
	 
	 public void finalize() {
	     log.info("Flow finalized");
	 }
	
	 public String createRegistration() {
		 log.info("create registration");
	     return "registration";
	 }
	 public String confirmRegistration() {
		 log.info("confirm registration");
	     return "confirm";
	 }
	 
	 @Transactional
	 public String endRegistration() {
	     log.info("end registration");
	     create();	
	     return "exitRegistration";
	 }
	 public String exitRegistration() {
	     log.info("exit registration");
	     return "exit";
	 }
	 
	 public Boolean switchFlowToEnd(){
		 log.info("switch registration");
		 return true;
	 }
	    
	 @PreDestroy 
	 public void onDestruction() {
		 log.info("Flowdestroyed");
	 }


	public Boolean getConfirmAgb() {
		return confirmAgb;
	}


	public void setConfirmAgb(Boolean confirmAgb) {
		this.confirmAgb = confirmAgb;
	}

	@NotEmpty(message="{register.city.length}")
	public String getCustomerCategory() {
		return customerCategory;
	}


	public void setCustomerCategory(String customerCategory) {
		this.customerCategory = customerCategory;
	}
	
	@Named                                                           
	@Produces
	List<PasswordTable> allPasswords;
	
	public int refresh(){
    	//int secureLvl = passwordStrength();
    	System.out.println("refresh");
    	return passwordStrength();
    }
	
	public List<PasswordTable> getAllPasswords()
	{
		List<PasswordTable> passwordList = PasswordTableDao.getAllPasswords();
	     
	    return passwordList;
	}
	
	
	public boolean passwordDatabaseCheck()
	{	
		List<PasswordTable> pwL = getAllPasswords();
		Iterator<PasswordTable> iterator = pwL.iterator();
		
		boolean dbCheck = false;
		
		while (iterator.hasNext())
		{
				if(password.equals(iterator.next().getPassword()))
				{
					dbCheck = true;
					break;
				}
		}
			
		return dbCheck;
	}	
	
	public int passwordStrength()
	{
		
		System.out.println("secureLvl " + secureLvl);
		if(password.matches(".*[a-z]+.*"))
			{
				secureLvl=1; 
			}
		if(password.matches(".*[A-Z]+.*"))
			{
				secureLvl=1; 
			}
		if(password.matches(".*[0-9]+.*"))
			{
				secureLvl=1; 
			}
		if(password.matches(".*[\\!§$%&/()=?\"]+.*"))
		{
			secureLvl=1; 
		}
		if((password.matches(".*[a-z]+.*")) && (password.matches(".*[A-Z]+.*")))
		{
			secureLvl=2; 
		}
		if((password.matches(".*[a-z]+.*")) && (password.matches(".*[\\!§$%&/()=?\"]+.*")) && (password.matches(".*[A-Z]+.*")))
		{
			secureLvl=3; 
		}
		
		System.out.println("secureLvl " + secureLvl);
	return secureLvl;
	}

	public Integer getSecureLvl() {
		return secureLvl;
	}
	
	public void setSecureLevel(Integer secureLvl) {
		this.secureLvl = secureLvl;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}
	public static RegistrationBean getCurrentInstance() {
		RegistrationBean ajaxBacking = null;
	    FacesContext context = FacesContext.getCurrentInstance();
	    if ((ajaxBacking = (RegistrationBean) context.getViewRoot().getViewMap().get("ajaxBacking")) == null){
	        ajaxBacking = (RegistrationBean)context.getApplication().evaluateExpressionGet(context,"#{ajaxBacking}",RegistrationBean.class);
	    }
	    return ajaxBacking;
	}

	public void displayMessages(){
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    for (String component : messages.keySet()){
	        for (FacesMessage message : messages.get(component)) {
	            if (!facesContext.getMessageList(component).contains(message)){
	                facesContext.addMessage(component, message);
	            }
	        }
	    }
	}

	public void putMessages(String component, List<FacesMessage> facesMessage){
	    List<FacesMessage> messages = new ArrayList<FacesMessage>();
	    for (FacesMessage message : facesMessage) messages.add(message);
	    this.messages.put(component, messages);
	}

	public Map<String, List<FacesMessage>> getMessages() {
	    return messages;
	}
}
