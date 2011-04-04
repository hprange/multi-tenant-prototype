package br.com.doit.multitenant.model;

import br.com.doit.multitenant.api.Tenant;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public enum DefaultTenant implements Tenant
{
	TENANT_A("jdbc:derby:tenant_1;create=true"),
	TENANT_B("jdbc:derby:tenant_2;create=true");

	private final String databaseUrl;

	DefaultTenant(String databaseUrl)
	{
		this.databaseUrl = databaseUrl;
	}

	public String databaseUrl()
	{
		return databaseUrl;
	}

	public String identifier()
	{
		return toString();
	}
}
