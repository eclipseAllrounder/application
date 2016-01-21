package net.application.web.util;


import java.util.List;

import net.application.customer.entity.Country;
import net.application.web.entity.Skins;


public interface CountryDao {
    Integer getSizeOfCountries();
    List<Country> getAllCountries();
}