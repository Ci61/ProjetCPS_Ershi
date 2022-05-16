package cps.cep.fireStation.regles;

import java.util.ArrayList;

import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationDemandeIntervention;
import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.ComplexEventI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class F3 extends F1 implements RuleI {

	public F3() {	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireStationCorrelatorI fireStationState = (FireStationCorrelatorI)c;
		try {
			AbsolutePosition p = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
			AbsolutePosition toPos = fireStationState.plusProcheStation(p);
			if(toPos!=null) {
				System.out.println("Caserne propager un evenement au firstation plus proche:" + toPos);
				return 	fireStationState.estEchelleDispo();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		System.out.println("Passer prochain fireStation");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		ComplexEventI intervention = new FireStationDemandeIntervention(matchedEvents);
		for(EventI e: matchedEvents) {
			eb.removeEvent(e);
		}
		eb.addEvent(intervention);
	}

}
