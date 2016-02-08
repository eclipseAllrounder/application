package net.application.authorization.util;

import static org.picketlink.idm.model.basic.BasicModel.getRole;
import static org.picketlink.idm.model.basic.BasicModel.hasRole;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.model.Account; import org.picketlink.idm.model.Attribute;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupMembership;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.idm.query.RelationshipQuery;

import net.application.customer.util.GroupTreeBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;


/**
  * This is a utility bean that may be used by the view layer to determine whether the
  * current user has specific privileges.
  *
  * @author Shane Bryzak
  *
  */
@Named
@SessionScoped
public class AuthorizationChecker implements Serializable {

     /**
      *
      */
     private static final long serialVersionUID = 1L;

     @Inject transient Logger log;

     @Inject
     private Identity identity;

     @Inject
     private IdentityManager identityManager;

     @Inject
     private RelationshipManager relationshipManager;

     @Inject IDMInitializer ia;

     private String roleNameS;
     private String groupNameS;

     @PostConstruct
     public void init() {
        log.info("init Setting default Values");
        roleNameS="xxx";
        groupNameS="xxx";
     }

     public boolean hasApplicationRole(String roleName) {
         Role role = BasicModel.getRole(identityManager, roleName);
         //log.info("hasApplicationRole: " + roleName);
         if (identity.getAccount()==null)return false;
         User user = ia.findUserByUsername(((User) identity.getAccount()).getLoginName(), identityManager, relationshipManager);
         //log.info("User found");
         // Lookup the user by their role
         Boolean test =false;
         test = ia.findRoleForUser(user, role, identityManager, relationshipManager);
         //log.info("test ready: " + test);
         return test;
     }

     public boolean isGroupMember(String groupName) {
         Group group = BasicModel.getGroup(identityManager, groupName);
         log.info("isGroupMember: " + groupName);
         if (identity.getAccount()==null)return false;
         return BasicModel.isMember(relationshipManager,identity.getAccount(), group);
     }
     public boolean isSubGroupMember(String groupName, String subgroupName) {
         Group group = BasicModel.getGroup(identityManager, groupName);
         Group subgroup = BasicModel.getGroup(identityManager,subgroupName, group);
         if (identity.getAccount()==null)return false;
         return BasicModel.isMember(relationshipManager,identity.getAccount(), subgroup);
     }

     public boolean hasGroupRole(String roleName, String groupName) {

         if (identity.getAccount()==null)return false;

         if ( (roleName!=null && groupName!=null) &&(!roleName.matches(roleNameS) || !groupName.matches(groupNameS))){
             Group group = BasicModel.getGroup(identityManager, groupName);
             Role role = BasicModel.getRole(identityManager, roleName);
             roleNameS=roleName;
             groupNameS=groupName;
             log.info(((User) identity.getAccount()).getLoginName() + " hasGroupRole: " + groupName + " - " + roleName);
             return BasicModel.hasGroupRole(relationshipManager,identity.getAccount(), role, group);
         }
         
         if ( (roleName!=null && groupName!=null) &&(roleName.matches(roleNameS) || groupName.matches(groupNameS))){
        	 log.info(((User) identity.getAccount()).getLoginName() + " hasGroupRole: without idm call " + groupName + " - " + roleName);
        	 return true;
         }
         
         return false;
     }
}


