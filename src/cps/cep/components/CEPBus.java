package cps.cep.components;

import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cps.cep.connections.BMCEPBusInboundPort;
import cps.cep.connections.EECEPBusCorrInboundPort;
import cps.cep.connections.EventReceptionOutboundPort;
import cps.cep.connector.EventReceptionConnector;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.EventEmissionI;
import cps.cep.interfaces.EventReceptionCI;
import cps.cep.plugins.BusPlugin;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * The class <code>CEPBus.java</code>
 *
 * <p>
 * <strong>Le composant bus. Le bus d'evenements va servir a la transmission
 * des evenements depuis les emetteurs jusqu'aux correlateurs et entre 
 * correlateurs. </strong>
 * </p>
 * 
 * <p>
 * Created on : 10/03/2022
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
@RequiredInterfaces(required = { EventReceptionCI.class })
@OfferedInterfaces(offered = { CEPBusManagementCI.class })
public class CEPBus 
extends AbstractComponent
implements EventEmissionI {
	
	protected final static String	BUS_PLUGIN_URI = "bus-plugin-uri";

	protected BMCEPBusInboundPort bmCepBusEmmIBP;
	protected BMCEPBusInboundPort bmCepBusCorrIBP;
	protected EECEPBusCorrInboundPort eeCepBusCorrIBP;
	
	protected String uri_EEcepBusEmetteurIP;

	// liste du uri des emetteurs
	// protected Vector<String> uriEmetteurs;
	// associations entre executeurs et son inboundPort
	protected ConcurrentHashMap<String, String> executeurs;
	// associations entre correlateurs et son inboundPort
	protected ConcurrentHashMap<String, EventReceptionOutboundPort> correlateurs;
	// abonne correlateurs aux emetteurs ou correlateurs
	protected ConcurrentHashMap<String, Vector<String>> abonneCorrelateurs;
	
	public static final int N = 3;
	public static final int CAPACITY = 5;
	protected ThreadPoolExecutor executor_ee = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());
	protected ThreadPoolExecutor exec_subsribe = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());

	protected CEPBus(
			String uri_BMcepBusEmetteurIP,
			String uri_BMcepBusCorreIP) throws Exception {
		super(2, 0);
		executeurs = new ConcurrentHashMap<>();
		correlateurs = new ConcurrentHashMap<>();
		abonneCorrelateurs = new ConcurrentHashMap<>();
		
		this.bmCepBusEmmIBP = new BMCEPBusInboundPort(uri_BMcepBusEmetteurIP,this);
		this.bmCepBusCorrIBP = new BMCEPBusInboundPort(uri_BMcepBusCorreIP,this);
		this.eeCepBusCorrIBP = new EECEPBusCorrInboundPort(this);
		
		this.bmCepBusEmmIBP.publishPort();
		this.bmCepBusCorrIBP.publishPort();
		this.eeCepBusCorrIBP.publishPort();
		
		uri_EEcepBusEmetteurIP = AbstractPort.generatePortURI();
		BusPlugin plugin = new BusPlugin(uri_EEcepBusEmetteurIP);
		plugin.setPluginURI(BUS_PLUGIN_URI);
		this.installPlugin(plugin);
	}
	
	
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}

	@Override
	public synchronized void finalise() throws Exception {
		super.finalise();
		for(EventReceptionOutboundPort port : correlateurs.values()) {
			port.doDisconnection();
		}
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.bmCepBusEmmIBP.unpublishPort();
			this.bmCepBusCorrIBP.unpublishPort();
			this.eeCepBusCorrIBP.unpublishPort();
			for(EventReceptionOutboundPort port : correlateurs.values()) {
				port.unpublishPort();
			}
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * enregistre l'emetteur d'evenements
	 * 
	 * @param uri URI de l'emetteur
	 * @return URI de son port entrant offrant l'interface EventEmissionCI
	 *         (EEIP_URI)
	 */
	public String registerEmitter(String uri) throws Exception {
		abonneCorrelateurs.put(uri, new Vector<String>());
		return uri_EEcepBusEmetteurIP;
	}

	/**
	 * supprime l'emetteur d'evenements
	 * 
	 * @param uri URI de l'emetteur
	 */
	public void unregisterEmitter(String uri) throws Exception {
		synchronized (this) {
			abonneCorrelateurs.remove(uri);
		}
	}

	/**
	 * enregistre le correlator avec l'URI de son port entrant
	 * 
	 * @param uri            URI du correlator
	 * @param inboundPortURI port entrant de ce correlator
	 * @return URI de son port entrant offrant l'interface EventEmissionCI
	 *         (EEIP_URI)
	 */
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		EventReceptionOutboundPort erop;
		try {
			erop = new EventReceptionOutboundPort(this);
			erop.publishPort();
			this.doPortConnection(erop.getPortURI(), inboundPortURI, EventReceptionConnector.class.getCanonicalName());
			correlateurs.put(uri, erop);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.eeCepBusCorrIBP.getPortURI();
	}

	/**
	 * supprime le correlator donne
	 * 
	 * @param uri URI du correlator
	 */
	public void unregisterCorrelator(String uri) throws Exception {
		correlateurs.remove(uri);
	}

	/**
	 * enregistrer l'association de uri de l'executeur et l'uri de port entrant de
	 * cet executeur
	 * 
	 * @param uri            URI de l'executeur
	 * @param inboundPortURI port entrant de l'executeur
	 */
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		synchronized (this) {
			executeurs.put(uri, inboundPortURI);
		}
	}

	/**
	 * obtenir l'uri de port entrant de l'executeur
	 * 
	 * @param uri URI de l'executeur
	 * @return inboundPortURI port entrant de cet executeur
	 */
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return executeurs.get(uri);
	}

	/**
	 * supprime l'executeur donne
	 * 
	 * @param uri URI de l'executeur
	 */
	public void unregisterExecutor(String uri) throws Exception {
		executeurs.remove(uri);
	}

	/**
	 * abonne emitteur aux correlateurs
	 * 
	 * @param subscriberURI l'uri de correlateur a  abonner
	 * @param emitterURI    l'uri d'emetteur ou de correlateur
	 */
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				if (abonneCorrelateurs.containsKey(emitterURI)) {
					abonneCorrelateurs.get(emitterURI).add(subscriberURI);
				} else {
					Vector<String> abonne = new Vector<String>();
					abonne.add(subscriberURI);
					abonneCorrelateurs.put(emitterURI, abonne);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		exec_subsribe.submit(RegisterTask);
		
	}

	/**
	 * desabonne le correlateur aux evenements emis par l'emetteur ou le correlateur
	 * 
	 * @param subscriberURI l'uri de correlateur a  desabonner
	 * @param emitterURI    emitterURI l'uri d'emetteur ou de correlateur
	 */
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		if (abonneCorrelateurs.containsKey(subscriberURI)) {
			abonneCorrelateurs.get(subscriberURI).remove(emitterURI);
		}
	}

	/**
	 * emettre un evenement a  propager vers les destinataires abonnes aux evenements
	 * en provence de l'emetteur
	 * 
	 * @param emitterURI l'uri d'emetteur
	 * @param event      l'evenement a  propager
	 */
	public void correReceiveEvent(String emitterURI, EventI event) throws Exception {
		if(!abonneCorrelateurs.get(emitterURI).isEmpty()) {
			for (String uri : abonneCorrelateurs.get(emitterURI)) {
				EventReceptionOutboundPort erop = correlateurs.get(uri);
				erop.receiveEvent(uri, event);
			}
		}
	}

	/**
	 * envoyer un tableau d'evenements a  propager vers les destinataires abonnes aux
	 * evenements en provence de l'emetteur
	 * 
	 * @param emitterURI l'uri d'emetteur
	 * @param events     l'evenement a  propager
	 */
	public void correReceiveEvents(String emitterURI, EventI[] events) throws Exception {
		if(!abonneCorrelateurs.get(emitterURI).isEmpty()) {
			for (String uri : abonneCorrelateurs.get(emitterURI)) {
				EventReceptionOutboundPort erop = correlateurs.get(uri);
				erop.receiveEvents(uri, events);
			}
		}
		
	}
	
	/** methodes offertes */
	
	/**
	 * recevoir un evenement et l'envoie aux correlateurs
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				this.correReceiveEvent(emitterURI, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		executor_ee.submit(RegisterTask);
	}

	/**
	 * recevoir les evenements et les envoie aux correlateurs
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				this.correReceiveEvents(emitterURI, events);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		executor_ee.submit(RegisterTask);
	}
	
}
