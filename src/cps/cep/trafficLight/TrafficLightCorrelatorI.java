package cps.cep.trafficLight;

import cps.cep.interfaces.CorrelatorStateI;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public interface TrafficLightCorrelatorI extends CorrelatorStateI {
	public void passerIntersection(
			IntersectionPosition intersection,
			TypeOfTrafficLightPriority priority) throws Exception ;
}
