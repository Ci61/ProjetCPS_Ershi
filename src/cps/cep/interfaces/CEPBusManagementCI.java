package cps.cep.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface CEPBusManagementCI extends RequiredCI, OfferedCI {
	public String registerEmitter(String uri) throws Exception ;
	public void unregisterEmitter(String uri) throws Exception ;
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception ;
	public void unregisterCorrelator(String uri) throws Exception;
	public void registerExecutor(String uri, String inboundPortURI) throws Exception;
	public String getExecutorInboundPortURI(String uri) throws Exception;
	public void unregisterExecutor(String uri) throws Exception;
	public void subscribe(String subscriberURI, String emitterURI) throws Exception;
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception;
}
