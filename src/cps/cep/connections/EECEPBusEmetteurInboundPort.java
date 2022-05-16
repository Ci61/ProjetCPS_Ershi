package cps.cep.connections;

import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventEmissionI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

//-----------------------------------------------------------------------------
/**
*
* 
* The class <code>EventEmissionInboundPort</code> implements the
 * inbound port for the {@code EventEmissionCI} interface. It's the port of CEPBusFacade
* <p><strong>Invariant</strong></p>
* 
* 
* <p>Created on : 27/01/2022</p>
* 
* @author	 <p>Hongyu YAN & Liuyi CHEN</p>
*/
public class EECEPBusEmetteurInboundPort 
extends AbstractInboundPort
implements EventEmissionCI {

	private static final long serialVersionUID = 1L;
	
	public EECEPBusEmetteurInboundPort(ComponentI owner)
	throws Exception 
	{
		super(EventEmissionCI.class, owner);
		assert owner instanceof EventEmissionI;
	}

	public EECEPBusEmetteurInboundPort(String uri, ComponentI owner)
	throws Exception 
	{
		super(uri, EventEmissionCI.class, owner);
		assert owner instanceof EventEmissionI;
	}

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((EventEmissionI)b).sendEvent(emitterURI, event);
						return null;
				});
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((EventEmissionI)b).sendEvents(emitterURI, events);
						return null;
				});
	}
}
