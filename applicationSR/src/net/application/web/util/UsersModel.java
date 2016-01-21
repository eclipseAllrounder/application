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

public class UsersModel extends LazyDataModel<User> implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private EntityManager entityManager;  
	private UserTransaction utx;
	private UserDao userDao;
	private String sortString;
    private Map<String, String>conditionSet;
    
    @Inject Logger log;
    
    public UsersModel(Map<String, String>conditionSet, String sortString, EntityManager entityManager, UserTransaction utx, UserDao userDao) {
        this.entityManager = entityManager;
        this.utx = utx;
        this.userDao = userDao;
        this.sortString = sortString;
        this.conditionSet = conditionSet;
        System.out.println("initUsersModel");
    }
 
    @Override
   
    public List<User> getDataList(int offset, int pageSize) {
    	System.out.println("getDataList offset: "+ offset + " getDataList pageSize: " + pageSize);
    	List<User> users=userDao.findUsersBy_offset_pageSize_sort_condition(conditionSet, sortString, offset, pageSize);
	    if (users!=null)System.out.println(Integer.toString(users.size()));
	    return users;	
    }
 
    @Override
    public Object getKey(User user) {
        return user.getId();
    }
 
    @Override
    public int getTotalCount() {
	   	Integer count=userDao.usersCountBy_condition(conditionSet);
	    if (count!=null)System.out.println("Count: " + count);
	    return count;
		
    }
}
