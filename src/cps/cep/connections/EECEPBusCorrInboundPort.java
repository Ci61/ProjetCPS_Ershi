package cps.cep.connections;

import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventEmissionI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * The class <code>EECEPBusCorrInboundPort.java</code> 
 *
 * 
 * <p>Created on : 12/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class EECEPBusCorrInboundPort 
extends AbstractInboundPort 
implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	public EECEPBusCorrInboundPort(ComponentI owner) throws Exception {
		super(EventEmissionCI.class, owner);
		assert owner instanceof EventEmissionI;
	}

	public EECEPBusCorrInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, EventEmissionCI.class, owner);
		assert owner instanceof EventEmissionI;
	}

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.getOwner().runTask(
				b -> { try {
					((EventEmissionI)b).sendEvent(emitterURI, event);
				} catch (Exception e) {
					e.printStackTrace();
				}
				});
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		this.getOwner().runTask(
				b -> { try {
					((EventEmissionI)b).sendEvents(emitterURI, events);
				} catch (Exception e) {
					e.printStackTrace();
				}
				});
	}

}
