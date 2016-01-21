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
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
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
@ManagedBean
@ViewScoped
public class CustomerIdmUserCombination implements Serializable
{

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;
   private String customerId;
   private String userName;
   private Boolean enabled;
   private String firstName;
   private String lastName;
   private String street;
   private String city;
   private String streetNumber;
   private Integer zip;
   private String country;
   private String dailNumber;
   private String email;
   private String tagline;
   private String gender;
   private Date dob;
   private Date userSince;
   private String idmUsername;
   private String idmFirstname;
   private String idmLastname;
   private String idmMail;
   private Date idmExpirationDate;
   private Date idmCreatedDate;
   private String groups;
   private String groupRoles;
   private String roles;
   private List<Role> rolesList;
   private List<CustomerIdmRolesCombination> customerIdmRolesCombination;
   private List<String> customerIdmRolesCombinationStrings;
   private List<String> customerIdmGroupRolesCombinationStrings;
   private List<GroupRole> groupRolesList;
   private List<Group> groupsList;
 
  
   

   public String getCustomerId(){ return customerId; }
   public void setCustomerId(String customerId) { this.customerId = customerId; }   
 
   public String getUserName() { return userName; }
   public void setUserName(String userName) { this.userName = userName; }
   
   public String getFirstName() { return firstName; }   
   public void setFirstName(String firstName) { this.firstName = firstName; }
   
   public String getLastName() { return lastName; }   
   public void setLastName(String lastName) { this.lastName = lastName; }   
   
   public String getEmail() { return email; }   
   public void setEmail(String email) { this.email = email; }
   
   public Date getDob() { return dob; }
   public void setDob(Date dob) { this.dob = dob; }

   public String getGender() { return gender; }
   public void setGender(String gender) { this.gender = gender; }
   
   public String getStreet() { return street; }
   public void setStreet(String street) { this.street = street; }
   
   public String getStreetNumber() { return streetNumber; }  
   public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
   
   public Integer getZip() { return zip; }
   public void setZip(Integer zip) { this.zip = zip; }
 
   public String getCity() { return city; }
   public void setCity(String city) { this.city = city; }
   
   public String getDailNumber() { return dailNumber; }
   public void setDailNumber(String dailNumber) { this.dailNumber = dailNumber; }

   public Date getUserSince() { return userSince; }   
   public void setUserSince(Date userSince) { this.userSince = userSince; }

   public String getTagline() { return tagline; }
   public void setTagline(String tagline) { this.tagline = tagline; }

   public String getCountry() { return country; }
   public void setCountry(String country) { this.country = country; }

   public String getIdmUsername() {
		return idmUsername;
	}
	public void setIdmUsername(String idmUsername) {
		this.idmUsername = idmUsername;
	}
	public String getIdmFirstname() {
		return idmFirstname;
	}
	public void setIdmFirstname(String idmFirstname) {
		this.idmFirstname = idmFirstname;
	}
	public String getIdmLastname() {
		return idmLastname;
	}
	public void setIdmLastname(String idmLastname) {
		this.idmLastname = idmLastname;
	}
	public String getIdmMail() {
		return idmMail;
	}
	public void setIdmMail(String idmMail) {
		this.idmMail = idmMail;
	}
	public Date getIdmExpirationDate() {
		return idmExpirationDate;
	}
	public void setIdmExpirationDate(Date idmExpirationDate) {
		this.idmExpirationDate = idmExpirationDate;
	}
	public Date getIdmCreatedDate() {
		return idmCreatedDate;
	}
	public void setIdmCreatedDate(Date idmCreatedDate) {
		this.idmCreatedDate = idmCreatedDate;
	}
	public String getGroups() {
		return groups;
	}
	public void setGroups(String groups) {
		this.groups = groups;
	}
	public String getGroupRoles() {
		return groupRoles;
	}
	public void setGroupRoles(String groupRoles) {
		this.groupRoles = groupRoles;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public List<Role> getRolesList() {
		return rolesList;
	}
	public void setRolesList(List<Role> rolesList) {
		this.rolesList = rolesList;
	}
	public List<GroupRole> getGroupRolesList() {
		return groupRolesList;
	}
	public void setGroupRolesList(List<GroupRole> groupRolesList) {
		this.groupRolesList = groupRolesList;
	}
	public List<Group> getGroupsList() {
		return groupsList;
	}
	public void setGroupsList(List<Group> groupsList) {
		this.groupsList = groupsList;
	}
	public List<CustomerIdmRolesCombination> getCustomerIdmRolesCombination() {
		return customerIdmRolesCombination;
	}
	public void setCustomerIdmRolesCombination(
			List<CustomerIdmRolesCombination> customerIdmRolesCombination) {
		this.customerIdmRolesCombination = customerIdmRolesCombination;
	}
	public List<String> getCustomerIdmRolesCombinationStrings() {
		return customerIdmRolesCombinationStrings;
	}
	public void setCustomerIdmRolesCombinationStrings(
			List<String> customerIdmRolesCombinationStrings) {
		this.customerIdmRolesCombinationStrings = customerIdmRolesCombinationStrings;
	}
	public List<String> getCustomerIdmGroupRolesCombinationStrings() {
		return customerIdmGroupRolesCombinationStrings;
	}
	public void setCustomerIdmGroupRolesCombinationStrings(
			List<String> customerIdmGroupRolesCombinationStrings) {
		this.customerIdmGroupRolesCombinationStrings = customerIdmGroupRolesCombinationStrings;
	}
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

   
  
 
    
}
