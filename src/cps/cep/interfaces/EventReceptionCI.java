package cps.cep.interfaces;

import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface EventReceptionCI extends RequiredCI, OfferedCI {
	public void receiveEvent(String emitterURI, EventI event) throws Exception;
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception;
}
