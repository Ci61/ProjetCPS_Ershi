package cps.cep.samu.regles;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * The class <code>S6.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */

public class S6 extends S5 {

	public S6() {}
	
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		boolean delai = matchedEvents.get(0).getTimeStamp().plus(
				Duration.of(10, ChronoUnit.MINUTES)).isBefore(
						TimeManager.get().getCurrentLocalTime());
		try {
			return  delai && !samuState.estMedecinDispo() ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		//A corriger apres
		try {
			samuState.triggerPasserSamuSuiv(matchedEvents.get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
