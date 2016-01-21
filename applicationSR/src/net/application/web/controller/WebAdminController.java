package net.application.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.SystemException;

import net.application.authorization.util.SendMailTLS;
import net.application.customer.entity.Customer;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.CustomerIdmRolesCombination;
import net.application.customer.util.PasswordTableDao;
import net.application.web.entity.ApplicationControl;
import net.application.web.entity.Content;
import net.application.web.entity.Mailserver;
import net.application.web.entity.Skins;
import net.application.web.util.ApplicationControlDao;
import net.application.web.util.ContentDao;
import net.application.web.util.MailserverDao;
import net.application.web.util.ManagedBeanMailserverDao;
import net.application.web.util.SkinBean;
import net.application.web.util.SkinDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;




@Named                                                         
@SessionScoped                                                    
public class WebAdminController implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject private FacesContext facesContext;
    @Inject Logger log;
    @Inject private SkinDao skinDao;
    @Inject private SkinBean skinBean;
    @Inject private MailserverDao mailserverDao;
    @Inject private ContentDao contentDao;
    @Inject private WebContentController webContentController;
    @Inject private ApplicationControlDao applicationControlDao;
    
    private List<Mailserver> allMailservers;
    private List<Content> allContents;
    private String currentMailApplication;
    private String currentContent;
    private String loadedContent;
    private String sideBar;
    private String contentText;
    private Boolean contentLoaded;
    


	@Named                                                           
    @Produces
    private Mailserver newMailserver;
    
    @Named                                                           
    @Produces
    private Content newContent;
    
    @Named                                                           
    @Produces
    private ApplicationControl newApplicationControl;
    
    @PostConstruct
	public void init() {
	   log.info("init");
	   newMailserver = new Mailserver();
	   newContent = new Content();
	   newApplicationControl = new ApplicationControl();
	   List<ApplicationControl> result = applicationControlDao.getAllApplicationControls();
	   if(result.size()>0)newApplicationControl=result.get(0);
	   allMailservers= null;
	   contentText=null;
	   allContents= null;
	   //Die neuen Inhalte werden aktiviert
	   webContentController.init();
	   setCurrentMailApplication(null);
	   setCurrentContent(null);
	   contentLoaded=false;
	   sideBar="skinControl";
	   
	}
    public void changeSidebarString(String value) {  
    	setSideBar(value);
    	log.info("changeSidebarString: " + value);
    }
    public void createMailserver() {
    	try {
            //persist Customer 
    		newMailserver.setDob(new Date());
    		mailserverDao.createMailserver(newMailserver);
    		log.info("Mailserver created");
    		init();
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during mailserver creation" + e);
    	}
    }
    public void deleteMailserver() {
    	try {            
    		mailserverDao.deleteByApplication(currentMailApplication);
    		log.info("Mailserver deleted for application " + currentMailApplication);
    		init();
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during mailserver deleting" + e);
    	}
    }
    
    public void createContent() {
    	try {
            //persist Customer 
    		newContent.setDob(new Date());
    		newContent.setText(loadAsBlob());
    		log.info(contentLoaded + currentContent + newContent.getName());
    		//if content loaded from table
    		if (contentLoaded){
    			//name was not changed
    			if(currentContent.matches(newContent.getName())){
    				contentDao.deleteByName(currentContent);
    				contentDao.createContent(newContent);
    			} else {
    			//name was changed
    				//bulid new content
    				Content contentTemp = new Content();
    				contentTemp.setActive(newContent.getActive());
    				contentTemp.setDob(newContent.getDob());
    				contentTemp.setName(newContent.getName());
    				contentTemp.setPosition(newContent.getPosition());
    				contentTemp.setText(newContent.getText());
    				contentTemp.setWebsite(newContent.getWebsite());
    				contentDao.createContent(contentTemp);
    			}
    		} else {
    			contentDao.createContent(newContent);
    		}
    		log.info("Content created");
    		init();
    		allContents=contentDao.getAllContents();
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during content creation" + e);
    	}
    }
    public byte[] loadAsBlob() {
   
        	byte[] b = contentText.getBytes(StandardCharsets.UTF_8); // Java 7+ only
            return b;
    }

    public void deleteContent() {
    	try {            
    		contentDao.deleteByName(currentContent);
    		log.info("Content deleted " + currentContent);
    		init();
    		allContents=contentDao.getAllContents();
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during content deleting" + e);
    	}
    }
    public String getContentTextByName(String name) {
    	try {            
    		Content content=contentDao.getByName(name);
    		log.info("Content loaded " + name);
    		init();
    		return new String(content.getText(), "UTF-8");
    		
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during content deleting" + e);
    		return null;
    	}
    }
    public void loadNewContent() {
    	try {            
    		newContent=contentDao.getByName(currentContent);
    		contentText=webContentController.getContentTextByName(currentContent);
    		contentLoaded=true;
    		log.info("Content loaded " + currentContent);
    		//log.info("Content Text " + contentText);
    		//init();
    		    		
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during content deleting" + e);
    	
    	}
    }
    public void createApplicationControl() {
    	try {
            //persist Customer 
    		applicationControlDao.deleteAllApplicationControls();
    		newApplicationControl.setDolm(new Date());
    		applicationControlDao.createApplicationControl(newApplicationControl);
    		log.info("newApplicationControl created");
    		init();
    	} catch (Exception e) {
    		String message = "An error has occured while creating" +
                     " the user (see log for details)";
    		facesContext.addMessage(null, new FacesMessage(message));
    		log.info("Problem during ApplicationControl creation" + e);
    	}
    }
    
    public SkinBean getSkinBean() {
		return skinBean;
	}
	public void setSkinBean(SkinBean skinBean) {
		this.skinBean = skinBean;
	}

	private String skin;
    private String selectedSkin;
   
       
    @Named                                                           
    @Produces
    public List<SelectItem> getAllSkins(){
    	   List<SelectItem> items = new ArrayList<SelectItem>();
    	   List<Skins> skinList = skinDao.getAllSkins();
    	    for(Skins skin: skinList){
    	       System.out.println(FacesContext.getCurrentInstance().getViewRoot().getLocale());
    	       
    	       items.add(new SelectItem(skin.getName(), skin.getName()));
    	   }
    	   return items;
    }
    public void create(){
    	  log.info("Selected Skin: " + selectedSkin);
    	  skinBean.setSkin(selectedSkin);	
    }
    public void setItemChangeListener(AjaxBehaviorEvent abe)
    {
    String itemId =(String) abe.getComponent().getId();

     

    System.out.println("Selected Item"+itemId);

     

    }
    public void valueChanged(ValueChangeEvent event) {
       
        if (null != event.getNewValue()) {
        }
    }
	public String getSkin() {
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

	public String getSelectedSkin() {
		 System.out.println("get Selected Item: "+selectedSkin);
		return selectedSkin;
	}

	public void setSelectedSkin(String selectedSkin) {
		this.selectedSkin = selectedSkin;
		 System.out.println("set Selected Item: "+selectedSkin);
	}

	public List<Mailserver> getAllMailservers() {
		if(allMailservers==null)allMailservers=mailserverDao.getAllMailservers();
		return allMailservers;
	}

	public void setAllMailservers(List<Mailserver> allMailservers) {
		this.allMailservers = allMailservers;
	}

	public String getCurrentMailApplication() {
		return currentMailApplication;
	}

	public void setCurrentMailApplication(String currentMailApplication) {
		this.currentMailApplication = currentMailApplication;
	}

	public List<Content> getAllContents() {
		if(allContents==null)allContents=contentDao.getAllContents();
		return allContents;
	}

	public void setAllContents(List<Content> allContents) {
		this.allContents = allContents;
	}
    public String getCurrentContent() {
		return currentContent;
	}

	public void setCurrentContent(String currentContent) {
		this.currentContent = currentContent;
	}

	public String getSideBar() {
		return sideBar;
	}

	public void setSideBar(String sideBar) {
		this.sideBar = sideBar;
	}
	public String getContentText() {
		return contentText;
	}
	public void setContentText(String contentText) {
		this.contentText = contentText;
	}
	public Boolean getContentLoaded() {
		return contentLoaded;
	}
	public void setContentLoaded(Boolean contentLoaded) {
		this.contentLoaded = contentLoaded;
	}
   
}