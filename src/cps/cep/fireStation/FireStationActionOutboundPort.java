package cps.cep.fireStation;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

/**
 * The class <code>SAMUActionOutboundPort.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class FireStationActionOutboundPort 
extends AbstractOutboundPort 
implements FireStationActionCI {

    private static final long serialVersionUID = 1L;

    public FireStationActionOutboundPort(ComponentI owner)
            throws Exception {
        super(FireStationActionCI.class, owner);
    }

    public FireStationActionOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, FireStationActionCI.class, owner);
	}

    @Override
    public void triggerFirstAlarm(AbsolutePosition p, TypeOfFirefightingResource r) throws Exception {
        ((FireStationActionCI)this.getConnector()).triggerFirstAlarm(p, r);
    }

    @Override
    public void triggerSecondAlarm(AbsolutePosition p) throws Exception {
       ((FireStationActionCI)this.getConnector()).triggerSecondAlarm(p);
        
    }

    @Override
    public void triggerGeneralAlarm(AbsolutePosition p) throws Exception {
       ((FireStationActionCI)this.getConnector()).triggerGeneralAlarm(p);
        
    }
    
}
