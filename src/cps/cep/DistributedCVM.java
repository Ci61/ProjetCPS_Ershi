package cps.cep;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cps.cep.components.CEPBus;
import cps.cep.fireStation.CorrelateurFireStation;
import cps.cep.fireStation.FireStation;
import cps.cep.fireStation.regles.F1;
import cps.cep.fireStation.regles.F2;
import cps.cep.samu.CorrelateurSAMU;
import cps.cep.samu.SAMU;
import cps.cep.samu.regles.S1;
import cps.cep.samu.regles.S13;
import cps.cep.samu.regles.S16;
import cps.cep.samu.regles.S17;
import cps.cep.samu.regles.S18;
import cps.cep.samu.regles.S19;
import cps.cep.samu.regles.S2;
import cps.cep.samu.regles.S3;
import cps.cep.samu.regles.S4;
import cps.cep.samu.regles.S5;
import cps.cep.samu.regles.S6;
import cps.cep.samu.regles.S7;
import cps.cep.samu.regles.S8;
import cps.cep.trafficLight.CorrelateurTrafficLight;
import cps.cep.trafficLight.TrafficLight;
import cps.cep.trafficLight.regles.C1;
import cps.regles.RuleBase;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.components.FireStationProxy;
import fr.sorbonne_u.cps.smartcity.components.SAMUStationProxy;
import fr.sorbonne_u.cps.smartcity.components.TrafficLightProxy;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.traffic.components.TrafficLightsSimulator;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * The class <code>DistributedCVM.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年5月12日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class DistributedCVM 
extends AbstractDistributedCVM 
{
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	protected static final String JVM1_URI = "jvm1"; 
	protected static final String JVM2_URI = "jvm2";
	
	public static final boolean	DEBUG = false;
	/** delay before the beginning of the smart city simulation after launching
	 *  the program.														*/
	protected static long		START_DELAY = 10000L;
	/** the start time of the simulation as a Java {@code LocalTime}.		*/
	protected static LocalTime simulatedStartTime;
	/** the end time of the simulation as a Java {@code LocalTime}.			*/
	protected static LocalTime simulatedEndTime;

	/** map that will contain the URI of the action inbound ports used
	 *  in proxy components to offer their services in the smart city
	 *  and the URI of notification inbound ports used by events emitter
	 *  components to receive the notifications from the smart city.	*/
	private	Map<String,String>				facadeNotificationInboundPortsURI;
	/** URI of the fire stations and SAMU centers inbound port used by the
	 *  traffic lights simulator to notify them of events concerning them.	*/
	protected final Map<String,String>					stationsEventIBPURI;
	/** URI of the traffic lights simulator inbound port used by the fire
	 *  stations and SAMU centers to execute the actions concerning them.	*/
	protected final Map<IntersectionPosition,String>	trafficLightsIBPURI;
	
	/**
	 * les uris des executeurs et emetteurs de SAMU
	 */
	protected ArrayList<String> executorSamuURIs = new ArrayList<String>();
	protected ArrayList<String> emitterSamuURIs = new ArrayList<String>();
	
	/**
	 * les uris des executeurs et emetteurs de FireStation
	 */
	ArrayList<String> executorFireStationURIs = new ArrayList<String>();
	ArrayList<String> emitterFireStationURIs = new ArrayList<String>();

	public DistributedCVM(String[] args) throws Exception {
		super(args);
		// initialise the basic simulator smart city descriptor.
		SmartCityDescriptor.initialise();
		assert	simulatedStartTime != null && simulatedEndTime != null
				&& simulatedEndTime.isAfter(simulatedStartTime);
		long realTimeOfStart = System.currentTimeMillis() + START_DELAY;
		new TimeManager(realTimeOfStart, simulatedStartTime, simulatedEndTime);
		// create a map that will contain the URI of the notification inbound
		// ports used in event emitter components to receive the notifications
		// from the smart city.
		this.facadeNotificationInboundPortsURI = new HashMap<>();

		AbstractCVM.getThisJVMURI();

		this.stationsEventIBPURI = new HashMap<>();
		Iterator<String> iterStation =
							SmartCityDescriptor.createFireStationIdIterator();
		while (iterStation.hasNext()) {
			String id = iterStation.next();
			this.stationsEventIBPURI.put(id, AbstractPort.generatePortURI());
		}
		iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
		while (iterStation.hasNext()) {
			stationsEventIBPURI.put(iterStation.next(),
									AbstractPort.generatePortURI());
		}

		this.trafficLightsIBPURI = new HashMap<>();
		Iterator<IntersectionPosition> iterTL =
				SmartCityDescriptor.createTrafficLightPositionIterator();
		while (iterTL.hasNext()) {
			this.trafficLightsIBPURI.put(iterTL.next(),
										 AbstractPort.generatePortURI());
		}
	}

	@Override
	public void instantiateAndPublish() throws Exception 
	{
		if (AbstractCVM.getThisJVMURI().equals(JVM1_URI)) {
			// CEPBus
			String uri_BMcepBusEmetteurIP = AbstractPort.generatePortURI();
			String uri_BMcepBusCorreIP = AbstractPort.generatePortURI();
			
			AbstractComponent.createComponent(
					CEPBus.class.getCanonicalName(), 
					new Object[] {
							uri_BMcepBusEmetteurIP,
							uri_BMcepBusCorreIP
					});
			
			int corre_samu_id = 0;
			int corre_fs_id = 0;
	
	
			// -------------------------------------------------------------------------
			// SAMU
			// -------------------------------------------------------------------------

			RuleBase ruleBaseSAMU = new RuleBase();
			S1 s1 = new S1();
			S2 s2 = new S2();
		    S3 s3 = new S3();
		    S4 s4 = new S4();
		    S5 s5 = new S5();
		    S6 s6 = new S6();
		    S7 s7 = new S7();
		    S8 s8 = new S8();
		    //S9 s9 = new S9();
		    S13 s13 = new S13();
		    S16 s16 = new S16();
		    S17 s17 = new S17();
		    S18 s18 = new S18();
		    S19 s19 = new S19();
			ruleBaseSAMU.addRule(s1);
			ruleBaseSAMU.addRule(s2);
		    ruleBaseSAMU.addRule(s3);
		    ruleBaseSAMU.addRule(s4);
		    ruleBaseSAMU.addRule(s5);
		    ruleBaseSAMU.addRule(s6);
		    ruleBaseSAMU.addRule(s7);
		    ruleBaseSAMU.addRule(s8);
		    //ruleBaseSAMU.addRule(s9);
		    ruleBaseSAMU.addRule(s13);
		    ruleBaseSAMU.addRule(s16);
		    ruleBaseSAMU.addRule(s17);
		    ruleBaseSAMU.addRule(s18);
		    ruleBaseSAMU.addRule(s19);
			
			Iterator<String> samuStationsIditerator = 
					SmartCityDescriptor.createSAMUStationIdIterator();
			while (samuStationsIditerator.hasNext()) {
				String samuStationId = samuStationsIditerator.next();
				String notificationInboundPortURI = AbstractPort.generatePortURI();
				this.register(samuStationId, notificationInboundPortURI);
				executorSamuURIs.add(samuStationId);
				emitterSamuURIs.add(samuStationId);
				
				AbstractComponent.createComponent(
						SAMU.class.getCanonicalName(), 
						new Object[] { 
								samuStationId,
								notificationInboundPortURI, 
								SmartCityDescriptor.
									getActionInboundPortURI(samuStationId),
									uri_BMcepBusEmetteurIP
						});
				
				corre_samu_id ++;
			}
			
			for(int i = 0; i < corre_samu_id; i++) {
				String eventReceptionInboundPortURI = AbstractPort.generatePortURI();
				AbstractComponent.createComponent(
						CorrelateurSAMU.class.getCanonicalName(), 
						new Object[] {
								"corre_samu_id"+i,
								uri_BMcepBusCorreIP,
								eventReceptionInboundPortURI,
								executorSamuURIs,
								emitterSamuURIs,
								ruleBaseSAMU
						});
			}
			
			
			// -------------------------------------------------------------------------
			// FireStation
			// -------------------------------------------------------------------------
			RuleBase ruleBaseFireStation = new RuleBase();
			F1 f1 = new F1();
			F2 f2 = new F2();
			ruleBaseFireStation.addRule(f1);
			ruleBaseFireStation.addRule(f2);
			
			Iterator<String> fireStationIdsIterator =
					SmartCityDescriptor.createFireStationIdIterator();
			while (fireStationIdsIterator.hasNext()) {
				String fireStationId = fireStationIdsIterator.next();
				String notificationInboundPortURI = AbstractPort.generatePortURI();
				this.register(fireStationId, notificationInboundPortURI);
				
				executorFireStationURIs.add(fireStationId);
				emitterFireStationURIs.add(fireStationId);
				
				AbstractComponent.createComponent(
					FireStation.class.getCanonicalName(),
					new Object[]{
						fireStationId,
						notificationInboundPortURI,
						SmartCityDescriptor.getActionInboundPortURI(fireStationId),
						uri_BMcepBusEmetteurIP
					});
				
				corre_fs_id++;
			}
			
			for(int i = 0; i < corre_fs_id; i++) {
				String fsEventReceptionInboundPortURI = AbstractPort.generatePortURI();
				AbstractComponent.createComponent(
						CorrelateurFireStation.class.getCanonicalName(), 
						new Object[] {
								"corre_fs_id" + i,
								uri_BMcepBusCorreIP,
								fsEventReceptionInboundPortURI,
								executorFireStationURIs,
								emitterFireStationURIs,
								ruleBaseFireStation
						});
				
			}
			
			// FireStationProxy
			Iterator<String> iterStation =
					SmartCityDescriptor.createFireStationIdIterator();
			while (iterStation.hasNext()) {
				String id = iterStation.next();
				AbstractComponent.createComponent(
						FireStationProxy.class.getCanonicalName(),
						new Object[]{
								SmartCityDescriptor.getActionInboundPortURI(id),
								this.facadeNotificationInboundPortsURI.get(id),
								id,
								SmartCityDescriptor.getPosition(id),
								this.stationsEventIBPURI.get(id),
								2,
								2
								});
			}
		
			// SAMUStationProxy
			iterStation = SmartCityDescriptor.createSAMUStationIdIterator();
			while (iterStation.hasNext()) {
				String id = iterStation.next();
				AbstractComponent.createComponent(
						SAMUStationProxy.class.getCanonicalName(),
						new Object[]{
								SmartCityDescriptor.getActionInboundPortURI(id),
								this.facadeNotificationInboundPortsURI.get(id),
								id,
								SmartCityDescriptor.getPosition(id),
								this.stationsEventIBPURI.get(id),
								2,
								2
								});
			}
			
		} else if (AbstractCVM.getThisJVMURI().equals(JVM2_URI)) {
			// CEPBus
			String uri_BMcepBusEmetteurIP = AbstractPort.generatePortURI();
			String uri_BMcepBusCorreIP = AbstractPort.generatePortURI();
			
			AbstractComponent.createComponent(
					CEPBus.class.getCanonicalName(), 
					new Object[] {
							uri_BMcepBusEmetteurIP,
							uri_BMcepBusCorreIP
					});
			
			// TrafficLight
			int corre_traffic_id = 0;
			
			AbstractComponent.createComponent(
					TrafficLightsSimulator.class.getCanonicalName(),
					new Object[]{this.stationsEventIBPURI,
								 this.trafficLightsIBPURI});
			
			// -------------------------------------------------------------------------
			// Circulation
			// -------------------------------------------------------------------------
			ArrayList<String> executorTrafficURIs = new ArrayList<String>();
			ArrayList<String> emitterTrafficURIs = new ArrayList<String>();
			
			emitterTrafficURIs.addAll(emitterSamuURIs);
			emitterTrafficURIs.addAll(emitterFireStationURIs);
			
			RuleBase ruleBaseTraffic = new RuleBase();
			C1 c1 = new C1();
			ruleBaseTraffic.addRule(c1);
			
			Iterator<IntersectionPosition> circulationIterator =
					SmartCityDescriptor.createTrafficLightPositionIterator();
			
			while (circulationIterator.hasNext()) {
				IntersectionPosition p = circulationIterator.next();
				executorTrafficURIs.add(p.toString());
				emitterTrafficURIs.add(p.toString());
				String notificationInboundPortURI = AbstractPort.generatePortURI();
				this.register(p.toString(), notificationInboundPortURI);
				
				AbstractComponent.createComponent(
						TrafficLight.class.getCanonicalName(),
						new Object[]{
								p.toString(),
								p,
								notificationInboundPortURI,
								SmartCityDescriptor.getActionInboundPortURI(p),
								uri_BMcepBusEmetteurIP
							});
				
				corre_traffic_id++;
			}
			
			for(int i = 0; i < corre_traffic_id; i++) {
				String trafficEventReceptionInboundPortURI = AbstractPort.generatePortURI();
				AbstractComponent.createComponent(
						CorrelateurTrafficLight.class.getCanonicalName(), 
						new Object[] {
								"corre_traffic_id" + i,
								uri_BMcepBusCorreIP,
								trafficEventReceptionInboundPortURI,
								executorTrafficURIs,
								emitterTrafficURIs,
								ruleBaseTraffic
						});
			}
			
			Iterator<IntersectionPosition> trafficLightsIterator =
					SmartCityDescriptor.createTrafficLightPositionIterator();
			while (trafficLightsIterator.hasNext()) {
				IntersectionPosition p = trafficLightsIterator.next();
				System.out.println(this.facadeNotificationInboundPortsURI.
							get(p.toString()));
				AbstractComponent.createComponent(
						TrafficLightProxy.class.getCanonicalName(),
						new Object[]{
								p,
								SmartCityDescriptor.getActionInboundPortURI(p),
								this.facadeNotificationInboundPortsURI.
									 							get(p.toString()),
								this.trafficLightsIBPURI.get(p)
								});
			}
			
		} else {
			System.out.println("Unknown JVM URI: " + 
									AbstractCVM.getThisJVMURI());
		}
		super.instantiateAndPublish();
	}

	/**
	 * Connecter les des bus de differents jvms
	 * */
	@Override
	public void interconnect() throws Exception 
	{
		super.interconnect();
	}
	
	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * return true if the asset has already a URI registered, false otherwise.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * post	true		// no postcondition.
	 * </pre>
	 *
	 * @param assetId	asset identifier as define the the smart city descriptor.
	 * @return			true if the asset has already a URI registered, false otherwise.
	 */
	protected boolean	registered(String assetId)
	{
		assert	assetId != null && !assetId.isEmpty();
		return this.facadeNotificationInboundPortsURI.containsKey(assetId);
	}

	/**
	 * register the URI if the notification inbound port used in the events
	 * emitter component associated with the asset identifier {@code assetId}.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	{@code assetId != null && !assetId.isEmpty()}
	 * pre	{@code !registered(assetId)}
	 * pre	{@code uri != null && !uri.isEmpty()}
	 * post	{@code registered(assetId)}
	 * </pre>
	 *
	 * @param assetId	asset identifier as define the the smart city descriptor.
	 * @param uri		URI of the notification inbound port of the corresponding events emitter component.
	 */
	protected void		register(String assetId, String uri)
	{
		assert	assetId != null && !assetId.isEmpty();
		assert	!this.registered(assetId);
		assert	uri != null && !uri.isEmpty();
		this.facadeNotificationInboundPortsURI.put(assetId, uri);
	}


	public static void main(String[] args) {
		try {
			// start time, in the logical time view; the choice is arbitrary
			simulatedStartTime = LocalTime.of(12, 0);
			// end time, in the logical time view; the chosen value must allow
			// the whole test scenario to be executed within the logical time
			// period between the start and the end times; the actual duration
			// of the program execution also depends upon the acceleration
			// factor defined in the class TimeManager
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			DistributedCVM dcvm = new DistributedCVM(args);
			// start the program execution which duration includes a simulation
			// start delay to allow for the interconnection of components and
			// then the duration of the simulation itself computed from the
			// start time, the end time and the acceleration factor
			dcvm.startStandardLifeCycle(
					START_DELAY + TimeManager.get().computeExecutionDuration());
			// delay after the execution during which the widows opened by
			// components remain visible
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
