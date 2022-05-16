package cps.cep.connector;

import java.io.Serializable;

import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>EXCOActionExeConnector.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class ActionExecutionConnector 
extends AbstractConnector 
implements ActionExecutionCI 
{
	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		return ((ActionExecutionCI) this.offering).execute(a, params);
	}

}
