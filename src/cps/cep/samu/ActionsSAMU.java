package cps.cep.samu;

import cps.cep.interfaces.ActionI;

/**
 * The class <code>ActionsSAMU.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public enum ActionsSAMU implements ActionI{

	/**	intervention d'ambulance					*/
	InterventionAmbulance,
	/**	intervention de medecin					*/
	InterventionMedecin,
	/**	un appel du medecin a personne specifique	*/
	InterventionTeleMedecin
}
