package net.application.util;

import static org.picketlink.idm.model.basic.BasicModel.addToGroup;
import static org.picketlink.idm.model.basic.BasicModel.grantRole;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;
import javax.faces.flow.Flow;
import javax.faces.flow.FlowScoped;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.apache.commons.io.IOUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;
import org.picketlink.idm.query.IdentityQuery;

import net.application.authorization.util.CreateUser;
import net.application.customer.entity.Country;
import net.application.customer.entity.Customer;
import net.application.customer.entity.PasswordTable;
import net.application.customer.util.CountryDao;
import net.application.customer.util.CustomerDao;
import net.application.customer.util.PasswordTableDao;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
 




import javax.faces.bean.ManagedBean;
 




import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.UploadedFile;

//@ManagedBean
@Named
//@FlowScoped("verification")
@SessionScoped
public class ImageUploadBean implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject Logger log;

    private String CKEditorFuncNum;
    private String CKEditor;
    private String langCode;
	

	
	public Boolean validateId(String id){
		return true;
	}
	public String redirect(){
		log.info("redirect, result is: " + CKEditorFuncNum + " " + CKEditor + " " +  langCode);
        //if (validateId(id)) return "/homeCdi.jsf?faces-redirect=true";
       // return "/error.jsf?faces-redirect=true";
		ExternalContext ec=FacesContext.getCurrentInstance().getExternalContext();
		log.info(ec.getRequestContextPath());
		
		try {
			ec.redirect(ec.getRequestContextPath() + "/homeCdi.jsf?faces-redirect=true");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		  return "/error.jsf?faces-redirect=true";
        
	}
	
	

	public String getCKEditorFuncNum() {
		return CKEditorFuncNum;
	}

	public void setCKEditorFuncNum(String cKEditorFuncNum) {
		CKEditorFuncNum = cKEditorFuncNum;
		//redirect();
	}

	public String getCKEditor() {
		return CKEditor;
	}

	public void setCKEditor(String cKEditor) {
		CKEditor = cKEditor;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	   private ArrayList<UploadedImage> files = new ArrayList<UploadedImage>();
	   
	    public void paint(OutputStream stream, Object object) throws IOException {
	        stream.write(getFiles().get((Integer) object).getData());
	        stream.close();
	    }
	 
	    public void listener(FileUploadEvent event) throws Exception {
	        UploadedFile item = event.getUploadedFile();
	        UploadedImage file = new UploadedImage();
	        file.setLength(item.getData().length);
	        file.setName(item.getName());
	        file.setData(item.getData());
	        files.add(file);
	    }
	 
	    public String clearUploadData() {
	        files.clear();
	        return null;
	    }
	 
	    public int getSize() {
	        if (getFiles().size() > 0) {
	            return getFiles().size();
	        } else {
	            return 0;
	        }
	    }
	 
	    public long getTimeStamp() {
	        return System.currentTimeMillis();
	    }
	 
	    public ArrayList<UploadedImage> getFiles() {
	        return files;
	    }
	 
	    public void setFiles(ArrayList<UploadedImage> files) {
	        this.files = files;
	    }
}
