package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.fireStation.FireStationCorrelatorI;
import cps.cep.fireStation.evenements.FireStationAlarmeFeu;
import cps.cep.fireStation.regles.F1;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.FireStationCorrelator;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFire;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class F1Test {

	/*
	 * Alarme feu dans immeuble à la position (3.0, 0.5)
	 * */
	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));
		AbsolutePosition p = new AbsolutePosition(3, 0.5);
		FireStationAlarmeFeu af = new FireStationAlarmeFeu(p);
		af.putProperty("type", TypeOfFire.Building);
		assertEquals(TypeOfFire.Building, af.getPropertyValue("type"));
		assertEquals("AbsolutePosition[3.0, 0.5]", af.getPosition().toString());
		assertEquals(p, af.getPosition());
		
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(af);
		
		F1 f1 = new F1();
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(f1);
		
		FireStationCorrelatorI pc = new FireStationCorrelator(eventbase);
		assertTrue(rulebase.fireFirstOn(eventbase, pc));
		
	}

}
