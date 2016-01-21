package net.application.web.util;

import java.util.List;
import java.util.Map;

import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.Condition;
import org.picketlink.idm.query.IdentityQueryBuilder;
import org.picketlink.idm.query.Sort;

import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerIdmGroupCombination;
import net.application.customer.util.CustomerIdmUserCombination;
import net.application.web.entity.Mailserver;


public interface UserCustomerDao {
    
	
	public List<CustomerIdmUserCombination> findUsersCustomersBy_offset_pageSize_sort_condition(Map<String, String> conditionSet, String sortString, Integer offset, Integer pageSize);
	public Integer usersCustomersCountBy_condition(Map<String, String>  conditionSet);
}