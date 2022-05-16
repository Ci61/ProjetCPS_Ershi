package cps.cep.fireStation.regles;

import java.util.ArrayList;
import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationAlarmeFeu;
import cps.cep.fireStation.evenements.FireStationPremiereAlarmeImm;
import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.ComplexEventI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class F1 implements RuleI {

	public F1() {}

	/**
	 * Trouver l'evenement en alarme feu du type immeuble 
	 * */
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI af = null;
		for(int i = 0; i < eb.numberOfEvents() && (af == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof FireStationAlarmeFeu && e.hasProperty("type") && e.hasProperty("position") 
				&& (e.getPropertyValue("type")).equals(TypeOfFire.Building)
				&& (e.getPropertyValue("position")).equals(((FireStationAlarmeFeu) e).getPosition()))
			{
				af = e;
			}

		}
		if(af != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(af);
			return matchedEvents;
		} else {
			return null;
		}
	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		return true;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireStationCorrelatorI fireStationState = (FireStationCorrelatorI)c;
		try {
			return fireStationState.estDansZone(((FireStationAlarmeFeu) matchedEvents.get(0)).getPosition()) && 
					fireStationState.estEchelleDispo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		FireStationCorrelatorI fireStationState = (FireStationCorrelatorI)c;
		EventI e = matchedEvents.get(0);
		try {
			fireStationState.triggerPremiereAlarmeImmeuble(((FireStationAlarmeFeu) e).getPosition(), matchedEvents);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		ComplexEventI di = new FireStationPremiereAlarmeImm(matchedEvents);
		eb.removeEvent(matchedEvents.get(0));
		eb.addEvent(di);
		
	}

}
