package net.application.customer.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;


public class ManagedBeanUserDao implements CustomerDao,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
    private EntityManager entityManager;       
	
	@Inject
    private PartitionManager partitionManager;

    @Inject
    private UserTransaction utx;  
    
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	private String password;
    
    public Integer getSizeOfCountries() {
    	try {
        	Integer countResult;
            try {
                utx.begin();
                Query query = entityManager.createQuery("select COUNT(c.name_de) from Country c");
                countResult=(Integer) query.getSingleResult();
                System.out.println("Count result:"+countResult);
            } catch (NoResultException e) {
            	countResult = null;
            }
            utx.commit();
            return countResult;
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException se) {
                throw new RuntimeException(se);
            }
            throw new RuntimeException(e);
        }
    	
    }

    public Customer getForUsername(String username) {                    
        try {
        	Customer customer;
            try {
                utx.begin();
                Query query = entityManager.createQuery("select u from User u where u.username = :username");
                query.setParameter("username", username);
                customer = (Customer) query.getSingleResult();
            } catch (NoResultException e) {
            	customer = null;
            }
            utx.commit();
            return customer;
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException se) {
                throw new RuntimeException(se);
            }
            throw new RuntimeException(e);
        }
    }

    public void createCustomer(Customer customer) {                             
        try {
            
                entityManager.persist(customer);
                
        } catch (Exception e) {
           
            throw new RuntimeException(e);
        }
    }
    public List<Customer> getAllCustomers() {                    
        try {
        	List<Customer> customers;
            try {
                utx.begin();
                Query queryAllCustomers = entityManager.createNamedQuery("findAllCustomers");
                customers = queryAllCustomers.getResultList();
     
            } catch (NoResultException e) {
            	customers = null;
            }
            utx.commit();
            return customers;
        } catch (Exception e) {
            try {
                utx.rollback();
            } catch (SystemException se) {
                throw new RuntimeException(se);
            }
            throw new RuntimeException(e);
        }
    }


		
		public List<Country> getAllCountries() {                    
	        try {
	        	List<Country> contries;
	            try {
	                utx.begin();
	                Query queryAllCountries = entityManager.createNamedQuery("findAllCountries");
	        		contries = queryAllCountries.getResultList();
	     
	            } catch (NoResultException e) {
	            	contries = null;
	            }
	            utx.commit();
	            return contries;
	        } catch (Exception e) {
	            try {
	                utx.rollback();
	            } catch (SystemException se) {
	                throw new RuntimeException(se);
	            }
	            throw new RuntimeException(e);
	        }
	    }

	
	
}