/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.application.customer.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import net.application.util.ProfileGroup;
import net.application.util.Status;

/**
 *
 * @author Christian Lewer
 * 
 */
@Entity
@NamedQueries({
	  @NamedQuery(name="findAllProfiles", query="SELECT o FROM Profile o"),
	  @NamedQuery(name="findAllProfilesCount", query="SELECT COUNT(o) FROM Profile o"),
	  @NamedQuery(name="findProfileByName", query="SELECT o FROM Profile o WHERE o.name = :name"),
})


public class Profile implements Serializable
{

	   /**
		 * 
		 */
	   private static final long serialVersionUID = 1L;
	   private Integer profileId;
	   private String name;
	   private String type;
	   private Date doc;
	   private Date dob;
	   private Date doe;
	   private String employer;
	   private String industry;
	   private String description;
	   
	   
	
	   @Enumerated
	   private Status profileStatus;
	   @Enumerated
	   private ProfileGroup profileGroup;
	
	   @ManyToOne
	   @JoinColumn(name="customerId")
	   private Customer customer;
	
		@Id @GeneratedValue
		public Integer getProfileId() {
			return profileId;
		}
			
		public void setProfileId(Integer profileId) {
			this.profileId = profileId;
		}
		
		
		public String getName() {
			return name;
		}
		
		
		public void setName(String name) {
			this.name = name;
		}
		
		
		public Date getDoc() {
			return doc;
		}
		
		
		public void setDoc(Date doc) {
			this.doc = doc;
		}
		
		
		public Status getProfileStatus() {
			return profileStatus;
		}
		
		
		public void setProfileStatus(Status profileStatus) {
			this.profileStatus = profileStatus;
		}
		
		public ProfileGroup getProfileGroup() {
			return profileGroup;
		}
	
	
		public void setProfileGroup(ProfileGroup profileGroup) {
			this.profileGroup = profileGroup;
		}
	
		public String getDescription() {
			return description;
		}
		
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public Customer getCustomer() {
			return customer;
		}
	
	
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
	
		 public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Date getDob() {
			return dob;
		}

		public void setDob(Date dob) {
			this.dob = dob;
		}

		public Date getDoe() {
			return doe;
		}

		public void setDoe(Date doe) {
			this.doe = doe;
		}

		public String getEmployer() {
			return employer;
		}

		public void setEmployer(String employer) {
			this.employer = employer;
		}

		public String getIndustry() {
			return industry;
		}

		public void setIndustry(String industry) {
			this.industry = industry;
		}


}
