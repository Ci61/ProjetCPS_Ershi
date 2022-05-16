package cps.evenements.interfaces;

import java.time.Duration;

public interface EventBaseI {
	
	public void addEvent(EventI e);
	
	public void removeEvent(EventI e);

	public EventI getEvent(int i);
	
	public int numberOfEvents();
	
	public boolean appearsIn(EventI e);
	
	public void clearEvents(Duration d);
	
}
