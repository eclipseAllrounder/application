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

import net.application.web.entity.Content;



public class ManagedBeanContentDao implements ContentDao,Serializable{

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
   		
	public List<Content> getAllContents() {                    
		//try {
	       	List<Content> contents;
	        try {
	        	//utx.begin();
	            Query queryAllContents = entityManager.createNamedQuery("findAllContents");
	            contents = queryAllContents.getResultList();
	            if (contents!=null) log.info("size of contents: " + contents.size());
	     
	        } catch (NoResultException e) {
	        	log.info("getAllContentsexception utx.begin" + e.getMessage());
	        	contents = null;
	        }
	       // utx.commit();
	        //if (Contents!=null)log.info(Integer.toString(Contents.size()));
	        return contents;
	//	} catch (Exception e) {
			//try {
				//utx.rollback();
	      //  } catch (SystemException se) {
	      //  	log.info("getAllContents exception utx.rollback" + se.getMessage());
	      //      throw new RuntimeException(se);
	            
	      //  }
		//	log.info("getAllContents exception" + e.getMessage());
	   //     throw new RuntimeException(e);
	        
	   // }
	}
	
	public List<Content> getAllContentsByWebsite() {                    
		try {
	       	List<Content> contents;
	        try {
	        	utx.begin();
	            Query queryAllContents = entityManager.createNamedQuery("findContentByWebsite");
	            contents = queryAllContents.getResultList();
	     
	        } catch (NoResultException e) {
	        	contents = null;
	        }
	        utx.commit();
	        //if (Contents!=null)log.info(Integer.toString(Contents.size()));
	        return contents;
		} catch (Exception e) {
			try {
				utx.rollback();
	        } catch (SystemException se) {
	            throw new RuntimeException(se);
	        }
	        throw new RuntimeException(e);
	    }
	}
	public Content getByName(String name) {                    
		try {
			Content Content;
		    try {
		    	utx.begin();
		        Query query = entityManager.createNamedQuery("findContentByName");
		        query.setParameter("name", name);
		        Content = (Content) query.getSingleResult();
		    } catch (NoResultException e) {
		       	Content = null;
		    }
		    utx.commit();
		    return Content;
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (SystemException se) {
				throw new RuntimeException(se);
		    }
			throw new RuntimeException(e);
		}
	}
		

	public void deleteByName(String name) {                    
		try {
		    try {
		    	utx.begin();
		    	Query query = entityManager.createNamedQuery("deleteContentByName");
		        query.setParameter("name", name);
		        query.executeUpdate();
		        log.info("deleteByName: " + name);
		      
		    } catch (NoResultException e) {
		    	log.info(e.toString());
		    }
		    utx.commit();
		    log.info("commit: " + name);
		} catch (Exception e) {
			log.info("deleteByName exception" + e.getMessage());
			try {
				utx.rollback();
		    } catch (SystemException se) {
		    	log.info("deleteByName exception utx.rollback" + se.getMessage());
		    	throw new RuntimeException(se);
		    }
			throw new RuntimeException(e);
		}
	}	
	public void createContent(Content content) {                             
		try {
			utx.begin();
			if (content.getContentId() != null) {
				entityManager.merge(content);
			} else {
				entityManager.persist(content);
			}
			entityManager.flush();
			log.info("createContent: " + content.getName());
			utx.commit();
			log.info("commit: " + content.getName());
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}
	}
		
}