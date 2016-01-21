package net.application.web.util;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * Session Bean implementation class SkinBean
 */
@ManagedBean
@ApplicationScoped
@Named
public class SkinBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Default constructor. 
     */
  
   private String skin;
   @Inject Logger log;
   
   
    public SkinBean() {
        // TODO Auto-generated constructor stub
    }



    public String getSkin() {
    	 log.info("get skin" + skin);
        return skin;

    }

    public void setSkin(String skin) {

        this.skin = skin;
        log.info("set skin" + skin);

    }
    @PostConstruct
	public void defaultSkin(){
    	skin="applicationSkin";
    }

}
