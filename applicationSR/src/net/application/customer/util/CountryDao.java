package net.application.customer.util;

import java.util.Collection;
import java.util.List;

import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;


public interface CountryDao {
    
	Country getForCountryName(String name);
    Integer getSizeOfCountries();
    List<Country> getAllCountries();
 }