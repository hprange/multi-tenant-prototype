// Generated by the Maven Archetype Plug-in
package br.com.doit.multitenant.app;

import br.com.doit.multitenant.components.Main;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WORequest;

import er.extensions.appserver.ERXDirectAction;


/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class DirectAction extends ERXDirectAction
{
	public DirectAction(WORequest request)
	{
		super(request);
	}

	@Override
	public WOActionResults defaultAction()
	{
		return pageWithName(Main.class.getName());
	}
}
