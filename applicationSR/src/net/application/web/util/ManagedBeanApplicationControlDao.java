package net.application.web.util;


import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.picketlink.idm.PartitionManager;

import net.application.web.entity.ApplicationControl;
import net.application.web.entity.Mailserver;



public class ManagedBeanApplicationControlDao implements ApplicationControlDao,Serializable{

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
    
    @Inject Logger log;
   		
	public List<ApplicationControl> getAllApplicationControls() {
		try {
	       	List<ApplicationControl> applicationControls;
	        try {
	        	utx.begin();
	            Query query = entityManager.createNamedQuery("findAllApplicationControls");
	            applicationControls = query.getResultList();
	     
	        } catch (NoResultException e) {
	        	applicationControls = null;
	        }
	        utx.commit();
	        if (applicationControls!=null)log.info(Integer.toString(applicationControls.size()));
	        return applicationControls;
		} catch (Exception e) {
			try {
				utx.rollback();
	        } catch (SystemException se) {
	            throw new RuntimeException(se);
	        }
	        throw new RuntimeException(e);
	    }
	}
	public void deleteAllApplicationControls() {
		try {
		    try {
		    	utx.begin();
		    	Query query = entityManager.createNamedQuery("deleteAllApplicationControls");
		        query.executeUpdate();
		      
		    } catch (NoResultException e) {
		    	log.info(e.toString());
		    }
		    utx.commit();
		} catch (Exception e) {
			try {
				utx.rollback();
		    } catch (SystemException se) {
		    	throw new RuntimeException(se);
		    }
			throw new RuntimeException(e);
		}
	}	
	public void createApplicationControl(ApplicationControl applicationControl) {
		try {
			utx.begin();
			entityManager.persist(applicationControl);
			utx.commit();
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	}
		
}