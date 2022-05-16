package cps.cep.samu.evenements;

import cps.evenements.AtomicEvent;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class SAMUAlarmeSante extends AtomicEvent {

	private static final long serialVersionUID = 1L;
	private AbsolutePosition position;
	
	public SAMUAlarmeSante(AbsolutePosition position) {
		super();
		this.position = position;
		this.putProperty("position", position);
	}
	
	public AbsolutePosition getPosition() {
		return position;
	}

	public void setPosition(AbsolutePosition position) {
		this.position = position;
	}

}
