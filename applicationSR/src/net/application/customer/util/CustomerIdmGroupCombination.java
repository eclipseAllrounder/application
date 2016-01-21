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

public class CustomerIdmGroupCombination implements Serializable
{

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;

   private String idmGroupName;
   private String idmGroupPath;
   private Date idmCreatedDate;
   private Integer customerCount;
   private String roles;
   private List<CustomerIdmGroupCombination> groupIdmRolesCombination;
   private List<String> groupIdmRolesCombinationStrings;
	
   public String getIdmGroupName() {
		return idmGroupName;
	}
	public void setIdmGroupName(String idmGroupName) {
		this.idmGroupName = idmGroupName;
	}
	public String getIdmGroupPath() {
		return idmGroupPath;
	}
	public void setIdmGroupPath(String idmGroupPath) {
		this.idmGroupPath = idmGroupPath;
	}
	public Date getIdmCreatedDate() {
		return idmCreatedDate;
	}
	public void setIdmCreatedDate(Date idmCreatedDate) {
		this.idmCreatedDate = idmCreatedDate;
	}
	public Integer getCustomerCount() {
		return customerCount;
	}
	public void setCustomerCount(Integer customerCount) {
		this.customerCount = customerCount;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public List<CustomerIdmGroupCombination> getGroupIdmRolesCombination() {
		return groupIdmRolesCombination;
	}
	public void setGroupIdmRolesCombination(List<CustomerIdmGroupCombination> groupIdmRolesCombination) {
		this.groupIdmRolesCombination = groupIdmRolesCombination;
	}
	public List<String> getGroupIdmRolesCombinationStrings() {
		return groupIdmRolesCombinationStrings;
	}
	public void setGroupIdmRolesCombinationStrings(
			List<String> groupIdmRolesCombinationStrings) {
		this.groupIdmRolesCombinationStrings = groupIdmRolesCombinationStrings;
	}

 
  
   
 

  
   
  
 
    
}
