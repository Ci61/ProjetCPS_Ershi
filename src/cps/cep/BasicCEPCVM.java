package cps.cep;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;

import cps.cep.components.CEPBus;
import cps.cep.fireStation.CorrelateurFireStation;
import cps.cep.fireStation.FireStation;
import cps.cep.fireStation.regles.F1;
import cps.cep.fireStation.regles.F2;
import cps.cep.fireStation.regles.F3;
import cps.cep.fireStation.regles.F5;
import cps.cep.samu.CorrelateurSAMU;
import cps.cep.samu.SAMU;
import cps.cep.samu.regles.*;
import cps.cep.trafficLight.CorrelateurTrafficLight;
import cps.cep.trafficLight.TrafficLight;
import cps.cep.trafficLight.regles.C1;
import cps.regles.RuleBase;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.cps.smartcity.AbstractSmartCityCVM;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

/**
 * The class <code>BasicCEPCVM.java</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : 21/02/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
public class BasicCEPCVM 
extends		AbstractSmartCityCVM
{

	public BasicCEPCVM() throws Exception {
		super();
	
	}

	@Override
	public void deploy() throws Exception {
		
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
		int corre_traffic_id = 0;
		
		// -------------------------------------------------------------------------
		// SAMU
		// -------------------------------------------------------------------------

		ArrayList<String> executorSamuURIs = new ArrayList<String>();
		ArrayList<String> emitterSamuURIs = new ArrayList<String>();
		
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
		ArrayList<String> executorFireStationURIs = new ArrayList<String>();
		ArrayList<String> emitterFireStationURIs = new ArrayList<String>();
		
		RuleBase ruleBaseFireStation = new RuleBase();
		F1 f1 = new F1();
		F2 f2 = new F2();
		F3 f3 = new F3();
		F5 f5 = new F5();
		ruleBaseFireStation.addRule(f1);
		ruleBaseFireStation.addRule(f2);
		ruleBaseFireStation.addRule(f3);
		ruleBaseFireStation.addRule(f5);
		
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
		
		Iterator<IntersectionPosition> trafficLightsIterator =
				SmartCityDescriptor.createTrafficLightPositionIterator();
		
		while (trafficLightsIterator.hasNext()) {
			IntersectionPosition p = trafficLightsIterator.next();
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
		
		super.deploy();
	}

	public static void main(String[] args) {
		// mv du composant
		try {
			// start time, in the logical time view; the choice is arbitrary
			simulatedStartTime = LocalTime.of(12, 0);
			// end time, in the logical time view; the chosen value must allow
			// the whole test scenario to be executed within the logical time
			// period between the start and the end times; the actual duration
			// of the program execution also depends upon the acceleration
			// factor defined in the class TimeManager
			simulatedEndTime = LocalTime.of(12, 0).plusMinutes(30);
			START_DELAY = 10000L;
			BasicCEPCVM c = new BasicCEPCVM();
			// start the program execution which duration includes a simulation
			// start delay to allow for the interconnection of components and
			// then the duration of the simulation itself computed from the
			// start time, the end time and the acceleration factor
			c.startStandardLifeCycle(
					START_DELAY + TimeManager.get().computeExecutionDuration());
			// delay after the execution during which the widows opened by
			// components remain visible
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
