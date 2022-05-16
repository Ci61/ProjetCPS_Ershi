package cps.evenements;

import java.io.Serializable;
import java.time.LocalTime;

import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * The class <code>Event.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 27/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public abstract class Event 
implements EventI {

	private static final long serialVersionUID = 1L;
	private LocalTime createTime;

	public Event() {
		this.createTime = TimeManager.get().getCurrentLocalTime();
	}

	@Override
	public LocalTime getTimeStamp() {
		return createTime;
	}
	
	public void setCreateTime(LocalTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public boolean hasProperty(String name) {
		return false;
	}

	@Override
	public Serializable getPropertyValue(String name) {
		return null;
	}

}
