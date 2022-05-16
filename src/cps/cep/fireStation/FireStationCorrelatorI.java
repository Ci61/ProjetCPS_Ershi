package cps.cep.fireStation;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public interface FireStationCorrelatorI extends CorrelatorStateI {
	public EventBaseI getEb() throws Exception ;
	public boolean estDansZone(AbsolutePosition p) throws Exception ;
	public void setEchelleDispo(boolean ed) throws Exception ;
	public boolean estEchelleDispo() throws Exception ;
	public void setCamionDispo(boolean ecd) throws Exception ;
	public boolean estCamionDispo() throws Exception ;
	public AbsolutePosition plusProcheStation(AbsolutePosition p) throws Exception ;
	public void triggerPremiereAlarmeImmeuble(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception ;
	public void triggerPremiereAlarmeMaison(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception ;
	public void triggerGeneraleAlarme(ArrayList<EventI> matchedEvents) throws Exception ;
	public void triggerGeneraleAlarmePosition(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception ;
	public void triggerSecondeAlarme(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception ;
}
