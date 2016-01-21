package net.application.web.entity;



import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;


@Entity
@NamedQueries({
	  @NamedQuery(name="findAllApplicationControls", query="SELECT o FROM ApplicationControl o"),
	  @NamedQuery(name="deleteAllApplicationControls", query="DELETE FROM ApplicationControl o")
})


public class ApplicationControl implements Serializable
{

	/**
	 *
	 * @author Christian Lewer
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer applicationControlId;
	private Boolean verificationViaMail;
	private Integer AccountExpireDays;
	private Date dolm;
	
	

	@Id @GeneratedValue
	public Integer getApplicationControlId() {
		return applicationControlId;
	}
	public void setApplicationControlId(Integer applicationControlId) {
		this.applicationControlId = applicationControlId;
	}
	@NotNull(message="{booleanYesNo.notNull}")
	public Boolean getVerificationViaMail() {
		return verificationViaMail;
	}
	public void setVerificationViaMail(Boolean verificationViaMail) {
		this.verificationViaMail = verificationViaMail;
	}
	@NotNull(message="{mailserver.port.notNull}")
	public Integer getAccountExpireDays() {
		return AccountExpireDays;
	}
	public void setAccountExpireDays(Integer accountExpireDays) {
		AccountExpireDays = accountExpireDays;
	}
	public Date getDolm() {
		return dolm;
	}
	public void setDolm(Date dolm) {
		this.dolm = dolm;
	}



}
