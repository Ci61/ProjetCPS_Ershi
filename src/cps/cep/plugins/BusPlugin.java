package cps.cep.plugins;

import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventEmissionI;
import cps.cep.plugins.connections.EECEPBusEmetteurIBPForPlugin;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>BusPlugin.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 04/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class BusPlugin 
extends AbstractPlugin
implements EventEmissionI
{
	
	private static final long serialVersionUID = 1L;
	/** the inbound port which calls will be on this plug-in.				*/
	protected EECEPBusEmetteurIBPForPlugin	mip;
	protected String eeCepBusEmmIBP_uri;
	
	// -------------------------------------------------------------------------
	// Life cycle
	// -------------------------------------------------------------------------
	
	public BusPlugin(String eeCepBusEmmIBP_uri) {
		super();
		this.eeCepBusEmmIBP_uri = eeCepBusEmmIBP_uri;
	}
	
	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		assert owner instanceof EventEmissionI;
	}

	@Override
	public void initialise() throws Exception {
		super.initialise();
		
		// Add interfaces and create ports
		this.addOfferedInterface(EventEmissionCI.class);
		this.mip = new EECEPBusEmetteurIBPForPlugin(eeCepBusEmmIBP_uri,
													this.getOwner(),
													this.getPluginURI());
		this.mip.publishPort();
	}

	@Override
	public void uninstall() throws Exception {
		this.mip.unpublishPort();
		this.mip.destroyPort();
		this.removeOfferedInterface(EventEmissionCI.class);
	}


	// -------------------------------------------------------------------------
	// Plug-in services implementation
	// -------------------------------------------------------------------------
	
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionI)this.getOwner()).sendEvent(emitterURI, event);
	}
	
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionI)this.getOwner()).sendEvents(emitterURI, events);
	}

}
