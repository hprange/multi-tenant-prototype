package br.com.doit.multitenant.api;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public interface Tenant
{
	public String databaseUrl();

	public String identifier();
}
