package net.application.web.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.picketlink.idm.model.basic.User;

import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.model.basic.User;

@SessionScoped 
@Named
public class loggedInBean  implements Serializable {

    /**
	 * 
	 */
	@Inject private Identity identity;
	private static final long serialVersionUID = 1L;
	private String selectedButtonString;
	private String selectedButtonSubTaskString;
	private String styleButtonNotSelected;
	private Boolean showMessage;
	private String styleButtonSelected;
	private String styleButtonOnmouseoutNotSelected;
	private String styleButtonOnmouseoutSelected;	
	private String styleButton;
	private String styleButtonOnmouseout;
	private String mainPage;
	
	public String getMainPage() {
		return mainPage;
	}

	public void setMainPage(String mainPage) {
		this.mainPage = mainPage;
	}

	@PostConstruct
    public void init() {
    	System.out.println("init");
    	showMessage=true;
    	//if ((User) identity.getAccount()).getLoginName().matches('adminstrator') mainSubPage=
    	mainPage="profile";
    	selectedButtonString="profileButton";
    	selectedButtonSubTaskString="personal";
    	System.out.println("init " + selectedButtonString);
    	String fontFamily="font-family:'Open Sans', arial, serif;";
    	String fontSize="font-size:16px;";
    	String textDecoration="text-decoration:none;";
    	String backGroundimage="background-image:none;";
    	String padding="padding:1px;";
    	String margin="margin:0px;";
    	styleButtonNotSelected=padding + margin + backGroundimage + fontFamily + fontSize +"color:#2d2d2d;background-color:#f2f2f2;";
    	styleButtonSelected=padding + margin + backGroundimage + fontFamily + fontSize + "color:#ffffff;background-color:#639cab;";
    	styleButtonOnmouseoutNotSelected="this.style.color='#2d2d2d';this.style.backgroundColor='#f2f2f2';this.style.border='0px';";
    	styleButtonOnmouseoutSelected=   "this.style.color='#ffffff';this.style.backgroundColor='#639cab';this.style.border='0px';";    	
	}
	
	public void closeMessageBox(){	
		System.out.println("disssssabled");
		showMessage=false;
	}
	public String switchToTask(String switcher){
		selectedButtonString=switcher;
		System.out.println("switch to " + switcher);
		return switcher;
	}
	public String switchToSubTask(String switcher){
		setSelectedButtonSubTaskString(switcher);
		System.out.println("switch to " + switcher);
		return switcher;
	}
	public String getSelectedButtonString() {
		return selectedButtonString;
	}
	public void setSelectedButtonString(String selectedButtonString) {
		this.selectedButtonString = selectedButtonString;
	}
	public String getStyleButtonNotSelected() {
		return styleButtonNotSelected;
	}
	public void setStyleButtonNotSelected(String styleButtonNotSelected) {
		this.styleButtonNotSelected = styleButtonNotSelected;
	}
	public String getStyleButtonSelected() {
		return styleButtonSelected;
	}
	public void setStyleButtonSelected(String styleButtonSelected) {
		this.styleButtonSelected = styleButtonSelected;
	}
	public String getStyleButtonOnmouseoutNotSelected() {
		return styleButtonOnmouseoutNotSelected;
	}
	public void setStyleButtonOnmouseoutNotSelected(String styleButtonOnmouseoutNotSelected) {
		this.styleButtonOnmouseoutNotSelected = styleButtonOnmouseoutNotSelected;
	}
	public String getStyleButtonOnmouseoutSelected() {
		return styleButtonOnmouseoutSelected;
	}
	public void setStyleButtonOnmouseoutSelected(String styleButtonOnmouseoutSelected) {
		this.styleButtonOnmouseoutSelected = styleButtonOnmouseoutSelected;
	}


	public String getStyleButton() {
		return styleButton;
	}


	public void setStyleButton(String styleButton) {
		this.styleButton = styleButton;
	}


	public String getStyleButtonOnmouseout() {
		return styleButtonOnmouseout;
	}


	public void setStyleButtonOnmouseout(String styleButtonOnmouseout) {
		this.styleButtonOnmouseout = styleButtonOnmouseout;
	}

	public String getSelectedButtonSubTaskString() {
		return selectedButtonSubTaskString;
	}

	public void setSelectedButtonSubTaskString(String selectedButtonSubTaskString) {
		this.selectedButtonSubTaskString = selectedButtonSubTaskString;
	}

	public Boolean getShowMessage() {
		return showMessage;
	}

	public void setShowMessage(Boolean showMessage) {
		this.showMessage = showMessage;
	}
	
}
