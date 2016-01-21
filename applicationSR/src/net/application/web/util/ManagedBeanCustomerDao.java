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

import net.application.customer.entity.Customer;
import net.application.web.entity.ApplicationControl;
import net.application.web.entity.Content;
import net.application.web.entity.Mailserver;



public class ManagedBeanCustomerDao implements CustomerDao,Serializable{

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
   		
    public List<Customer> findCustomers(String name, int offset, int pageSize) {
		try {
	       	List<Customer> customers;
	        try {
	        	utx.begin();
	        	Query query = entityManager.createNamedQuery("findAllCustomers");
		        //query.setParameter("userName", name);
		        customers = query.getResultList();
	     
	        } catch (NoResultException e) {
	        	customers = null;
	        }
	        utx.commit();
	        if (customers!=null)log.info(Integer.toString(customers.size()));
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
    public Customer getCustomerByMail(String mail) {                    
		try {
			Customer customer;
		    try {
		    	utx.begin();
		        Query query = entityManager.createNamedQuery("findCustomerByMail");
		        query.setParameter("email", mail);
		        customer= (Customer) query.getSingleResult();
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
    public Customer getCustomerByUserName(String userName) {                    
		try {
			Customer customer;
		    try {
		    	utx.begin();
		        Query query = entityManager.createNamedQuery("findUserByName");
		        query.setParameter("userName", userName);
		        customer= (Customer) query.getSingleResult();
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
    public Customer getCustomerByVerifyId(String id) {                    
		try {
			Customer customer;
		    try {
		    	utx.begin();
		        Query query = entityManager.createNamedQuery("findCustomerByVerifyId");
		        query.setParameter("accountActivationString", id);
		        customer= (Customer) query.getSingleResult();
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
    public List<Customer> findAllCustomers() {
		try {
	       	List<Customer> customers;
	        try {
	        	utx.begin();
	        	Query query = entityManager.createNamedQuery("findAllCustomers");
		        //query.setParameter("userName", name);
		        customers = query.getResultList();
	     
	        } catch (NoResultException e) {
	        	customers = null;
	        }
	        utx.commit();
	        if (customers!=null)log.info(Integer.toString(customers.size()));
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
    public int customersCount(String name) {
		try {
	       	Integer count;
	        try {
	        	utx.begin();
	        	Query query = entityManager.createNamedQuery("findAllCustomers");
		        //query.setParameter("userName", name);
	        	count = query.getMaxResults();
	     
	        } catch (NoResultException e) {
	        	count = null;
	        }
	        utx.commit();
	        if (count!=null)log.info(count.toString());
	        return count;
		} catch (Exception e) {
			try {
				utx.rollback();
	        } catch (SystemException se) {
	            throw new RuntimeException(se);
	        }
	        throw new RuntimeException(e);
	    }
	}
    public int customersAllCount() {
		try {
	       	Integer count;
	        try {
	        	utx.begin();
	        	Query query = entityManager.createNamedQuery("findAllCustomersCount");
		        //query.setParameter("userName", name);
	        	count = query.getFirstResult();
	     
	        } catch (NoResultException e) {
	        	count = null;
	        }
	        utx.commit();
	        if (count!=null)log.info(count.toString());
	        return count;
		} catch (Exception e) {
			try {
				utx.rollback();
	        } catch (SystemException se) {
	            throw new RuntimeException(se);
	        }
	        throw new RuntimeException(e);
	    }
	}
    public void deleteCustomerByUserName(String userName) {                    
		try {
		    try {
		    	utx.begin();
		    	Query query = entityManager.createNamedQuery("deleteCustomerByuserName");
		        query.setParameter("userName", userName);
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
}