package cps.cep.samu.regles;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAmbulancesEnIntervention;
import java.util.ArrayList;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;

/**
 * The class <code>S16.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S16 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI as = null;
		for(int i = 0; i < eb.numberOfEvents() && (as == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUAmbulancesEnIntervention)
			{
				as = e;
			}
		}
		
		if(as != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(as);
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
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		try {
			samuState.setAmbulDispo(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Ambulances en " + samuState + " sont passes en non disponibles ~");
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
