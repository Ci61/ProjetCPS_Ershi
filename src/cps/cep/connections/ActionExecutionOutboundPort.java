package cps.cep.connections;

import java.io.Serializable;

import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * The class <code>ActionExecutionOutboundPort</code> implements the outbound
 * port for the {@code ActionExecutionCI} interface.
 *
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
public class ActionExecutionOutboundPort 
extends AbstractOutboundPort 
implements ActionExecutionCI 
{
	private static final long serialVersionUID = 1L;

	public ActionExecutionOutboundPort(ComponentI owner) throws Exception {
		super(ActionExecutionCI.class, owner);
	}

	public ActionExecutionOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, ActionExecutionCI.class, owner);
	}

	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		return ((ActionExecutionCI)this.getConnector()).
			execute(a, params);
	}

}
