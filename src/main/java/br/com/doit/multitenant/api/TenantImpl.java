package br.com.doit.multitenant.api;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class TenantImpl implements Tenant
{
	private final String databaseUrl;

	private final String identifier;

	public TenantImpl(String identifier, String databaseUrl)
	{
		super();

		this.identifier = identifier;
		this.databaseUrl = databaseUrl;
	}

	public String databaseUrl()
	{
		return databaseUrl;
	}

	public String identifier()
	{
		return identifier;
	}
}
