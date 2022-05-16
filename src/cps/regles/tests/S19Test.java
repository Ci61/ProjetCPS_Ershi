package cps.regles.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUMedicsDispos;
import cps.cep.samu.regles.S19;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.regles.RuleBase;
import cps.regles.bouchons.HealthCorrelator;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

class S19Test {

	@Test
	void test() {
		new TimeManager(System.currentTimeMillis() + 10000L, LocalTime.of(12, 0), LocalTime.of(12, 0).plusMinutes(30));

		SAMUMedicsDispos dispo = new SAMUMedicsDispos();
		
		S19 s19 = new S19();
		EventBaseI eventbase = new EventBase();
		eventbase.addEvent(dispo);
		
		RuleBase rulebase = new RuleBase();
		rulebase.addRule(s19);
		
		HealthCorrelatorI s = new HealthCorrelator();
		assertTrue(rulebase.fireAllOn(eventbase, s));
		
	}

}
