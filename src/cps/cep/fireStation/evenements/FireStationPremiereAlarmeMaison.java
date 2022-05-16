package cps.cep.fireStation.evenements;

import java.util.ArrayList;

import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventI;

public class FireStationPremiereAlarmeMaison 
extends ComplexEvent {

	private static final long serialVersionUID = 1L;

	public FireStationPremiereAlarmeMaison(ArrayList<EventI> CorrelatedEvents) {
		super(CorrelatedEvents);
	}
}
