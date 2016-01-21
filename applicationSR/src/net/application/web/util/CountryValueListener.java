package net.application.web.util;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import net.application.web.controller.IdmUtilCreateController;
 
public class CountryValueListener implements ValueChangeListener{
 
	@Override
	public void processValueChange(ValueChangeEvent event)
			throws AbortProcessingException {
 
		//access country bean directly
		IdmUtilCreateController idm = (IdmUtilCreateController) FacesContext.getCurrentInstance().
			getExternalContext().getSessionMap().get("wantRootLevelId:0");
 
		idm.setWantRootLevel(event.getNewValue().toString());
 
	}
 
}
