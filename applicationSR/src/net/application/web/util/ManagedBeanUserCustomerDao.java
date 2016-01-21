package net.application.web.util;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
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

import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerIdmGroupCombination;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.CustomerIdmUserCombination;
import net.application.web.entity.Mailserver;



public class ManagedBeanUserCustomerDao implements UserCustomerDao,Serializable{

	@Inject
    private EntityManager entityManager;       
	
	@Inject
    private PartitionManager partitionManager;

    @Inject
    private UserTransaction utx;  
    
    @Inject Logger log;
   		


    public List<CustomerIdmUserCombination> findUsersCustomersBy_offset_pageSize_sort_condition(Map<String, String>  conditionSet,  String sortString, Integer offset, Integer pageSize) {
		
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	//get all users
    	List<User> resultUser = getAllIdmUsers(conditionSet, sortString, offset, pageSize);
    	//find all customers, filtered by userList
    	List<Customer> resultCustomer = getAllRegisteredCustomers(resultUser);
    	List<CustomerIdmUserCombination> CICs = new ArrayList<CustomerIdmUserCombination>();    	
    	CustomerIdmUserCombination CIC = new CustomerIdmUserCombination();
    	CustomerIdmRolesCombination CIRC = new CustomerIdmRolesCombination();
    	Customer customerFound;
    	List<String>roleList = new ArrayList<String>();
    	List<String>groupList = new ArrayList<String>();
    	
    	for (User user : resultUser) {
    		System.out.println("User: "+ user.getLoginName());
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
    		CIC.setCustomerId(user.getId());
    		
    		//link to customer
    		customerFound=new Customer();
    		if(resultCustomer!=null){	    		
	    		for (Customer customer : resultCustomer) {
	    			if (customer.getUserName().equals(user.getLoginName())){
	    				customerFound=customer;
	    				break;
	    			}
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
    	System.out.println("beendet: Anzah der gefundenen Einträge: " + CICs.size());
		return CICs;
	}
	public Integer usersCustomersCountBy_condition(Map<String, String> conditionSet) {
		log.info("User zählen");
		IdentityManager identityManager = this.partitionManager.createIdentityManager();        
		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
		IdentityQuery<User> query = builder.createIdentityQuery(User.class);
		//Loop trough conditions
		Condition conditionLOGIN_NAME = null;
	    Condition conditionFIRST_NAME = null;
	    Condition conditionLAST_NAME = null;
		if(conditionSet!=null){
			Iterator iterator = conditionSet.keySet().iterator();
				while(iterator.hasNext()){
				  String key   = (String) iterator.next();
				  String value = conditionSet.get(key); 
				  log.info("conditionMaP: " + key +" - " + value);
				  if(key.matches("ColumnLastnameFilter"))conditionLAST_NAME = builder.like(User.LAST_NAME, value);
				  if(key.matches("ColumnFistnameFilter"))conditionFIRST_NAME = builder.like(User.FIRST_NAME, value);
				  if(key.matches("ColumnUsernameFilter"))conditionLOGIN_NAME = builder.like(User.LOGIN_NAME, value);
				}
		}
		if(conditionLAST_NAME!=null)query.where(conditionLAST_NAME);
		if(conditionFIRST_NAME!=null)query.where(conditionFIRST_NAME);
		if(conditionLOGIN_NAME!=null)query.where(conditionLOGIN_NAME);
        Integer resultCount=query.getResultCount();
        return resultCount;
	}
	
	public List<User> getAllIdmUsers(Map<String, String>  conditionSet,  String sortString, Integer offset, Integer pageSize){
		log.info("User abrufen");
		IdentityManager identityManager = this.partitionManager.createIdentityManager();        
		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
		IdentityQuery<User> query = builder.createIdentityQuery(User.class);	
        //Loop trough conditions
		Condition conditionLOGIN_NAME = null;
	    Condition conditionFIRST_NAME = null;
	    Condition conditionLAST_NAME = null;
		if(conditionSet!=null){
			Iterator iterator = conditionSet.keySet().iterator();
			while(iterator.hasNext()){
			  String key   = (String) iterator.next();
			  String value = conditionSet.get(key);
			  log.info("conditionMaP: " + key +" - " + value);
			  if(key.matches("ColumnLastnameFilter"))conditionLAST_NAME = builder.like(User.LAST_NAME, value);
			  if(key.matches("ColumnFistnameFilter"))conditionFIRST_NAME = builder.like(User.FIRST_NAME, value);
			  if(key.matches("ColumnUsernameFilter"))conditionLOGIN_NAME = builder.like(User.LOGIN_NAME, value);
			}
		}
		if(offset!=null)query.setOffset(offset);
		if(pageSize!=null)query.setLimit(pageSize);
		//default Sort
		Sort sort = builder.desc(User.LOGIN_NAME);
		if(sortString!=null){
			log.info("sortString: " + sortString);
			if(sortString.matches("sortLoginNameUp")) sort = builder.desc(User.LOGIN_NAME);
		    if(sortString.matches("sortLoginNameDown")) sort = builder.asc(User.LOGIN_NAME);
		    if(sortString.matches("sortCreateDateUp")) sort = builder.desc(User.CREATED_DATE);
		    if(sortString.matches("sortCreateDateDown")) sort = builder.asc(User.CREATED_DATE);
		    if(sortString.matches("sortEnableUp")) sort = builder.desc(User.ENABLED);
		    if(sortString.matches("sortEnableDown")) sort = builder.asc(User.ENABLED);
		}
		if(offset!=null)query.setOffset(offset);
		if(pageSize!=null)query.setLimit(pageSize);
		if(conditionLAST_NAME!=null)query.where(conditionLAST_NAME);
		if(conditionFIRST_NAME!=null)query.where(conditionFIRST_NAME);
		if(conditionLOGIN_NAME!=null)query.where(conditionLOGIN_NAME);
		query.sortBy(sort);
    	List<User> users = query.getResultList();
        return users;
	    }
	public List<Customer> getAllRegisteredCustomers(List<User> users){
		List<Customer> customers = new ArrayList<Customer>();
		ArrayList<Integer> arrlist = new ArrayList<Integer>(5);
	   	Customer customer=null;	
	   	for (User user : users) {
	        try {      	
	       		//System.out.println("User found "+ user.getLoginName());
	        	Query query = entityManager.createNamedQuery("findUserByName");
		        query.setParameter("userName", user.getLoginName());
		        customer = (Customer) query.getSingleResult();	
		        customers.add(customer);
	        } catch (NoResultException e) {
	        	System.out.println("User found NoResultException"+ e);
	        	customer = null;
	        }
	       
	   	}
        return customers;
	 }
}