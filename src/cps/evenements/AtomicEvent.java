package cps.evenements;

import java.io.Serializable;
import java.util.HashMap;

import cps.evenements.interfaces.AtomicEventI;

public class AtomicEvent 
extends AbstractEvent
implements AtomicEventI {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Serializable> properties; 
	
	public AtomicEvent() {
		super();
		properties = new HashMap<String, Serializable>();
	}


	@Override
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}

	@Override
	public Serializable getPropertyValue(String name) {
		return properties.get(name);
	}

	@Override
	public Serializable putProperty(String name, Serializable value) {
		return properties.put(name, value);
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

}
