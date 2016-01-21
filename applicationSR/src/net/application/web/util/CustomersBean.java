package net.application.web.util;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.picketlink.idm.PartitionManager;

@Named
@SessionScoped
public class CustomersBean implements Serializable {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private CustomersModel customersModel;
    

	@Inject
    private EntityManager entityManager;       

	@Inject
    private UserTransaction utx; 
	
	@Inject
	private CustomerDao customerDao;
    
    private String filterName = "";
    
    
    private EntityManager lookupEntityManager() {
    	// obtain the initial JNDI context
        Context initCtx = null;
		try {
			initCtx = new InitialContext();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // perform JNDI lookup to obtain container-managed entity manager
        javax.persistence.EntityManager entityManager = null;
		try {
			entityManager = (EntityManager)initCtx.lookup("applicationSR.war#primary");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return entityManager;
    }
    public TransactionManager getTransactionManager() throws Exception {

        return (TransactionManager) new InitialContext().lookup("java:jboss/TransactionManager");

     }
    
    public String getFilterName() {
        return filterName;
    }
 
    public void setFilterName(String filterName) {
        boolean changed = !this.filterName.equals(filterName);
        this.filterName = filterName;
        if (changed){
            initCustomersModel();
        }
    }
 
    public CustomersBean() {
        initCustomersModel();
    }
 
    public CustomersModel getCustomers() {
    	customersModel = new CustomersModel(filterName, entityManager, utx, customerDao);
        return customersModel;
    }
 
    private void initCustomersModel() {
    	customersModel = new CustomersModel(filterName, entityManager, utx, customerDao);
    }
}