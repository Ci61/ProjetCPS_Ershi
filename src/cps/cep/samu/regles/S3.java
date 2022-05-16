package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

/**
 * The class <code>S3.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S3 implements RuleI {

	public S3() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI as = null;
		for(int i = 0; i < eb.numberOfEvents() && (as == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUAlarmeSante && e.hasProperty("type") && e.hasProperty("position") 
				&& ((TypeOfHealthAlarm)e.getPropertyValue("type")).equals(TypeOfHealthAlarm.MEDICAL)
				&& ((AbsolutePosition)e.getPropertyValue("position")).equals(((SAMUAlarmeSante) e).getPosition()))
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
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		EventI e = matchedEvents.get(0);
		try {
			return samuState.estDansCentre(((SAMUAlarmeSante) e).getPosition()) && samuState.estMedecinDispo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		AbsolutePosition pos = (AbsolutePosition) matchedEvents.get(0).getPropertyValue("position");
		try {
			samuState.triggerMedecin(pos) ;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
