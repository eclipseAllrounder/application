package net.application.web.util;


import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
 
@Named
public class CaptchaAction implements Serializable {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CaptchaAction() {
    }
    String validate;
 
    public String getValidate() {
        return validate;
    }
 
    public void setValidate(String validate) {
        this.validate = validate;
    }
 
    public String execute() throws Exception {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Boolean isResponseCorrect = Boolean.FALSE;
        javax.servlet.http.HttpSession session = request.getSession();
        String parm = validate;
        String c = (String) session.getAttribute(MyCaptcha.CAPTCHA_KEY);
        if (parm.equals(c)) {
            return "verification-end";
        } else {
            return "verification-notEnd";
        }
    }
}
