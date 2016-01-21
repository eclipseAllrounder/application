package net.application.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.picketlink.idm.query.Condition;

	@Named
	@SessionScoped
	public class UsersBean implements Serializable {
	 
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		private UsersModel usersModel;
	    

		@Inject
	    private EntityManager entityManager;       

		@Inject
	    private UserTransaction utx; 
		
		@Inject
		private UserDao userDao;
	    
		@Inject Logger log;
		
		private String customerListColumnUsernameFilter = "";
		private String customerListColumnFirstnameFilter = "";
	    private String customerListColumnLastnameFilter = "";
	    private List<Condition>conditionSet= new ArrayList<Condition>();
	    
	    private String sort;
		private Boolean sortLoginNameUp;
	    private Boolean sortMailUp;
	    private Boolean sortEnableUp;
	    private Boolean sortCreateDateUp;
	    private Boolean sortExpiryDateUp;
		private Boolean sortFirstNameUp;
	    private Boolean sortLastNameUp;
	    private Boolean sortLoginNameDown;
	    private Boolean sortMailDown;
	    private Boolean sortEnableDown;
	    private Boolean sortCreateDateDown;
	    private Boolean sortExpiryDateDown;
		private Boolean sortFirstNameDown;
	    private Boolean sortLastNameDown;
	    private String currentUserName;
	    
	    
	   
	 
	    public UsersBean() {
	        initUsersModel();
	    }
	 
	    public UsersModel getUsers() {
	    	log.info("getUsers");
	    	// create the condition
	        String customerListColumnUsernameFilterHelper=customerListColumnUsernameFilter;
	        String customerListColumnFirstnameFilterHelper=customerListColumnFirstnameFilter;
	        String customerListColumnLastnameFilterHelper= customerListColumnLastnameFilter;
	    	if(customerListColumnUsernameFilter==null){
	    		customerListColumnUsernameFilterHelper="%";
	    	} else {
	    		if(customerListColumnUsernameFilter.isEmpty()){
	 	        	customerListColumnUsernameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnUsernameFilterHelper="%" + customerListColumnUsernameFilter + "%";
	 	        }
	        }
	        if(customerListColumnFirstnameFilter==null){
	         	customerListColumnFirstnameFilterHelper="%";
	        } else {
	 	        if(customerListColumnFirstnameFilter.isEmpty()){
	 	        	customerListColumnFirstnameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnFirstnameFilterHelper="%" + customerListColumnFirstnameFilter + "%";
	 	        }
	        }
	        if(customerListColumnLastnameFilter==null){
	         	customerListColumnLastnameFilterHelper="%";
	        } else {
	 	        if(customerListColumnLastnameFilter.isEmpty()){
	 	        	customerListColumnLastnameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnLastnameFilterHelper="%" + customerListColumnLastnameFilter + "%";
	 	        }
	        }
	        
	        Map<String, String>  mapCondition = new HashMap<String, String>();
	        mapCondition.put("ColumnLastnameFilter", customerListColumnLastnameFilterHelper);
	        mapCondition.put("ColumnFirstnameFilter", customerListColumnFirstnameFilterHelper);
	        mapCondition.put("ColumnUsernameFilter", customerListColumnUsernameFilterHelper);
	    
	        
	    	usersModel = new UsersModel(mapCondition, sort, entityManager, utx, userDao);
	        return usersModel;
	    }
	 
	    private void initUsersModel() {
	    	System.out.println("initUsersModel");
	    	setSortFalse();
	    	sort="sortLoginNameUp";
	    	sortLoginNameUp=true;
	    	// create the condition
	        String customerListColumnUsernameFilterHelper=customerListColumnUsernameFilter;
	        String customerListColumnFirstnameFilterHelper=customerListColumnFirstnameFilter;
	        String customerListColumnLastnameFilterHelper= customerListColumnLastnameFilter;
	    	if(customerListColumnUsernameFilter==null){
	    		customerListColumnUsernameFilterHelper="%";
	    	} else {
	    		if(customerListColumnUsernameFilter.isEmpty()){
	 	        	customerListColumnUsernameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnUsernameFilterHelper="%" + customerListColumnUsernameFilter + "%";
	 	        }
	        }
	        if(customerListColumnFirstnameFilter==null){
	         	customerListColumnFirstnameFilterHelper="%";
	        } else {
	 	        if(customerListColumnFirstnameFilter.isEmpty()){
	 	        	customerListColumnFirstnameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnFirstnameFilterHelper="%" + customerListColumnFirstnameFilter + "%";
	 	        }
	        }
	        if(customerListColumnLastnameFilter==null){
	         	customerListColumnLastnameFilterHelper="%";
	        } else {
	 	        if(customerListColumnLastnameFilter.isEmpty()){
	 	        	customerListColumnLastnameFilterHelper="%";
	 	        } else {
	 	        	customerListColumnLastnameFilterHelper="%" + customerListColumnLastnameFilter + "%";
	 	        }
	        }	        
	        Map<String, String>  mapCondition= new HashMap<String, String>();
	        mapCondition.put("ColumnLastnameFilter", customerListColumnLastnameFilterHelper);
	        mapCondition.put("ColumnFirstnameFilter", customerListColumnFirstnameFilterHelper);
	        mapCondition.put("ColumnUsernameFilter", customerListColumnUsernameFilterHelper);
	    	usersModel = new UsersModel(mapCondition, sort, entityManager, utx, userDao);
	    }
	    public void toggleSort(String sortType){
			log.info("sortLoginNameUp: " + sortLoginNameUp);
			log.info("sortLoginNameDown: " + sortLoginNameDown);
			setSortFalse();
	    	if(sortType!=null){
			if(sortType.matches("sortLoginNameUp")&&sortLoginNameUp==false) {setSortFalse();sortLoginNameUp=true;sortLoginNameDown=false;}
			if(sortType.matches("sortLoginNameDown")&&sortLoginNameDown==false) {setSortFalse();sortLoginNameUp=false;sortLoginNameDown=true;}
			if(sortType.matches("sortCreateDateUp")&&sortCreateDateUp==false) {setSortFalse();sortCreateDateUp=true;sortCreateDateDown=false;}
			if(sortType.matches("sortCreateDateDown")&&sortCreateDateDown==false) {setSortFalse();sortCreateDateUp=false;sortCreateDateDown=true;}
			if(sortType.matches("sortEnableUp")&&sortEnableUp==false) {setSortFalse();sortEnableUp=true;sortEnableDown=false;}
			if(sortType.matches("sortEnableDown")&&sortEnableDown==false) {setSortFalse();sortEnableUp=false;sortEnableDown=true;}
			sort=sortType;
	    	}
	    	log.info("sortLoginNameUp: " + sortLoginNameUp);
			log.info("sortLoginNameDown: " + sortLoginNameDown);
		}
		public void setSortFalse(){
			sortMailUp=false;
	    	sortMailUp=false;
	    	sortEnableUp=false;
	    	sortCreateDateUp=false;
	    	sortLoginNameUp=false;
	    	sortExpiryDateUp=false;
	    	sortFirstNameUp=false;
	    	sortLastNameUp=false;
	    	sortLoginNameDown=false;
	    	sortMailDown=false;
	    	sortEnableDown=false;
	    	sortCreateDateDown=false;
	    	sortExpiryDateDown=false;
	    	sortFirstNameDown=false;
	    	sortLastNameDown=false;
		}
		public Boolean getSortCreateDateUp() {
			return sortCreateDateUp;
		}

		public void setSortCreateDateUp(Boolean sortCreateDateUp) {
			this.sortCreateDateUp = sortCreateDateUp;
		}

		public Boolean getSortCreateDateDown() {
			return sortCreateDateDown;
		}

		public void setSortCreateDateDown(Boolean sortCreateDateDown) {
			this.sortCreateDateDown = sortCreateDateDown;
		}

		public Boolean getSortMailUp() {
			return sortMailUp;
		}

		public void setSortMailUp(Boolean sortMailUp) {
			this.sortMailUp = sortMailUp;
		}

		public Boolean getSortMailDown() {
			return sortMailDown;
		}

		public void setSortMailDown(Boolean sortMailDown) {
			this.sortMailDown = sortMailDown;
		}
		 public Boolean getSortEnableUp() {
				return sortEnableUp;
			}

			public void setSortEnableUp(Boolean sortEnableUp) {
				this.sortEnableUp = sortEnableUp;
			}

			public Boolean getSortExpiryDateUp() {
				return sortExpiryDateUp;
			}

			public void setSortExpiryDateUp(Boolean sortExpiryDateUp) {
				this.sortExpiryDateUp = sortExpiryDateUp;
			}

			public Boolean getSortFirstNameUp() {
				return sortFirstNameUp;
			}

			public void setSortFirstNameUp(Boolean sortFirstNameUp) {
				this.sortFirstNameUp = sortFirstNameUp;
			}

			public Boolean getSortLastNameUp() {
				return sortLastNameUp;
			}

			public void setSortLastNameUp(Boolean sortLastNameUp) {
				this.sortLastNameUp = sortLastNameUp;
			}

			public Boolean getSortEnableDown() {
				return sortEnableDown;
			}

			public void setSortEnableDown(Boolean sortEnableDown) {
				this.sortEnableDown = sortEnableDown;
			}

			public Boolean getSortExpiryDateDown() {
				return sortExpiryDateDown;
			}

			public void setSortExpiryDateDown(Boolean sortExpiryDateDown) {
				this.sortExpiryDateDown = sortExpiryDateDown;
			}

			public Boolean getSortFirstNameDown() {
				return sortFirstNameDown;
			}

			public void setSortFirstNameDown(Boolean sortFirstNameDown) {
				this.sortFirstNameDown = sortFirstNameDown;
			}

			public Boolean getSortLastNameDown() {
				return sortLastNameDown;
			}

			public void setSortLastNameDown(Boolean sortLastNameDown) {
				this.sortLastNameDown = sortLastNameDown;
			}


			public Boolean getSortLoginNameUp() {
				return sortLoginNameUp;
			}


			public void setSortLoginNameUp(Boolean sortLoginNameUp) {
				this.sortLoginNameUp = sortLoginNameUp;
			}


			public Boolean getSortLoginNameDown() {
				return sortLoginNameDown;
			}


			public void setSortLoginNameDown(Boolean sortLoginNameDown) {
				this.sortLoginNameDown = sortLoginNameDown;
			}
			  public String getCustomerListColumnUsernameFilter() {
					return customerListColumnUsernameFilter;
				}

				public void setCustomerListColumnUsernameFilter(
						String customerListColumnUsernameFilter) {
					this.customerListColumnUsernameFilter = customerListColumnUsernameFilter;
				}

				public String getCustomerListColumnFirstnameFilter() {
					return customerListColumnFirstnameFilter;
				}

				public void setCustomerListColumnFirstnameFilter(
						String customerListColumnFirstnameFilter) {
					this.customerListColumnFirstnameFilter = customerListColumnFirstnameFilter;
				}

				public String getCustomerListColumnLastnameFilter() {
					return customerListColumnLastnameFilter;
				}

				public void setCustomerListColumnLastnameFilter(
						String customerListColumnLastnameFilter) {
					this.customerListColumnLastnameFilter = customerListColumnLastnameFilter;
				}

				public String getCurrentUserName() {
					System.out.println("UsersBean getCurrentUserName");
					if (currentUserName!=null)System.out.println("get CurrentUserName: " + currentUserName);
					return currentUserName;
				}

				public void setCurrentUserName(String currentUserName) {
					System.out.println("UsersBean setCurrentUserName");
					if (currentUserName!=null)System.out.println("set CurrentUserName: " + currentUserName);
					this.currentUserName = currentUserName;
				}
	}