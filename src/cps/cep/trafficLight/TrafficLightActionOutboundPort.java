package cps.cep.trafficLight;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

/**
 * The class <code>CirculationActionOutboundPort.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 12/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class TrafficLightActionOutboundPort 
extends AbstractOutboundPort
implements TrafficLightActionCI 
{
	private static final long serialVersionUID = 1L;
	
	public TrafficLightActionOutboundPort(ComponentI owner)
			throws Exception {
		super(TrafficLightActionCI.class, owner);
	}

	public TrafficLightActionOutboundPort(String uri, Class<? extends RequiredCI> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, TrafficLightActionCI.class, owner);
	}

	@Override
	public void changePriority(TypeOfTrafficLightPriority priority) throws Exception {
		((TrafficLightActionCI)this.getConnector()).returnToNormalMode();
	}

	@Override
	public void returnToNormalMode() throws Exception {
		((TrafficLightActionCI)this.getConnector()).returnToNormalMode();
	}

}
