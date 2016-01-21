package net.application.customer.controller;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.flow.Flow;
import javax.faces.flow.builder.FlowBuilder;
import javax.faces.flow.builder.FlowBuilderParameter;
import javax.faces.flow.builder.FlowDefinition;

/**
 *
 * @author Thomas Asel
 */
@ApplicationScoped
public class FlowFactory implements Serializable {
    
    private final static Logger LOG = Logger.getLogger(FlowFactory.class.getSimpleName());
    private static final long serialVersionUID = 1L;

    @Produces @FlowDefinition
    public Flow defineFlowRegistration( @FlowBuilderParameter FlowBuilder flowBuilder) {
        
        String flowId = "registration";   // id for this flow 
        flowBuilder.id("", flowId); // set flow id
        
        // add a view to the flow and mark it as start node of the flow graph
        flowBuilder.viewNode(flowId, "/secure/registration/registration.xhtml?faces-redirect=true").markAsStartNode(); 
       
        // add another view to the flow 
        flowBuilder.viewNode("confirm", "/secure/registration/confirm.xhtml?faces-redirect=true");
        
        flowBuilder.methodCallNode("confirmRegistration")  // create a method call node
        .defaultOutcome("/yyy.xhtml")            // default outcome
        .expression("#{registrationBean.confirmRegistration()}");    // method expression to invoke
    
        flowBuilder.methodCallNode("createRegistration")  // create a method call node
        .defaultOutcome("/homeCdi2.xhtml?faces-redirect=true")            // default outcome
        .expression("#{registrationBean.createRegistration()}");    // method expression to invoke
        
        flowBuilder.methodCallNode("endRegistration")  // create a method call node
        .defaultOutcome("/homeCdi2.xhtml?faces-redirect=true")            // default outcome
        .expression("#{registrationBean.endRegistration()}");    // method expression to invoke
        
        flowBuilder.methodCallNode("exitRegistration")  // create a method call node
        .defaultOutcome("/homeCdi2.xhtml?faces-redirect=true")            // default outcome
        .expression("#{registrationBean.exitRegistration()}");    // method expression to invoke

        // add a return node. The flow is exited with the outcome "home" once this node is reached.
        flowBuilder.returnNode("exit").fromOutcome("/homeCdi2.xhtml?faces-redirect=true");
        
        // call this when the flow is entered
        flowBuilder.initializer("#{registrationBean.initialize()}");
           
        // call this when the flow is exited
        //flowBuilder.finalizer("#{registrationBean.finalize()}");

       
        return flowBuilder.getFlow();
    }
    
    @Produces @FlowDefinition
    public Flow defineFlowVerification( @FlowBuilderParameter FlowBuilder flowBuilder) {
        
        String flowId = "verification";   // id for this flow 
        flowBuilder.id("", flowId); // set flow id
        
     // add a view to the flow and mark it as start node of the flow graph
       // flowBuilder.viewNode(flowId, "/secure/registration/verification.xhtml").markAsStartNode(); 
       
        flowBuilder.methodCallNode("verification")  // create a method call node
       .defaultOutcome("/homeCdi2.xhtml?faces-redirect=true")            // default outcome
       .expression("#{verifyBean.onload()}").markAsStartNode();    // method expression to invoke
        
        flowBuilder.methodCallNode("verification-captcha")  // create a method call node
        .defaultOutcome("/homeCdi.xhtml?faces-redirect=true")            // default outcome
        .expression("#{verifyBean.execute()}");    // method expression to invoke
        
     // add another view to the flow 
        flowBuilder.viewNode("verification-confirm", "/secure/registration/verification.xhtml?faces-redirect=true");
                
        flowBuilder.methodCallNode("verification-end")  // create a method call node
        .defaultOutcome("/homeCdi2.xhtml?faces-redirect=true")            // default outcome
        .expression("#{verifyBean.exitVerification}");    // method expression to invoke
        
     // add a return node. The flow is exited with the outcome "home" once this node is reached.
        flowBuilder.returnNode("verification-exit").fromOutcome("/homeCdi2.xhtml?faces-redirect=true");
        flowBuilder.returnNode("verification-errorNoUser").fromOutcome("/error.xhtml?errorId=errorWrongVerificationKey");
        flowBuilder.returnNode("verification-errorUserWithWrongStatus").fromOutcome("/error.xhtml?errorId=errorWronguserStatus");
        
        // call this when the flow is entered
        flowBuilder.initializer("#{verifyBean.initialize()}");
           
        // call this when the flow is exited
        //flowBuilder.finalizer("#{verifyBean.finalize()}");

       
        return flowBuilder.getFlow();
    }
    @Produces @FlowDefinition
    public Flow defineFlowCreateIdmGroup( @FlowBuilderParameter FlowBuilder flowBuilder) {
        
        String flowId = "idmUtilCreateGroup";   // id for this flow 
        flowBuilder.id("", flowId); // set flow id
        
        // add a view to the flow and mark it as start node of the flow graph
        flowBuilder.viewNode(flowId, "/secure/" + "administration" + "/" + "utilCreateGroup" + ".xhtml").markAsStartNode(); 
            // add another view to the flow 
        flowBuilder.viewNode("confirmUtilCreateGroup", "/secure/" + "administration" + "/" + "confirmUtilCreateGroup" + ".xhtml");
       
       
        // add a return node. The flow is exited with the outcome "home" once this node is reached.
        flowBuilder.returnNode("end").fromOutcome("/secure/administration/userAdministration.xhtml");
        
        // call this when the flow is entered
        flowBuilder.initializer("#{idmUtilCreateController.initialize()}");
           
        // call this when the flow is exited
        flowBuilder.finalizer("#{idmUtilCreateController.finalize()}");

       
        return flowBuilder.getFlow();
    }
    
  
}