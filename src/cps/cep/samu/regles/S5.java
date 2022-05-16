package cps.cep.samu.regles;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * The class <code>S5.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S5 implements RuleI {

	public S5() {}

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI as = null;
		for(int i = 0; i < eb.numberOfEvents() && (as == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUAlarmeSante && e.hasProperty("type") && e.hasProperty("position") 
				&& ((TypeOfHealthAlarm)e.getPropertyValue("type")).equals(TypeOfHealthAlarm.TRACKING)
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
//		long toV = TimeManager.get().localTime2nanoDelay(
//				TimeManager.get().getCurrentLocalTime().plusMinutes(10));
		boolean delai = matchedEvents.get(0).getTimeStamp().plus(
				Duration.of(10, ChronoUnit.MINUTES)).isBefore(
						TimeManager.get().getCurrentLocalTime());
		try {
			return samuState.estMedecinDispo() && delai;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {

	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		EventI e = matchedEvents.get(0);
		AbsolutePosition pos = (AbsolutePosition)e.getPropertyValue("position");
		SAMUAlarmeSante as = new SAMUAlarmeSante(pos);
		as.putProperty("type", TypeOfHealthAlarm.MEDICAL);
		eb.removeEvent(e);
		eb.addEvent(as);
	}

}
