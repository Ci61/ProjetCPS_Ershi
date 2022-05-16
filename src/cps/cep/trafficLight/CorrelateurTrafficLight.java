package cps.cep.trafficLight;

import java.util.ArrayList;
import cps.cep.components.Correlateur;
import cps.cep.connector.CBEMEventEmissionConnector;
import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventReceptionCI;
import cps.regles.RuleBase;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

/**
 * The class <code>CorrelateurCirculation.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 10/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
@OfferedInterfaces(offered = { EventReceptionCI.class })
@RequiredInterfaces(required = { EventEmissionCI.class, CEPBusManagementCI.class, ActionExecutionCI.class })
public class CorrelateurTrafficLight 
extends Correlateur 
implements TrafficLightCorrelatorI 
{	
	public CorrelateurTrafficLight(
			String corre_id, 
			String cepBusManageInboundPortURI, 
			String eventReceptionInboundPortURI,
			ArrayList<String> executorURIs,
			ArrayList<String> emitterURIs,
			RuleBase ruleBase) throws Exception 
	{
		super(corre_id,
				cepBusManageInboundPortURI, 
				eventReceptionInboundPortURI,
				executorURIs,
				emitterURIs,
				ruleBase);
	}

	// ???
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		this.eventEmissionInboundPortURI = 
				this.cbcOBP.registerCorrelator(corre_id, erIBP.getPortURI());
		/** Connection entre Correlator et Bus (EventEmission) */
		this.doPortConnection(this.eeOBP.getPortURI(), this.eventEmissionInboundPortURI,
				CBEMEventEmissionConnector.class.getCanonicalName());
		
		for (String uri : emitterURIs) {
			this.cbcOBP.subscribe(corre_id, uri);
		}
	}
	
	@Override
	public void passerIntersection(
			IntersectionPosition intersection,
			TypeOfTrafficLightPriority priority) throws Exception {
		System.out.println("Passer l'intersection " + intersection.toString() + " en mode " + priority);
	}

	

}
