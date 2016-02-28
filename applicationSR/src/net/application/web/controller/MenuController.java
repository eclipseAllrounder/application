package net.application.web.controller;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped
@Named
public class MenuController implements Serializable
{

	   /**
		 * 
		 */
	   private static final long serialVersionUID = 1L;
	   private String mainPage;
	   private String sideBarPage;
	   private String selectedButtonStringMainPage;
	   private String selectedButtonSubTaskStringMainPage;
	   private String selectedButtonStringSideBarPage;
	   private String selectedButtonSubTaskStringSideBarPage;
	   
	   
		
		public String getMainPage() {
			return mainPage;
		}

		public void setMainPage(String mainPage) {
			this.mainPage = mainPage;
		}

		public String getSelectedButtonStringMainPage() {
			return selectedButtonStringMainPage;
		}

		public void setSelectedButtonStringMainPage(String selectedButtonStringMainPage) {
			this.selectedButtonStringMainPage = selectedButtonStringMainPage;
		}

		public String getSelectedButtonSubTaskStringMainPage() {
			return selectedButtonSubTaskStringMainPage;
		}

		public void setSelectedButtonSubTaskStringMainPage(String selectedButtonSubTaskStringMainPage) {
			this.selectedButtonSubTaskStringMainPage = selectedButtonSubTaskStringMainPage;
		}

		public String getSelectedButtonStringSideBarPage() {
			return selectedButtonStringSideBarPage;
		}

		public void setSelectedButtonStringSideBarPage(String selectedButtonStringSideBarPage) {
			this.selectedButtonStringSideBarPage = selectedButtonStringSideBarPage;
		}

		public String getSelectedButtonSubTaskStringSideBarPage() {
			return selectedButtonSubTaskStringSideBarPage;
		}

		public void setSelectedButtonSubTaskStringSideBarPage(String selectedButtonSubTaskStringSideBarPage) {
			this.selectedButtonSubTaskStringSideBarPage = selectedButtonSubTaskStringSideBarPage;
		}

		public String getSideBarPage() {
			return sideBarPage;
		}

		public void setSideBarPage(String sideBarPage) {
			this.sideBarPage = sideBarPage;
		}
	   
	   

}
