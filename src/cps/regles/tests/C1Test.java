package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import cps.cep.trafficLight.TrafficLightCorrelatorI;
import cps.cep.trafficLight.evenements.TrafficLightDemande;
import cps.cep.trafficLight.evenements.TrafficLightDemandePriorite;
import cps.cep.trafficLight.evenements.TrafficLightvehiculePassage;
import cps.cep.trafficLight.regles.C1;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.RuleBase;
import cps.regles.bouchons.TrafficLightCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.IntersectionPosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfTrafficLightPriority;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class C1Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));
		IntersectionPosition intersection = new IntersectionPosition(3, 4);
		TrafficLightDemandePriorite demandePriorite = new TrafficLightDemandePriorite();
		TrafficLightvehiculePassage vehiculePassage = new TrafficLightvehiculePassage();
		ArrayList<EventI> correlatedEvents = new ArrayList<EventI>();
		
		demandePriorite.putProperty("priority", TypeOfTrafficLightPriority.EMERGENCY);
		vehiculePassage.putProperty("vehicleId", "ambulance");
		vehiculePassage.putProperty("direction", intersection);
		vehiculePassage.putProperty("intersection", intersection);
		correlatedEvents.add(demandePriorite);
		correlatedEvents.add(vehiculePassage);
		
		EventI tld = new TrafficLightDemande(correlatedEvents);
		assertEquals(TypeOfTrafficLightPriority.EMERGENCY, tld.getPropertyValue("priority"));
		assertEquals("ambulance", tld.getPropertyValue("vehicleId"));
		assertEquals("IntersectionPosition[3.0, 4.0]", tld.getPropertyValue("direction").toString());
		
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(tld);
		
		C1 c1 = new C1();
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(c1);
		
		TrafficLightCorrelatorI cc = new TrafficLightCorrelator();
		assertTrue(rulebase.fireFirstOn(eventbase, cc));
	}

}
