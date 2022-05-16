package cps.cep.plugins.connections;

import java.io.Serializable;

import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.ActionExecutionI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import cps.cep.plugins.PluginFacadeIn;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * The class <code>ActionExecutionIBPForPlugin.java</code> serves to facilitate the re-integration of code
 * It replaces the old inboundPort of executor. 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 04/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class ActionExecutionIBPForPlugin 
extends	AbstractInboundPort
implements ActionExecutionCI 
{

    private static final long serialVersionUID = 1L;

	public	ActionExecutionIBPForPlugin(
		ComponentI owner,
		String pluginURI
		) throws Exception
	{
		super(ActionExecutionCI.class, owner, pluginURI, null);
		assert	owner instanceof ActionExecutionI;
	}

	public	ActionExecutionIBPForPlugin(
		String uri,
		ComponentI owner,
		String pluginURI
		) throws Exception {
		super(uri, ActionExecutionCI.class, owner, pluginURI, null);
		assert	owner instanceof ActionExecutionI;
	}

    @Override
    public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
        return this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<ResponseI>(this.getPluginURI()) {
				@Override
				public ResponseI call() throws Exception {
					return ((PluginFacadeIn)this.getServiceProviderReference()).
                        execute(a, params);
				}
			});
    }
    
}
