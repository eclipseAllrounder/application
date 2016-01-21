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
import net.application.web.entity.Skins;

import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;
import org.richfaces.skin.Skin;


public class ManagedBeanSkinDao implements SkinDao,Serializable {

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
    
    public Integer getSizeOfSkins() {
    	try {
        	Integer countResult;
            try {
                utx.begin();
                Query query = entityManager.createQuery("select COUNT(c.name) from Skin c");
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

  		
		public List<Skins> getAllSkins() {                    
	        try {
	        	List<Skins> skins;
	            try {
	                utx.begin();
	                Query queryAllSkins = entityManager.createNamedQuery("findAllSkins");
	                skins = queryAllSkins.getResultList();
	     
	            } catch (NoResultException e) {
	            	skins = null;
	            }
	            utx.commit();
	            return skins;
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