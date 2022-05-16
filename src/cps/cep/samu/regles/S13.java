package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUChuteDePersonne;
import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
/**
 * The class <code>S13.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S13 implements RuleI {

	public S13() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI event = null;
		for(int i = 0; i < eb.numberOfEvents() && (event == null); i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUChuteDePersonne ){
				ArrayList<EventI> correlatedEvents = ((SAMUChuteDePersonne) e).getCorrelatedEvents();
				if(correlatedEvents.size() == 2) {
					EventI fall = correlatedEvents.get(0);
					EventI signal = correlatedEvents.get(1);
					if(fall.hasProperty("type") && 
						fall.hasProperty("position") && 
						fall.hasProperty("personId") && 
						signal.hasProperty("personId")) {
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
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		try {
			return samuState.estMedecinDispo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		ComplexEvent ce = (ComplexEvent) matchedEvents.get(0);
		AbsolutePosition pos = (AbsolutePosition)ce.getCorrelatedEvents().get(0).getPropertyValue("position");
		String personId = (String)ce.getCorrelatedEvents().get(0).getPropertyValue("personId");
		try {
			samuState.triggerMedicCall(pos, personId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
