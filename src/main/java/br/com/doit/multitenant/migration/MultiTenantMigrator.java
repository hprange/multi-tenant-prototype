package br.com.doit.multitenant.migration;

import br.com.doit.multitenant.api.Tenant;
import br.com.doit.multitenant.app.Application;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrator;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class MultiTenantMigrator extends ERXMigrator
{
	private final Tenant tenant;

	public MultiTenantMigrator(String lockOwnerName, Tenant tenant)
	{
		super(lockOwnerName);

		this.tenant = tenant;
	}

	@Override
	protected EOEditingContext newEditingContext()
	{
		return Application.application().newEditingContextForTenant(tenant);
	}
}
