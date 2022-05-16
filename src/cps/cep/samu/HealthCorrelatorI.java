package cps.cep.samu;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public interface HealthCorrelatorI extends CorrelatorStateI {
	public EventBaseI getEb() throws Exception ;
	public boolean estDansCentre(AbsolutePosition position)  throws Exception ;
	public void setAmbulDispo(boolean amb)  throws Exception ;
	public boolean estAmbulDispo()  throws Exception ;
	public void setMedecinAvailable(boolean amb)  throws Exception ;
	public boolean estMedecinDispo() throws Exception ;
	public void triggerAmbulance(AbsolutePosition pos) throws Exception ;
	public void triggerMedecin(AbsolutePosition pos) throws Exception ;
	public void triggerMedecin(AbsolutePosition pos, String personId) throws Exception ;
	public void triggerMedicCall(AbsolutePosition pos, String personId) throws Exception ;
	public void triggerDemande( ArrayList<EventI> matchedEvents ) throws Exception ;
	public void triggerPasserSamuSuiv(EventI e)  throws Exception ;
}
