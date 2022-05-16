package cps.cep.connections;

import cps.cep.interfaces.EventEmissionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>EECEPBusCorreOutboundPort.java</code> 
 *
 * <p>Created on : 10/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class EECEPBusCorreOutboundPort 
extends AbstractOutboundPort 
implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	public EECEPBusCorreOutboundPort(ComponentI owner)
			throws Exception {
		super(EventEmissionCI.class, owner);
	}

	public EECEPBusCorreOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, EventEmissionCI.class, owner);
	}

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI)this.getConnector()).
			sendEvent(emitterURI, event);
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI)this.getConnector()).
			sendEvents(emitterURI, events);
	}

}
