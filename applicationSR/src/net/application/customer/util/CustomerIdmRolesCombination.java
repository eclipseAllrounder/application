/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.application.customer.util;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import net.application.customer.entity.Country;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupRole;
import org.picketlink.idm.model.basic.Role;

/**
 *
 * @author Christian Lewer
 * 
 */                                                     
public class CustomerIdmRolesCombination implements Serializable
{

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;

   private String idmRoleName;
   private Date idmRoleCreatedDate;
   
   
   public String getIdmRoleName() {
	return idmRoleName;
   }

public void setIdmRoleName(String idmRoleName) {
	this.idmRoleName = idmRoleName;
}

public Date getIdmRoleCreatedDate() {
	return idmRoleCreatedDate;
}

public void setIdmRoleCreatedDate(Date idmRoleCreatedDate) {
	this.idmRoleCreatedDate = idmRoleCreatedDate;
}
  
	
  

 
  
   
 

  
   
  
 
    
}
