/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.application.customer.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import net.application.util.Status;

/**
 *
 * @author Christian Lewer
 * 
 */
@Entity
@NamedQueries({
	  @NamedQuery(name="findAllCustomers", query="SELECT o FROM Customer o"),
	  @NamedQuery(name="findAllCustomersCount", query="SELECT COUNT(o) FROM Customer o"),
	  @NamedQuery(name="findCustomerByVerifyId", 
      query="SELECT o FROM Customer o WHERE o.accountActivationString = :accountActivationString"),
	  @NamedQuery(name="findCustomerByMail", 
	  query="SELECT o FROM Customer o WHERE o.email = :email"),
	  @NamedQuery(name="findUserByName", 
	  query="SELECT o FROM Customer o WHERE o.userName = :userName"),
	  @NamedQuery(name="deleteCustomerByuserName", 
      query="DELETE FROM Customer o WHERE o.userName = :userName")
	})


@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Customer implements Serializable
{

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;
   private Integer customerId;
   private String accountActivationString;
   private String userName;
   private String firstName;
   private String lastName;
   private String street;
   private String city;
   private String streetNumber;
   private Integer zip;
   private Country country;
   private String dailNumber;
   private String email;
   private String internalEmail;
   private String tagline;
   private String gender;
   private Date dob;
   private Date userSince;
   private Boolean confirmAgb;
   @Enumerated
   private Status userStatus;

 
  
   
 

   @Id @GeneratedValue
   public Integer getCustomerId(){ return customerId; }
   public void setCustomerId(Integer customerId) { this.customerId = customerId; }   
 
   @NotEmpty(message="{register.city.length}")
   @Length(max = 40, message="{register.city.length}")
   @Size(max = 40, message="{register.city.size}")
   @Email
   public String getUserName() { return userName; }
   public void setUserName(String userName) { this.userName = userName; }
   
   @NotEmpty(message="{register.firstName.notEmpty}")
   @NotNull(message="{register.firstName.notNull}")
   @Length(max = 40, message="{register.firstName.length}")
   @Size(max = 40, message="{register.firstName.size}")
   @Pattern(regexp="[üöäÜÖÄa-zA-Z- ]+", message="{register.firstName.pattern}")
   public String getFirstName() { return firstName; }   
   public void setFirstName(String firstName) { this.firstName = firstName; }
   
   @NotEmpty(message="{register.lastName.notEmpty}")
   @NotNull(message="{register.lastName.notNull}")
   @Length(max = 40, message="{register.lastName.length}")
   @Size(max = 40, message="{register.lastName.size}")
   @Pattern(regexp="[üöäÜÖÄa-zA-Z- ]+", message="{register.lastName.pattern}")
   public String getLastName() { return lastName; }   
   public void setLastName(String lastName) { this.lastName = lastName; }   
   
   @NotEmpty(message="{register.mail.notEmpty}")
   @NotNull(message="{register.mail.notNull}")
   @Length(max = 40, message="{register.mail.length}")
   @Size(max = 40, message="{register.mail.size}")
   @Email
   public String getEmail() { return email; }   
   public void setEmail(String email) { this.email = email; }
   
   public Date getDob() { return dob; }
   public void setDob(Date dob) { this.dob = dob; }

  // @NotEmpty(message="{register.city.length}")
   public String getGender() { return gender; }
   public void setGender(String gender) { this.gender = gender; }
   
   //@NotEmpty(message="{register.street.notEmpty}")
   @Length(max = 40, message="{register.street.length}")
   @Size(max = 40, message="{register.street.size}")
   @Pattern(regexp="[üöäÜÖÄa-zA-Z-]+", message="{register.street.pattern}")
   public String getStreet() { return street; }
   public void setStreet(String street) { this.street = street; }
   
  // @NotEmpty(message="{register.streetNumber.notEmpty}")
   @Length(max = 40, message="{register.streetNumber.length}")
   @Size(max = 40, message="{register.streetNumber.size}")
   @Pattern(regexp="^[0-9]+[a-z]+", message="{register.streetNumber.pattern}")
   public String getStreetNumber() { return streetNumber; }  
   public void setStreetNumber(String streetNumber) { this.streetNumber = streetNumber; }
   
   
   //@NotNull(message="{register.streetNumber.notNull}")
   @Range(min=1000, message="{register.zip.min}")
   public Integer getZip() { return zip; }
   public void setZip(Integer zip) { this.zip = zip; }
 
   
   //@NotEmpty(message="{register.city.notEmpty}")
   @Length(max = 40, message="{register.city.length}")
   @Size(max = 40, message="{register.city.size}")
   @Pattern(regexp="[üöäÜÖÄa-zA-Z-]+", message="{register.city.pattern}")
   public String getCity() { return city; }
   public void setCity(String city) { this.city = city; }
   
   @Length(min = 5, max = 40)
   @Pattern(regexp="[+ ]?[0-9]+", message="{register.dailNumber.pattern}")
   public String getDailNumber() { return dailNumber; }
   public void setDailNumber(String dailNumber) { this.dailNumber = dailNumber; }


   public Date getUserSince() { return userSince; }   
   public void setUserSince(Date userSince) { this.userSince = userSince; }

   public String getTagline() { return tagline; }
   public void setTagline(String tagline) { this.tagline = tagline; }

   public Status getUserStatus() {
	return userStatus;
	}
	public void setUserStatus(Status userStatus) {
		this.userStatus = userStatus;
	}
  
   @OneToOne
   @JoinColumn(name = "countryId")
   public Country getCountry() { return country; }
   public void setCountry(Country country) { this.country = country; }
public String getAccountActivationString() {
	return accountActivationString;
}
public void setAccountActivationString(String accountActivationString) {
	this.accountActivationString = accountActivationString;
}
public String getInternalEmail() {
	return internalEmail;
}
public void setInternalEmail(String internalEmail) {
	this.internalEmail = internalEmail;
}
@AssertTrue(message="{register.confirmAgb.notEmpty}")
public Boolean getConfirmAgb() {
	return confirmAgb;
}
public void setConfirmAgb(Boolean confirmAgb) {
	this.confirmAgb = confirmAgb;
}
   
  
 
    
}
