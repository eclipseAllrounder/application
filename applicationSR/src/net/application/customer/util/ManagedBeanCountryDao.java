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


public class ManagedBeanCountryDao implements CountryDao,Serializable {

	@Inject
    private EntityManager entityManager;       
	
    @Inject
    private UserTransaction utx;  
    
       
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

    public Country getForCountryName(String name) {                    
        try {
        	Country country;
            Query query = entityManager.createQuery("select u from Country u where u.name_de = :name or u.name_en = :name");
            query.setParameter("name", name);
            country = (Country) query.getSingleResult();
            return country;
        } catch (Exception e) {
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