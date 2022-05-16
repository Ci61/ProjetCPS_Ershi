package cps.cep.connector;

import cps.cep.interfaces.EventReceptionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>EventReceptionConnector.java</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : 21/02/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
public class EventReceptionConnector 
extends AbstractConnector 
implements EventReceptionCI {

	/**
	 * de l'interface requise à l'interface offerte
	 */
	@Override
	public void receiveEvent(String emitterURI, EventI event) throws Exception {
		((EventReceptionCI) this.offering).receiveEvent(emitterURI, event);
	}

	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		((EventReceptionCI) this.offering).receiveEvents(emitterURI, events);
	}

}
