package net.application.web.util;



import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.application.customer.entity.Country;
import net.application.web.entity.Skins;



@Singleton
@Startup
public class SkinInitializer {
	
	@Inject
    private EntityManager entityManager;  

	@Inject
	transient Logger log;

	 @PostConstruct
	 public void createDefaultSet() {
	 if (getSizeOfSkins()==0){ 
	    	log.info("init ...");
	    	Skins skins=new Skins();
	    	skins.setName("applicationSkin");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("ruby");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("classic");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("wine");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("blueSky");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("deepMarine");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("japanCherry");
        	entityManager.persist(skins);
        	skins=new Skins();
	    	skins.setName("emeraldTown");
        	entityManager.persist(skins);
        	
	 	}
	   
	 }
	 
	   public Long getSizeOfSkins() {
		   Query query = entityManager.createQuery("select COUNT(c.name) from Skins c");
		   Long countResult=(Long) query.getSingleResult();
           System.out.println("Count result:"+countResult);
           return countResult;
	   }
}
