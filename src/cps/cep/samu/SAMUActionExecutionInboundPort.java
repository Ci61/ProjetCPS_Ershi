package cps.cep.samu;

import java.io.Serializable;

import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.ActionExecutionI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/*
* 
*/

/**
 *
 * <p>
 * The class <code>SAMUActionExecutionInboundPort.java</code> implements the inbound
* port for the {@code ActionExecutionCI} interface.
 * </p>
 * 
 * <p>
 * Created on : 11/03/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
public class SAMUActionExecutionInboundPort 
extends AbstractInboundPort 
implements ActionExecutionCI 
{

	private static final long serialVersionUID = 1L;

	public SAMUActionExecutionInboundPort(ComponentI owner)
			throws Exception {
		super(ActionExecutionCI.class, owner);
		assert owner instanceof ActionExecutionI;
	}

	public SAMUActionExecutionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ActionExecutionCI.class, owner);
		assert owner instanceof ActionExecutionI;
	}

	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		return this.getOwner().handleRequest(
				b -> ((ActionExecutionI)b).execute(a, params));
	}

}
