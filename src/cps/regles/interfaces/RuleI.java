package cps.regles.interfaces;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;

public interface RuleI {
	
	public ArrayList<EventI> match( EventBaseI eb );
	
	public boolean correlate( ArrayList<EventI> matchedEvents );
	
	public boolean filter( ArrayList<EventI> matchedEvents, CorrelatorStateI c );
	
	public void act( ArrayList<EventI> matchedEvents, CorrelatorStateI c );
	
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb);
}
