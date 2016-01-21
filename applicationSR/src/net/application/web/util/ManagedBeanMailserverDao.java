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

import net.application.web.entity.Mailserver;



public class ManagedBeanMailserverDao implements MailserverDao,Serializable{

	@Inject
    private EntityManager entityManager;       
	
	@Inject
    private PartitionManager partitionManager;

    @Inject
    private UserTransaction utx;  
    
    @Inject Logger log;
   		
	public List<Mailserver> getAllMailservers() {                    
		try {
	       	List<Mailserver> mailservers;
	        try {
	        	utx.begin();
	            Query queryAllMailservers = entityManager.createNamedQuery("findAllMailservers");
	            mailservers = queryAllMailservers.getResultList();
	     
	        } catch (NoResultException e) {
	        	mailservers = null;
	        }
	        utx.commit();
	        //if (mailservers!=null)log.info(Integer.toString(mailservers.size()));
	        return mailservers;
		} catch (Exception e) {
			try {
				utx.rollback();
	        } catch (SystemException se) {
	            throw new RuntimeException(se);
	        }
	        throw new RuntimeException(e);
	    }
	}
		
	public Mailserver getByName(String name) {                    
		try {
			Mailserver mailserver;
		    try {
		    	utx.begin();
		        Query query = entityManager.createNamedQuery("findMailserverByName");
		        query.setParameter("name", name);
		        mailserver = (Mailserver) query.getSingleResult();
		    } catch (NoResultException e) {
		       	mailserver = null;
		    }
		    utx.commit();
		    return mailserver;
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (SystemException se) {
				throw new RuntimeException(se);
		    }
			throw new RuntimeException(e);
		}
	}
		
	public Mailserver getByApplication(String application) {                    
		try {
			Mailserver mailserver;
		    try {
		    	utx.begin();
		    	log.info("looking fpr Mailserver Application: " + application);
		        Query query = entityManager.createNamedQuery("findMailserverByApplication");
		        query.setParameter("application", application);
		        mailserver = (Mailserver) query.getSingleResult();
		        log.info(mailserver.getMailSmtpHost());
		    } catch (NoResultException e) {
		    	mailserver = null;
		    }
		    utx.commit();
		    return mailserver;
		} catch (Exception e) {
			try {
				utx.rollback();
		    } catch (SystemException se) {
		    	throw new RuntimeException(se);
		    }
			throw new RuntimeException(e);
		}
	}
	public void deleteByApplication(String application) {                    
		try {
		    try {
		    	utx.begin();
		    	Query query = entityManager.createNamedQuery("deleteMailserverByApplication");
		        query.setParameter("application", application);
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
	public void createMailserver(Mailserver mailserver) {                             
		try {
			utx.begin();
			entityManager.persist(mailserver);
			utx.commit();
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	}
		
}