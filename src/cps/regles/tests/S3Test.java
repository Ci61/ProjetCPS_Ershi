package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.regles.S3;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.HealthCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class S3Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));

		SAMUAlarmeSante as = new SAMUAlarmeSante(new AbsolutePosition(1, 0.5));
		as.putProperty("type", TypeOfHealthAlarm.MEDICAL);
		assertEquals(TypeOfHealthAlarm.MEDICAL, as.getPropertyValue("type"));
		assertEquals("AbsolutePosition[1.0, 0.5]", as.getPropertyValue("position").toString());
		
		S3 s3 = new S3();
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(as);
		
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(s3);
		
		HealthCorrelatorI s = new HealthCorrelator();
		assertTrue(rulebase.fireAllOn(eventbase, s));
		
	}

}
