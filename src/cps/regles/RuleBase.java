package cps.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;

public class RuleBase {
	
	public ArrayList<RuleI> rules;
	
	public RuleBase() {
		this.rules = new ArrayList<RuleI>();
	}

	public void addRule(RuleI r) {
		this.rules.add(r);
	}
	
	/**
	 * 
	 * @param eb la base des evenemetns
     * @param c le correlateur
     * @return la premiere regle qui peut etre activee
	 */
//	public boolean fireFirstOn(EventBaseI eb, CorrelatorStateI c) {
//		ArrayList<EventI> matchedEvents;
//		for (RuleI r : rules) {
//			matchedEvents = r.match(eb);
//			if(matchedEvents != null && 
//			   r.correlate(matchedEvents) && 
//			   r.filter(matchedEvents, c)) 
//			{
//				r.act(matchedEvents, c);
//				r.update(matchedEvents, eb);
//				return true;
//			}
//		}
//		return false;
//	}
	
	public boolean fireFirstOn(EventBaseI eb, CorrelatorStateI c) {
		ArrayList<EventI> matchedEvents;
		for (RuleI r : rules) {
			while((matchedEvents = r.match(eb)) != null) {
				if(
				   r.correlate(matchedEvents) && 
				   r.filter(matchedEvents, c)) 
				{
					r.act(matchedEvents, c);
					r.update(matchedEvents, eb);
					return true;
				}
				
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param eb la base des evenemetns
     * @param c le correlateur
     * @return toutes les regles qui peuvent etre activee
	 */
	public boolean fireAllOn(EventBaseI eb, CorrelatorStateI c) {
		EventBaseI tmp = eb;
		boolean flag = true;
		while(flag) {
			if(!fireFirstOn(eb, c)) {
				flag = false;
				break;
			}
			if(tmp.equals(eb)) {
				break;
			}
		}
		return flag;
	}
}
