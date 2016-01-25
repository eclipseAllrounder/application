package net.application.web.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SessionScoped 
@Named
public class loggedInBean  implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String selectedButtonString;
	private String selectedButtonSubTaskString;
	private String styleButtonNotSelected;
	private String styleButtonSelected;
	private String styleButtonSubTaskNotSelected;
	private String styleButtonSubTaskSelected;
	private String styleButtonOnmouseoutNotSelected;
	private String styleButtonOnmouseoutSelected;
	
	private String styleButton;
	private String styleButtonOnmouseout;
	
	@PostConstruct
    public void init() {
    	System.out.println("init");
    	selectedButtonString="profileButton";
    	selectedButtonSubTaskString="address";
    	System.out.println("init " + selectedButtonString);
    	String fontFamily="font-family:'Droid Sans', arial, serif;";
    	String fontSize="font-size:16px;";
    	String textDecoration="text-decoration:none;";
    	String backGroundimage="background-image:none;";
    	String padding="padding:1px;";
    	String margin="margin:0px;";
    	styleButtonNotSelected=padding + margin + backGroundimage + fontFamily + fontSize + textDecoration +"color:#2d2d2d;background-color:#f2f2f2;";
    	styleButtonSelected=padding + margin + backGroundimage + fontFamily + fontSize + textDecoration + "color:#ffffff;background-color:#639cab;";
    	styleButtonOnmouseoutNotSelected="this.style.color='#2d2d2d';this.style.backgroundColor='#f2f2f2';this.style.border='0px';";
    	styleButtonOnmouseoutSelected=   "this.style.color='#ffffff';this.style.backgroundColor='#639cab';this.style.border='0px';";    	
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
	
}
