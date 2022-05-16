package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationAlarmeFeu;
import cps.cep.fireStation.regles.F3;
import cps.cep.fireStation.regles.F5;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.FireStationCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class F3F5Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));
		EventBaseI eventbase = new EventBase();
		RuleBase rulebase = new RuleBase();
		FireStationCorrelatorI caserne = new FireStationCorrelator(eventbase);
		
		AbsolutePosition p = new AbsolutePosition(1.0, 1.5);
		FireStationAlarmeFeu af = new FireStationAlarmeFeu(p);
		af.putProperty("type", TypeOfFire.Building);
		
		assertEquals(TypeOfFire.Building, af.getPropertyValue("type"));
		assertEquals("AbsolutePosition[1.0, 1.5]", af.getPosition().toString());
		assertEquals(p, af.getPosition());
		
		eventbase.addEvent(af);
		F3 f3 = new F3();
		rulebase.addRule(f3);
		
		F5 f5 = new F5();
		rulebase.addRule(f5);
		assertTrue(rulebase.fireAllOn(eventbase, caserne));
	}

}
