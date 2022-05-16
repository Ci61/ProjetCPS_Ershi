package cps.cep.plugins;

import cps.cep.connections.EECEPBusEmetteurOutboundPort;
import cps.cep.connector.CBEMEventEmissionConnector;
import cps.cep.interfaces.EventEmissionCI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>FacadePlugin.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 28/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class PluginFacadeOut 
extends AbstractPlugin 
{

	private static final long serialVersionUID = 1L;

	/** eventEmission outbound port. */
	protected EECEPBusEmetteurOutboundPort eeOBP;
	private String eventEmissionIBPURI;


	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------

//	public PluginFacadeOut(String eventEmissionInboundPortURI) {
//		super();
//		this.eventEmissionIBPURI = eventEmissionInboundPortURI;
//	}
	
	@Override
	public void installOn(ComponentI owner) 
	throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(EventEmissionCI.class);
		this.eeOBP = new EECEPBusEmetteurOutboundPort(this.getOwner());
		this.eeOBP.publishPort();
	}

	@Override
	public void initialise() throws Exception {
		this.getOwner().doPortConnection(
			this.eeOBP.getPortURI(),
			this.eventEmissionIBPURI,
			CBEMEventEmissionConnector.class.getCanonicalName());
	}

	@Override
	public void finalise() throws Exception {
		this.getOwner().doPortDisconnection(this.eeOBP.getPortURI());
	}

	@Override
	public void uninstall() throws Exception {
		this.eeOBP.unpublishPort();
		this.eeOBP.destroyPort();
		this.removeRequiredInterface(EventEmissionCI.class);
	}

	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------
	
	public void setEventEmissionIBPURI(String eventEmissionInboundPortURI) {
		this.eventEmissionIBPURI = eventEmissionInboundPortURI;
	}
	
	public EventEmissionCI getEventEmission()
	{
		assert this.eeOBP != null;
		return this.eeOBP;
	}
}
