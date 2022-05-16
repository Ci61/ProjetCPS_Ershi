package cps.cep.plugins;

import java.io.Serializable;

import cps.cep.interfaces.ActionExecutionCI;
import cps.cep.interfaces.ActionExecutionI;
import cps.cep.interfaces.ActionI;
import cps.cep.interfaces.ResponseI;
import cps.cep.plugins.connections.ActionExecutionIBPForPlugin;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;

/**
 * The class <code>PluginFacadeIn.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 09/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class PluginFacadeIn 
extends AbstractPlugin 
implements ActionExecutionI 
{

	private static final long serialVersionUID = 1L;
	/** action inbound port. */
	protected ActionExecutionIBPForPlugin aeIBP;

	@Override
	public void installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		assert owner instanceof ActionExecutionI;
	}
	
	@Override
	public void initialise() throws Exception {
		super.initialise();
		this.addOfferedInterface(ActionExecutionCI.class);
		this.aeIBP = new ActionExecutionIBPForPlugin(this.getOwner(),
													 this.getPluginURI());
		this.aeIBP.publishPort();
	}
	
	@Override
	public void uninstall() throws Exception {
		this.aeIBP.unpublishPort();
		this.aeIBP.destroyPort();
		this.removeOfferedInterface(ActionExecutionCI.class);
	}

	public ActionExecutionCI getActionExecution() throws Exception{
		return this.aeIBP;
	}

	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception {
		return ((ActionExecutionI)this.getOwner()).execute(a, params);
	}

}
