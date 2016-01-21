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
	  @NamedQuery(name="findAllMailservers", query="SELECT o FROM Mailserver o"),
	  @NamedQuery(name="findMailserverByName", 
      query="SELECT o FROM Mailserver o WHERE o.name = :name"),
	  @NamedQuery(name="findMailserverByApplication", 
      query="SELECT o FROM Mailserver o WHERE o.application = :application"),
	  @NamedQuery(name="deleteMailserverByApplication", 
      query="DELETE FROM Mailserver o WHERE o.application = :application")
})

@Table(uniqueConstraints = @UniqueConstraint(columnNames = "application"))
public class Mailserver implements Serializable
{

	/**
	 *
	 * @author Christian Lewer
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer mailserverId;
	private String name;
	private Boolean mailSmtpAuth;
	private Boolean mailSmtpStarttlsEnable;
	private String mailSmtpHost;
	private String application;
	private Integer mailSmtpPort;
	private String userName;
	private String password;
	private Date dob;
	
	@Id @GeneratedValue
	public Integer getMailserverId() {
		return mailserverId;
	}
	public void setMailserverId(Integer mailserverId) {
		this.mailserverId = mailserverId;
	}

	
	 public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@NotNull(message="{booleanYesNo.notNull}")
	public Boolean getMailSmtpAuth() {
		return mailSmtpAuth;
	}
	public void setMailSmtpAuth(Boolean mailSmtpAuth) {
		this.mailSmtpAuth = mailSmtpAuth;
	}
	@NotNull(message="{booleanYesNo.notNull}")
	public Boolean getMailSmtpStarttlsEnable() {
		return mailSmtpStarttlsEnable;
	}
	public void setMailSmtpStarttlsEnable(Boolean mailSmtpStarttlsEnable) {
		this.mailSmtpStarttlsEnable = mailSmtpStarttlsEnable;
	}
	@NotEmpty
	public String getMailSmtpHost() {
		return mailSmtpHost;
	}
	public void setMailSmtpHost(String mailSmtpHost) {
		this.mailSmtpHost = mailSmtpHost;
	}
	

	@NotNull(message="{mailserver.port.notNull}")
	@Range(min=1,max=65000, message="{mailserver.port.range}")
	public Integer getMailSmtpPort() {
		return mailSmtpPort;
	}
	public void setMailSmtpPort(Integer mailSmtpPort) {
		this.mailSmtpPort = mailSmtpPort;
	}
	@NotEmpty
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	@NotEmpty
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	@NotEmpty
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}


}
