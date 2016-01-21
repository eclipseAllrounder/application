package net.application.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;

import net.application.authorization.annotation.Admin;
import net.application.authorization.util.IDMInitializer;
import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.CustomerIdmGroupCombination;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.CustomerIdmUserCombination;
import net.application.customer.util.GroupTreeBean;
import net.application.web.entity.Skins;
import net.application.web.util.SkinDao;

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
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.RelationshipQuery;
import org.richfaces.event.ItemChangeEvent;
import org.richfaces.event.TreeSelectionChangeEvent;


@ManagedBean 
@SessionScoped 
public class UserManagementManagedBean  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Inject private FacesContext facesContext;    
    @Inject private CustomerDao customerDao;    
    @Inject IDMInitializer ia;
    @Inject private PartitionManager partitionManager;   
    @Inject private GroupTreeBean groupTreeBean;
   // @Inject private IdentityManager identityManager;
    private String currentUserName; 
    private String currentGroupName; 
    private CustomerIdmUserCombination currentCustomerIdmUser;
    private List<CustomerIdmUserCombination> allCustomerIdmUsers;
    private List<CustomerIdmGroupCombination> allCustomerIdmGroups;
    private List<CustomerIdmRolesCombination> allCustomerIdmRoles;
    private Integer currentIndex;
    private String groupNameFilter;
    private String groupNameFilterOld;
    private String selectedTab;
  
    
    
    
    @PostConstruct
    public void init() {
    	System.out.println("init PostConstruct userManagementManagedBean");
    	groupNameFilter="deleteFilter";
    	selectedTab="userTab";
    	allCustomerIdmGroups=null;
    	allCustomerIdmUsers=null;
    	allCustomerIdmRoles=null;
    	//preRender(null);
    	//allCustomerIdmGroups=getAllCustomerIdmGroups();
    	//allCustomerIdmUsers=getAllCustomerIdmUsers();
    	//allCustomerIdmRoles=getAllCustomerIdmRoles();
    }
    public void preRender(ComponentSystemEvent event) {
        // Or in some SystemEvent method (e.g. <f:event type="preRenderView">).
    	//allCustomerIdmGroups=null;
    	//allCustomerIdmUsers=null;
    	//allCustomerIdmRoles=null;
    	System.out.println("preRender");
    	if(allCustomerIdmGroups==null)setAllCustomerIdmGroups(loadCustomerIdmGroups());
    	if(allCustomerIdmUsers==null)setAllCustomerIdmUsers(loadCustomerIdmUsers());
    	if(allCustomerIdmRoles==null)setAllCustomerIdmRoles(loadCustomerIdmRoles());
    }  
    
       
    public List<User> getAllIdmUsers(){
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	IdentityQuery<User> query = identityManager.<User> createIdentityQuery(User.class);
    	query.setParameter(User.CREATED_BEFORE, new Date());
    	// find only by the first name
    	List<User> resultUser = query.getResultList();
    	for (User user : resultUser) {
    	// do something
    	}
		return resultUser;
    }
   
    public List<Customer> getAllRegisteredUsers(){
    	
    	List<Customer> resultCustomer = customerDao.getAllCustomers();
    	for (Customer customer : resultCustomer) {
    	// do something
    	}
		return resultCustomer;
    }
    
                                                          
 
    public List<CustomerIdmGroupCombination> loadCustomerIdmGroups(){
    	System.out.println("Gruppen werden ermittelt");
    	CustomerIdmGroupCombination CIGC = new CustomerIdmGroupCombination();
    	List<CustomerIdmGroupCombination> CIGCs = new ArrayList<CustomerIdmGroupCombination>(); 
    	List<String>roleList = new ArrayList<String>();
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
    	queryg.setParameter(Group.ENABLED, true);
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		if (group.getPath().startsWith(groupNameFilter) || groupNameFilter.contains("deleteFilter")) {
	    		CIGC = new CustomerIdmGroupCombination();
	    		CIGC.setIdmCreatedDate(group.getCreatedDate());
	    		CIGC.setIdmGroupName(group.getName());
	    		CIGC.setIdmGroupPath(group.getPath());
	    		//System.out.println("Gruppe: "+ group.getPath());
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
	        		//System.out.println("Rollen: "+ grant.getRole().getName());
	        		roleList.add(grant.getRole().getName());
	        		if(CIGC.getRoles()==null){
	        			CIGC.setRoles(grant.getRole().getName());
	        		}else{
	        			if(!CIGC.getRoles().contains(grant.getRole().getName())){
	        				CIGC.setRoles(CIGC.getRoles()  + ", " + grant.getRole().getName());}
	        			}
	        	}
	        	CIGCs.add(CIGC);
    		}
    	}
    	System.out.println("beendet");
    	return CIGCs;
    		
    	
    }
    
   
    public List<CustomerIdmRolesCombination> loadCustomerIdmRoles(){
    	System.out.println("Rollen werden ermittelt");
    	CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
    	List<CustomerIdmRolesCombination> CIRCs = new ArrayList<CustomerIdmRolesCombination>(); 
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	IdentityQuery<Role> queryg = identityManager.<Role> createIdentityQuery(Role.class);
    	queryg.setParameter(Role.ENABLED, true);
    	List<Role> resultGrant = queryg.getResultList();
    	for (Role role : resultGrant) {
    		CIRC = new CustomerIdmRolesCombination();
    		CIRC.setIdmRoleName(role.getName());
    		CIRC.setIdmRoleCreatedDate(role.getCreatedDate());
    		CIRCs.add(CIRC);
    	}
    	System.out.println("beendet");
    	return CIRCs;
    	
    }
    
   
    public List<CustomerIdmUserCombination> loadCustomerIdmUsers(){
    	System.out.println("Kunden-Daten werden ermittelt");
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	List<User> resultUser = getAllIdmUsers();
    	List<Customer> resultCustomer = getAllRegisteredUsers();
    	List<CustomerIdmUserCombination> CICs = new ArrayList<CustomerIdmUserCombination>();    	
    	CustomerIdmUserCombination CIC = new CustomerIdmUserCombination();
    	Customer customerFound;
    	List<String>roleList = new ArrayList<String>();
    	List<String>groupList = new ArrayList<String>();
    	for (User user : resultUser) {
    		CIC = new CustomerIdmUserCombination();
    		//System.out.println("User: "+ user.getLoginName());
    		RelationshipQuery<Grant> query = relationshipManager.<Grant> createRelationshipQuery(Grant.class);
        	query.setParameter(Grant.ASSIGNEE, user);
        	List<Grant> resultGrant = query.getResultList();
        	for (Grant grant : resultGrant) {
        		//System.out.println("Rollen: "+ grant.getRole().getName());
        		roleList.add(grant.getRole().getName());
        		if(CIC.getRoles()==null){CIC.setRoles(grant.getRole().getName());}else{CIC.setRoles(CIC.getRoles()  + ", " + grant.getRole().getName());}
        	}
        
        	RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
        	querygms.setParameter(GroupMembership.MEMBER, user);
        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
        	for (GroupMembership groupMembership : resultGroupMembership) {
        		//System.out.println("Gruppe: "+ groupMembership.getGroup().getPath());
        		groupList.add(groupMembership.getGroup().getPath());
        		if(CIC.getGroups()==null){CIC.setGroups(groupMembership.getGroup().getPath());}else{CIC.setGroups(CIC.getGroups()  + ", " + groupMembership.getGroup().getPath());}        		
        	}
     
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
    	System.out.println("beendet");
		return CICs;
    }

    public void removeGroup() {
    	ia.deleteGroup(currentGroupName);
    	System.out.println("delete Group: "+ currentGroupName);
    	allCustomerIdmGroups=null;
    	allCustomerIdmUsers=null;
    	allCustomerIdmRoles=null;
    	if(allCustomerIdmGroups==null)setAllCustomerIdmGroups(loadCustomerIdmGroups());
    } 
    public void deleteGroupFilter() {
    	groupNameFilter="deleteFilter";
    	System.out.println("Reset filter " + groupNameFilter + " added!");
    	groupTreeBean.init();
    	allCustomerIdmGroups=null;
    	allCustomerIdmUsers=null;
    	allCustomerIdmRoles=null;
    	//preRender(null);
    	//FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("tableForm:groupTab");
    	//FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds().add("treeForm:tree");
    }
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) { 
        // considering only single selection 
    	groupTreeBean.selectionChanged(selectionChangeEvent);
    	TreeNode currentSelection = groupTreeBean.getCurrentSelection();
    	String groupName= currentSelection.toString();
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
		queryg.setParameter(Group.NAME, groupName);
		List<Group> result = queryg.getResultList();
		Group subgroup = result.get(0);
		setGroupNameFilter(subgroup.getPath());
		setGroupNameFilterOld(subgroup.getPath());
        System.out.println("New Filter " + groupNameFilter + " added!");
    	allCustomerIdmGroups=null;
    	allCustomerIdmUsers=null;
    	allCustomerIdmRoles=null;
        //preRender(null);
       
    } 
    public void select(){
    	if (groupNameFilterOld!=null ){
        	if (!groupNameFilterOld.matches(groupNameFilter) ){
	    		groupNameFilter=groupNameFilterOld;
	    		System.out.println("select: " + groupNameFilterOld);
	        	allCustomerIdmGroups=null;
	        	allCustomerIdmUsers=null;
	        	allCustomerIdmRoles=null;
	            //preRender(null);
        	}
    	}
    	
    }
    public void changeTab(ActionEvent ae){
    	selectedTab=ae.getComponent().getId();
    	System.out.println("Tab changed to " + selectedTab);
    }
    
    public void processItemChange(ItemChangeEvent p_event) throws AbortProcessingException 
    {
    	System.out.println("ItemChangeEvent: " + p_event.getOldItemName() + " => " + p_event.getNewItemName());   
    	setSelectedTab(p_event.getNewItemName());
    FacesContext.getCurrentInstance().renderResponse();     }  

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public CustomerIdmUserCombination getCurrentCustomerIdmUser() {
		//if (currentCustomerIdmUser!=null)System.out.println("Get currentCustomerIdmUser: "+ currentCustomerIdmUser.getIdmUsername());
		System.out.println("getCurrentCustomerIdmUser");
		return currentCustomerIdmUser;
	}

	public void setCurrentCustomerIdmUser(CustomerIdmUserCombination currentCustomerIdmUser) {
		//if (currentCustomerIdmUser!=null)System.out.println("Set currentCustomerIdmUser: "+ currentCustomerIdmUser.getIdmUsername());
		System.out.println("setCurrentCustomerIdmUser");
		this.currentCustomerIdmUser = currentCustomerIdmUser;
	}

	public Integer getCurrentIndex() {
		if (currentIndex!=null)System.out.println("Get Index: "+ currentIndex);
		return currentIndex;
	}

	public void setCurrentIndex(Integer currentIndex) {
		if (currentIndex!=null)System.out.println("Set Index: "+ currentIndex);
		
		this.currentIndex = currentIndex;
	}
	public List<CustomerIdmUserCombination> getAllCustomerIdmUsers() {
		System.out.println("getAllCustomerIdmUsers");
		return allCustomerIdmUsers;
	}
	public void setAllCustomerIdmUsers(List<CustomerIdmUserCombination> allCustomerIdmUsers) {
		System.out.println("setAllCustomerIdmUsers");
		this.allCustomerIdmUsers = allCustomerIdmUsers;
	}
	public List<CustomerIdmGroupCombination> getAllCustomerIdmGroups() {
		System.out.println("getAllCustomerIdmGroups");
		return allCustomerIdmGroups;
	}
	public void setAllCustomerIdmGroups(List<CustomerIdmGroupCombination> allCustomerIdmGroups) {
		System.out.println("setAllCustomerIdmGroups");
		this.allCustomerIdmGroups = allCustomerIdmGroups;
	}
	public List<CustomerIdmRolesCombination> getAllCustomerIdmRoles() {
		System.out.println("getAllCustomerIdmRoles");
		return allCustomerIdmRoles;
	}
	public void setAllCustomerIdmRoles(List<CustomerIdmRolesCombination> allCustomerIdmRoles) {
		System.out.println("setAllCustomerIdmRoles");
		this.allCustomerIdmRoles = allCustomerIdmRoles;
	}
	public String getCurrentGroupName() {
		System.out.println("getCurrentGroupName: "+ selectedTab);
		return currentGroupName;
	}
	public void setCurrentGroupName(String currentGroupName) {
		this.currentGroupName = currentGroupName;
		System.out.println("setCurrentGroupName: "+ selectedTab);
	}
	public String getGroupNameFilter() {
		return groupNameFilter;
	}
	public void setGroupNameFilter(String groupNameFilter) {
		this.groupNameFilter = groupNameFilter;
	}
	public String getSelectedTab() {
		//System.out.println("getSelectedTab: "+ selectedTab);
		return selectedTab;
		
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
		//System.out.println("setSelectedTab: "+ selectedTab);
	}
	public String getGroupNameFilterOld() {
		return groupNameFilterOld;
	}
	public void setGroupNameFilterOld(String groupNameFilterOld) {
		this.groupNameFilterOld = groupNameFilterOld;
	}


    

   
   
}