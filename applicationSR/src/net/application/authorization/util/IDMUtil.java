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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import net.application.authorization.annotation.Admin;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.CustomerIdmUserCombination;
import net.application.util.Status;

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
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQuery;
import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.idm.query.RelationshipQuery;
import org.picketlink.idm.query.QueryParameter;
import org.picketlink.idm.query.Sort;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.removeFromGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantGroupRole;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

/**
 * startup bean creates default users, groups and roles when the application is started.
 * 
 * @author Christian Lewer
 * 
  */

@Singleton
@Startup
//@Named
//@RequestScoped
public class IDMUtil {

    @Inject
    private PartitionManager partitionManager;
    @Inject Logger log;

   
    public PartitionManager getPartitionManager() {
		return partitionManager;
	}
	public String createNewRootGroup(String groupName) {
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
		Group group = new Group();
        // Create group
		group = new Group(groupName);
        identityManager.add(group);
        identityManager.update(group);
        System.out.println("New group " + groupName + " added!");
        //User Administration become the groupRole for this group and all existing roles
        IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, "administrator");
        queryu.where(conditionu);
        List<User> resultu = queryu.getResultList();
        User user = resultu.get(0);
        
        IdentityQuery queryr = builder.createIdentityQuery(Role.class);
        Condition conditionr = builder.lessThan(Role.CREATED_DATE, new Date());
        queryr.where(conditionr);
        List<Role> resultr = queryr.getResultList();
        for (Role role : resultr) {
        	identityManager.update(user);
    		BasicModel.grantGroupRole(relationshipManager, user, role, group);
    		System.out.println("New groupRole for User administrator and Group " + group.getPath() + " and role " + role.getName() + " added!");
    		identityManager.update(user);
        }              
		return "success";
	}
    public String createNewGroup(Integer Level, String parentGroup, String groupName) {
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.like(Group.NAME, parentGroup);
        queryg.where(conditiong);
        
        List<Group> resultg = queryg.getResultList();
		Group subgroup = new Group(groupName, resultg.get(0));
        identityManager.add(subgroup);
        identityManager.update(subgroup);
        System.out.println("New group " + groupName + " added!");
        
        //User Administration become the groupRole for this group and all existing roles
        IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, "administrator");
        queryu.where(conditionu);
        List<User> resultu = queryu.getResultList();
        User user = resultu.get(0);
        
        IdentityQuery queryr = builder.createIdentityQuery(Role.class);
        Condition conditionr = builder.lessThan(Role.CREATED_DATE, new Date());
        queryr.where(conditionr);
        List<Role> resultr = queryr.getResultList();
        for (Role role : resultr) {
        	identityManager.update(user);
    		BasicModel.grantGroupRole(relationshipManager, user, role, subgroup);
    		System.out.println("New groupRole for User administrator and Group " + subgroup.getPath() + " and role " + role.getName() + " added!");
    		identityManager.update(user);
        }        
		return "success";
	}

   
    public String createNewRole(String roleName) {
		IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
		Role role = new Role();
        // Create role
		role = new Role(roleName);
        identityManager.add(role);
        identityManager.update(role);
        System.out.println("New role " + roleName + " added!");
        //User Administration become the groupRole for this group and all existing roles
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, "administrator");
        queryu.where(conditionu);
        List<User> resultu = queryu.getResultList();
        User user = resultu.get(0);
        IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
        queryg.where(conditiong);
        List<Group> resultg = queryg.getResultList();
        for (Group group : resultg) {
        	identityManager.update(user);
    		BasicModel.grantGroupRole(relationshipManager, user, role, group);
    		System.out.println("New groupRole for User administrator and Group " + group.getPath() + " and role " + role.getName() + " added!");
    		identityManager.update(user);
        }       
		return "success";
	}

    @Admin
    public String editUser(String userName, String groupPathDelete, String groupPathAdd,String groupPathGroupRole, List<String> customerIdmRolesCombinationStrings, List<String> customerIdmGroupRolesCombinationStrings, Boolean activateForAllDeleteSubGroups, Boolean activateForAllAddSubGroups) {
    	if (customerIdmRolesCombinationStrings!=null)System.out.println("call editUser " + groupPathDelete + " and " + groupPathAdd + " and " + customerIdmRolesCombinationStrings.toString() + " and " + activateForAllDeleteSubGroups.toString() + activateForAllAddSubGroups.toString());
    	if (customerIdmRolesCombinationStrings==null)System.out.println("call editUser " + groupPathDelete + " and " + groupPathAdd + " and Keine Rollen and " + activateForAllDeleteSubGroups.toString() + activateForAllAddSubGroups.toString());
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	
    	// ################################
    	// find user
    	//
    	if (userName==null)return null;
    	User user = findUserByUsername(userName,identityManager,relationshipManager);
    	List<Group> userGroups = findGroupByUser(user,identityManager,relationshipManager);
    	List<Role> userRoles = findRoleByUser(user,identityManager,relationshipManager);
    	List<GroupRole> userGroupRoles = findGroupRoleByUser(user,identityManager,relationshipManager);
    	
    	// ################################
    	// adding giving group and sugroups
    	//    	
    	//Is the user already a member of this group? 
    	Boolean groupGefu=false;
    	org.picketlink.idm.model.basic.Group groupDelete=null;
    	org.picketlink.idm.model.basic.Group groupAdd=null;
    	try {
    		String[]parts=groupPathAdd.split("/");
    		if (parts.length>1) groupAdd = findGroupByGroupPath(groupPathAdd,identityManager,relationshipManager);
    		
    	} catch (Exception exception) {
        exception.printStackTrace();
    	}
    	//System.out.println("group to add " + groupAdd.getPath());
        if (groupAdd!=null) {        	
        	System.out.println("add groupPathAdd " + groupPathAdd);
        	System.out.println("add while group " + groupAdd.getName());
       		if (!activateForAllAddSubGroups) {
       			if (!BasicModel.isMember(relationshipManager, user, groupAdd)) {
       				addToGroup(relationshipManager, user, groupAdd);
       				identityManager.update(user);
       			} else {
       				//System.out.println("user is already member of group " + groupAdd.getPath());
       			}
        		//find all child groups and add the user to this groups
       		} else {
       			//error correction, the rootGroup must save first
       			if (!BasicModel.isMember(relationshipManager, user, groupAdd)) {
	   				System.out.println("add " + user.getLoginName().toString() + " as member to groupPath " + groupAdd.getPath() + " as member to group " + groupAdd.getName());
	   				addToGroup(relationshipManager, user, groupAdd);
	   				identityManager.update(user);
    			} else {
    				System.out.println("add user is already member of root group " + groupAdd.getPath());
    			}
   	    		Group whileGroup;
   	    		while (true) {
    	    		whileGroup=findNonMemberLastGroupInPath(groupAdd, user);
    				System.out.println("add while group " + whileGroup.getPath());

    	    		//if all child found, proof the root
    	    		if (whileGroup.getPath().matches(groupAdd.getPath())){
    	    			if (!BasicModel.isMember(relationshipManager, user, groupAdd)) {
	    	   				System.out.println("add " + user.getLoginName().toString() + " as member to groupPath " + groupAdd.getPath() + " as member to group " + groupAdd.getName());
	    	   				addToGroup(relationshipManager, user, groupAdd);
	    	   				identityManager.update(user);
	    	   				break;
    	    			} else {
    	    				System.out.println("add user is already member of root group " + groupAdd.getPath());
	    	   				break;
    	    			}
    		        } else {
    		        	System.out.println("add " + user.getLoginName().toString() + " as member to whileGroupPath " + whileGroup.getPath() + " as member to whileGroup " + whileGroup.getName());
    		        	addToGroup(relationshipManager, user, whileGroup);
    	    			identityManager.update(user);
    		        }
    	    	}
        	}
        }
        try {
    		String[]parts=groupPathDelete.split("/");
    		if (parts.length>1) groupDelete = findGroupByGroupPath(groupPathDelete,identityManager,relationshipManager);
    		
    	} catch (Exception exception) {
        exception.printStackTrace();
    	}
       	if (groupDelete!=null) {       
       		System.out.println("delete while group " + groupPathDelete);

           		if (!activateForAllDeleteSubGroups) {
           			if (BasicModel.isMember(relationshipManager, user, groupDelete)) {
           				removeFromGroup(relationshipManager, user, groupDelete);
           				identityManager.update(user);
           			} else {
           				//System.out.println("user is not a member of group " + group.getPath());
           			}
            		//find all child groups and delete the user from this groups
           		} else {
       	    		Group whileGroup;
       	    		while (true) {
        				whileGroup=findMemberLastGroupInPath(groupDelete, user);
        				System.out.println("delete while group " + whileGroup.getPath());

        	    		//if all child found, proof the root
        	    		if (whileGroup.getPath().matches(groupDelete.getPath())){
        	    			if (BasicModel.isMember(relationshipManager, user, groupDelete)) {
    	    	   				//System.out.println("add " + user.getLoginName().toString() + " as member to groupPath " + group.getPath() + " as member to group " + group.getName());
    	    	   				removeFromGroup(relationshipManager, user, groupDelete);
    	    	   				//identityManager.update(group);
    	    	   				identityManager.update(user);
    	    	   				break;
        	    			} else {
        	    				//System.out.println("user is not a member of group" + group.getPath());
    	    	   				break;
        	    			}
        		        } else {
        		        	//System.out.println("add " + user.getLoginName().toString() + " as member to whileGroupPath " + whileGroup.getPath() + " as member to whileGroup " + whileGroup.getName());
        		        	removeFromGroup(relationshipManager, user, whileGroup);
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
	    			if(role.getName().matches(roleName)){foundRole=true;}
	    		}
	    		if((!foundRole && !user.getLoginName().matches("administrator")) || (!foundRole && user.getLoginName().matches("administrator") && !role.getName().matches("administrator"))){
	    			//System.out.println("revokeRole " + role.getName());
	    			identityManager.update(role);
	    			identityManager.update(user);
	    			BasicModel.revokeRole(relationshipManager, user, role);
	    			identityManager.update(user);
	    		} else {
	    			//System.out.println("unchangedRole " + role.getName());
	    			unchangedRole.add(role);
	    		}
    		}
    		//add new roles
	        Boolean foundRole=false;
	    	for (String roleName : customerIdmRolesCombinationStrings) {
	    		foundRole=false;
	    		for (Role role : unchangedRole) {
	    			if(role.getName().matches(roleName)){foundRole=true;}
	    		}
	    		if(!foundRole){
	    			Role roleToAdd=findRoleByName(roleName, identityManager, relationshipManager);
	    			//System.out.println("grantRole " + roleToAdd.getName());
	    			identityManager.update(roleToAdd);
	    			identityManager.update(user);
	    			BasicModel.grantRole(relationshipManager, user, roleToAdd);
    				identityManager.update(user);
	    		}
	    	}
      	}
    	//assign groupRole to user
    	if (customerIdmGroupRolesCombinationStrings!=null){
    		List<GroupRole>unchangedGroupRole = new ArrayList<GroupRole>();
    		//delete all User related roles except selected roles
    		for (GroupRole groupRole : userGroupRoles) {
	    		Boolean foundGroupRole = false;
	    		for (String groupRoleName : customerIdmGroupRolesCombinationStrings) {
	    			//Is role a selected role?
	    			String compareString=groupRole.getGroup().getName() + " - " + groupRole.getRole().getName();
	    			if(compareString.matches(groupRoleName)){foundGroupRole=true;}
	    		}
	    		if((!foundGroupRole && !user.getLoginName().matches("administrator")) || (!foundGroupRole && user.getLoginName().matches("administrator") && !groupRole.getRole().getName().matches("administrator"))){
	    			//System.out.println("revokeRole " + role.getName());
	    			identityManager.update(user);
	    			BasicModel.revokeGroupRole(relationshipManager, user, groupRole.getRole(), groupRole.getGroup());
	    			identityManager.update(user);
	    		} else {
	    			//System.out.println("unchangedRole " + role.getName());
	    			unchangedGroupRole.add(groupRole);
	    		}
    		}
    		//add new roles
	        Boolean foundGroupRole=false;
	    	for (String groupRoleName : customerIdmGroupRolesCombinationStrings) {
	    		foundGroupRole=false;
	    		for (GroupRole groupRole : unchangedGroupRole) {
	    			String compareString=groupRole.getGroup().getName() + " - " + groupRole.getRole().getName();
	    			if(compareString.matches(groupRoleName)){foundGroupRole=true;}
	    		}
	    		if(!foundGroupRole){
	    			String[]parts=groupRoleName.split(" - ");
	    			if (parts.length==2){
			    		Group groupToAdd=findGroupByGroupPath(parts[0],identityManager,relationshipManager);
			    		Role roleToAdd=findRoleByName(parts[1],identityManager,relationshipManager);
		    			//System.out.println("grantRole " + roleToAdd.getName());
		    			identityManager.update(user);
		    			BasicModel.grantGroupRole(relationshipManager, user, roleToAdd, groupToAdd);
	    				identityManager.update(user);
	    			}
	    		}
	    	}
      	}
    	
    	return "success";
    }
    public String editGroup(String groupPathName, List<String> customerIdmRolesCombinationStrings) {
    	if (customerIdmRolesCombinationStrings!=null)System.out.println("call editGroup " + groupPathName + " and " + customerIdmRolesCombinationStrings.toString());
    	if (customerIdmRolesCombinationStrings==null)System.out.println("call editGroup" + groupPathName + " and Keine Rollen");
    	
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	
    	List<Role> userRoles = findRoleByGroupPath(groupPathName,identityManager,relationshipManager);
    	Group group = findGroupByGroupPath(groupPathName,identityManager,relationshipManager);
        //assign role to group
    	if (customerIdmRolesCombinationStrings!=null){
    		List<Role>unchangedRole = new ArrayList<Role>();
    		//delete all User related roles except selected roles
    		for (Role role : userRoles) {
	    		Boolean foundRole = false;
	    		for (String roleName : customerIdmRolesCombinationStrings) {
	    			//Is role a selected role?
	    			if(role.getName().matches(roleName)){foundRole=true;}
	    		}
	    		if(!foundRole){
	    			//System.out.println("revokeRole " + role.getName());
	    			identityManager.update(role);
	    			identityManager.update(group);
	    			BasicModel.revokeRole(relationshipManager, group, role);
	    			identityManager.update(group);
	    		} else {
	    			//System.out.println("unchangedRole " + role.getName());
	    			unchangedRole.add(role);
	    		}
    		}
    		//add new roles
	        Boolean foundRole=false;
	    	for (String roleName : customerIdmRolesCombinationStrings) {
	    		foundRole=false;
	    		for (Role role : unchangedRole) {
	    			if(role.getName().matches(roleName)){foundRole=true;}
	    		}
	    		if(!foundRole){
	    			Role roleToAdd=findRoleByName(roleName, identityManager, relationshipManager);
	    			//System.out.println("grantRole " + roleToAdd.getName());
	    			identityManager.update(roleToAdd);
	    			identityManager.update(group);
	    			BasicModel.grantRole(relationshipManager, group, roleToAdd);
    				identityManager.update(group);
    				
	    		}
	    	}
      	}
    	
    	return "success";
    }
    public Group findNonMemberLastGroupInPath(Group rootGroup, User user){
    	//System.out.println("call findLastGroupInPath for group: " + rootGroup.getPath());
    	Group lastGroup = rootGroup;
    	Boolean hasChild=false;
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
        queryg.where(conditiong);
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
    	//System.out.println("last Group is: " + lastGroup.getPath()); 
    	return lastGroup;
    }
    public Group findMemberLastGroupInPath(Group rootGroup, User user){
    	//System.out.println("call findLastGroupInPath for group: " + rootGroup.getPath());
    	Group lastGroup = rootGroup;
    	Boolean hasChild=false;
    	IdentityManager identityManager = this.partitionManager.createIdentityManager();
    	RelationshipManager relationshipManager = this.partitionManager.createRelationshipManager();
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
        queryg.where(conditiong);
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		//the group is a child and a non member
    		if ((group.getPath().length()>rootGroup.getPath().length()) && group.getPath().contains(rootGroup.getPath()) && BasicModel.isMember(relationshipManager, user, group)){
    			hasChild=true;
    			//choose the child as last group and search again
    			lastGroup=findMemberLastGroupInPath(group, user);
    			break;
    		}
    	}
    	//System.out.println("last Group is: " + lastGroup.getPath()); 
    	return lastGroup;
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
    public List<GroupRole> findGroupRoleByUser(User user, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	RelationshipQuery<GroupRole> querygms = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
    	querygms.setParameter(Grant.ASSIGNEE, user);
    	List<GroupRole> resultGrouRole = querygms.getResultList();        
    	return resultGrouRole;
    
    }
    public List<Role> findRoleByGroupPath(String groupPath, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	List<Role>roleList = new ArrayList<Role>();
    	
    	RelationshipQuery<GroupRole> querygms = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
    	querygms.setParameter(GroupRole.GROUP, findGroupByGroupPath(groupPath, identityManager, relationshipManager));
    	List<GroupRole> resultGrant = querygms.getResultList();
        for (GroupRole groupRole : resultGrant) {
        	roleList.add(groupRole.getRole());
    	}
    	return roleList;
    
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
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, userName);
        queryu.where(conditionu);
		// find only by the first name
		List<User> resultUser = queryu.getResultList();
		if(!resultUser.isEmpty()) {return resultUser.get(0);} else {return null;}		
    }
    public Group findLastGroupInPath(Group groupToDelete, IdentityManager identityManager, RelationshipManager relationshipManager){
    	//System.out.println("call findLastGroupInPath for group: " + groupToDelete.getPath());
    	Group lastGroup = groupToDelete;
    	Boolean hasChild=false;
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
        queryg.where(conditiong);
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
    	//System.out.println("last Group is: " + lastGroup.getPath()); 
    	return lastGroup;
    }
    public org.picketlink.idm.model.basic.Group findGroupByGroupPath(String groupPath, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.like(Group.PATH, groupPath);
        queryg.where(conditiong);
		List<Group> result = queryg.getResultList();
		//System.out.println("looking for group " + groupPath);
	    //group found			
	    if (!result.isEmpty()) {
	    	return result.get(0);
	    } else {
	    	return null;
	    }
    }
    
    public String deleteUser(String userName, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, userName);
        queryu.where(conditionu);
       
    	// find only by the first name
    	List<User> resultUser = queryu.getResultList();
    	for (User user : resultUser) {
    		deleteUserConstraints(user,identityManager,relationshipManager);
        	identityManager.update(user);
        	identityManager.remove(user);
    	}
    	return "success";
    }  
    
    public String updateUserAttribute(String userName, String attribute, String value, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryu = builder.createIdentityQuery(User.class);
        Condition conditionu = builder.like(User.LOGIN_NAME, userName);
        queryu.where(conditionu);
       
    	// find only by the first name
    	List<User> resultUser = queryu.getResultList();
    	for (User user : resultUser) {
    		user.removeAttribute("status");
   		    log.info("remove attribute");
		    user.setAttribute( new Attribute<String>(attribute, value));
   		    log.info("set Attribute");
        	identityManager.update(user);
        	log.info("update user");
    	}
    	return "success";
    } 
    public void deleteUserConstraints(User userToDelete, IdentityManager identityManager, RelationshipManager relationshipManager) {
	    
    	if(userToDelete!=null){
    		//find the users in all groups and delete this relation
    		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
            Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
            queryg.where(conditiong);
        	List<Group> resultG = queryg.getResultList();
        	for (Group group : resultG) {
        		//Delete user from the group
	    		RelationshipQuery<GroupMembership> querygms = relationshipManager.<GroupMembership> createRelationshipQuery(GroupMembership.class);
	        	querygms.setParameter(GroupMembership.GROUP, group);
	        	List<GroupMembership> resultGroupMembership = querygms.getResultList();
	        	for (GroupMembership memberShip : resultGroupMembership) {
	        		//System.out.println("remove memberShip: " + memberShip.getMember().getId());
	        		if (userToDelete.getId().matches(memberShip.getMember().getId()))relationshipManager.remove(memberShip);
	        	}
	        	
        	}
    		    		
    	}
    	
    }
    public String deleteGroup(String groupPath, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	//search Group
    	if (!groupPath.isEmpty()) {
	    	
    		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
        	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
            Condition conditiong = builder.like(Group.PATH, groupPath);
            queryg.where(conditiong);
			List<Group> result = queryg.getResultList();
			//System.out.println("want to remove Group " + groupPath);
		    			
	        if (!result.isEmpty()) {
	        	Group group = result.get(0);
	        	//System.out.println("found Group " + group.getPath());
	        	String[]parts=group.getPath().split("/");
	    		//is a first-level-group
	    		if(parts.length==2){
	    			//System.out.println("is a first-level Group");
	    		}
	    		//is a second-level-group
	    		if(parts.length==3){ 
	    			//System.out.println("is a second-level Group");
	    		}
	    		//is a third-level-group
	    		if(parts.length==4){
	    			//System.out.println("is a third-level Group");
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
    public String deleteRole(String roleName, IdentityManager identityManager, RelationshipManager relationshipManager) {
    	//search Group
    	if (!roleName.isEmpty()) {
    		IdentityQueryBuilder builder = identityManager.getQueryBuilder();
            IdentityQuery queryr = builder.createIdentityQuery(Role.class);
            Condition conditionr = builder.like(Role.NAME, roleName);
            queryr.where(conditionr);
			List<Role> result = queryr.getResultList();
			//System.out.println("want to remove Group " + roleName);
		    			
	        if (!result.isEmpty()) {
	        	Role role = result.get(0);
	        	deleteRoleConstraints(role, identityManager, relationshipManager);
	        	identityManager.update(role);
	        	identityManager.remove(role);
	        	

		        
	        }
	        
    	}
		
    	return "success";
    }
    public void deleteGroupConstraints(Group groupToDelete, IdentityManager identityManager, RelationshipManager relationshipManager){
    	// Childs are not allowed!
    	//System.out.println("call deleteGroupConstraints for group: " + groupToDelete.getPath());
    	Boolean hasChild=false;
    	IdentityQueryBuilder builder = identityManager.getQueryBuilder();
    	IdentityQuery queryg = builder.createIdentityQuery(Group.class);
        Condition conditiong = builder.lessThan(Group.CREATED_DATE, new Date());
        queryg.where(conditiong);
    	List<Group> resultG = queryg.getResultList();
    	for (Group group : resultG) {
    		//the group is a child
    		if ((group.getPath().length()>groupToDelete.getPath().length()) && group.getPath().contains(groupToDelete.getPath())){
    			//System.out.println("found a child!");
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
        		//System.out.println("remove memberShip: " + memberShip.getMember().toString());
        		relationshipManager.remove(memberShip);
        	}
    		//find the groupRoleRelations and delete this relations
    		RelationshipQuery<GroupRole> query = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
        	query.setParameter(GroupRole.GROUP, groupToDelete);
        	List<GroupRole> resultGroupRole = query.getResultList();
        	for (GroupRole groupRole : resultGroupRole) {
        		//System.out.println("remove GroupRole: " + groupRole.getRole().getName());
        		relationshipManager.remove(groupRole);
        	}    		
    	}
    	
    }
    public void deleteRoleConstraints(Role roleToDelete, IdentityManager identityManager, RelationshipManager relationshipManager){
   	
    	//System.out.println("call deleteRoleConstraints for role: " + roleToDelete.getName());
    	//find the groupRoleRelations and delete this relations
		RelationshipQuery<GroupRole> query = relationshipManager.<GroupRole> createRelationshipQuery(GroupRole.class);
    	query.setParameter(GroupRole.ROLE, roleToDelete);
    	List<GroupRole> resultGroupRole = query.getResultList();
    	for (GroupRole groupRole : resultGroupRole) {
    		//System.out.println("remove GroupRole: " + groupRole.getRole().getName());
    		relationshipManager.remove(groupRole);
    	}   
    	
    	
    }
}
