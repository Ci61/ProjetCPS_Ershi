package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.evenements.SAMUSignalOk;
import cps.cep.samu.regles.S7;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.HealthCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class S7Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));

		SAMUAlarmeSante as = new SAMUAlarmeSante(new AbsolutePosition(2.5, 2));
		SAMUSignalOk s = new SAMUSignalOk();
		as.putProperty("type", TypeOfHealthAlarm.TRACKING);
		as.putProperty("personId", "001");
		s.putProperty("personId", "001");
		assertEquals(TypeOfHealthAlarm.TRACKING, as.getPropertyValue("type"));
		assertEquals("001", as.getPropertyValue("personId"));
		
		S7 s7 = new S7();
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(as);
		eventbase.addEvent(s);
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(s7);
		HealthCorrelatorI hc = new HealthCorrelator();
		assertTrue(rulebase.fireFirstOn(eventbase, hc));
	}

}
