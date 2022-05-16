package cps.cep.plugins;

import cps.cep.connections.ActionExecutionOutboundPort;
import cps.cep.connector.ActionExecutionConnector;
import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.CorrelatorStateI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>CorrePlugin.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 28/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class CorrePlugin 
extends AbstractPlugin 
implements CorrelatorStateI {

	// -------------------------------------------------------------------------
	// Plug-in variables and constants
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	/** actionExecution outbound port. */
	protected ActionExecutionOutboundPort aeOBP;
	protected String actionExecutionIBPURI;
	
	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

	public CorrePlugin(String actionExecutionInboundPortURI) {
		super();
		this.actionExecutionIBPURI = actionExecutionInboundPortURI;
	}

	@Override
	public void installOn(ComponentI owner) 
	throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(ActionExecutionCI.class);
		this.aeOBP = new ActionExecutionOutboundPort(this.getOwner());
		this.aeOBP.publishPort();
//		this.addRequiredInterface(CEPBusManagementCI.class);
	}

	@Override
	public void initialise() throws Exception {
		this.getOwner().doPortConnection(
				this.aeOBP.getPortURI(), 
				this.actionExecutionIBPURI,
				ActionExecutionConnector.class.getCanonicalName());
	}

	@Override
	public void finalise() throws Exception {
		this.getOwner().doPortDisconnection(this.aeOBP.getPortURI());
	}

	@Override
	public void uninstall() throws Exception {
		this.aeOBP.unpublishPort();
		this.aeOBP.destroyPort();
		this.removeRequiredInterface(ActionExecutionCI.class);
	}
	
	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------
	
	public ActionExecutionCI getActionExecution()
	{
		return this.aeOBP;
	}
	

}
