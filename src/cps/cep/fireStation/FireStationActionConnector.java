package cps.cep.fireStation;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.FireStationActionCI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfFirefightingResource;

/**
 * The class <code>SAMUActionConnector.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 11/03/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class FireStationActionConnector
extends AbstractConnector
implements FireStationActionCI  {

    @Override
    public void triggerFirstAlarm(AbsolutePosition p, TypeOfFirefightingResource r) throws Exception {
        ((FireStationActionCI)this.offering).triggerFirstAlarm(p, r);
    }

    @Override
    public void triggerSecondAlarm(AbsolutePosition p) throws Exception {
         ((FireStationActionCI)this.offering).triggerSecondAlarm(p);
    }

    @Override
    public void triggerGeneralAlarm(AbsolutePosition p) throws Exception {
         ((FireStationActionCI)this.offering).triggerGeneralAlarm(p);
    }
    
}
