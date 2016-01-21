package net.application.web.util;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.picketlink.idm.PartitionManager;

import net.application.customer.entity.Customer;

public class CustomersModel extends LazyDataModel<Customer> implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private EntityManager entityManager;  
	private UserTransaction utx;
	private CustomerDao customerDao;
	
	@Inject
    private PartitionManager partitionManager;

     
    
    @Inject Logger log;
    
    public CustomersModel(String name, EntityManager entityManager, UserTransaction utx, CustomerDao customerDao) {
        this.name = name;
        this.entityManager = entityManager;
        this.utx = utx;
        this.customerDao = customerDao;
    }
 
    @Override
   
    public List<Customer> getDataList(int offset, int pageSize) {
    	System.out.println("getDataList offset: "+ offset + " getDataList pageSize: " + pageSize);
    	List<Customer> customers=customerDao.findAllCustomers();
	    if (customers!=null)System.out.println(Integer.toString(customers.size()));
	    return customers;	
    }
 
    @Override
    public Object getKey(Customer customer) {
        return customer.getCustomerId();
    }
 
    @Override
    public int getTotalCount() {
	   	Integer count=customerDao.customersAllCount();
	    if (count!=null)System.out.println("Count: " + count);
	    return count;
		
    }
}
