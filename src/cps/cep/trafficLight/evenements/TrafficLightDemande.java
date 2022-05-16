package cps.cep.trafficLight.evenements;

import java.util.ArrayList;

import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventI;

public class TrafficLightDemande extends ComplexEvent {

	private static final long serialVersionUID = 1L;
	
	public TrafficLightDemande(ArrayList<EventI> CorrelatedEvents) {
		super(CorrelatedEvents);
	}
	
}
