package cps.cep.interfaces;

import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface EventEmissionCI 
extends RequiredCI, OfferedCI, EventEmissionI {
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception ;
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception ;
}
