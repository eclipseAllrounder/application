package net.application.customer.util;



import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.application.customer.entity.Country;



@Singleton
@Startup
public class CountryInitializer {
	
	@Inject
    private EntityManager entityManager;  

	@Inject
	transient Logger log;

	 @PostConstruct
	 public void createDefaultSet() {
	 if (getSizeOfCountries()==0){ 
	    	log.info("init ...");
	    	Country country=new Country();
        	country.setName_de("Deutschland");
        	country.setName_en("germany");
        	country.setContinent_de("Europa");
        	country.setContinent_en("europe");
            entityManager.persist(country);
            log.info("fisrt saved");
            country=new Country();
        	country.setName_de("Schweiz");
        	country.setName_en("swiss");
        	country.setContinent_de("Europa");
        	country.setContinent_en("europe");
            entityManager.persist(country);
            
            country=new Country();
        	country.setName_de("Österreich");
        	country.setName_en("austria");
        	country.setContinent_de("Europa");
        	country.setContinent_en("europe");
            entityManager.persist(country);
	 	}
	   
	 }
	 
	   public Long getSizeOfCountries() {
		   Query query = entityManager.createQuery("select COUNT(c.name_de) from Country c");
		   Long countResult=(Long) query.getSingleResult();
           System.out.println("Count result:"+countResult);
           
           return countResult;
	    	
	    }
}
