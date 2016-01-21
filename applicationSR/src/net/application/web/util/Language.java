package net.application.web.util;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
 
@ManagedBean
@SessionScoped
public class Language implements Serializable{
 
	private static final long serialVersionUID = 1L;
 
	@Inject
    private FacesContext facesContext;
	
	private String localeCode;
	private Locale locale;
 
	
	public String getLocaleCode() {
		return localeCode;
	}
 
 	public void setLocaleCode(String localeCode) {
		this.localeCode = localeCode;
	}
 	
 	public void changeToEnglish(){
 		localeCode="en";
 		locale = new Locale(localeCode);
 		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
 	}
 	public void changeToGerman(){
 		localeCode="de";
 		locale = new Locale(localeCode);
 		FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
 	}
 	@PostConstruct
 	public void readLanguageFromFacesContext(){
 		locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
 		localeCode=locale.getLanguage();
 	}

	public Locale getLocale() {
	
	    return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		//System.out.println("####### setLocal getLanguage: " + locale.getLanguage());
		
		
	}
 
}
