package cps.cep.fireStation.evenements;

import java.util.ArrayList;

import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventI;

public class FireStationPremiereAlarmeImm extends ComplexEvent {

	private static final long serialVersionUID = 1L;

	public FireStationPremiereAlarmeImm(ArrayList<EventI> CorrelatedEvents) {
		super(CorrelatedEvents);
	}

}
