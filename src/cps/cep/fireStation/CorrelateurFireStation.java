package cps.cep.fireStation;

import java.io.Serializable;
import java.util.ArrayList;

import cps.cep.components.Correlateur;
import cps.cep.connections.ActionExecutionOutboundPort;
import cps.cep.interfaces.ActionExecutionCI;
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
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

/**
 * The class <code>CorrelateurPompier.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 10/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */

@OfferedInterfaces(offered = { EventReceptionCI.class })
@RequiredInterfaces(required = { EventEmissionCI.class, CEPBusManagementCI.class, ActionExecutionCI.class })
public class CorrelateurFireStation
extends Correlateur
implements FireStationCorrelatorI {

	/**disponibilite de grande echelle 				*/
	private boolean echelleDispo;
	/**disponibilite de camion stantard 			*/
	private boolean camionDispo;
	private String exe_uri;
	
	protected CorrelateurFireStation(
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
		
		this.echelleDispo = true;
		this.camionDispo = true;
		this.exe_uri = executorURIs.get(0);
	}

	@Override
	public EventBaseI getEb() throws Exception {
		return this.eventBase;
	}

	@Override
	public boolean estDansZone(AbsolutePosition p) throws Exception {
		for(String exeUri : executorURIs) {
			if (SmartCityDescriptor.dependsUpon(p, exeUri)) {
				this.exe_uri = exeUri;
				return true;
			}
		}
		return false;
	}

	@Override
	public void setEchelleDispo(boolean ed) throws Exception {
		this.echelleDispo = ed;
	}

	@Override
	public boolean estEchelleDispo() throws Exception {
		return echelleDispo;
	}

	@Override
	public void setCamionDispo(boolean ecd) throws Exception {
		this.camionDispo = ecd;
	}

	@Override
	public boolean estCamionDispo() throws Exception {
		return camionDispo;
	}

	/**
	 * Alarme feu en position pos delenche une premiere alarme sur immeurble 
	 * et on a besoin d'une grande echelle de la caserne
	*/
	@Override
	public void triggerPremiereAlarmeImmeuble(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		ActionsFireStation a = ActionsFireStation.PremiereAlarmeImmeuble;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerPremiereAlarmeImmeuble--");
		aeOBP.execute(a, new Serializable[] {
					pos, TypeOfFirefightingResource.HighLadderTruck});
	}

	/**
	 * Alarme feu en position pos delenche une premiere alarme sur maison 
	 * et on n'a besoin qu'un camion stantard
	*/
	@Override
	public void triggerPremiereAlarmeMaison(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		ActionsFireStation a = ActionsFireStation.PremiereAlarmeMaison;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerPremiereAlarmeMaison--");

		aeOBP.execute(a, new Serializable[] {
					pos, TypeOfFirefightingResource.StandardTruck});
	}

	@Override
	public void triggerGeneraleAlarme(ArrayList<EventI> matchedEvents) throws Exception {
		ActionsFireStation a = ActionsFireStation.AlarmeGenerale;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerGeneraleAlarme--");

		aeOBP.execute(a, new Serializable[] {
					null, TypeOfFirefightingResource.HighLadderTruck});
		
	}

	@Override
	public void triggerGeneraleAlarmePosition(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		ActionsFireStation a = ActionsFireStation.AlarmeGeneralePos;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);

		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerGeneraleAlarmePosition--");

		aeOBP.execute(a, new Serializable[] {
					pos, TypeOfFirefightingResource.HighLadderTruck});
	}

	@Override
	public void triggerSecondeAlarme(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		ActionsFireStation a = ActionsFireStation.SecondeAlarmePos;
		this.actionExecutionInboundPortURI = 
				this.cbcOBP.getExecutorInboundPortURI(this.exe_uri);
		
		CorrePlugin plugin = new CorrePlugin(this.actionExecutionInboundPortURI);
		plugin.setPluginURI(CORRE_PLUGIN_URI);
		this.installPlugin(plugin);
		ActionExecutionOutboundPort aeOBP = (ActionExecutionOutboundPort) plugin.getActionExecution();
		System.out.println("--triggerSecondeAlarme--");

		aeOBP.execute(a, new Serializable[] {
					pos, TypeOfFirefightingResource.StandardTruck});
		
	}

	@Override
	public AbsolutePosition plusProcheStation(AbsolutePosition p) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
