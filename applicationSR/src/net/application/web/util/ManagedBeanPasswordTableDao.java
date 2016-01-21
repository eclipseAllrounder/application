package net.application.web.util;

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
import net.application.customer.entity.PasswordTable;
import net.application.customer.util.PasswordTableDao;
import net.application.web.entity.Skins;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;
import org.richfaces.skin.Skin;


public class ManagedBeanPasswordTableDao implements PasswordTableDao,Serializable {

	@Inject
    private EntityManager entityManager;       
	
	@Inject
    private PartitionManager partitionManager;

    @Inject
    private UserTransaction utx;  
    
       
    public Integer getSizeOfPasswords() {
    	try {
        	Integer countResult;
            try {
                utx.begin();
                Query query = entityManager.createQuery("select COUNT(*) from PasswordTable c");
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

  		
		public List<PasswordTable> getAllPasswords() {                    
	        try {
	        	List<PasswordTable> passwords;
	            try {
	                utx.begin();
	                Query queryAllPasswords = entityManager.createNamedQuery("getAllPasswords");
	                passwords = queryAllPasswords.getResultList();
	     
	            } catch (NoResultException e) {
	            	passwords = null;
	            }
	            utx.commit();
	            return passwords;
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
