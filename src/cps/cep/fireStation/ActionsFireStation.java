package cps.cep.fireStation;

import cps.cep.interfaces.ActionI;

/**
 * The class <code>ActionsFireStation.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public enum ActionsFireStation implements ActionI {
	/** grande echelle diponible pour une premiere alarme sur immeuble 		*/
	PremiereAlarmeImmeuble,
	/** camion stantard diponible pour une premiere alarme sur maison 		*/
	PremiereAlarmeMaison,
	/** alarme generale feu 												*/
	AlarmeGenerale,
	/** alarme generale en position specifique						 		*/
	AlarmeGeneralePos,
	/** seconde alarme en position specifique								*/
	SecondeAlarmePos
	
}
