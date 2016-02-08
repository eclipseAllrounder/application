package net.application.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;

import net.application.authorization.annotation.Admin;
import net.application.authorization.util.IDMInitializer;
import net.application.authorization.util.IDMUtil;
import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.CustomerIdmGroupCombination;
import net.application.customer.util.CustomerIdmGroupRolesCombination;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.CustomerIdmUserCombination;
import net.application.customer.util.GroupTreeBean;
import net.application.util.CreateMailAdress;
import net.application.web.entity.Skins;
import net.application.web.util.*;

import org.picketlink.idm.IdentityManagementException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Credentials.Status;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupMembership;
import org.picketlink.idm.model.basic.GroupRole;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.idm.query.RelationshipQuery;
import org.picketlink.idm.query.Sort;
import org.richfaces.event.ItemChangeEvent;
import org.richfaces.event.TreeSelectionChangeEvent;



@Named("userManagementController")
//@ManagedBean
@SessionScoped
public class UserManagementController  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject Logger log;
	@Inject private FacesContext facesContext;    
    @Inject private CustomerDao customerDao;   
    @Inject private ManagedBeanCustomerDao managedBeanCustomerDao;
    @Inject private CreateMailAdress createMailAdress;
    @Inject IDMInitializer ia;    
    @Inject private PartitionManager partitionManager;   
    @Inject
    @RequestScoped
    private GroupTreeBean treeBeanAdd;
    @Inject
    private GroupTreeBean treeBeanDelete;
    @Inject
    private GroupTreeBean treeBeanGroupRole;
    @Inject
    private GroupTreeBean treeBean;
    
    @Inject UsersBean usersBean;
    @Inject UsersCustomersBean usersCustomersBean;
 
	
	// @Inject private IdentityManager identityManager;
    private String currentUserName; 
    private String currentGroupName; 
    private String currentGroupPath;
    private String currentRoleName;
    private CustomerIdmUserCombination currentCustomerIdmUser;
    private Integer currentCustomerIdmUserIndex;
    private CustomerIdmGroupCombination currentCustomerIdmGroup;
    private List<CustomerIdmUserCombination> allCustomerIdmUsers=null;
    private List<CustomerIdmGroupCombination> allCustomerIdmGroups;
    private List<CustomerIdmRolesCombination> allCustomerIdmRoles;
    private Integer currentIndex;
    private String groupNameFilter;
    private String groupNameFilterOld;
    private String selectedTab;
    private CustomerIdmRolesCombination selectCustomerIdmRolesCombination;
    private List<CustomerIdmRolesCombination> customerIdmRolesCombination;
    private List<CustomerIdmRolesCombination> selectedCustomerIdmRolesCombination;
    private List<String> selectedCustomerIdmRolesCombinationStrings;
    private List<String> customerIdmRolesCombinationStrings;
    private List<String> selectedCustomerIdmGroupRolesCombinationStrings;
    private List<String> customerIdmGroupRolesCombinationStrings;
    private List<String> selectedGroupIdmRolesCombinationStrings;
    private List<String> groupIdmRolesCombinationStrings;
    private String selectedRole;
    private Boolean activateForAllAddSubGroups;
    private Boolean activateForAllDeleteSubGroups;
    private List<Role> rolesList;
    @Inject
    private IDMUtil iu;
    private TreeSelectionChangeEvent selectionChangeEventOld;
    private Integer selectedDeleteNodeID;
    
    private String customerListColumnUsernameFilter = "";
    private String customerListColumnFirstnameFilter = "";
    private String customerListColumnLastnameFilter = "";
   
	private Boolean sortLoginNameUp;
    private Boolean sortMailUp;
    private Boolean sortEnableUp;
    private Boolean sortCreateDateUp;
    private Boolean sortExpiryDateUp;
	private Boolean sortFirstNameUp;
    private Boolean sortLastNameUp;
    private Boolean sortLoginNameDown;
    private Boolean sortMailDown;
    private Boolean sortEnableDown;
    private Boolean sortCreateDateDown;
    private Boolean sortExpiryDateDown;
	private Boolean sortFirstNameDown;
    private Boolean sortLastNameDown;
    
    private int page = 1;
    private int rowCount = 10;
    private int offSet = 0;
    private int limit = 0;
    private int maxPages = 2;
    
   
	@PostConstruct
	@Admin
    public void init() {
    	System.out.println("init PostConstruct userManagementController");
    
    	if(groupNameFilter==null)groupNameFilter="deleteFilter";
    	if(selectedTab==null)selectedTab="tableForm:userTab";
    	allCustomerIdmGroups=null;
    	//allCustomerIdmUsers=null;
    	resetIdmUser();
    	allCustomerIdmRoles=null;
    	activateForAllDeleteSubGroups=true;
    	activateForAllAddSubGroups=true;
    	setCustomerIdmRolesCombination(loadCustomerIdmRolesCombination());
    	setCustomerIdmRolesCombinationStrings(loadCustomerIdmRolesCombinationStrings());
    	setGroupIdmRolesCombinationStrings(loadCustomerIdmRolesCombinationStrings());
    	setCustomerIdmGroupRolesCombinationStrings(loadCustomerIdmGroupRolesCombinationStrings());
    	setSortFalse();
		
    	//preRender(null);
    	//allCustomerIdmGroups=getAllCustomerIdmGroups();
    	//allCustomerIdmUsers=getAllCustomerIdmUsers();
    	//allCustomerIdmRoles=getAllCustomerIdmRoles();
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
        IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        List<Condition>conditionSet= new ArrayList<Condition>();
        Condition conditionLOGIN_NAME = builder.like(User.LOGIN_NAME, "%");
        conditionSet.add(conditionLOGIN_NAME);
        Sort sort = builder.desc(User.LOGIN_NAME);
    	//usersModel = new UsersModel(builder,conditionSet,sort);
    	
    }
 
   
	public void preRenderUserTable(ComponentSystemEvent event) {
        // Or in some SystemEvent method (e.g. <f:event type="preRenderView">).
    	//System.out.println("UserMangerController preRenderUserTable for " + event.getComponent().getId());
    	//allCustomerIdmUsers=null;
    } 
    public void preRenderGroupTable(ComponentSystemEvent event) {
        // Or in some SystemEvent method (e.g. <f:event type="preRenderView">).
    	//System.out.println("UserMangerController preRenderGroupTable for " + event.getComponent().getId());
    	//allCustomerIdmGroups=null;
    	
    }
    public void preRenderRoleTable(ComponentSystemEvent event) {
        // Or in some SystemEvent method (e.g. <f:event type="preRenderView">).
    	//System.out.println("UserMangerController preRenderRoleTable for " + event.getComponent().getId());
    	//allCustomerIdmRoles=null;
    	  }
    public void srollerSelected(org.richfaces.event.DataScrollEvent event) {
    	//System.out.println("UserMangerController srollerSelected for " + event.getComponent().getId());
    	if (event.getComponent().getId().matches("scrollerUserTable")) allCustomerIdmUsers=null;
    	if (event.getComponent().getId().matches("scrollerGroupTable")) allCustomerIdmGroups=null;
    	if (event.getComponent().getId().matches("scrollerRoleTable")) allCustomerIdmRoles=null;
    } 
   
    
    public void showLastUserInTable(){
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	
    	//IdentityManager identityManager = getIdentityManager();
        // here we get the query builder
        IdentityQueryBuilder builder = identityManager.getQueryBuilder();

        // create the condition
        String customerListColumnUsernameFilterHelper=customerListColumnUsernameFilter;
        String customerListColumnFirstnameFilterHelper=customerListColumnFirstnameFilter;
        String customerListColumnLastnameFilterHelper= customerListColumnLastnameFilter;
        if(customerListColumnUsernameFilter==null){
        	customerListColumnUsernameFilterHelper="%";
        } else {
	        if(customerListColumnUsernameFilter.isEmpty()){
	        	customerListColumnUsernameFilterHelper="%";
	        } else {
	        	customerListColumnUsernameFilterHelper="%" + customerListColumnUsernameFilter + "%";
	        }
        }
        if(customerListColumnFirstnameFilter==null){
        	customerListColumnFirstnameFilterHelper="%";
        } else {
	        if(customerListColumnFirstnameFilter.isEmpty()){
	        	customerListColumnFirstnameFilterHelper="%";
	        } else {
	        	customerListColumnFirstnameFilterHelper="%" + customerListColumnFirstnameFilter + "%";
	        }
        }
        if(customerListColumnLastnameFilter==null){
        	customerListColumnLastnameFilterHelper="%";
        } else {
	        if(customerListColumnLastnameFilter.isEmpty()){
	        	customerListColumnLastnameFilterHelper="%";
	        } else {
	        	customerListColumnLastnameFilterHelper="%" + customerListColumnLastnameFilter + "%";
	        }
        }
        Condition conditionLOGIN_NAME = builder.like(User.LOGIN_NAME, customerListColumnUsernameFilterHelper);
        Condition conditionFIRST_NAME = builder.like(User.FIRST_NAME, customerListColumnFirstnameFilterHelper);
        Condition conditionLAST_NAME = builder.like(User.LAST_NAME, customerListColumnLastnameFilterHelper);
      
        IdentityQuery query = builder.createIdentityQuery(User.class);
        query.where(conditionLOGIN_NAME);
        query.where(conditionFIRST_NAME);
        query.where(conditionLAST_NAME);
        int resultCount=query.getResultCount();
    	System.out.println("UserMangerController resultCount: " + resultCount);

        //Calculate page
        page = resultCount/rowCount;
        offSet = resultCount-(rowCount*maxPages);
        limit = resultCount;
        System.out.println("UserMangerController resultCount: " + resultCount + "page: " + page + "offSet: " + offSet + "limit: " + limit);
        calculateOffSetAndLimit=false;
        
        allCustomerIdmUsers=null;
        
       
    }
    public void showFirstUserInTable(){
         //Calculate page
        page = 1;
        offSet = 0;
        limit = rowCount*maxPages;
        System.out.println("UserMangerController page: " + page + "offSet: " + offSet + "limit: " + limit);

        calculateOffSetAndLimit=false;
        allCustomerIdmUsers=null;
     }
    public void resetIdmUser(){
    	//System.out.println("UserMangerController uuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuuu");
    	page = 1;
        offSet = 0;
        limit = rowCount*maxPages;
        calculateOffSetAndLimit=false;
        allCustomerIdmUsers=null;
    }
    public void initAddTree() {
    	//System.out.println("initAddTree");
    	throw new AbortProcessingException("Invalid node reference");
    }
    public void initDeleteTree() {
    	//System.out.println("initDeleteTree");
    	throw new AbortProcessingException("Invalid node reference");
    } 
    public List<CustomerIdmRolesCombination> loadCustomerIdmRolesCombination() {
    	//System.out.println("UserMangerController loadCustomerIdmRolesCombination");
	    List<SelectItem> items = new ArrayList<SelectItem>();
		   List<CustomerIdmRolesCombination> rolesFromDb= loadCustomerIdmRoles();
		   CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
		List<CustomerIdmRolesCombination> CIRCs = new ArrayList<CustomerIdmRolesCombination>(); 
		List<String> CIRCSs = new ArrayList<String>();
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        IdentityQuery queryr = builder.createIdentityQuery(Role.class);
        Condition conditionr = builder.lessThan(Role.CREATED_DATE, new Date());
        queryr.where(conditionr);
		List<Role> resultGrant = queryr.getResultList();
		for (Role role : resultGrant) {
			CIRC = new CustomerIdmRolesCombination();
			CIRC.setIdmRoleName(role.getName());
			CIRC.setIdmRoleCreatedDate(role.getCreatedDate());
			CIRCSs.add(role.getName());
			CIRCs.add(CIRC);
		}
	  
	  
	   return CIRCs;
    }
    public List<String> loadCustomerIdmRolesCombinationStrings() {
    	//System.out.println("UserMangerController loadCustomerIdmRolesCombinationStrings");
	    List<SelectItem> items = new ArrayList<SelectItem>();
		   List<CustomerIdmRolesCombination> rolesFromDb= loadCustomerIdmRoles();
		   CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
		List<CustomerIdmRolesCombination> CIRCs = new ArrayList<CustomerIdmRolesCombination>(); 
		List<String> CIRCSs = new ArrayList<String>();
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        IdentityQuery queryr = builder.createIdentityQuery(Role.class);
        Condition conditionr = builder.lessThan(Role.CREATED_DATE, new Date());
        queryr.where(conditionr);
		List<Role> resultGrant = queryr.getResultList();
		for (Role role : resultGrant) {
			CIRC = new CustomerIdmRolesCombination();
			CIRC.setIdmRoleName(role.getName());
			CIRC.setIdmRoleCreatedDate(role.getCreatedDate());
			CIRCSs.add(role.getName());
			CIRCs.add(CIRC);
		}
	  
	  
	   return CIRCSs;
    }
    

    @SuppressWarnings("unchecked")
    public static <T> void removeDuplicate(List <T> list) {
      Set <T> set = new HashSet <T>();
      List <T> newList = new ArrayList <T>();
      for (Iterator <T>iter = list.iterator();    iter.hasNext(); ) {
         Object element = iter.next();
         if (set.add((T) element))
            newList.add((T) element);
         }
         list.clear();
         list.addAll(newList);
      }
    
    public List<String> loadCustomerIdmGroupRolesCombinationStrings() {
    	//System.out.println("UserMangerController loadCustomerIdmGroupRolesCombinationStrings");
   		List<String> CIGRCSs = new ArrayList<String>();
   	
   		IdentityManager identityManager = this.partitionManager.createIdentityManager();
   		RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
	   	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
	    Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
	    queryg.where(conditiong);
   		List<Group> resultG = queryg.getResultList();
   		for (Group group : resultG) {
   		   	RelationshipQuery<GroupRole> querygr = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
   		   	querygr.setParameter(GroupRole.GROUP, group);
   		   	List<GroupRole> resultGrant = querygr.getResultList();
   		   	for (GroupRole groupRole : resultGrant) {
				CIGRCSs.add(groupRole.getGroup().getPath() + " - " + groupRole.getRole().getName());
   		   	}
			
		}
   		UserManagementController.removeDuplicate(CIGRCSs);
   		Collections.sort(CIGRCSs);
   		return CIGRCSs;
    }
    @Admin
    public List<User> getAllIdmUsers(){
    	//System.out.println("UserMangerController getAllIdmUsers");
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
        // here we get the query builder
        IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	//get the total UserCount
    	Condition condition = builder.like(User.LOGIN_NAME, "%");        
        IdentityQuery query = builder.createIdentityQuery(User.class);
        query.where(condition);
        int resultCount=query.getResultCount();
    	System.out.println("UserMangerController resultCount: " + resultCount);
    	

    	// create the condition
        String customerListColumnUsernameFilterHelper=customerListColumnUsernameFilter;
        String customerListColumnFirstnameFilterHelper=customerListColumnFirstnameFilter;
        String customerListColumnLastnameFilterHelper= customerListColumnLastnameFilter;
        if(customerListColumnUsernameFilter==null){
        	customerListColumnUsernameFilterHelper="%";
        } else {
	        if(customerListColumnUsernameFilter.isEmpty()){
	        	customerListColumnUsernameFilterHelper="%";
	        } else {
	        	customerListColumnUsernameFilterHelper="%" + customerListColumnUsernameFilter + "%";
	        }
        }
        if(customerListColumnFirstnameFilter==null){
        	customerListColumnFirstnameFilterHelper="%";
        } else {
	        if(customerListColumnFirstnameFilter.isEmpty()){
	        	customerListColumnFirstnameFilterHelper="%";
	        } else {
	        	customerListColumnFirstnameFilterHelper="%" + customerListColumnFirstnameFilter + "%";
	        }
        }
        if(customerListColumnLastnameFilter==null){
        	customerListColumnLastnameFilterHelper="%";
        } else {
	        if(customerListColumnLastnameFilter.isEmpty()){
	        	customerListColumnLastnameFilterHelper="%";
	        } else {
	        	customerListColumnLastnameFilterHelper="%" + customerListColumnLastnameFilter + "%";
	        }
        }
        Condition conditionLOGIN_NAME = builder.like(User.LOGIN_NAME, customerListColumnUsernameFilterHelper);
        Condition conditionFIRST_NAME = builder.like(User.FIRST_NAME, customerListColumnFirstnameFilterHelper);
        Condition conditionLAST_NAME = builder.like(User.LAST_NAME, customerListColumnLastnameFilterHelper);
      
        
        
        // create a Sort
        Sort sort = builder.desc(User.LOGIN_NAME);
        if(sortLoginNameUp) sort = builder.desc(User.LOGIN_NAME);
        if(sortLoginNameDown) sort = builder.asc(User.LOGIN_NAME);
        if(sortCreateDateUp) sort = builder.desc(User.CREATED_DATE);
        if(sortCreateDateDown) sort = builder.asc(User.CREATED_DATE);
        if(sortEnableUp) sort = builder.desc(User.ENABLED);
        if(sortEnableDown) sort = builder.asc(User.ENABLED);
        // calculate limit and offSet
        if (calculateOffSetAndLimit){
          	offSet=(page-1)*rowCount;
        	limit=((page-1)*rowCount)+(rowCount*maxPages);
        	if ((resultCount-limit)<rowCount){
        		offSet=offSet+resultCount-limit;
        		limit=resultCount;
        		
        	}
        }
        calculateOffSetAndLimit=true;
        System.out.println("UserMangerController rowCount: " + rowCount);
        System.out.println("UserMangerController page: " + page);
        System.out.println("UserMangerController limit: " + limit);
        System.out.println("UserMangerController offSet: " + offSet);
        System.out.println("UserMangerController Username Filter: " + customerListColumnUsernameFilterHelper);
        System.out.println("UserMangerController Lastname Filter: " + customerListColumnLastnameFilterHelper);
        System.out.println("UserMangerController Firstname Filter: " + customerListColumnFirstnameFilterHelper);
        // create a query for a specific identity type using the previously created condition
        query = builder.createIdentityQuery(User.class);
        query.where(conditionLOGIN_NAME);
        query.where(conditionFIRST_NAME);
        query.where(conditionLAST_NAME);
        query.setOffset(offSet);
        query.setLimit(limit);
        query.sortBy(sort);
        // execute the query
    	List<User> resultUser = query.getResultList();
    	for (User user : resultUser) {
    	// do something
    	}
		return resultUser;
    }
   

	public List<Customer> getAllRegisteredUsers(){
    	//System.out.println("UserMangerController getAllRegisteredUsers");
    	List<Customer> resultCustomer = customerDao.getAllCustomers();
    	for (Customer customer : resultCustomer) {
    	// do something
    	}
		return resultCustomer;
    }
    
    @Admin                                                        
    public List<CustomerIdmGroupCombination> loadCustomerIdmGroups(){
    	//System.out.println("UserMangerController loadCustomerIdmGroups");
    	//System.out.println("Gruppen werden ermittelt");
    	CustomerIdmGroupCombination CIGC = new CustomerIdmGroupCombination();
    	List<CustomerIdmGroupCombination> CIGCs = new ArrayList<CustomerIdmGroupCombination>(); 
    	List<String>roleList = new ArrayList<String>();
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
	   	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
	    Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
	    queryg.where(conditiong);
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		if (group.getPath().startsWith(groupNameFilter) || groupNameFilter.contains("deleteFilter")) {
	    		CIGC = new CustomerIdmGroupCombination();
	    		CIGC.setIdmCreatedDate(group.getCreatedDate());
	    		CIGC.setIdmGroupName(group.getName());
	    		CIGC.setIdmGroupPath(group.getPath());
	    		////System.out.println("Gruppe: "+ group.getPath());
	    		// Customer count
	    		RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
	        	querygms.setParameter(GroupMembership.GROUP, group);
	        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
	        	CIGC.setCustomerCount(resultGroupMembership.size());
	        	// Grant Roles
	        	RelationshipQuery<GroupRole> query = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
	        	query.setParameter(GroupRole.GROUP, group); 
	    
	        	List<GroupRole> resultGrant = query.getResultList();
	        	for (GroupRole grant : resultGrant) {
	        		////System.out.println("Rollen: "+ grant.getRole().getName());
	        		roleList.add(grant.getRole().getName());
	        		if(CIGC.getRoles()==null){
	        			CIGC.setRoles(grant.getRole().getName());
	        		}else{
	        			if(!CIGC.getRoles().contains(grant.getRole().getName())){
	        				CIGC.setRoles(CIGC.getRoles()  + ", " + grant.getRole().getName());}
	        		}
	        		if(CIGC.getGroupIdmRolesCombinationStrings()==null){CIGC.setGroupIdmRolesCombinationStrings(new ArrayList<String>());CIGC.getGroupIdmRolesCombinationStrings().add(grant.getRole().getName());}else{CIGC.getGroupIdmRolesCombinationStrings().add(grant.getRole().getName());}

	        	}
	        	CIGCs.add(CIGC);
    		}
    	}
    	//System.out.println("beendet");
    	return CIGCs;
    		
    	
    }
    @Admin
    public List<CustomerIdmRolesCombination> loadCustomerIdmRoles(){
    	//System.out.println("UserMangerController loadCustomerIdmRoles");
    	//System.out.println("Rollen werden ermittelt");
    	CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
    	List<CustomerIdmRolesCombination> CIRCs = new ArrayList<CustomerIdmRolesCombination>(); 
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        IdentityQuery queryr = builder.createIdentityQuery(Role.class);
        Condition conditionr = builder.lessThan(Role.CREATED_DATE, new Date());
        queryr.where(conditionr);
    	List<Role> resultGrant = queryr.getResultList();
    	for (Role role : resultGrant) {
    		CIRC = new CustomerIdmRolesCombination();
    		CIRC.setIdmRoleName(role.getName());
    		CIRC.setIdmRoleCreatedDate(role.getCreatedDate());
    		CIRCs.add(CIRC);
    	}
    	//System.out.println("beendet");
    	return CIRCs;
    	
    }
    @Admin
    public List<CustomerIdmUserCombination> loadCustomerIdmUsers(){
    	//System.out.println("UserMangerController loadCustomerIdmUsers");    	
    	// get grant relation (role)
    	//System.out.println("Kunden-Daten werden ermittelt");
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	List<User> resultUser = getAllIdmUsers();
    	List<Customer> resultCustomer = getAllRegisteredUsers();
    	List<CustomerIdmUserCombination> CICs = new ArrayList<CustomerIdmUserCombination>();    	
    	CustomerIdmUserCombination CIC = new CustomerIdmUserCombination();
    	CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
    	Customer customerFound;
    	List<String>roleList = new ArrayList<String>();
    	List<String>groupList = new ArrayList<String>();
    	
    	for (User user : resultUser) {
    		//System.out.println("User: "+ user.getLoginName());
    		CIC = new CustomerIdmUserCombination();
    		CIC.setRolesList(new ArrayList<Role>());
    		CIC.setCustomerIdmRolesCombination(new ArrayList<CustomerIdmRolesCombination>());
    		CIC.setCustomerIdmRolesCombinationStrings(new ArrayList<String>());
        	CIC.setGroupsList(new ArrayList<Group>());
    		List<String>groupRoleList = new ArrayList<String>();
    		RelationshipQuery<Grant> query = relationshipManager.<Grant> createRelationshipQuery(Grant.class);
        	query.setParameter(Grant.ASSIGNEE, user);
        	List<Grant> resultGrant = query.getResultList();
        	// get grant relation (role)
        	for (Grant grant : resultGrant) {
        		CIRC = new CustomerIdmRolesCombination();
        		CIRC.setIdmRoleCreatedDate(grant.getRole().getCreatedDate());
        		CIRC.setIdmRoleName(grant.getRole().getName());
        		roleList.add(grant.getRole().getName());
        		if(CIC.getRoles()==null){CIC.setRoles(grant.getRole().getName());}else{CIC.setRoles(CIC.getRoles()  + ", " + grant.getRole().getName());}
        		if(CIC.getRolesList()==null){CIC.setRolesList(new ArrayList<Role>());CIC.getRolesList().add(grant.getRole());}else{CIC.getRolesList().add(grant.getRole());}
        		if(CIC.getCustomerIdmRolesCombination()==null){
        			CIC.setCustomerIdmRolesCombination(new ArrayList<CustomerIdmRolesCombination>());
        			CIC.getCustomerIdmRolesCombination().add(CIRC);
        			////System.out.println("Role added: "+ CIRC.getIdmRoleName());
        		}else{
        			CIC.getCustomerIdmRolesCombination().add(CIRC);
        			////System.out.println("Role added: "+ CIRC.getIdmRoleName());
        		}
        		if(CIC.getCustomerIdmRolesCombinationStrings()==null){CIC.setCustomerIdmRolesCombinationStrings(new ArrayList<String>());CIC.getCustomerIdmRolesCombinationStrings().add(grant.getRole().getName());
        		//System.out.println("Role added: "+ CIRC.getIdmRoleName());
        		}else{
        			CIC.getCustomerIdmRolesCombinationStrings().add(grant.getRole().getName());
        		//System.out.println("Role added: "+ CIRC.getIdmRoleName());}
        		}

        		
        	}
        
        	RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
        	querygms.setParameter(GroupMembership.MEMBER, user);
        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
        	// get group membership (group)
        	for (GroupMembership groupMembership : resultGroupMembership) {
        		//System.out.println("Gruppe: "+ groupMembership.getGroup().getPath());
        		groupList.add(groupMembership.getGroup().getName() + " -- " + groupMembership.getGroup().getPath());
        		if(CIC.getGroups()==null){
        			CIC.setGroups(groupMembership.getGroup().getName() + " -- " + groupMembership.getGroup().getPath());
        			}else{
        				CIC.setGroups(CIC.getGroups()  + ", " + groupMembership.getGroup().getName() + " -- " + groupMembership.getGroup().getPath());
        			}        		
        		if(CIC.getGroupsList()==null){CIC.setGroupsList(new ArrayList<Group>());CIC.getGroupsList().add(groupMembership.getGroup());}else{CIC.getGroupsList().add(groupMembership.getGroup());}

        	}
        	
        	RelationshipQuery<GroupRole> querygr = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
        	querygr.setParameter(GroupRole.ASSIGNEE, user);
        	List<GroupRole> resultGroupRole = querygr.getResultList();
        	// get grouprole relation (role)
        	for (GroupRole groupRole : resultGroupRole) {
        		if(CIC.getGroupRoles()==null){CIC.setGroupRoles(groupRole.getRole().getName() + " -- " + groupRole.getGroup().getPath());}else{CIC.setGroupRoles(CIC.getGroupRoles()  + ", " + groupRole.getRole().getName() + " -- " + groupRole.getGroup().getPath());}
        		if(groupRoleList==null){
        			groupRoleList = new ArrayList<String>();
        			groupRoleList.add(groupRole.getGroup().getPath() + " - " + groupRole.getRole().getName());
        			////System.out.println("GroupRole added: " + groupRole.getGroup().getPath() + " - " + groupRole.getRole().getName());
        		}else{
        			groupRoleList.add(groupRole.getGroup().getPath() + " - " + groupRole.getRole().getName());
        			////System.out.println("GroupRole added:  "+ groupRole.getGroup().getPath() + " - " + groupRole.getRole().getName());
        			}

        	}
        	Collections.sort(groupRoleList); 
        	CIC.setCustomerIdmGroupRolesCombinationStrings(groupRoleList);
        	if(user.isEnabled())CIC.setEnabled(true);
        	if(!user.isEnabled())CIC.setEnabled(false);
    		CIC.setIdmMail(user.getEmail());
    		CIC.setIdmCreatedDate(user.getCreatedDate());
    		CIC.setIdmExpirationDate(user.getExpirationDate());
    		CIC.setIdmFirstname(user.getFirstName());
    		CIC.setIdmLastname(user.getLastName());
    		CIC.setIdmUsername(user.getLoginName());
    		
    		//link to customer
    		customerFound=new Customer();
    		for (Customer customer : resultCustomer) {
    			if (customer.getUserName().equals(user.getLoginName())){
    				customerFound=customer;
    				break;
    			}
    		}
    		if (customerFound!=null){
	    		CIC.setCity(customerFound.getCity());
	    		if (customerFound.getCountry()!=null) CIC.setCountry(customerFound.getCountry().getName_de());
	    		CIC.setDailNumber(customerFound.getDailNumber());
	    		CIC.setDob(customerFound.getDob());
	    		CIC.setEmail(customerFound.getEmail());
	    		CIC.setFirstName(customerFound.getFirstName());
	    		CIC.setGender(customerFound.getGender());
	    		CIC.setLastName(customerFound.getLastName());
	    		CIC.setStreet(customerFound.getStreet());
	    		CIC.setStreetNumber(customerFound.getStreetNumber());
	    		CIC.setUserName(customerFound.getUserName());
	    		CIC.setUserSince(customerFound.getUserSince());
	    		CIC.setZip(customerFound.getZip());
    		}
    		CICs.add(CIC);
    	}
    	//System.out.println("beendet");
		return CICs;
    }
    @Admin
    public void removeGroup() {
    	try {
	    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
	    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
	    	iu.deleteGroup(currentGroupPath,identityManager,relationshipManager);
		    //System.out.println("delete Group: "+ currentGroupName);
		    allCustomerIdmGroups=null;
		    allCustomerIdmUsers=null;
		    allCustomerIdmRoles=null;
		    treeBean.setGroupsListToAdd(new ArrayList<Group>());
			treeBean.setGroupsListToDelete(new ArrayList<Group>());
			treeBean.init();
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    }	
    }
    @Admin
    public void removeRole() {
    	try {
	    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
	    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
	    	if(!currentRoleName.matches("administrator")){
		    	iu.deleteRole(currentRoleName,identityManager,relationshipManager);
			    //System.out.println("delete Role: "+ currentRoleName);
			    allCustomerIdmGroups=null;
			    allCustomerIdmUsers=null;
			    allCustomerIdmRoles=null;
	    	} else {
	        	//System.out.println("Role administrator not removeable");	
	    	}
	    	setGroupIdmRolesCombinationStrings(loadCustomerIdmRolesCombinationStrings());
	    } catch (Exception exception) {
	        exception.printStackTrace();
	    }	
    } 
    @Admin
    public void removeUser() {
    	try {
	    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
	    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
	    	if(!currentUserName.matches("administrator")){
	    		// delete idm user
	    		iu.deleteUser(currentUserName,identityManager,relationshipManager);
	    		//delete cutomer
	    		Customer customer = managedBeanCustomerDao.getCustomerByUserName(currentUserName);
	    		managedBeanCustomerDao.deleteCustomerByUserName(currentUserName);
	    		//clean mailserver
	    		//Check for existence
	     	    Integer exitStatus=createMailAdress.checkAccount(customer.getEmail());
	     	    if (exitStatus==0) {
	     	    	//log.info("mail forwarding in configuration");
	     	    	//internalMailAccount = internalMailAccount + "@letterbee.de";
	     	    	exitStatus=createMailAdress.deleteAccount(customer.getInternalEmail(), customer.getEmail());
	     	    	log.info("mail-address deletetd");
	     	    } else {
	     	    	log.info("external Mail-Address dont exist");
	     	    }
	    		//System.out.println("delete User: "+ currentUserName);
	        	allCustomerIdmGroups=null;
	        	//allCustomerIdmUsers=null;
	        	resetIdmUser();
	        	allCustomerIdmRoles=null;
	        	//if(allCustomerIdmUsers==null)setAllCustomerIdmUsers(loadCustomerIdmUsers());
	        	treeBean.setGroupsListToAdd(new ArrayList<Group>());
				treeBean.setGroupsListToDelete(new ArrayList<Group>());
	        	treeBean.init();
	    	} else {
	        	//System.out.println("User administrator not removeable");
	
	    	}
    	} catch (Exception exception) {
	        exception.printStackTrace();
	    }	
    } 
    @Admin
    public void editUser() {
    	//System.out.println("edit User fuer: "+ currentUserName);
    	iu.editUser(currentUserName,treeBeanDelete.getGroupNamePath(),treeBeanAdd.getGroupNamePath(),treeBeanGroupRole.getGroupNamePath(),selectedCustomerIdmRolesCombinationStrings,selectedCustomerIdmGroupRolesCombinationStrings,activateForAllDeleteSubGroups,activateForAllAddSubGroups);
    	//System.out.println("edit User beendet: "+ currentUserName);
    	allCustomerIdmGroups=null;
    	//allCustomerIdmUsers=null;
    	resetIdmUser();
    	allCustomerIdmRoles=null;
    	//if(allCustomerIdmUsers==null)setAllCustomerIdmUsers(loadCustomerIdmUsers());
    	treeBean.setGroupsListToAdd(new ArrayList<Group>());
		treeBean.setGroupsListToDelete(new ArrayList<Group>());
    	treeBean.init();
    } 
    @Admin
    public void editGroup() {
    	//System.out.println("edit Group fuer: "+ currentGroupPath);
    	iu.editGroup(currentGroupPath,selectedGroupIdmRolesCombinationStrings);
    	//System.out.println("edit Group beendet: "+ currentGroupPath);
    	allCustomerIdmGroups=null;
    	//allCustomerIdmUsers=null;
    	resetIdmUser();
    	allCustomerIdmRoles=null;
    } 
    public void deleteGroupFilter() {
    	//System.out.println("UserMangerController deleteGroupFilter");   
    	groupNameFilter="deleteFilter";
    	//System.out.println("Reset filter " + groupNameFilter + " added!");
    	treeBean.setGroupsListToAdd(new ArrayList<Group>());
		treeBean.setGroupsListToDelete(new ArrayList<Group>());
    	treeBean.init();
    	allCustomerIdmGroups=null;
    	//allCustomerIdmUsers=null;
    	resetIdmUser();
    	allCustomerIdmRoles=null;
    	//preRender(null);
    	//FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("tableForm:groupTab");
    	//FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("treeForm:tree");
    }
    public void onBeforeSelectionChanged() {
    	//System.out.println("UserMangerController onBeforeSelectionChanged");
    	if (selectionChangeEventOld!=null){
	    	if (selectionChangeEventOld.getComponent().getId().matches("treePanelDeleteGroup")) {
	    		treeBeanDelete.selectionChanged(selectionChangeEventOld);
	    	}
	    	if (selectionChangeEventOld.getComponent().getId().matches("treePanelAddGroup")) {
	    		treeBeanAdd.selectionChanged(selectionChangeEventOld);
	    	}
    		
    	}
    }
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) { 
    	System.out.println("UserMangerController selectionChanged");
        // considering only single selection 
    	try {
    		
    		TreeNode currentSelection=null;
    		if (selectionChangeEvent.getComponent().getId().matches("treePanelGroupRoleEdit")) {
	    		treeBeanGroupRole.selectionChanged(selectionChangeEvent);
	        	currentSelection = treeBeanGroupRole.getCurrentSelection();
	    	}
	    	if (selectionChangeEvent.getComponent().getId().matches("treePanelDeleteGroup")) {
	    		treeBeanDelete.selectionChanged(selectionChangeEvent);
	        	currentSelection = treeBeanDelete.getCurrentSelection();
	    	}
	    	if (selectionChangeEvent.getComponent().getId().matches("treePanelAddGroup")) {
	    		treeBeanAdd.selectionChanged(selectionChangeEvent);
	        	currentSelection = treeBeanAdd.getCurrentSelection();
	    	}
	    	if (selectionChangeEvent.getComponent().getId().matches("tree")) {
	    		treeBean.selectionChanged(selectionChangeEvent);
	        	currentSelection = treeBean.getCurrentSelection();
	    	}
	    	if (currentSelection!=null){
		    	String groupName= currentSelection.toString();
		    	System.out.println("UserMangerController groupName " + groupName);
		    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
		    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
		    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
		        IdentityQuery queryg = builder.createIdentityQuery(Role.class);
		        Condition conditiong = builder.like(Group.NAME, groupName);
		        queryg.where(conditiong);
				List<Group> result = queryg.getResultList();
				Group subgroup = result.get(0);
				setGroupNameFilter(subgroup.getPath());
				setGroupNameFilterOld(subgroup.getPath());
		        //System.out.println("New Filter " + groupNameFilter + " added!");
	    	}
	    	//for filter 
	    	//allCustomerIdmGroups=null;
	    	//allCustomerIdmUsers=null;
	    	//allCustomerIdmRoles=null;
    	 } catch (Exception ime) {
    		 //System.out.println("Problem occure during TreeSelection");
    	 }
    	
    	   
    } 
    public GroupTreeBean getTreeBean() {
    	//System.out.println("UserMangerController getTreeBean");
		return treeBean;
	}
	public void setTreeBean(GroupTreeBean treeBean) {
		//System.out.println("UserMangerController setTreeBean");
		this.treeBean = treeBean;
	}
	public GroupTreeBean getTreeBeanAdd() {
		//System.out.println("UserMangerController getTreeBeanAdd");
		return treeBeanAdd;
	}
	public void setTreeBeanAdd(GroupTreeBean treeBeanAdd) {
		//System.out.println("UserMangerController setTreeBeanAdd");
		this.treeBeanAdd = treeBeanAdd;
	}
	public GroupTreeBean getTreeBeanDelete() {
		//System.out.println("UserMangerController getTreeBeanDelete");
		return treeBeanDelete;
	}
	public void setTreeBeanDelete(GroupTreeBean treeBeanDelete) {
		//System.out.println("UserMangerController setTreeBeanDelete");
		this.treeBeanDelete = treeBeanDelete;
	}
	public GroupTreeBean getTreeBeanGroupRole() {
		//System.out.println("UserMangerController getTreeBeanGroupRole");
		return treeBeanGroupRole;
	}
	public void setTreeBeanGroupRole(GroupTreeBean treeBeanGroupRole) {
		//System.out.println("UserMangerController setTreeBeanGroupRole");
		this.treeBeanGroupRole = treeBeanGroupRole;
	}
	public void select(){
		//System.out.println("UserMangerController select");
    	if (groupNameFilterOld!=null ){
        	if (!groupNameFilterOld.matches(groupNameFilter) ){
	    		groupNameFilter=groupNameFilterOld;
	    		//System.out.println("select: " + groupNameFilterOld);
	        	allCustomerIdmGroups=null;
	        	//allCustomerIdmUsers=null;
	        	resetIdmUser();
	        	allCustomerIdmRoles=null;
	            //preRender(null);
        	}
    	}
    	
    }
    public void changeTab(ActionEvent ae){
    	selectedTab=ae.getComponent().getId();
    	//System.out.println("Tab changed to " + selectedTab);
    }
    
    public void processItemChange(ItemChangeEvent p_event) throws AbortProcessingException 
    {
    	//System.out.println("ItemChangeEvent: " + p_event.getOldItemName() + " => " + p_event.getNewItemName());   
    	setSelectedTab(p_event.getNewItemName());
    	//if(p_event.getNewItemName().contains("userTab"))allCustomerIdmUsers=null;
    	//if(p_event.getNewItemName().contains("groupTab"))allCustomerIdmGroups=null;
    	//if(p_event.getNewItemName().contains("roleTab"))allCustomerIdmRoles=null;
   // FacesContext.getCurrentInstance().renderResponse(); 
    }  


	public String getCurrentUserName() {
		currentUserName=usersCustomersBean.getCurrentUserCustomerName();
		System.out.println("UserMangerController getCurrentUserName");
		if (currentUserName!=null)System.out.println("get CurrentUserName: " + currentUserName);
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		System.out.println("UserMangerController setCurrentUserName");
		if (currentUserName!=null)System.out.println("set CurrentUserName: " + currentUserName);
		this.currentUserName = currentUserName;
	}

	public CustomerIdmGroupCombination getCurrentCustomerIdmGroup() {
		//System.out.println("UserMangerController getCurrentCustomerIdmGroup");
		//if (currentCustomerIdmUser!=null)//System.out.println("Get currentCustomerIdmUser: " + currentCustomerIdmUser.getIdmUsername());
		////System.out.println("getCurrentCustomerIdmUser");
		return currentCustomerIdmGroup;
	}

	public void setCurrentCustomerIdmGroup(CustomerIdmGroupCombination currentCustomerIdmGroup) {
		//System.out.println("UserMangerController setCurrentCustomerIdmGroup");
		//System.out.println("setCurrentCustomerIdmGroup" + currentCustomerIdmGroup.getIdmGroupName());
		this.currentCustomerIdmGroup = currentCustomerIdmGroup;
	}
	public CustomerIdmUserCombination getCurrentCustomerIdmUser() {
		//System.out.println("UserMangerController getCurrentCustomerIdmUser");
		//if (currentCustomerIdmUser!=null)//System.out.println("Get currentCustomerIdmUser: " + currentCustomerIdmUser.getIdmUsername());
		////System.out.println("getCurrentCustomerIdmUser");
		return currentCustomerIdmUser;
	}

	public void setCurrentCustomerIdmUser(CustomerIdmUserCombination currentCustomerIdmUser) {
		System.out.println("UserMangerController setCurrentCustomerIdmUser");
		//if (currentCustomerIdmUser!=null)//System.out.println("Set currentCustomerIdmUser: "+ currentCustomerIdmUser.getIdmUsername());
		//System.out.println("setCurrentCustomerIdmUser " + currentCustomerIdmUser.getIdmUsername());
		//System.out.println("setCurrentCustomerIdmUsers GroupsList() size " + currentCustomerIdmUser.getGroupsList().size());

    	activateForAllDeleteSubGroups=true;
    	activateForAllAddSubGroups=true;
    	setCustomerIdmRolesCombination(loadCustomerIdmRolesCombination());
    	setCustomerIdmRolesCombinationStrings(loadCustomerIdmRolesCombinationStrings());
    	setGroupIdmRolesCombinationStrings(loadCustomerIdmRolesCombinationStrings());
    	setCustomerIdmGroupRolesCombinationStrings(loadCustomerIdmGroupRolesCombinationStrings());
		treeBeanAdd.setGroupsListToAdd(currentCustomerIdmUser.getGroupsList());
		treeBeanAdd.setGroupsListToDelete(null);
		treeBeanDelete.setGroupsListToDelete(currentCustomerIdmUser.getGroupsList());
		treeBeanDelete.setGroupsListToAdd(null);
		treeBeanGroupRole.setGroupsListToDelete(currentCustomerIdmUser.getGroupsList());
		treeBeanGroupRole.setGroupsListToAdd(null);
		treeBeanAdd.init();
		treeBeanDelete.init();
		treeBeanGroupRole.init();
		this.currentCustomerIdmUser = currentCustomerIdmUser;
	}

	public Integer getCurrentIndex() {
		//System.out.println("UserMangerController getCurrentIndex");
		//if (currentIndex!=null)System.out.println("Get Index: "+ currentIndex);
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		//System.out.println("UserMangerController setCurrentIndex");
		//if (currentIndex!=null)//System.out.println("Set Index: "+ currentIndex);
		
		this.currentIndex = currentIndex;
	}
	@Admin
	public List<CustomerIdmUserCombination> getAllCustomerIdmUsers() {
		//System.out.println("UserMangerController getAllCustomerIdmUsers");
    	if(allCustomerIdmUsers==null){
    		//System.out.println("init allCustomerIdmUsers from UserMangerController");
    		allCustomerIdmUsers=loadCustomerIdmUsers();
    	}
		return allCustomerIdmUsers;		
	}

	public List<CustomerIdmGroupCombination> getAllCustomerIdmGroups() {
		//System.out.println("UserMangerController getAllCustomerIdmGroups");
		if(allCustomerIdmGroups==null){
    		//System.out.println("init allCustomerIdmGroups from UserMangerController");
    		allCustomerIdmGroups=loadCustomerIdmGroups();
    	}
		return allCustomerIdmGroups;
	}
	
	public List<CustomerIdmRolesCombination> getAllCustomerIdmRoles() {
		//System.out.println("UserMangerController getAllCustomerIdmRoles");
		if(allCustomerIdmRoles==null){
    		//System.out.println("init allCustomerIdmRoles from UserMangerController");
    		allCustomerIdmRoles=loadCustomerIdmRoles();
    	}
		return allCustomerIdmRoles;
	}
	public String getCurrentGroupName() {
		//System.out.println("UserMangerController getCurrentGroupName");
		////System.out.println("getCurrentGroupName: "+ selectedTab);
		return currentGroupName;
	}
	public void setCurrentGroupName(String currentGroupName) {
		//System.out.println("UserMangerController setCurrentGroupName");
		this.currentGroupName = currentGroupName;
		////System.out.println("setCurrentGroupName: "+ selectedTab);
	}
	public String getGroupNameFilter() {
		//System.out.println("UserMangerController getGroupNameFilter");
		return groupNameFilter;
	}
	public void setGroupNameFilter(String groupNameFilter) {
		//System.out.println("UserMangerController setGroupNameFilter");
		this.groupNameFilter = groupNameFilter;
	}
	public String getSelectedTab() {
		//System.out.println("UserMangerController getSelectedTab");
		////System.out.println("getSelectedTab: "+ selectedTab);
		return selectedTab;
		
	}
	public void setSelectedTab(String selectedTab) {
		//System.out.println("UserMangerController setSelectedTab");
		this.selectedTab = selectedTab;
		////System.out.println("setSelectedTab: "+ selectedTab);
	}
	public String getGroupNameFilterOld() {
		//System.out.println("UserMangerController getGroupNameFilterOld");
		return groupNameFilterOld;
	}
	public void setGroupNameFilterOld(String groupNameFilterOld) {
		//System.out.println("UserMangerController setGroupNameFilterOld");
		this.groupNameFilterOld = groupNameFilterOld;
	}
	public String getCurrentGroupPath() {
		//System.out.println("UserMangerController getCurrentGroupPath");
		return currentGroupPath;
	}
	public void setCurrentGroupPath(String currentGroupPath) {
		//System.out.println("UserMangerController setCurrentGroupPath");
		this.currentGroupPath = currentGroupPath;
	}
	public CustomerIdmRolesCombination getSelectCustomerIdmRolesCombination() {
		//System.out.println("UserMangerController CustomerIdmRolesCombination");
		return selectCustomerIdmRolesCombination;
	}
	public void setSelectCustomerIdmRolesCombination(
			CustomerIdmRolesCombination selectCustomerIdmRolesCombination) {
		//System.out.println("UserMangerController setSelectCustomerIdmRolesCombination");
		this.selectCustomerIdmRolesCombination = selectCustomerIdmRolesCombination;
	}
	public Boolean getActivateForAllAddSubGroups() {
		//System.out.println("UserMangerController getActivateForAllAddSubGroups");
		return activateForAllAddSubGroups;
	}
	public void setActivateForAllAddSubGroups(Boolean activateForAllAddSubGroups) {
		//System.out.println("UserMangerController setActivateForAllAddSubGroups");
		this.activateForAllAddSubGroups = activateForAllAddSubGroups;
	}
	public void checkBoxForAllAddSubgroupsChanged(ValueChangeEvent ev) {
		  //System.out.println("UserMangerController checkBoxForAllAddSubgroupsChanged");
		  Boolean activateForAllAddSubGroups = (Boolean) ev.getNewValue();
		  //System.out.println("checkBoxForAllAddSubgroupsChanged" + activateForAllAddSubGroups);
		  if (activateForAllAddSubGroups != null) {
		    this.activateForAllAddSubGroups = activateForAllAddSubGroups;
		  }
		  //FacesContext.getCurrentInstance().renderResponse();
	}
	public Boolean getActivateForAllDeleteSubGroups() {
		//System.out.println("UserMangerController getActivateForAllDeleteSubGroups");
		return activateForAllDeleteSubGroups;
	}
	public void setActivateForAllDeleteSubGroups(Boolean activateForAllDeleteSubGroups) {
		//System.out.println("UserMangerController setActivateForAllDeleteSubGroups");
		this.activateForAllDeleteSubGroups = activateForAllDeleteSubGroups;
	}
	public void checkBoxForAllDeleteSubgroupsChanged(ValueChangeEvent ev) {
		//System.out.println("UserMangerController checkBoxForAllDeleteSubgroupsChanged");
		  Boolean activateForAllDeleteSubGroups = (Boolean) ev.getNewValue();
		  //System.out.println("checkBoxForAllDeleteSubgroupsChanged" + activateForAllDeleteSubGroups);
		  if (activateForAllDeleteSubGroups != null) {
		    this.activateForAllDeleteSubGroups = activateForAllDeleteSubGroups;
		  }
		  //FacesContext.getCurrentInstance().renderResponse();
	}
    public void selectionChangedEditUser(ValueChangeEvent evt){
        try {
           
            //System.out.println("Calling Selection Changed Listener");
            //System.out.println("New value is:"+evt.getNewValue());
            //System.out.println("Old value is:"+evt.getOldValue());
            Object newObj = evt.getNewValue();
            if (newObj instanceof List) {
                //System.out.println("List Object");
                List list = (List)newObj;
                if (list != null && list.size() > 0) {/*
                    //System.out.println("Size of List:" + list.size());
                    //System.out.println("contents :" + list);
                    for (Object object : list) {
                        //System.out.println("obj : " + object);
                        //System.out.println("hash code: " + object.hashCode());
                        if (object instanceof ServicesVO) {
                            ServicesVO ss= (ServicesVO)object;
                            //System.out.println("Service :" + ss.getName());
                        } else if (object instanceof Integer) {
                            //System.out.println("Integer vo");
                        } else if (object instanceof String){
                            String st = (String)object;
                            //System.out.println("String value: "+st);
                        } else {
                            //System.out.println("Not object");
                        }   
                    }
                */} else {
                    //System.out.println("List is null!");
                }
            }
            } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
   
	public String getSelectedRole() { 
    	//System.out.println("UserMangerController getSelectedRole");
		return selectedRole;
	}
	public void setSelectedRole(String selectedRole) {
    	//System.out.println("UserMangerController setSelectedRole");
		this.selectedRole = selectedRole;
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	IdentityQuery<Role> queryg = identityManager.<Role> createIdentityQuery(Role.class);
    	queryg.setParameter(Role.NAME, selectedRole);
    	List<Role> resultGrant = queryg.getResultList();
    	for (Role role : resultGrant) {
    		////System.out.println("found role " + role.getName());
    		if(role.getName().equals(selectedRole)){
    			//System.out.println(" role " + role.getName());
    			selectCustomerIdmRolesCombination=new CustomerIdmRolesCombination();
	    		selectCustomerIdmRolesCombination.setIdmRoleName(role.getName());
	    		selectCustomerIdmRolesCombination.setIdmRoleCreatedDate(role.getCreatedDate());
    		}
    		
    	}
	}
	public List<Role> getRolesList() {
    	//System.out.println("UserMangerController getRolesList");
		return rolesList;
	}
	public void setRolesList(List<Role> rolesList) {
    	//System.out.println("UserMangerController setRolesList");
		this.rolesList = rolesList;
	}
	public List<CustomerIdmRolesCombination> getCustomerIdmRolesCombination() {
    	//System.out.println("UserMangerController getCustomerIdmRolesCombination");
		return customerIdmRolesCombination;
	}
	public void setCustomerIdmRolesCombination(
			List<CustomerIdmRolesCombination> customerIdmRolesCombination) {
    	//System.out.println("UserMangerController setCustomerIdmRolesCombination");
		this.customerIdmRolesCombination = customerIdmRolesCombination;
	}
	public List<CustomerIdmRolesCombination> getSelectedCustomerIdmRolesCombination() {
    	//System.out.println("UserMangerController getSelectedCustomerIdmRolesCombination");
		////System.out.println("call getter");
		if(currentCustomerIdmUser!=null){
			////System.out.println("call getter");
			selectedCustomerIdmRolesCombination=new ArrayList<CustomerIdmRolesCombination>();
			selectedCustomerIdmRolesCombination=currentCustomerIdmUser.getCustomerIdmRolesCombination();
					
		}
		return selectedCustomerIdmRolesCombination;
	}
	public void setSelectedCustomerIdmRolesCombination(
			List<CustomerIdmRolesCombination> selectedCustomerIdmRolesCombination) {
    	//System.out.println("UserMangerController selectedCustomerIdmRolesCombination");
		////System.out.println("call setter");
		if(currentCustomerIdmUser!=null){
			////System.out.println("call setter");
			selectedCustomerIdmRolesCombination=new ArrayList<CustomerIdmRolesCombination>();
			selectedCustomerIdmRolesCombination=currentCustomerIdmUser.getCustomerIdmRolesCombination();
			
			
		}
		this.selectedCustomerIdmRolesCombination = selectedCustomerIdmRolesCombination;
	}
	
	public List<String> getSelectedCustomerIdmGroupRolesCombinationStrings() {
    	//System.out.println("UserMangerController getSelectedCustomerIdmGroupRolesCombinationStrings");
	//	//System.out.println("call getter selectedCustomerIdmRolesCombinationStrings");
		if(currentCustomerIdmUser!=null){
	//		//System.out.println("call getter for " + currentCustomerIdmUser.getIdmUsername());
			selectedCustomerIdmGroupRolesCombinationStrings=new ArrayList<String>();
			selectedCustomerIdmGroupRolesCombinationStrings=currentCustomerIdmUser.getCustomerIdmGroupRolesCombinationStrings();
			
			
		}
		return selectedCustomerIdmGroupRolesCombinationStrings;
	}
	public void setSelectedCustomerIdmGroupRolesCombinationStrings(
			List<String> selectedCustomerIdmGroupRolesCombinationStrings) {
    	//System.out.println("UserMangerController selectedCustomerIdmGroupRolesCombinationStrings");
		if(currentCustomerIdmUser!=null){
			currentCustomerIdmUser.setCustomerIdmGroupRolesCombinationStrings(selectedCustomerIdmGroupRolesCombinationStrings);
			this.selectedCustomerIdmGroupRolesCombinationStrings = selectedCustomerIdmGroupRolesCombinationStrings;
		}
	}
	
	
	
	public List<String> getCustomerIdmGroupRolesCombinationStrings() {
    	//System.out.println("UserMangerController getCustomerIdmGroupRolesCombinationStrings");
		return customerIdmGroupRolesCombinationStrings;
	}
	public void setCustomerIdmGroupRolesCombinationStrings(
			List<String> customerIdmGroupRolesCombinationStrings) {
    	//System.out.println("UserMangerController setCustomerIdmGroupRolesCombinationStrings");
		this.customerIdmGroupRolesCombinationStrings = customerIdmGroupRolesCombinationStrings;
	}
	
	
	public List<String> getSelectedCustomerIdmRolesCombinationStrings() {
    	//System.out.println("UserMangerController getSelectedCustomerIdmRolesCombinationStrings");
	//	//System.out.println("call getter selectedCustomerIdmRolesCombinationStrings");
		if(currentCustomerIdmUser!=null){
	//		//System.out.println("call getter for " + currentCustomerIdmUser.getIdmUsername());
			selectedCustomerIdmRolesCombinationStrings=new ArrayList<String>();
			selectedCustomerIdmRolesCombinationStrings=currentCustomerIdmUser.getCustomerIdmRolesCombinationStrings();
			
			
		}
		return selectedCustomerIdmRolesCombinationStrings;
	}
	public void setSelectedCustomerIdmRolesCombinationStrings(
			List<String> selectedCustomerIdmRolesCombinationStrings) {
    	//System.out.println("UserMangerController selectedCustomerIdmRolesCombinationStrings");
	//	//System.out.println("call setter selectedCustomerIdmRolesCombinationStrings");
	//	//System.out.println(selectedCustomerIdmRolesCombinationStrings.toString());
		if(currentCustomerIdmUser!=null){
		currentCustomerIdmUser.setCustomerIdmRolesCombinationStrings(selectedCustomerIdmRolesCombinationStrings);
		this.selectedCustomerIdmRolesCombinationStrings = selectedCustomerIdmRolesCombinationStrings;
		}
	}
	
	
	
	public List<String> getCustomerIdmRolesCombinationStrings() {
    	//System.out.println("UserMangerController getCustomerIdmRolesCombinationStrings");
		return customerIdmRolesCombinationStrings;
	}
	public void setCustomerIdmRolesCombinationStrings(
			List<String> customerIdmRolesCombinationStrings) {
    	//System.out.println("UserMangerController setCustomerIdmRolesCombinationStrings");
		this.customerIdmRolesCombinationStrings = customerIdmRolesCombinationStrings;
	}
	
	public List<String> getSelectedGroupIdmRolesCombinationStrings() {
    	//System.out.println("UserMangerController getSelectedGroupIdmRolesCombinationStrings");
		if(currentCustomerIdmGroup!=null){
	//		//System.out.println("call getter for " + currentCustomerIdmUser.getIdmUsername());
			selectedGroupIdmRolesCombinationStrings=new ArrayList<String>();
			selectedGroupIdmRolesCombinationStrings=currentCustomerIdmGroup.getGroupIdmRolesCombinationStrings();
			
			
		}
		return selectedGroupIdmRolesCombinationStrings;
	}	
	public void setSelectedGroupIdmRolesCombinationStrings(
			List<String> selectedGroupIdmRolesCombinationStrings) {
    	//System.out.println("UserMangerController selectedGroupIdmRolesCombinationStrings");
	//	//System.out.println("call setter selectedCustomerIdmRolesCombinationStrings");
	//	//System.out.println(selectedCustomerIdmRolesCombinationStrings.toString());
			currentCustomerIdmGroup.setGroupIdmRolesCombinationStrings(selectedGroupIdmRolesCombinationStrings);
		this.selectedGroupIdmRolesCombinationStrings = selectedGroupIdmRolesCombinationStrings;
	}
	
	
	public List<String> getGroupIdmRolesCombinationStrings() {
    	//System.out.println("UserMangerController getGroupIdmRolesCombinationStrings");
		return groupIdmRolesCombinationStrings;
	}
	public void setGroupIdmRolesCombinationStrings(
			List<String> groupIdmRolesCombinationStrings) {
    	//System.out.println("UserMangerController setGroupIdmRolesCombinationStrings");
		this.groupIdmRolesCombinationStrings = groupIdmRolesCombinationStrings;
	}
	
	public Integer getSelectedDeleteNodeID() {
    	//System.out.println("UserMangerController selectedDeleteNodeID");
		return selectedDeleteNodeID;
	}
	public void setSelectedDeleteNodeID(Integer selectedDeleteNodeID) {
    	//System.out.println("UserMangerController setSelectedDeleteNodeID");
		this.selectedDeleteNodeID = selectedDeleteNodeID;
	}
	public String getCurrentRoleName() {
		//System.out.println("UserMangerController getCurrentRoleName");
		return currentRoleName;
	}
	public void setCurrentRoleName(String currentRoleName) {
		//System.out.println("UserMangerController setCurrentRoleName");
		this.currentRoleName = currentRoleName;
	}
	public String getCustomerListColumnUsernameFilter() {
		return customerListColumnUsernameFilter;
	}
	public void setCustomerListColumnUsernameFilter(
			String customerListColumnUsernameFilter) {
		//allCustomerIdmUsers=null;
    	if(!this.customerListColumnUsernameFilter.matches(customerListColumnUsernameFilter))resetIdmUser();
		this.customerListColumnUsernameFilter = customerListColumnUsernameFilter;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	 public int getMaxPages() {
			return maxPages;
		}
		public void setMaxPages(int maxPages) {
			this.maxPages = maxPages;
		}
		boolean calculateOffSetAndLimit=true;
	    
	    
	  
	    
	    public int getRowCount() {
			return rowCount;
		}
		public void setRowCount(int rowCount) {
			this.rowCount = rowCount;
		}

		public Integer getCurrentCustomerIdmUserIndex() {
			return currentCustomerIdmUserIndex;
		}

		public void setCurrentCustomerIdmUserIndex(
				Integer currentCustomerIdmUserIndex) {
			if(currentCustomerIdmUserIndex!=null){
				setCurrentCustomerIdmUser(allCustomerIdmUsers.get(currentCustomerIdmUserIndex));
				log.info("currentCustomerIdmUserIndex: " + currentCustomerIdmUserIndex);
			}
			this.currentCustomerIdmUserIndex = currentCustomerIdmUserIndex;
		}
		public void toggleSort(String sortType){
			//log.info("sortLoginNameUp: " + sortLoginNameUp);
			//log.info("sortLoginNameDown: " + sortLoginNameDown);
			if(sortType.matches("sortLoginNameUp")&&sortLoginNameUp==false) {setSortFalse();sortLoginNameUp=true;sortLoginNameDown=false;}
			if(sortType.matches("sortLoginNameDown")&&sortLoginNameDown==false) {setSortFalse();sortLoginNameUp=false;sortLoginNameDown=true;}
			if(sortType.matches("sortCreateDateUp")&&sortCreateDateUp==false) {setSortFalse();sortCreateDateUp=true;sortCreateDateDown=false;}
			if(sortType.matches("sortCreateDateDown")&&sortCreateDateDown==false) {setSortFalse();sortCreateDateUp=false;sortCreateDateDown=true;}
			if(sortType.matches("sortEnableUp")&&sortEnableUp==false) {setSortFalse();sortEnableUp=true;sortEnableDown=false;}
			if(sortType.matches("sortEnableDown")&&sortEnableDown==false) {setSortFalse();sortEnableUp=false;sortEnableDown=true;}
			//log.info("sortLoginNameUp: " + sortLoginNameUp);
			//log.info("sortLoginNameDown: " + sortLoginNameDown);

			resetIdmUser();
		}
		public void setSortFalse(){
			sortMailUp=false;
	    	sortMailUp=false;
	    	sortEnableUp=false;
	    	sortCreateDateUp=false;
	    	sortLoginNameUp=false;
	    	sortExpiryDateUp=false;
	    	sortFirstNameUp=false;
	    	sortLastNameUp=false;
	    	sortLoginNameDown=false;
	    	sortMailDown=false;
	    	sortEnableDown=false;
	    	sortCreateDateDown=false;
	    	sortExpiryDateDown=false;
	    	sortFirstNameDown=false;
	    	sortLastNameDown=false;
		}

		public Boolean getSortCreateDateUp() {
			return sortCreateDateUp;
		}

		public void setSortCreateDateUp(Boolean sortCreateDateUp) {
			this.sortCreateDateUp = sortCreateDateUp;
		}

		public Boolean getSortCreateDateDown() {
			return sortCreateDateDown;
		}

		public void setSortCreateDateDown(Boolean sortCreateDateDown) {
			this.sortCreateDateDown = sortCreateDateDown;
		}

		public Boolean getSortMailUp() {
			return sortMailUp;
		}

		public void setSortMailUp(Boolean sortMailUp) {
			this.sortMailUp = sortMailUp;
		}

		public Boolean getSortMailDown() {
			return sortMailDown;
		}

		public void setSortMailDown(Boolean sortMailDown) {
			this.sortMailDown = sortMailDown;
		}
		 public Boolean getSortEnableUp() {
				return sortEnableUp;
			}

			public void setSortEnableUp(Boolean sortEnableUp) {
				this.sortEnableUp = sortEnableUp;
			}

			public Boolean getSortExpiryDateUp() {
				return sortExpiryDateUp;
			}

			public void setSortExpiryDateUp(Boolean sortExpiryDateUp) {
				this.sortExpiryDateUp = sortExpiryDateUp;
			}

			public Boolean getSortFirstNameUp() {
				return sortFirstNameUp;
			}

			public void setSortFirstNameUp(Boolean sortFirstNameUp) {
				this.sortFirstNameUp = sortFirstNameUp;
			}

			public Boolean getSortLastNameUp() {
				return sortLastNameUp;
			}

			public void setSortLastNameUp(Boolean sortLastNameUp) {
				this.sortLastNameUp = sortLastNameUp;
			}

			public Boolean getSortEnableDown() {
				return sortEnableDown;
			}

			public void setSortEnableDown(Boolean sortEnableDown) {
				this.sortEnableDown = sortEnableDown;
			}

			public Boolean getSortExpiryDateDown() {
				return sortExpiryDateDown;
			}

			public void setSortExpiryDateDown(Boolean sortExpiryDateDown) {
				this.sortExpiryDateDown = sortExpiryDateDown;
			}

			public Boolean getSortFirstNameDown() {
				return sortFirstNameDown;
			}

			public void setSortFirstNameDown(Boolean sortFirstNameDown) {
				this.sortFirstNameDown = sortFirstNameDown;
			}

			public Boolean getSortLastNameDown() {
				return sortLastNameDown;
			}

			public void setSortLastNameDown(Boolean sortLastNameDown) {
				this.sortLastNameDown = sortLastNameDown;
			}


			public Boolean getSortLoginNameUp() {
				return sortLoginNameUp;
			}


			public void setSortLoginNameUp(Boolean sortLoginNameUp) {
				this.sortLoginNameUp = sortLoginNameUp;
			}


			public Boolean getSortLoginNameDown() {
				return sortLoginNameDown;
			}


			public void setSortLoginNameDown(Boolean sortLoginNameDown) {
				this.sortLoginNameDown = sortLoginNameDown;
			}


			public String getCustomerListColumnFirstnameFilter() {
				return customerListColumnFirstnameFilter;
			}


			public void setCustomerListColumnFirstnameFilter(
					String customerListColumnFirstnameFilter) {
				this.customerListColumnFirstnameFilter = customerListColumnFirstnameFilter;
			}
			 public String getCustomerListColumnLastnameFilter() {
					return customerListColumnLastnameFilter;
			}


			public void setCustomerListColumnLastnameFilter(
					String customerListColumnLastnameFilter) {
				this.customerListColumnLastnameFilter = customerListColumnLastnameFilter;
			}
			  public UsersBean getUsersBean() {
					return usersBean;
				}


				public void setUsersBean(UsersBean usersBean) {
					this.usersBean = usersBean;
				}
				public UsersCustomersBean getUsersCustomersBean() {
					return usersCustomersBean;
				}


				public void setUsersCustomersBean(UsersCustomersBean usersCustomersBean) {
					this.usersCustomersBean = usersCustomersBean;
				}
}
