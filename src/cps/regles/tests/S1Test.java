package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.regles.S1;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.HealthCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class S1Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));

		SAMUAlarmeSante as = new SAMUAlarmeSante(new AbsolutePosition(2.5, 2));
		as.putProperty("type", TypeOfHealthAlarm.EMERGENCY);
		assertEquals(TypeOfHealthAlarm.EMERGENCY, as.getPropertyValue("type"));
		assertEquals("AbsolutePosition[2.5, 2.0]", as.getPropertyValue("position").toString());
		
		S1 s1 = new S1();
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(as);
		
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(s1);
		
		HealthCorrelatorI s = new HealthCorrelator();
		assertTrue(rulebase.fireAllOn(eventbase, s));
		
	}

}
