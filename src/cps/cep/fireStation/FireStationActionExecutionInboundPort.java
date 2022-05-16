package cps.cep.fireStation;

import java.io.Serializable;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import cps.cep.interfaces.ActionExecutionCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * The class <code>SAMUActionConnector.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class FireStationActionExecutionInboundPort 
extends AbstractInboundPort 
implements ActionExecutionCI 
{
	private static final long serialVersionUID = 1L;

	public FireStationActionExecutionInboundPort(ComponentI owner)
			throws Exception {
		super(ActionExecutionCI.class, owner);
		assert owner instanceof FireStation;
	}

	public FireStationActionExecutionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ActionExecutionCI.class, owner);
		assert owner instanceof FireStation;
	}

	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		return this.getOwner().handleRequest(
				s -> ((FireStation)s).execute(a, params));
	}

}
