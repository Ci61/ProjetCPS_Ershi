package cps.evenements.interfaces;

import java.io.Serializable;
import java.time.LocalTime;

public interface EventI 
extends Serializable 
{
	public LocalTime getTimeStamp();
	public boolean hasProperty(String name);
	public Serializable getPropertyValue(String name);
}
