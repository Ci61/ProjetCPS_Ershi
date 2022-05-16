package cps.cep.samu;

import java.io.Serializable;
import java.util.ArrayList;

import cps.cep.components.Correlateur;
import cps.cep.connections.ActionExecutionOutboundPort;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventReceptionCI;
import cps.cep.plugins.CorrePlugin;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.RuleBase;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.smartcity.SmartCityDescriptor;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfSAMURessources;

/**
 * The class <code>CorrelateurSAMU.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 10/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
@OfferedInterfaces(offered = { EventReceptionCI.class })
@RequiredInterfaces(required = { EventEmissionCI.class, CEPBusManagementCI.class })
public class CorrelateurSAMU 
extends Correlateur 
implements HealthCorrelatorI
{	
	private boolean medecinDispo;
	private boolean ambulanceDispo;
	private String exe_uri;
	
	protected CorrelateurSAMU(
			String corre_id, 
			String cepBusManageInboundPortURI, 
			String eventReceptionInboundPortURI,
			ArrayList<String> executorURIs,
			ArrayList<String> emitterURIs,
			RuleBase ruleBase) throws Exception 
	{
		super(corre_id,
				cepBusManageInboundPortURI, 
				eventReceptionInboundPortURI,
				executorURIs,
				emitterURIs,
				ruleBase);
		medecinDispo = true;
		ambulanceDispo = true;
		this.exe_uri = executorURIs.get(0);
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}

	@Override
	public boolean estDansCentre(AbsolutePosition position) throws Exception {
		for(String exeUri : executorURIs) {
			if (SmartCityDescriptor.dependsUpon(position, exeUri)) {
				this.exe_uri = exeUri;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setAmbulDispo(boolean amb) throws Exception {
		this.ambulanceDispo = amb;
	}

	@Override
	public boolean estAmbulDispo() throws Exception {
		return ambulanceDispo;
	}

	@Override
	public void setMedecinAvailable(boolean med) throws Exception {
		this.medecinDispo = med;		
	}

	@Override
	public boolean estMedecinDispo() throws Exception {
		return medecinDispo;
	}	

	@Override
	public void triggerAmbulance(AbsolutePosition pos) throws Exception {
		ActionsSAMU a = ActionsSAMU.InterventionAmbulance;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(this.exe_uri + CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerAmbulance--");

		aeOBP.execute(a, new Serializable[] {
				pos, null, TypeOfSAMURessources.AMBULANCE});
	}
	
	/**
	 * Appel du medecin apres chute d'une personne consciente
	 */
	@Override
	public void triggerMedecin(AbsolutePosition pos) throws Exception {
		ActionsSAMU a = ActionsSAMU.InterventionMedecin;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(this.exe_uri + CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerMedecin--");

		aeOBP.execute(a, new Serializable[] {
				pos, null, TypeOfSAMURessources.MEDIC});
	}
	
	/**
	 * Signalement manuel pour une personne et puis medecin l'appel
	 */
	@Override
	public void triggerMedecin(AbsolutePosition pos, String personId) throws Exception{
		ActionsSAMU a = ActionsSAMU.InterventionMedecin;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(this.exe_uri + CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerMedecin--");

		aeOBP.execute(a, new Serializable[] {
				pos, personId, TypeOfSAMURessources.MEDIC});
	}

	@Override
	public void triggerMedicCall(AbsolutePosition pos, String personId) throws Exception {
		ActionsSAMU a = ActionsSAMU.InterventionTeleMedecin;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(this.exe_uri + CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerMedicCall--");

		aeOBP.execute(a, new Serializable[] {
				pos, personId, TypeOfSAMURessources.TELEMEDIC});
	}

	@Override
	public void triggerDemande(ArrayList<EventI> matchedEvents) throws Exception {
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
	}

	@Override
	public void triggerPasserSamuSuiv(EventI e) throws Exception {
		System.out.println("Passer prochain Samu");
	}

	@Override
	public EventBaseI getEb() throws Exception {
		return this.eventBase;
	}

}
