package net.application.web.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;

import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.Sort;

import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerIdmUserCombination;

public class UsersCustomersModel extends LazyDataModel<CustomerIdmUserCombination> implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private EntityManager entityManager;  
	private UserTransaction utx;
	private UserDao userDao;
	private CustomerDao customerDao;
	private UserCustomerDao userCustomerDao;
	private String sortString;
    private Map<String, String>conditionSet;
    
    @Inject Logger log;
    
    public UsersCustomersModel(Map<String, String>conditionSet, String sortString, EntityManager entityManager, UserTransaction utx, UserDao userDao, CustomerDao customerDao, UserCustomerDao userCustomerDao) {
        this.entityManager = entityManager;
        this.utx = utx;
        this.userDao = userDao;
        this.customerDao = customerDao;
        this.userCustomerDao = userCustomerDao;
        this.sortString = sortString;
        this.conditionSet = conditionSet;
        System.out.println("init UsersCustomersModel");
    }
 
    @Override
   
    public List<CustomerIdmUserCombination> getDataList(int offset, int pageSize) {
    	System.out.println("getDataList offset: "+ offset + " getDataList pageSize: " + pageSize);
    	List<CustomerIdmUserCombination> customerIdmUserCombinations=userCustomerDao.findUsersCustomersBy_offset_pageSize_sort_condition(conditionSet, sortString, offset, pageSize);
	    if (customerIdmUserCombinations!=null)System.out.println(Integer.toString(customerIdmUserCombinations.size()));
	    return customerIdmUserCombinations;	
    }
 
    @Override
    public Object getKey(CustomerIdmUserCombination customerIdmUserCombination) {
        return customerIdmUserCombination.getCustomerId();
    }
 
    @Override
    public int getTotalCount() {
	   	Integer count=userCustomerDao.usersCustomersCountBy_condition(conditionSet);
	    if (count!=null)System.out.println("Count: " + count);
	    return count;
		
    }
}
