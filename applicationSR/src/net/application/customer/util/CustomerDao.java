package net.application.customer.util;

import java.util.Collection;
import java.util.List;

import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;


public interface CustomerDao {
    
	Customer getForUsername(String username);
	public List<Customer> getAllCustomers();
    Integer getSizeOfCountries();
    List<Country> getAllCountries();
    void createCustomer(Customer customer);
    public String getPassword();
    public void setPassword(String password);
}