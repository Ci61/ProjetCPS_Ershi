package cps.cep.connections;

import cps.cep.components.Correlateur;
import cps.cep.interfaces.EventReceptionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * The class <code>EventReceptionInboundPort</code> implements theminbound
 * port for the {@code EventReceptionCI} interface.
 *
 * 
 * <p>
 * Created on : 11/03/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
public class EventReceptionInboundPort 
extends AbstractInboundPort 
implements EventReceptionCI 
{

	private static final long serialVersionUID = 1L;

	public EventReceptionInboundPort(ComponentI owner)
	throws Exception {
		super(EventReceptionCI.class, owner);
		assert owner instanceof Correlateur;
	}

	public EventReceptionInboundPort(String uri, ComponentI owner)
	throws Exception {
		super(uri, EventReceptionCI.class, owner);
		assert owner instanceof Correlateur;
	}
	
	@Override
	public void receiveEvent(String emitterURI, EventI event) 
	throws Exception {
		this.getOwner().handleRequest(
				b -> { ((Correlateur)b).receiveEvent(emitterURI, event);
						return null;
				});
	}

	@Override
	public void receiveEvents(String emitterURI, EventI[] events) 
	throws Exception {
		this.getOwner().handleRequest(
				b -> { ((Correlateur)b).receiveEvents(emitterURI, events);
						return null;
				});
	}

}
