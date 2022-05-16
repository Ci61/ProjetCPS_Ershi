package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationAlarmeFeu;
import cps.cep.fireStation.regles.F2;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.FireStationCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class F2Test {

	/*
	 * Alarme feu dans maison à la position (3.0, 2.5)
	 * */
	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));
		AbsolutePosition p = new AbsolutePosition(3.0, 2.5);
		FireStationAlarmeFeu af = new FireStationAlarmeFeu(p);
		af.putProperty("type", TypeOfFire.House);
		assertEquals(TypeOfFire.House, af.getPropertyValue("type"));
		assertEquals("AbsolutePosition[3.0, 2.5]", af.getPosition().toString());
		assertEquals(p, af.getPosition());
		
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(af);
		
		F2 f2 = new F2();
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(f2);
		
		FireStationCorrelatorI pc = new FireStationCorrelator(eventbase);
		assertTrue(rulebase.fireFirstOn(eventbase, pc));
	}

}
