package net.application.util;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Named
public class CreateMailAdress implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	
	@Inject transient Logger log;
	private String host = "letterbee.de";
	private String user = "root";
    private String password = "VdmG6.48";
    private String command1 = "ls -ltr";
    private Pattern pattern;
	private Matcher matcher;
	
	private static final String EMAIL_PATTERN = 
		"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
   
public Integer createAccount(String source, String destination){
		Integer exitStatus = 10;
		pattern = Pattern.compile(EMAIL_PATTERN);
		log.info("Source Destination: " + source + " - " + destination);
		if (validate(source) && validate(destination)){
		    //mysql -uroot -pVdmG6.48 mail -e "INSERT INTO forwardings (source, destination) VALUES ('staffscoutUser3@letterbee.de', 'christian.lewer@gmx.de');"
		    String command="mysql -uroot -pVdmG6.48 mail -e \"INSERT INTO forwardings (source, destination) VALUES ('" + source + "', '" + destination + "');\"";
		  
	    	try{	         
			        java.util.Properties config = new java.util.Properties(); 
			        config.put("StrictHostKeyChecking", "no");
			        JSch jsch = new JSch();
			        Session session=jsch.getSession(user, host, 22);
			        session.setPassword(password);
			        session.setConfig(config);
			        session.connect();
			        System.out.println("Connected");
			         
			        Channel channel=session.openChannel("exec");
			        ((ChannelExec)channel).setCommand(command);
			        channel.setInputStream(null);
			        ((ChannelExec)channel).setErrStream(System.err);
			         
			        InputStream in=channel.getInputStream();
			        channel.connect();
			        byte[] tmp=new byte[1024];
			        while(true){
			          while(in.available()>0){
			            int i=in.read(tmp, 0, 1024);
			            if(i<0)break;
			            System.out.print(new String(tmp, 0, i));
			          }
			          if(channel.isClosed()){
			            System.out.println("exit-status: "+channel.getExitStatus());
			            exitStatus=channel.getExitStatus();
			            break;
			          }
			          try{Thread.sleep(1000);}catch(Exception ee){}
			        }
			        channel.disconnect();
			        session.disconnect();
			        System.out.println("DONE");
		    	}catch (Exception e){
		    		e.printStackTrace();
		    	}
		} else {
			System.out.println("No regular Mail-Address detected");
		}
		return exitStatus;
}
public Integer deleteAccount(String source, String destination){
	Integer exitStatus = 10;
	pattern = Pattern.compile(EMAIL_PATTERN);
	log.info("Source Destination: " + source + " - " + destination);
	if (validate(source) && validate(destination)){
	    //mysql -uroot -pVdmG6.48 mail -e "delete from forwardings where source = 'VCLEWDsGLcOesf8Rlb6qJcwRVIr0wA@letterbee.de' and destination = 'christianlewer@gmx.de';"
	    String command="mysql -uroot -pVdmG6.48 mail -e \"DELETE FROM forwardings where source = '" + source + "' and destination = '" + destination + "';\"";
	  
    	try{	         
		        java.util.Properties config = new java.util.Properties(); 
		        config.put("StrictHostKeyChecking", "no");
		        JSch jsch = new JSch();
		        Session session=jsch.getSession(user, host, 22);
		        session.setPassword(password);
		        session.setConfig(config);
		        session.connect();
		        System.out.println("Connected");
		         
		        Channel channel=session.openChannel("exec");
		        ((ChannelExec)channel).setCommand(command);
		        channel.setInputStream(null);
		        ((ChannelExec)channel).setErrStream(System.err);
		         
		        InputStream in=channel.getInputStream();
		        channel.connect();
		        byte[] tmp=new byte[1024];
		        while(true){
		          while(in.available()>0){
		            int i=in.read(tmp, 0, 1024);
		            if(i<0)break;
		            System.out.print(new String(tmp, 0, i));
		          }
		          if(channel.isClosed()){
		            System.out.println("exit-status: "+channel.getExitStatus());
		            exitStatus=channel.getExitStatus();
		            break;
		          }
		          try{Thread.sleep(1000);}catch(Exception ee){}
		        }
		        channel.disconnect();
		        session.disconnect();
		        System.out.println("DONE");
	    	}catch (Exception e){
	    		e.printStackTrace();
	    	}
	} else {
		System.out.println("No regular Mail-Address detected");
	}
	return exitStatus;
}

public Integer checkAccount(String mail){
	
	Integer exitStatus = 10;
	pattern = Pattern.compile(EMAIL_PATTERN);
	
	if (validate(mail)){
		String command="mysql -uroot -pVdmG6.48 mail -e \"select * from forwardings where destination = '" + mail + "'\"|grep source";
	    
		try{	         
		        java.util.Properties config = new java.util.Properties(); 
		        config.put("StrictHostKeyChecking", "no");
		        JSch jsch = new JSch();
		        Session session=jsch.getSession(user, host, 22);
		        session.setPassword(password);
		        session.setConfig(config);
		        session.connect();
		        System.out.println("Connected");
		         
		        Channel channel=session.openChannel("exec");
		        ((ChannelExec)channel).setCommand(command);
		        channel.setInputStream(null);
		        ((ChannelExec)channel).setErrStream(System.err);
		         
		        InputStream in=channel.getInputStream();
		        channel.connect();
		        byte[] tmp=new byte[1024];
		        while(true){
		          while(in.available()>0){
		            int i=in.read(tmp, 0, 1024);
		            if(i<0)break;
		            System.out.print(new String(tmp, 0, i));
		          }
		          if(channel.isClosed()){
		            System.out.println("exit-status: "+channel.getExitStatus());
		            exitStatus=channel.getExitStatus();
		            break;
		          }
		          try{Thread.sleep(1000);}catch(Exception ee){}
		        }
		        channel.disconnect();
		        session.disconnect();
		        System.out.println("DONE");
	    	}catch (Exception e){
	    		e.printStackTrace();
	    	}
		} else {
			System.out.println("No regular Mail-Address detected");
		}
	return exitStatus;
	}

	
	public Boolean validate(final String hex) {
	
		matcher = pattern.matcher(hex);
		return matcher.matches();
	
	}

    public void test(){
	    
    	try{	         
		        java.util.Properties config = new java.util.Properties(); 
		        config.put("StrictHostKeyChecking", "no");
		        JSch jsch = new JSch();
		        Session session=jsch.getSession(user, host, 22);
		        session.setPassword(password);
		        session.setConfig(config);
		        session.connect();
		        System.out.println("Connected");
		         
		        Channel channel=session.openChannel("exec");
		        ((ChannelExec)channel).setCommand(command1);
		        channel.setInputStream(null);
		        ((ChannelExec)channel).setErrStream(System.err);
		         
		        InputStream in=channel.getInputStream();
		        channel.connect();
		        byte[] tmp=new byte[1024];
		        while(true){
		          while(in.available()>0){
		            int i=in.read(tmp, 0, 1024);
		            if(i<0)break;
		            System.out.print(new String(tmp, 0, i));
		          }
		          if(channel.isClosed()){
		            System.out.println("exit-status: "+channel.getExitStatus());
		            break;
		          }
		          try{Thread.sleep(1000);}catch(Exception ee){}
		        }
		        channel.disconnect();
		        session.disconnect();
		        System.out.println("DONE");
	    	}catch (Exception e){
	    		e.printStackTrace();
	    	}
    	}
    public Boolean TestMailString(){
    	
		return null;
    	
    }
}
