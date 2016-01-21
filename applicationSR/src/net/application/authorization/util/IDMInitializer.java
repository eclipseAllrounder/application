/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.application.authorization.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.SubGroup;
import net.application.customer.util.SubSubGroup;

import org.jboss.ejb3.annotation.TransactionTimeout;
import org.picketlink.annotations.PicketLink;
import org.picketlink.idm.IdentityManagementException;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupMembership;
import org.picketlink.idm.model.basic.GroupRole;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.RelationshipQuery;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.removeFromGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantGroupRole;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

/**
 * startup bean creates default users, groups and roles when the application is started.
 * 
 * @author Christian Bibi
 * 
 * groups:
 * 
 * importance____
 * 	             |-- gold
 * 			     |-- silver
 *               |-- bronze
 * 
 * customer/
 * author________ 
 *               |-- europe
 * 		         |-- asia      
 * 		         |-- africa
 * 		         |-- america
 *  	         |-- australia
 * 
 * company   ____
 * 			     |-- head
 * 			     |-- sales
 * 			     |-- hr
 * 				 |-- employee
 * 
 * provider
 * 
 * applicant
 *  
 * continent______ 
 *               |-- europe
 * 		         |-- asia      
 * 		         |-- africa
 * 		         |-- america
 *  	         |-- australia
 * 
 * same roles for all Group: 
 * 
 * administrator, guest
 * 
 * 
 * 
 * User and group assignment:
 *  __________________________________________________________________________________________________________________________________________________________________________
 * |username         |firstname         |lastname         |mail                         |group / subgroup              |grouprole for all (sub)groups  |application role      |
 * |-----------------|------------------|-----------------|-----------------------------|------------------------------|-------------------------------|----------------------|
 * |                 |                  |                 |                             |                              |                               |                      |
 * |administrator    |na                |na               |administrator@letterbee.de   |                              |administrator                  |administrator         |
 * |lillibee         |Lillibee          |Letter           |lillibee.letter@letterbee.de |author / europe               |                               |                      |
 * |conrad           |Conrad            |Ebook            |conrad.ebook@letterbee.de    |customer / america            |                               |                      |
 * |bcoboss          |Babett            |Bubi             |babett.coboss@letterbee.de   |company / head,hr             |guest                          |                      |
 * |cboss            |Christian         |Bibi             |christian.boss@letterbee.de  |company / head                |guest                          |                      |
 * |_________________|__________________|_________________|_____________________________|______________________________|_______________________________|______________________|
 * 
 *
 * 
 */

@Singleton
@Startup
public class IDMInitializer {

    @Inject PartitionManager partitionManager;
    
	

	
    @PostConstruct
    public void createDefaultSet() {
    	
    	
    	
    	IdentityManager identityManager = partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	
    	// proof for default set creation 
    	// Lookup for role administrator
    	Role proofRole = new Role();
    	proofRole=findRoleByName("administrator", identityManager, relationshipManager);
    	// Lookup for user administrator
    	User proofUser = new User();
    	proofUser=findUserByUsername("administrator", identityManager, relationshipManager);
     	
        if(proofRole==null || proofUser==null) {
        	deleteAllIdmInformation(identityManager, relationshipManager);
    		create(identityManager, relationshipManager);
        }
        if(proofRole!=null && proofUser!=null) {
	    	if (!proofRole.getName().matches("administrator")){
	    		deleteAllIdmInformation(identityManager, relationshipManager);
	    		create(identityManager, relationshipManager);
	    	} else {
	    		if (!proofRole.getName().matches("administrator")){
	    			deleteAllIdmInformation(identityManager, relationshipManager);
	    			create(identityManager, relationshipManager);                  
	    		} else {
	    			// Lookup for userRole
	    	    	if (!findRoleForUser(proofUser, proofRole, identityManager, relationshipManager)){
	    	    		deleteAllIdmInformation(identityManager, relationshipManager);
	    	    		create(identityManager, relationshipManager);
	    	    	} else {
	    			 System.out.println("User administrator exist");
	    	    	}
	    		}
	    	}
        }
    }
    
    public String createNewRootGroup(String groupName) {
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
		Group group = new Group();
        // Create group
		group = new Group(groupName);
        identityManager.add(group);
        identityManager.update(group);
        System.out.println("New group " + groupName + " added!");
		return "success";
	}
    
    public String createNewGroup(Integer Level, String parentGroup, String groupName) {
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
		queryg.setParameter(Group.NAME, parentGroup);
        List<Group> result = queryg.getResultList();
		Group subgroup = new Group(groupName, result.get(0));
        identityManager.add(subgroup);
        identityManager.update(subgroup);
        System.out.println("New group " + groupName + " added!");
		return "success";
	}
  
    public String deleteGroup(String groupPath) {
    	//search Group
    	if (!groupPath.isEmpty()) {
        	
	    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
	    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
	    	
	    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
			queryg.setParameter(Group.PATH, groupPath);
			List<Group> result = queryg.getResultList();
			System.out.println("want to remove Group " + groupPath);
		    			
	        if (!result.isEmpty()) {
	        	Group group = result.get(0);
	        	System.out.println("found Group " + group.getPath());
	        	String[]parts=group.getPath().split("/");
	    		//is a first-level-group
	    		if(parts.length==2){
	    			System.out.println("is a first-level Group");
	    		}
	    		//is a second-level-group
	    		if(parts.length==3){ 
	    			System.out.println("is a second-level Group");
	    		}
	    		//is a third-level-group
	    		if(parts.length==4){
	    			System.out.println("is a third-level Group");
	    		}
	    		//find all child groups an delete this groups with all constraints
	    		Group whileGroup;
	    		while (true) {
	    			whileGroup=findLastGroupInPath(group, identityManager, relationshipManager);
	    			//if all child deleted
	    			if (whileGroup.getPath().matches(group.getPath())){
	    				deleteGroupConstraints(group, identityManager, relationshipManager);
	    				identityManager.update(group);
	    				identityManager.remove(group);
	    				break;
			        } else {
			        	deleteGroupConstraints(whileGroup, identityManager, relationshipManager);
			        	identityManager.update(whileGroup);
			        	identityManager.remove(whileGroup);
			        }
	    		
	    		}
	    		

		        
	        }
	        
    	}
		
    	return "success";
    }
    
    public Group findLastGroupInPath(Group groupToDelete, IdentityManager identityManager, RelationshipManager relationshipManager){
    	System.out.println("call findLastGroupInPath for group: " + groupToDelete.getPath());
    	Group lastGroup = groupToDelete;
    	Boolean hasChild=false;
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
    	queryg.setParameter(Group.CREATED_BEFORE, new Date());
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		//the group is a child
    		if ((group.getPath().length()>groupToDelete.getPath().length()) && group.getPath().contains(groupToDelete.getPath())){
    			hasChild=true;
    			//choose the child as last group and search again
    			lastGroup=findLastGroupInPath(group, identityManager, relationshipManager);
    			break;
    		}
    	}
    	System.out.println("last Group is: " + lastGroup.getPath()); 
    	return lastGroup;
    }
    public Group findNonMemberLastGroupInPath(Group rootGroup, User user){
    	System.out.println("call findLastGroupInPath for group: " + rootGroup.getPath());
    	Group lastGroup = rootGroup;
    	Boolean hasChild=false;
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
    	queryg.setParameter(Group.CREATED_BEFORE, new Date());
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		//the group is a child and a non member
    		if ((group.getPath().length()>rootGroup.getPath().length()) && group.getPath().contains(rootGroup.getPath()) && !BasicModel.isMember(relationshipManager, user, group)){
    			hasChild=true;
    			//choose the child as last group and search again
    			lastGroup=findNonMemberLastGroupInPath(group, user);
    			break;
    		}
    	}
    	System.out.println("last Group is: " + lastGroup.getPath()); 
    	return lastGroup;
    }
 
    
    
    public void deleteGroupConstraints(Group groupToDelete, IdentityManager identityManager, RelationshipManager relationshipManager){
    	// Childs are not allowed!
    	System.out.println("call deleteGroupConstraints for group: " + groupToDelete.getPath());
    	Boolean hasChild=false;
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
    	queryg.setParameter(Group.CREATED_BEFORE, new Date());
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		//the group is a child
    		if ((group.getPath().length()>groupToDelete.getPath().length()) && group.getPath().contains(groupToDelete.getPath())){
    			System.out.println("found a child!");
    			hasChild=true;
    			break;
    		}
    	}
    	if(!hasChild){
    		//find the users, e.g. groupMembership and delete this relation
    		RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
        	querygms.setParameter(GroupMembership.GROUP, groupToDelete);
        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
        	for (GroupMembership memberShip : resultGroupMembership) {
        		System.out.println("remove memberShip: " + memberShip.getMember().toString());
        		relationshipManager.remove(memberShip);
        	}
    		//find the groupRoleRelations and delete this relations
    		RelationshipQuery<GroupRole> query = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
        	query.setParameter(GroupRole.GROUP, groupToDelete);
        	List<GroupRole> resultGroupRole = query.getResultList();
        	for (GroupRole groupRole : resultGroupRole) {
        		System.out.println("remove GroupRole: " + groupRole.getRole().getName());
        		relationshipManager.remove(groupRole);
        	}    		
    	}
    	
    }
    
	
    public String deleteUser(String userName, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	IdentityQuery<User> query = identityManager.<User> createIdentityQuery(User.class);
    	query.setParameter(User.LOGIN_NAME, userName);
    	// find only by the first name
    	List<User> resultUser = query.getResultList();
    	for (User user : resultUser) {
    		deleteUserConstraints(user,identityManager,relationshipManager);
        	identityManager.update(user);
        	identityManager.remove(user);
    	}
    	return "success";
    }    
    public String editUser(String userName, String groupPath, List<String> customerIdmRolesCombinationStrings, Boolean activateForAllSubGroups){
    	if (customerIdmRolesCombinationStrings!=null)System.out.println("call editUser " + groupPath + " and " + customerIdmRolesCombinationStrings.toString() + " and " + activateForAllSubGroups.toString());
    	if (customerIdmRolesCombinationStrings==null)System.out.println("call editUser " + groupPath + " and Keine Rollen and " + activateForAllSubGroups.toString());
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	
    	// ################################
    	// find user
    	//
    	if (userName==null)return null;
    	User user = findUserByUsername(userName,identityManager,relationshipManager);
    	List<Group> userGroups = findGroupByUser(user,identityManager,relationshipManager);
    	List<Role> userRoles = findRoleByUser(user,identityManager,relationshipManager);
    	
    	// ################################
    	// adding giving group and sugroups
    	//    	
    	//Is the user already a member of this group? 
    	Boolean groupGefu=false;
    	
		Group group = findGroupByGroupPath(groupPath,identityManager,relationshipManager);
        if (group!=null) {        	
       		if (!activateForAllSubGroups) {
       			if (!BasicModel.isMember(relationshipManager, user, group)) {
       				addToGroup(relationshipManager, user, group);
       				identityManager.update(user);
       			} else {
       				System.out.println("user is already member of group" + group.getPath());
       			}
        		//find all child groups and add the user to this groups
       		} else {
   	    		Group whileGroup;
   	    		while (true) {
    	    		whileGroup=findNonMemberLastGroupInPath(group, user);
    	    		//if all child found, proof the root
    	    		if (whileGroup.getPath().matches(group.getPath())){
    	    			if (!BasicModel.isMember(relationshipManager, user, group)) {
	    	   				System.out.println("add " + user.getLoginName().toString() + " as member to groupPath " + group.getPath() + " as member to group " + group.getName());
	    	   				addToGroup(relationshipManager, user, group);
	    	   				//identityManager.update(group);
	    	   				identityManager.update(user);
	    	   				break;
    	    			} else {
    	    				System.out.println("user is already member of group" + group.getPath());
	    	   				break;
    	    			}
    		        } else {
    		        	System.out.println("add " + user.getLoginName().toString() + " as member to whileGroupPath " + whileGroup.getPath() + " as member to whileGroup " + whileGroup.getName());
    		        	addToGroup(relationshipManager, user, whileGroup);
    		        	//identityManager.update(group);
    	    			identityManager.update(user);
    		        }
    	    	}
        	}
        	
        }
        //assign role to user
    	if (customerIdmRolesCombinationStrings!=null){
    		List<Role>unchangedRole = new ArrayList<Role>();
    		//delete all User related roles except selected roles
    		for (Role role : userRoles) {
	    		Boolean foundRole = false;
	    		for (String roleName : customerIdmRolesCombinationStrings) {
	    			//Is role a selected role?
	    			if(role.getName().matches(roleName))foundRole=true;
	    		}
	    		if(!foundRole && (!user.getLoginName().matches("administrator") && !role.getName().matches("administrator"))){
	    			BasicModel.revokeRole(relationshipManager, user, role);
	    		} else {
	    			unchangedRole.add(role);
	    		}
    		}
    		//add new roles
	        Boolean foundRole=false;
	    	for (String roleName : customerIdmRolesCombinationStrings) {
	    		for (Role role : unchangedRole) {
	    			if(role.getName().matches(roleName))foundRole=true;
	    		}
	    		if(!foundRole){
	    			Role roleToAdd=findRoleByName(roleName, identityManager, relationshipManager);
	    			BasicModel.grantRole(relationshipManager, user, roleToAdd);
    				identityManager.update(user);
	    		}
	    	}
      	}
    	
    	return "success";
    }
   
    public List<Role> findRoleByUser(User user, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	List<Role>roleList = new ArrayList<Role>();
    	RelationshipQuery<Grant> querygms = relationshipManager.<Grant> createRelationshipQuery(Grant.class);
    	querygms.setParameter(Grant.ASSIGNEE, user);
    	List<Grant> resultGrant = querygms.getResultList();
        for (Grant grant : resultGrant) {
        	roleList.add(grant.getRole());
    	}
    	return roleList;
    
    }
    public Boolean findRoleForUser(User user, Role role, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	Role returnRole = new Role();
    	RelationshipQuery<Grant> querygms = relationshipManager.<Grant> createRelationshipQuery(Grant.class);
    	querygms.setParameter(Grant.ASSIGNEE, user);
    	List<Grant> resultGrant = querygms.getResultList();
        for (Grant grant : resultGrant) {
        	if(grant.getRole().getName().matches(role.getName())) return true;
    	}
    	return false;
    
    }
    public Role findRoleByName(String roleName, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	IdentityQuery<Role> queryr = identityManager.<Role> createIdentityQuery(Role.class);
		queryr.setParameter(Role.NAME, roleName);
		List<Role> roles=queryr.getResultList();
		if(!roles.isEmpty()) {return roles.get(0);} else {return null;}	
    }
    public List<Group> findGroupByUser(User user, IdentityManager identityManager, RelationshipManager relationshipManager){
    	List<Group>groupList = new ArrayList<Group>();
    	RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
    	querygms.setParameter(GroupMembership.MEMBER, user);
    	List<GroupMembership> resultGroup = querygms.getResultList();
        for (GroupMembership groupMembership : resultGroup) {
        	groupList.add(groupMembership.getGroup());
    	}
    	return groupList;    
    }
    
    public User findUserByUsername(String userName, IdentityManager identityManager, RelationshipManager relationshipManager) {
		IdentityQuery<User> query = identityManager.<User> createIdentityQuery(User.class);
		query.setParameter(User.LOGIN_NAME, userName);
		// find only by the first name
		List<User> resultUser = query.getResultList();
		if(!resultUser.isEmpty()) {return resultUser.get(0);} else {return null;}		
    }
    
    public Group findGroupByGroupPath(String groupPath, IdentityManager identityManager, RelationshipManager relationshipManager) {
		IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
		queryg.setParameter(Group.PATH, groupPath);
		List<Group> result = queryg.getResultList();
		//System.out.println("looking for group " + groupPath);
	    //group found			
	    if (!result.isEmpty()) {
	    	return result.get(0);
	    } else {
	    	return null;
	    }
    }
    public void deleteUserConstraints(User userToDelete, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	    
    	if(userToDelete!=null){
    		//find the users in all groups and delete this relation
        	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
        	queryg.setParameter(Group.CREATED_BEFORE, new Date());
        	List<Group> resultG = queryg.getResultList();
        	for (Group group : resultG) {
        		//Delete user from the group
	    		RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
	        	querygms.setParameter(GroupMembership.GROUP, group);
	        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
	        	for (GroupMembership memberShip : resultGroupMembership) {
	        		System.out.println("remove memberShip: " + memberShip.getMember().getId());
	        		if (userToDelete.getId().matches(memberShip.getMember().getId()))relationshipManager.remove(memberShip);
	        	}
	        	
        	}
    		    		
    	}
    	
    }
    public void deleteAllIdmInformation(IdentityManager identityManager, RelationshipManager relationshipManager) {
    	//Delete GroupMembership, GroupRoles and Groups
    	RelationshipQuery<GroupMembership> querygms;
    	List<GroupMembership> resultGroupMembership;
    	RelationshipQuery<GroupRole> query;
    	List<GroupRole> resultGroupRole;
    	RelationshipQuery<Grant> querygrt; 
    	List<Grant> resultGrant;
    	
    	IdentityQuery<Group> queryg = identityManager.<Group> createIdentityQuery(Group.class);
		queryg.setParameter(Group.CREATED_BEFORE, new Date());
        List<Group> resultGroup = queryg.getResultList();
        while (resultGroup.size()>0) {
        	Group groupLoop=resultGroup.get(0);
        	System.out.println("Group in Loop: " + groupLoop.getPath());
        	Group group=findLastGroupInPath(groupLoop, identityManager, relationshipManager);
        	//find the users, e.g. groupMembership and delete this relation
    		System.out.println("Group to delete: " + group.getPath());
    		
    		querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
        	querygms.setParameter(GroupMembership.GROUP, group);
        	resultGroupMembership = querygms.getResultList();
        	for (GroupMembership memberShip : resultGroupMembership) {
        		System.out.println("remove memberShip: " + memberShip.getMember().toString());
        		relationshipManager.remove(memberShip);
        		System.out.println("update " + group.getPath());
            	identityManager.update(group);
            	System.out.println("updated " + group.getPath());
        	}
    		//find the groupRoleRelations and delete this relations
    		query = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
        	query.setParameter(GroupRole.GROUP, group);
        	resultGroupRole = query.getResultList();
        	for (GroupRole groupRole : resultGroupRole) {
        		System.out.println("remove GroupRole: " + groupRole.getRole().getName());
        		relationshipManager.remove(groupRole);
        		System.out.println("update " + group.getPath());
            	identityManager.update(group);
            	System.out.println("updated " + group.getPath());
        	}  
    		System.out.println("update " + group.getPath());
        	identityManager.update(group);
        	System.out.println("updated " + group.getPath());
        	System.out.println("remove: " + group.getPath());
			identityManager.remove(group);
			System.out.println("removed: " + group.getPath());
			//Delete from List
			int i=0;
			System.out.println("List Size : " + resultGroup.size());
			while(resultGroup.size()!=0)  
			{  
			  if(resultGroup.get(i).getPath().matches(group.getPath()))  
			  {  
				  resultGroup.remove(i);  
				  System.out.println("removed in List: " + group.getPath());
				  break;  
			  }  
			  else  
			  {  
			    i++;  
			  }  
			}  
			
		}
        
     	//Delete Grants and Users
    	IdentityQuery<User> queryU = identityManager.<User> createIdentityQuery(User.class);
    	queryU.setParameter(User.CREATED_BEFORE, new Date());
    	List<User> resultUser = queryU.getResultList();
    	for (User user : resultUser) {
    		List<Role>roleList = new ArrayList<Role>();
        	querygrt = relationshipManager.<Grant> createRelationshipQuery(Grant.class);
        	querygrt.setParameter(Grant.ASSIGNEE, user);
        	resultGrant = querygrt.getResultList();
            for (Grant grant : resultGrant) {
            	BasicModel.revokeRole(relationshipManager, user, grant.getRole());
        	}
            identityManager.update(user);
    		deleteUserConstraints(user, identityManager, relationshipManager);
        	identityManager.update(user);
        	identityManager.remove(user);
    	}
    	// Delete Roles
    	IdentityQuery<Role> queryr = identityManager.<Role> createIdentityQuery(Role.class);
		queryr.setParameter(Role.CREATED_BEFORE, new Date());
		List<Role> resultRole=queryr.getResultList();
		for (Role role : resultRole) {
			identityManager.update(role);
			identityManager.remove(role);
		}
        
    }
    @TransactionTimeout(value = 60000000)
    public void create(IdentityManager identityManager, RelationshipManager relationshipManager) {

		Calendar date = Calendar.getInstance();   
	    date.setTime(new Date());   
	   // Format f = new SimpleDateFormat("dd-MMMM-yyyy");   
	    System.out.println((date.getTime()));   
	    date.add(Calendar.YEAR,1);   
	    System.out.println((date.getTime()));
     	IdentityQuery<User> queryU = identityManager.<User> createIdentityQuery(User.class);
 		queryU.setParameter(User.LOGIN_NAME, "administrator");
 		List<User> resultU = queryU.getResultList();
 	// Lookup the user by their username
 		//User user = BasicModel.getUser(identityManager, "administrator");
         if (resultU.isEmpty()) {
        	 System.out.println("administrator not found");
 			                   
		 } else {
			 System.out.println("User administrator exist"); 
		 }
         
    	User user = new User();
        // Create user bcoboss
        user = new User("bcoboss");
        user.setEmail("babett.coboss@letterbee.de");
        user.setFirstName("Babett");
        user.setLastName("Bubi");
        user.setExpirationDate(new Date());
        user.setCreatedDate(new Date());  
        user.setAttribute( new Attribute<String>("city", "Stuttgart") );
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("demo"), new Date(), new Date());
      

        // Create user cboss
        user = new User("cboss");
        user.setEmail("christian.boss@letterbee.de");
        user.setFirstName("Christian");
        user.setLastName("Bibi");
        user.setExpirationDate(new Date());
        user.setCreatedDate(new Date());
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("demo"));
        
        // Create user conrad
        user = new User("conrad");
        user.setEmail("conrad.ebook@letterbee.de");
        user.setFirstName("Conrad");
        user.setLastName("Ebook");
        user.setExpirationDate(new Date());
        user.setCreatedDate(new Date());
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("demo"));
        

        // Create user lillibee
        user = new User("lillibee");
        user.setEmail("lillibee.letter@letterbee.de");
        user.setFirstName("Lillibee");
        user.setLastName("Letter");
        user.setExpirationDate(new Date());
        user.setCreatedDate(new Date());
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("demo"));
        
        for(int i=0; i<100; i++){
        	
        	// Create user
        	System.out.println("i " + i);
            user = new User("test_" + i);
            user.setEmail("test_" + i + "@letterbee.de");
            user.setFirstName("na");
            user.setLastName("na");
            user.setExpirationDate(new Date());
            user.setCreatedDate(new Date());
            user.setAttribute( new Attribute<String>("address", "test_" + i + "Street") );
            identityManager.add(user);
            identityManager.updateCredential(user, new Password("demo"));
            
         
        }

        // Create user administrator
        user = new User("administrator");
        user.setEmail("administrator@letterbee.de");
        user.setFirstName("na");
        user.setLastName("na");
        user.setExpirationDate(new Date());
        user.setCreatedDate(new Date());
        identityManager.add(user);
        identityManager.updateCredential(user, new Password("demo"));
        
        
        
      
       
        
        System.out.println("Default User created");
        
        Role role = new Role();
        Group group = new Group();
        Group subgroup = new Group();
        
        // Create group "importance"
        group = new Group("importance");
        identityManager.add(group);
        subgroup = new Group("gold", group);
        identityManager.add(subgroup);
        subgroup = new Group("silver", group);
        identityManager.add(subgroup);
        subgroup = new Group("bronze", group);
        identityManager.add(subgroup);

        group = new Group("applicant");
        identityManager.add(group);
        
        group = new Group("provider");
        identityManager.add(group);
        
        // Create application role "customer"
        group = new Group("continent");
        identityManager.add(group);
        subgroup = new Group("europe", group);
        identityManager.add(subgroup);
        subgroup = new Group("asias", group);
        identityManager.add(subgroup);
        subgroup = new Group("africa", group);
        identityManager.add(subgroup);
        subgroup = new Group("australia", group);
        identityManager.add(subgroup);
        subgroup = new Group("america", group);
        identityManager.add(subgroup);
        
        // Create group "company"
        group = new Group("company");
        identityManager.add(group);
        subgroup = new Group("head", group);
        identityManager.add(subgroup);
        subgroup = new Group("sales", group);
        identityManager.add(subgroup);
        subgroup = new Group("hr", group);
        identityManager.add(subgroup);
        subgroup = new Group("employee", group);
        identityManager.add(subgroup);
        
        // Create application group "customer"
        group = new Group("customer");
        identityManager.add(group);
        subgroup = new Group("europe", group);
        identityManager.add(subgroup);
        subgroup = new Group("asias", group);
        identityManager.add(subgroup);
        subgroup = new Group("africa", group);
        identityManager.add(subgroup);
        subgroup = new Group("australia", group);
        identityManager.add(subgroup);
        subgroup = new Group("america", group);
        identityManager.add(subgroup);
        
        // Create application group "author"
        group = new Group("author");
        identityManager.add(group);
        subgroup = new Group("europe", group);
        identityManager.add(subgroup);
        subgroup = new Group("asias", group);
        identityManager.add(subgroup);
        subgroup = new Group("africa", group);
        identityManager.add(subgroup);
        subgroup = new Group("australia", group);
        identityManager.add(subgroup);
        subgroup = new Group("america", group);
        identityManager.add(subgroup);
        
        System.out.println("Default Groups created");
        
        // Create Role administrator
        role = new Role("administrator");
        identityManager.add(role);
        
        // Create Role guest
        role = new Role("guest");
        identityManager.add(role);
        
        System.out.println("Default Roles created");
        
        
        // Assign user Admintsrator the application role administrator
        user = BasicModel.getUser(identityManager, "administrator");
        role = BasicModel.getRole(identityManager, "administrator");
        BasicModel.grantRole(relationshipManager, user, role);
        
        // Make cboss and bcoboss a member of the "head" group
        group = BasicModel.getGroup(identityManager, "company");  
        subgroup = BasicModel.getGroup(identityManager, "head", group);
        user = BasicModel.getUser(identityManager, "cboss");
        addToGroup(relationshipManager, user, group);
        identityManager.update(user);
        addToGroup(relationshipManager, user, subgroup);
        
        user = BasicModel.getUser(identityManager, "bcoboss");
        addToGroup(relationshipManager, user, group);
        identityManager.update(user);
        addToGroup(relationshipManager, user, subgroup);
        identityManager.update(user);
        // Make bcoboss a member of the "hr" group
        subgroup = BasicModel.getGroup(identityManager, "hr", group);
        user = BasicModel.getUser(identityManager, "bcoboss");
        addToGroup(relationshipManager, user, subgroup); 
        // Make lillibee a member of the "europe" subgroup of group author
        group = BasicModel.getGroup(identityManager, "author");  
        subgroup = BasicModel.getGroup(identityManager, "europe", group);
        user = BasicModel.getUser(identityManager, "lillibee");
        addToGroup(relationshipManager, user, group);
        identityManager.update(user);
        addToGroup(relationshipManager, user, subgroup); 
        // Make lillibee a member of the "america" subgroup of group customer
        group = BasicModel.getGroup(identityManager, "customer");  
        subgroup = BasicModel.getGroup(identityManager, "america", group);
        user = BasicModel.getUser(identityManager, "conrad");
        addToGroup(relationshipManager, user, group);
        identityManager.update(user);
        addToGroup(relationshipManager, user, subgroup); 
        System.out.println("Group membership created");
        
        // Administrator is admin of group company and all subgroups of company
        group = BasicModel.getGroup(identityManager, "company");   
        role = BasicModel.getRole(identityManager, "administrator");
        user = BasicModel.getUser(identityManager, "administrator");
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "head", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "sales", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "hr", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "employee", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // Administrator is admin of group author and all subgroups of author
        group = BasicModel.getGroup(identityManager, "author");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // Administrator is guest of group customer and all subgroups of customer
        group = BasicModel.getGroup(identityManager, "customer");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
     
        // Administrator is admin of group importance and all subgroups of importance
        group = BasicModel.getGroup(identityManager, "importance"); 
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "gold", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "silver", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "bronze", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
       
        // cboss is guest of group company and all subgroups of company
        group = BasicModel.getGroup(identityManager, "company");   
        role = BasicModel.getRole(identityManager, "guest");
        user = BasicModel.getUser(identityManager, "cboss");
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "head", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "sales", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "hr", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "employee", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // cboss is guest of group author and all subgroups of author
        group = BasicModel.getGroup(identityManager, "author");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // cboss is guest of group customer and all subgroups of customer
        group = BasicModel.getGroup(identityManager, "customer");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
     
        // cboss is guest admin of group importance and all subgroups of importance
        group = BasicModel.getGroup(identityManager, "importance");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "gold", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "silver", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "bronze", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // bcoboss is guest of group company and all subgroups of company
        group = BasicModel.getGroup(identityManager, "company");   
        role = BasicModel.getRole(identityManager, "guest");
        user = BasicModel.getUser(identityManager, "bcoboss");
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "head", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "sales", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "hr", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "employee", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // bcoboss is guest of group author and all subgroups of author
        group = BasicModel.getGroup(identityManager, "author");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        
        // bcoboss is guest of group customer and all subgroups of customer
        group = BasicModel.getGroup(identityManager, "customer");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "asias", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "africa", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "australia", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "america", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
     
        // bcoboss is guest admin of group importance and all subgroups of importance
        group = BasicModel.getGroup(identityManager, "importance");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "gold", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "silver", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        subgroup = BasicModel.getGroup(identityManager, "bronze", group); 
        BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
        System.out.println("Role group created");
        
        group = BasicModel.getGroup(identityManager, "customer");   
        BasicModel.grantGroupRole(relationshipManager, user, role, group);
        subgroup = BasicModel.getGroup(identityManager, "europe", group); 
        role = BasicModel.getRole(identityManager, "guest");
        
    }
}

