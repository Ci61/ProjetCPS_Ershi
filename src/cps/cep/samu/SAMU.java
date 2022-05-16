package cps.cep.samu;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

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
import cps.cep.samu.evenements.*;
import cps.cep.trafficLight.evenements.AtDestination;
import cps.cep.trafficLight.evenements.AtStation;
import cps.cep.trafficLight.evenements.TrafficLightDemande;
import cps.cep.trafficLight.evenements.TrafficLightDemandePriorite;
import cps.cep.trafficLight.evenements.TrafficLightvehiculePassage;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.SAMUActionConnector;
import fr.sorbonne_u.cps.smartcity.connections.SAMUActionOutboundPort;
import fr.sorbonne_u.cps.smartcity.connections.SAMUNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.SAMUActionCI;


/**
 * The class <code>SAMU.java</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : 09/03/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */

@OfferedInterfaces(offered = { SAMUNotificationCI.class})
@RequiredInterfaces(required = {CEPBusManagementCI.class, SAMUActionCI.class })
public class SAMU
extends AbstractComponent
implements SAMUNotificationImplI, ActionExecutionI
{
	/** identifier of the corresponding SAMU station. */
	protected String stationId;
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
	protected SAMUActionOutboundPort actionOBP;
	
	
	/** notification inbound port.											*/
	protected SAMUNotificationInboundPort	notificationIBP;

	/** the URI that will be used for the plug-in (assumes a singleton).	*/
	protected final static String SAMU_PLUGIN_OUT_URI = "facade-samu-out-plugin-uri";
	protected final static String SAMU_PLUGIN_IN_URI = "facade-samu-in-plugin-uri";

	protected SAMU(
			String stationId, 
			String notificationInboundPortURI,
			String actionInboundPortURI,
			String cepBusManageInboundPortURI) throws Exception 
	{
		super(2, 0);

		assert SmartCityDescriptor.isValidSAMUStationId(stationId);
		assert notificationInboundPortURI != null && !notificationInboundPortURI.isEmpty();
		assert cepBusManageInboundPortURI != null && !cepBusManageInboundPortURI.isEmpty();
		assert actionInboundPortURI != null && !actionInboundPortURI.isEmpty();

		this.stationId = stationId;
		this.cepBusManageInboundPortURI = cepBusManageInboundPortURI;
		this.actionInboundPortURI = actionInboundPortURI;

		this.cbemOBP = new BMCEPBusOutboundPort(this);
		this.cbemOBP.publishPort();
		
		this.actionOBP = new SAMUActionOutboundPort(this);
		this.actionOBP.publishPort();
		
		this.notificationIBP = 
				new SAMUNotificationInboundPort(notificationInboundPortURI, this);
		this.notificationIBP.publishPort();

		this.getTracer().setTitle("SAMUStationFacade");
		this.getTracer().setRelativePosition(1, 0);
		this.toggleTracing();
	}

	@Override
	public synchronized void	start() throws ComponentStartException
	{
		super.start();
		try {
			/** se connecte avec le bus avec le connector BusManager */
			this.doPortConnection(
					this.cbemOBP.getPortURI(),
					this.cepBusManageInboundPortURI,
					CEPBusManagerConnector.class.getCanonicalName());
			
			/** se connecte avec le proxy pour executer des actions */
			this.doPortConnection(
					this.actionOBP.getPortURI(), 
					this.actionInboundPortURI,
					SAMUActionConnector.class.getCanonicalName());
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
		pluginOut.setPluginURI(SAMU_PLUGIN_OUT_URI);
		this.installPlugin(pluginOut);
		this.eeOBP = (EECEPBusEmetteurOutboundPort)pluginOut.getEventEmission();
		
		PluginFacadeIn pluginIn = new PluginFacadeIn();
		pluginIn.setPluginURI(SAMU_PLUGIN_IN_URI);
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
	 * declencher l'action correspondante en fonctions des parametres reels recus
	 * @param a l'identification de l'action a executer
	 * @param params un tableau de parametres a utiliser si necessaire
	 * @return
	 */
	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		assert a instanceof ActionsSAMU;
		assert params != null && params.length == 3 && 
				params[0] instanceof AbsolutePosition &&
				params[1] instanceof String &&
				params[2] instanceof TypeOfSAMURessources;
		AbsolutePosition pos = (AbsolutePosition) params[0];
		String personId = (String) params[1];
		TypeOfSAMURessources src = (TypeOfSAMURessources) params[2];
		
		System.out.println("SAMU execute~");
		
		switch ((ActionsSAMU) a) {
			case InterventionAmbulance: 
				this.actionOBP.triggerIntervention(pos, personId, src); break;
			case InterventionMedecin: 
				this.actionOBP.triggerIntervention(pos, personId, src); break;
			case InterventionTeleMedecin: 
				this.actionOBP.triggerIntervention(pos, personId, src);
		}
		return (ResponseI)null;
	}
	
	
	/**       Emetteur */
	
	@Override
	public void			healthAlarm(
		AbsolutePosition position,
		TypeOfHealthAlarm type,
		LocalTime occurrence
		) throws Exception
	{
		assert	position != null;
		assert	!type.isTracking();
		assert	occurrence != null;

		assert	SmartCityDescriptor.dependsUpon(position, this.stationId);

		this.traceMessage("Health notification of type " + type +
						  " at position " + position +
						  " received at " + occurrence + "\n");
		
		System.out.println("send healthAlarm~");
		
		SAMUAlarmeSante alarmeSante = new SAMUAlarmeSante(position);
		alarmeSante.putProperty("type", type);
		alarmeSante.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, alarmeSante);

	}

	@Override
	public void			trackingAlarm(
		AbsolutePosition position,
		String personId,
		LocalTime occurrence
		) throws Exception
	{
		assert	position != null;
		assert	personId != null && !personId.isEmpty();
		assert	occurrence != null;

		assert	SmartCityDescriptor.dependsUpon(position, this.stationId);

		this.traceMessage("Health notification of type tracking for " +
						  personId + " at position " + position +
						  " received at " + occurrence + "\n");
		System.out.println("send trackingAlarm~");
		SAMUAlarmeSante alarmeSante = new SAMUAlarmeSante(position);
		alarmeSante.putProperty("type", TypeOfHealthAlarm.TRACKING);
		
		SAMUSignalOk signal = new SAMUSignalOk();
		signal.putProperty("personId", personId);	
		
		ArrayList<EventI> CorrelatedEvents = new ArrayList<EventI>();
		CorrelatedEvents.add(alarmeSante);
		CorrelatedEvents.add(signal);
		SAMUChuteDePersonne trackingAlarm = new SAMUChuteDePersonne(CorrelatedEvents);
		trackingAlarm.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, trackingAlarm);
		
	}

	@Override
	public void			manualSignal(
		String personId,
		LocalTime occurrence
		) throws Exception
	{
		assert	personId != null && !personId.isEmpty();
		assert	occurrence != null;

		this.traceMessage("Manual signal emitted by " + personId +
						  " received at " + occurrence + "\n");
		System.out.println("send manualSignal~");
		SAMUSignalOk signal = new SAMUSignalOk();
		signal.putProperty("personId", personId);	
		signal.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, signal);
	}

	@Override
	public void			requestPriority(
		IntersectionPosition intersection,
		TypeOfTrafficLightPriority priority,
		String vehicleId,
		AbsolutePosition destination,
		LocalTime occurrence
		) throws Exception
	{
		this.traceMessage("priority " + priority + " requested for vehicle " +
						  vehicleId + " at intersection " + intersection +
						  " towards " + destination + " at " + occurrence + "\n");
		System.out.println("send requestPriority~");
		TrafficLightDemandePriorite demandePriorite = new TrafficLightDemandePriorite();
		demandePriorite.putProperty("priority", priority);
		
		TrafficLightvehiculePassage vehiculePassage = new TrafficLightvehiculePassage();
		vehiculePassage.putProperty("vehicleId", vehicleId);
		vehiculePassage.putProperty("direction", destination);
		vehiculePassage.putProperty("intersection", intersection);
		
		ArrayList<EventI> CorrelatedEvents = new ArrayList<EventI>();
		CorrelatedEvents.add(demandePriorite);
		CorrelatedEvents.add(vehiculePassage);
		
		TrafficLightDemande demande = new TrafficLightDemande(CorrelatedEvents);
		demande.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, demande);
	}

	@Override
	public void			atDestination(String vehicleId, LocalTime occurrence)
	throws Exception
	{
		this.traceMessage("Vehicle " + vehicleId +
						  " has arrived at destination at " + occurrence + "\n");
		System.out.println("Vehicle arrive atDestination~");
		AtDestination atDestination = new AtDestination();
        atDestination.setCreateTime(occurrence);
        atDestination.putProperty("vehicleId", vehicleId);
        this.eeOBP.sendEvent(stationId, atDestination);
	}

	@Override
	public void			atStation(String vehicleId, LocalTime occurrence)
	throws Exception
	{
		this.traceMessage("Vehicle " + vehicleId + " has arrived at station at "
								+ occurrence + "\n");
		System.out.println("Vehicle arrive station~");
		AtStation atStation = new AtStation();
        atStation.setCreateTime(occurrence);
        atStation.putProperty("vehicleId", vehicleId);
        this.eeOBP.sendEvent(stationId, atStation);
	}

	@Override
	public void			notifyMedicsAvailable(LocalTime occurrence)
	throws Exception
	{
		assert	occurrence != null;

		this.traceMessage(
				"Notification that medics are available received at " +
															occurrence + "\n");
		System.out.println("notifyMedicsAvailable~");
		SAMUMedicsDispos medicDispo = new SAMUMedicsDispos();
		medicDispo.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, medicDispo);
	}

	@Override
	public void			notifyNoMedicAvailable(LocalTime occurrence)
	throws Exception
	{
		assert	occurrence != null;

		this.traceMessage(
				"Notification that no medic are available received at " +
															occurrence + "\n");
		System.out.println("notifyNoMedicAvailable~");
		SAMUMedicsEnIntervention medicEnIntervention = new SAMUMedicsEnIntervention();
		medicEnIntervention.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, medicEnIntervention);
	}

	@Override
	public void			notifyAmbulancesAvailable(LocalTime occurrence)
	throws Exception
	{
		assert	occurrence != null;

		this.traceMessage(
				"Notification that ambulances are available received at " +
															occurrence + "\n");
		System.out.println("notifyAmbulancesAvailable~");
		SAMUAmbulancesDispos ambDispo = new SAMUAmbulancesDispos();
		ambDispo.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, ambDispo);
		
	}

	@Override
	public void			notifyNoAmbulanceAvailable(LocalTime occurrence)
	throws Exception
	{
		assert	occurrence != null;

		this.traceMessage(
				"Notification that no ambulance are available received at " +
															occurrence + "\n");
		System.out.println("notifyNoAmbulanceAvailable~");
		SAMUAmbulancesEnIntervention ambEnIntervention = new SAMUAmbulancesEnIntervention();
		ambEnIntervention.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, ambEnIntervention);
	}

	
	
}
