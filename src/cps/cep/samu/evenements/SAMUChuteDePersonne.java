package cps.cep.samu.evenements;

import java.util.ArrayList;

import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventI;

public class SAMUChuteDePersonne extends ComplexEvent{

	private static final long serialVersionUID = 1L;

	public SAMUChuteDePersonne(ArrayList<EventI> CorrelatedEvents) {
		super(CorrelatedEvents);
	}


}
