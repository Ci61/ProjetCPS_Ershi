package cps.regles.bouchons;

import cps.cep.trafficLight.TrafficLightCorrelatorI;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

public class TrafficLightCorrelator 
implements TrafficLightCorrelatorI 
{

	@Override
	public void passerIntersection(
			IntersectionPosition intersection,
			TypeOfTrafficLightPriority priority) throws Exception {
		System.out.println("Passer l'intersection " + intersection.toString() + " en mode " + priority);
	}

}
