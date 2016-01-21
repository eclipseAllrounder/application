package net.application.web.util;


import java.util.List;

import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;
import net.application.web.entity.Content;
import net.application.web.entity.Skins;


public interface CustomerDao {
	List<Customer> findCustomers(String name, int offset, int pageSize);
	List<Customer> findAllCustomers();
	int customersCount(String name);
	int customersAllCount();
	Customer getCustomerByVerifyId(String id);
	Customer getCustomerByMail(String email);
	void deleteCustomerByUserName(String userName);
}