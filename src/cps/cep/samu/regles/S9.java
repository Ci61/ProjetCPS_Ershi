package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

/**
 * The class <code>S9.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S9 implements RuleI {

	public S9() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI di = null;
		for(int i = 0; i < eb.numberOfEvents() && (di == null); i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof ComplexEvent){
				ArrayList<EventI> CorrelatedEvents = ((ComplexEvent) e).getCorrelatedEvents();
				for(EventI event: CorrelatedEvents) {
					if(!((TypeOfHealthAlarm)event.getPropertyValue("type")).equals(TypeOfHealthAlarm.EMERGENCY)){
						break;
					}
				}
				di = e;
			}
		}
		if(di != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(di);
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
			return samuState.estAmbulDispo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		EventI e = matchedEvents.get(0);
		AbsolutePosition pos = (AbsolutePosition)e.getPropertyValue("position");
		try {
			samuState.triggerAmbulance(pos);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
