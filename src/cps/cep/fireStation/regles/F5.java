package cps.cep.fireStation.regles;

import java.util.ArrayList;
import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationDemandeIntervention;
import cps.cep.fireStation.evenements.FireStationPremiereAlarmeImm;
import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.ComplexEventI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;

public class F5 implements RuleI {

	public F5() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI alarme = null;
		for(int i = 0; i < eb.numberOfEvents() && (alarme == null); i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof FireStationDemandeIntervention){
				ArrayList<EventI> CorrelatedEvents = ((FireStationDemandeIntervention) e).getCorrelatedEvents();
				for(EventI event: CorrelatedEvents) {
					if(!((TypeOfFire)event.getPropertyValue("type")).equals(TypeOfFire.Building)
						|| !(event.hasProperty("position"))){
						break;
					}
				}
				alarme = e;
			}
		}
		if(alarme != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(alarme);
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
			return fireStationState.estEchelleDispo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		System.out.println("triggerPremiereAlarmeImmeuble");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		ComplexEventI premierAlarme = new FireStationPremiereAlarmeImm(matchedEvents);
		for(EventI e: matchedEvents) {
			eb.removeEvent(e);
		}
		eb.addEvent(premierAlarme);
	}

}
