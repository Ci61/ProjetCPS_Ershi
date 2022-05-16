package cps.cep.interfaces;

import java.io.Serializable;

/**
 * The class <code>ActionExecutionI.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 04/04/2022</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public interface ActionExecutionI {
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception;
}
