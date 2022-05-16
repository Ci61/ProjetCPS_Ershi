package cps.cep.trafficLight.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.trafficLight.TrafficLightCorrelatorI;
import cps.cep.trafficLight.evenements.TrafficLightDemande;
import cps.cep.trafficLight.evenements.TraficLightAttend;
import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class C1 implements RuleI {
	
	public C1() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI event = null;
		for(int i = 0; i < eb.numberOfEvents() && (event == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof TrafficLightDemande) {
				ArrayList<EventI> correlatedEvents = ((TrafficLightDemande) e).getCorrelatedEvents();
				if(correlatedEvents.size() == 2) {
					EventI demandePriorite = correlatedEvents.get(0);
					EventI vehiculePassage = correlatedEvents.get(1);
					if(demandePriorite.hasProperty("priority") && 
							vehiculePassage.hasProperty("vehicleId") && 
							vehiculePassage.hasProperty("direction") && 
							vehiculePassage.hasProperty("intersection")) {
						event = e;
					}
				}
			}
		}
		if(event != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(event);
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
		return true;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		TrafficLightCorrelatorI cc = (TrafficLightCorrelatorI)c;
		ComplexEvent ce = (ComplexEvent) matchedEvents.get(0);
		IntersectionPosition intersection = (IntersectionPosition)ce.getCorrelatedEvents().get(1).getPropertyValue("intersection");
		TypeOfTrafficLightPriority priority = (TypeOfTrafficLightPriority)ce.getCorrelatedEvents().get(0).getPropertyValue("priority");
		try {
			cc.passerIntersection(intersection, priority);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		TraficLightAttend tla = new TraficLightAttend(matchedEvents);
		eb.removeEvent(matchedEvents.get(0));
		eb.addEvent(tla);
	}

}
