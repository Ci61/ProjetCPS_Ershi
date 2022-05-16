package cps.evenements;

import java.io.Serializable;
import java.util.ArrayList;

import cps.evenements.interfaces.ComplexEventI;
import cps.evenements.interfaces.EventI;

public class ComplexEvent 
extends AbstractEvent
implements ComplexEventI {

	private static final long serialVersionUID = 1L;
	private ArrayList<EventI> CorrelatedEvents;

	
	public ComplexEvent(ArrayList<EventI> CorrelatedEvents) {
		super();
		this.CorrelatedEvents = CorrelatedEvents;
	}

	

	/**
	 * Verifier si au moins un des evenements correles definissent la propriete passe en parametre
	 * @param name nom de la propriete
	 * */
	@Override
	public boolean hasProperty(String name) {
		for(EventI e: CorrelatedEvents) {
			if(e.hasProperty(name))
				return true;
		}
		return false;
	}

	/**
	 * Obtenir la valeur de la propriete demandee sur le premier evenement parmi 
	 * ses evenements correles aui definissent cette propriete. 
	 * @param name nom de la propriete
	 * */
	@Override
	public Serializable getPropertyValue(String name) {
		for(EventI e: CorrelatedEvents) {
			if(e.hasProperty(name))
				return e.getPropertyValue(name);
		}
		return null;
	}

	@Override
	public ArrayList<EventI> getCorrelatedEvents() {
		return CorrelatedEvents;
	}

}
