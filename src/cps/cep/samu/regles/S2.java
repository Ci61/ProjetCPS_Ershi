package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.HealthCorrelatorI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.evenements.SAMUDemandeIntervention;

/**
 * The class <code>S2.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S2 extends S1 {

	public S2() {}


	/**
	 * Ici nous n'avons pas encore implemente la propagation des evenements d'une centre SAMU a un autre,
	 * donc nous verifions juste la position d'evenement ici
	 */
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		// boolean flag = true;
		// if(!samuState.estAmbulDispo()) flag = false;
		// if(flag) System.out.println("Ambulances non disponibles ! T^T");
		// HealthCorrelator sPrime = samuState.getSamuPlusProche();
		// return  flag && samuState.estDansCentre("p") && 
		// 		samuState.estSuivantPlusPres(sPrime) && 
		// 		sPrime.estAmbulDispo();
		EventI e = matchedEvents.get(0);
		try {
			return samuState.estDansCentre(((SAMUAlarmeSante) e).getPosition()) && !samuState.estAmbulDispo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (HealthCorrelatorI)c;
		//A corriger
		SAMUDemandeIntervention demande = new SAMUDemandeIntervention(matchedEvents);
		try {
			System.out.println("S2 triggerPasserSamuSuiv");
			samuState.triggerPasserSamuSuiv(demande);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}
