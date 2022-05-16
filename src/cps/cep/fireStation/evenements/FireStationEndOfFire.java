package cps.cep.fireStation.evenements;

import cps.evenements.AtomicEvent;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class FireStationEndOfFire
extends AtomicEvent  {
    private static final long serialVersionUID = 1L;
	private AbsolutePosition position;
	
    public FireStationEndOfFire(AbsolutePosition p) {
		super();
		this.position = p;
		putProperty("position", p);
		
	}
	
	public AbsolutePosition getPosition() {
		return position;
	}

	public void setPosition(AbsolutePosition position) {
		this.position = position;
	}
}
