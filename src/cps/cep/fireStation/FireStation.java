package cps.cep.fireStation;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

import cps.cep.connections.BMCEPBusOutboundPort;
import cps.cep.connections.EECEPBusEmetteurOutboundPort;
import cps.cep.connector.CEPBusManagerConnector;
import cps.cep.fireStation.evenements.FireStationAlarmeFeu;
import cps.cep.fireStation.evenements.FireStationCGrandIntervention;
import cps.cep.fireStation.evenements.FireStationCStantardIntervention;
import cps.cep.fireStation.evenements.FireStationComionsGrandDispo;
import cps.cep.fireStation.evenements.FireStationComionsStantardDispo;
import cps.cep.fireStation.evenements.FireStationEndOfFire;
import cps.cep.interfaces.ActionExecutionI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.ResponseI;
import cps.cep.plugins.PluginFacadeIn;
import cps.cep.plugins.PluginFacadeOut;
import cps.cep.plugins.connections.ActionExecutionIBPForPlugin;
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
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.connections.FireStationNotificationInboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationCI;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;

@OfferedInterfaces(offered = { FireStationNotificationCI.class})
@RequiredInterfaces(required = {CEPBusManagementCI.class, FireStationActionCI.class })
public class FireStation 
extends AbstractComponent 
implements FireStationNotificationImplI, ActionExecutionI
{

	/** identifier of the corresponding SAMU station. */
	protected String stationId;
	/** URI of the event emission inbound port. */
	protected String eventEmissionInboundPortURI;
	/** URI of the cep bus inbound port. */
	protected String cepBusManageInboundPortURI;
	/** URI of the FireStation action inbound port.										*/
	protected String fsActionInboundPortURI;

	/** cepBus outbound port. */
	protected BMCEPBusOutboundPort cbemOBP;
	/** eventEmission outbound port. */
	protected EECEPBusEmetteurOutboundPort eeOBP;
	/** action outbound port.												*/
	protected FireStationActionOutboundPort actionOBP;

	/** notification inbound port.											*/
	protected FireStationNotificationInboundPort	notificationIBP;

	/** the URI that will be used for the plug-in (assumes a singleton).	*/
	protected final static String FIRESTATION_PLUGIN_OUT_URI = "facade-firestation-out-plugin-uri";
	protected final static String FIRESTATION_PLUGIN_IN_URI = "facade-firestation-in-plugin-uri";

	
	protected FireStation(
		String stationId, 
		String fsNotificationInboundPortURI,
		String fsActionInboundPortURI,
		String cepBusManageInboundPortURI
	) throws Exception  {
		super(2, 0);

		assert SmartCityDescriptor.isValidFireStationId(stationId);
		assert cepBusManageInboundPortURI != null && !cepBusManageInboundPortURI.isEmpty();
		assert fsActionInboundPortURI != null && !fsActionInboundPortURI.isEmpty();
		assert fsNotificationInboundPortURI != null && !fsNotificationInboundPortURI.isEmpty();

		this.stationId = stationId;
		this.cepBusManageInboundPortURI = cepBusManageInboundPortURI;
		this.fsActionInboundPortURI = fsActionInboundPortURI;

		this.cbemOBP = new BMCEPBusOutboundPort(this);
		this.cbemOBP.publishPort();

		this.actionOBP = new FireStationActionOutboundPort(this);
		this.actionOBP.publishPort();

		this.notificationIBP =
				new FireStationNotificationInboundPort(fsNotificationInboundPortURI, this);
			this.notificationIBP.publishPort();
		
		this.getTracer().setTitle("FireStationFacade");
		this.getTracer().setRelativePosition(1, 1);
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
					this.fsActionInboundPortURI,
					FireStationActionConnector.class.getCanonicalName());
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
		pluginOut.setPluginURI(FIRESTATION_PLUGIN_OUT_URI);
		this.installPlugin(pluginOut);
		this.eeOBP = (EECEPBusEmetteurOutboundPort)pluginOut.getEventEmission();
		
		PluginFacadeIn pluginIn = new PluginFacadeIn();
		pluginIn.setPluginURI(FIRESTATION_PLUGIN_IN_URI);
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
		assert a instanceof ActionsFireStation;
		assert params != null && params.length == 2 && 
				params[0] instanceof AbsolutePosition &&
				params[1] instanceof TypeOfFirefightingResource;
		AbsolutePosition pos = (AbsolutePosition) params[0];
		TypeOfFirefightingResource src = (TypeOfFirefightingResource) params[1];
		
		System.out.println("FireStation execute~");
		switch ((ActionsFireStation) a) {
			case PremiereAlarmeImmeuble: 
				this.actionOBP.triggerFirstAlarm(pos, src); break;
			case PremiereAlarmeMaison: 
				this.actionOBP.triggerFirstAlarm(pos, src); break;
			case AlarmeGenerale: 
				this.actionOBP.triggerGeneralAlarm(pos); break;
			case AlarmeGeneralePos:
				this.actionOBP.triggerGeneralAlarm(pos); break;
			case SecondeAlarmePos:  
				this.actionOBP.triggerSecondAlarm(pos);
		}
		return (ResponseI)null;
	}

	// -------------------------------------------------------------------------
	// Component services implementation
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#fireAlarm(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition, java.time.LocalTime, fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire)
	 */
	@Override
	public void			fireAlarm(
		AbsolutePosition position,
		LocalTime occurrence,
		TypeOfFire type
		) throws Exception
	{
		this.traceMessage("Fire alarm of type " + type +
						  " received from position " + position +
						  " at " + occurrence + "\n");
		System.out.println("FireStation send fireAlarm");
		FireStationAlarmeFeu alarmeFeu = new FireStationAlarmeFeu(position);
		alarmeFeu.setCreateTime(occurrence);
		alarmeFeu.putProperty("type", type);
		this.eeOBP.sendEvent(stationId, alarmeFeu);
		
	}

	/**
	 * @see fr.sorbonne_u.cps.smartcity.interfaces.FireStationNotificationImplI#endOfFire(fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition, java.time.LocalTime)
	 */
	@Override
	public void			endOfFire(
		AbsolutePosition position,
		LocalTime occurrence
		) throws Exception
	{
		this.traceMessage("End of fire received from position " + position +
						  " at " + occurrence + "\n");
		System.out.println("FireStation send endOfFire");
		FireStationEndOfFire finFeu = new FireStationEndOfFire(position);
		finFeu.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, finFeu);
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
						  " towards " + destination + " at " + occurrence +
						  "\n");
		System.out.println("FireStation send requestPriority");
		TrafficLightDemandePriorite demandePriorite = new TrafficLightDemandePriorite();
		demandePriorite.putProperty("priority", priority);

		TrafficLightvehiculePassage vehiculePassage = new TrafficLightvehiculePassage();
		vehiculePassage.putProperty("vehicleId", vehicleId);
		vehiculePassage.putProperty("direction", destination);

		ArrayList<EventI> CorrelatedEvents = new ArrayList<EventI>();
		CorrelatedEvents.add(demandePriorite);
		CorrelatedEvents.add(vehiculePassage);

		TrafficLightDemande demande = new TrafficLightDemande(CorrelatedEvents);
		demande.setCreateTime(occurrence);
		this.eeOBP.sendEvent(stationId, demande);
	}

	@Override
    public void            atDestination(String vehicleId, LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("Vehicle " + vehicleId +
                           " has arrived at destination\n");
        System.out.println("FireStation Vehicle at destination");
        AtDestination atDestination = new AtDestination();
        atDestination.setCreateTime(occurrence);
        atDestination.putProperty("vehicleId", vehicleId);
        this.eeOBP.sendEvent(stationId, atDestination);

    }

    @Override
    public void            atStation(String vehicleId, LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("Vehicle " + vehicleId + " has arrived at station\n");
        System.out.println("FireStation Vehicle at station");
        AtStation atStation = new AtStation();
        atStation.setCreateTime(occurrence);
        atStation.putProperty("vehicleId", vehicleId);
        this.eeOBP.sendEvent(stationId, atStation);
    }
@Override
    public void            notifyNoStandardTruckAvailable(LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("No standard truck available received at " +
                          occurrence + "\n");
        System.out.println("FireStation send notifyNoStandardTruckAvailable");
        FireStationCStantardIntervention fsCStantardIntervention = new FireStationCStantardIntervention();
        fsCStantardIntervention.setCreateTime(occurrence);
        this.eeOBP.sendEvent(stationId, fsCStantardIntervention);
    }

    /**
     * camion stantard disponible
     */
    @Override
    public void            notifyStandardTrucksAvailable(LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("Standard trucks available received at " +
                          occurrence + "\n");
        System.out.println("FireStation send notifyStandardTrucksAvailable");
        FireStationComionsStantardDispo  fsComionsStantardDispo = new FireStationComionsStantardDispo();
        fsComionsStantardDispo.setCreateTime(occurrence);
        this.eeOBP.sendEvent(stationId, fsComionsStantardDispo);
    }

    @Override
    public void            notifyNoHighLadderTruckAvailable(LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("No high ladder truck available received at " +
                          occurrence + "\n");
        System.out.println("FireStation send notifyNoHighLadderTruckAvailable");
        FireStationCGrandIntervention fsCGrandIntervention = new FireStationCGrandIntervention();
        fsCGrandIntervention.setCreateTime(occurrence);
        this.eeOBP.sendEvent(stationId, fsCGrandIntervention);
    }

    @Override
    public void         notifyHighLadderTrucksAvailable(LocalTime occurrence)
    throws Exception
    {
        this.traceMessage("High ladder trucks available received at " +
                          occurrence + "\n");
        System.out.println("FireStation send notifyHighLadderTrucksAvailable");
        FireStationComionsGrandDispo  fsComionsGrandDispo = new FireStationComionsGrandDispo();
        fsComionsGrandDispo.setCreateTime(occurrence);
        this.eeOBP.sendEvent(stationId, fsComionsGrandDispo);
    }

}
