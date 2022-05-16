package cps.cep.trafficLight;

import java.io.Serializable;
import java.time.LocalTime;

import cps.cep.connections.BMCEPBusOutboundPort;
import cps.cep.connections.EECEPBusEmetteurOutboundPort;
import cps.cep.connector.CEPBusManagerConnector;
import cps.cep.interfaces.ActionExecutionI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.ResponseI;
import cps.cep.plugins.PluginFacadeIn;
import cps.cep.plugins.PluginFacadeOut;
import cps.cep.plugins.connections.ActionExecutionIBPForPlugin;
import cps.cep.trafficLight.evenements.TrafficLightvehiculePassage;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.TrafficLightNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.Direction;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TrafficLightNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

/**
 * The class <code>TrafficLight.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 12/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */

@OfferedInterfaces(offered = {TrafficLightNotificationCI.class})
@RequiredInterfaces(required = {CEPBusManagementCI.class, TrafficLightActionCI.class })
public class TrafficLight 
extends AbstractComponent 
implements TrafficLightNotificationImplI, ActionExecutionI
{
	/** identifier of the traffic light. */
	protected String stationId;
	/** position of the traffic light.										*/
	protected IntersectionPosition					position;
	
	/** URI of the event emission inbound port. */
	protected String eventEmissionInboundPortURI;
	/** URI of the cep bus inbound port. */
	protected String cepBusManageInboundPortURI;
	/** URI of the action inbound port.										*/
	protected String actionInboundPortURI;
	
	/** cepBus outbound port. */
	protected BMCEPBusOutboundPort cbemOBP;
	/** eventEmission outbound port. */
	protected EECEPBusEmetteurOutboundPort eeOBP;
	/** action outbound port.												*/
	protected TrafficLightActionOutboundPort actionOBP;

	/** notification inbound port.											*/
	protected TrafficLightNotificationInboundPort	notificationIBP;
	
	/** the URI that will be used for the plug-in (assumes a singleton).	*/
	protected final static String TRAFFIC_PLUGIN_OUT_URI = "facade-trafficLight-out-plugin-uri";
	protected final static String TRAFFIC_PLUGIN_IN_URI = "facade-trafficLight-in-plugin-uri";


	protected TrafficLight(
			String stationId,
			IntersectionPosition position,
			String notificationInboundPortURI,
			String actionInboundPortURI,
			String cepBusManageInboundPortURI) throws Exception
	{
		super(2, 0);
		assert	position != null &&
				SmartCityDescriptor.isInCity(position);
		assert notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty();
		assert cepBusManageInboundPortURI != null && !cepBusManageInboundPortURI.isEmpty();
		assert actionInboundPortURI != null && !actionInboundPortURI.isEmpty();
		
		this.stationId = stationId;
		this.position = position;
		this.cepBusManageInboundPortURI = cepBusManageInboundPortURI;
		this.actionInboundPortURI = actionInboundPortURI;

		this.cbemOBP = new BMCEPBusOutboundPort(this);
		this.cbemOBP.publishPort();
		
		this.actionOBP = new TrafficLightActionOutboundPort(this);
		this.actionOBP.publishPort();
		
		this.notificationIBP =
			new TrafficLightNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationIBP.publishPort();
		
		this.getTracer().setTitle("TrafficLightFacade");
		this.getTracer().setRelativePosition(1, 2);
		this.toggleTracing();
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			this.doPortConnection(
					this.cbemOBP.getPortURI(),
					this.cepBusManageInboundPortURI,
					CEPBusManagerConnector.class.getCanonicalName());
			
			this.doPortConnection(
					this.actionOBP.getPortURI(), 
					this.actionInboundPortURI,
					TrafficLightActionConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		this.eventEmissionInboundPortURI = this.cbemOBP.registerEmitter(stationId);

		/** Plugin entre Emetteur et Bus (EventEmissionCI) */
		PluginFacadeOut pluginOut = new PluginFacadeOut();
		pluginOut.setEventEmissionIBPURI(eventEmissionInboundPortURI);
		pluginOut.setPluginURI(TRAFFIC_PLUGIN_OUT_URI);
		this.installPlugin(pluginOut);
		this.eeOBP = (EECEPBusEmetteurOutboundPort)pluginOut.getEventEmission();
		
		PluginFacadeIn pluginIn = new PluginFacadeIn();
		pluginIn.setPluginURI(TRAFFIC_PLUGIN_IN_URI);
		this.installPlugin(pluginIn);
		ActionExecutionIBPForPlugin aeIBP = (ActionExecutionIBPForPlugin)pluginIn.getActionExecution();
		this.cbemOBP.registerExecutor(stationId, aeIBP.getPortURI());
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.cbemOBP.unregisterEmitter(stationId);
		this.cbemOBP.unregisterExecutor(stationId);

		this.doPortDisconnection(this.cbemOBP.getPortURI());
		this.doPortDisconnection(this.actionOBP.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.cbemOBP.unpublishPort();
			this.actionOBP.unpublishPort();
			this.notificationIBP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}

		super.shutdown();
	}
	
/**       Executeur */
	
	/**
	 * déclencher l'action correspondante en fonctions des paramètres réels réçus
	 * @param a l'identification de l'action à exécuter
	 * @param params un tableau de paramètres à utiliser si nécessaire
	 * @return
	 */
	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		assert a instanceof ActionsTrafficLight;
		System.out.println("TrafficLight execute~");
		switch ((ActionsTrafficLight) a) {
			case ChangePriority: 
				assert params != null && params.length == 1 && 
				params[0] instanceof TypeOfTrafficLightPriority;
				this.actionOBP.changePriority((TypeOfTrafficLightPriority) params[0]); break;
			case ReturnToNormalMode: 
				assert params == null;
				this.actionOBP.returnToNormalMode(); break;
		}
		return (ResponseI)null;
	}
	
	/**       Emetteur */
	@Override
	public void vehiclePassage(String vehicleId, Direction d, LocalTime occurrence) throws Exception {
		this.traceMessage("Traffic light at " + this.position +
				  " receives the notification of the passage of " +
				  vehicleId + " in the direction of " + d +
				  " at " + occurrence + "\n");
		System.out.println("TrafficLight send vehiclePassage");
		TrafficLightvehiculePassage vehiculePassage = new TrafficLightvehiculePassage();
		vehiculePassage.setCreateTime(occurrence);
		vehiculePassage.putProperty("vehiculeId", vehicleId);
		vehiculePassage.putProperty("direction", d);
		this.eeOBP.sendEvent(stationId, vehiculePassage);
	}

	
}
