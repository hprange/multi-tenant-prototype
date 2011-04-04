// Generated by the Maven Archetype Plug-in
package br.com.doit.multitenant.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.doit.multitenant.api.Tenant;
import br.com.doit.multitenant.api.TenantImpl;
import br.com.doit.multitenant.components.Main;
import br.com.doit.multitenant.migration.MultiTenantMigrator;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOSession;
import com.webobjects.eoaccess.EOModel;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSMutableDictionary;

import er.extensions.appserver.ERXApplication;
import er.extensions.eof.ERXEC;
import er.extensions.eof.ERXModelGroup;
import er.extensions.eof.ERXObjectStoreCoordinator;
import er.extensions.foundation.ERXPatcher;
import er.extensions.foundation.ERXProperties;
import er.extensions.migration.ERXMigrator;

/**
 * @author <a href="mailto:hprange@gmail.com">Henrique Prange</a>
 */
public class Application extends ERXApplication
{
	public static Application application()
	{
		return (Application) WOApplication.application();
	}

	public static void main(String[] argv)
	{
		ERXApplication.main(argv, Application.class);
	}

	final int MAX_ENTRIES = 5;

	private final Map<Tenant, ERXModelGroup> modelGroupsForTenant = new LinkedHashMap<Tenant, ERXModelGroup>(MAX_ENTRIES + 1, .75F, true)
	{
		@Override
		public boolean removeEldestEntry(Map.Entry<Tenant, ERXModelGroup> eldest)
		{
			return size() > MAX_ENTRIES;
		}
	};

	private final Map<Tenant, EOObjectStoreCoordinator> objectStoreForTenant = new LinkedHashMap<Tenant, EOObjectStoreCoordinator>(MAX_ENTRIES + 1, .75F, true)
	{
		@Override
		public boolean removeEldestEntry(Map.Entry<Tenant, EOObjectStoreCoordinator> eldest)
		{
			return size() > MAX_ENTRIES;
		}
	};

	private final NSMutableDictionary<String, Tenant> tenants = new NSMutableDictionary<String, Tenant>();

	public Application()
	{
		super();

		NSLog.out.appendln("Welcome to " + name() + " !");

		ERXProperties.setStringForKey("DOitDerbyPlugIn", "dbConnectPluginGLOBAL");
		ERXProperties.setStringForKey("EOJDBCDerbyPrototypes", "dbEOPrototypesEntityGLOBAL");
		ERXProperties.setStringForKey("org.apache.derby.jdbc.EmbeddedDriver", "dbConnectDriverGLOBAL");

		for(int i = 0; i < 5; i++)
		{
			tenants().put("TENANT " + i, new TenantImpl("TENANT " + i, "jdbc:derby:tenant_" + i + ";create=true"));
		}

		// TOSCO, MAS E O UNICO JEITO DE FUNCIONAR O MIGRATIONS
		try
		{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		}
		catch(ClassNotFoundException exception)
		{
			exception.printStackTrace();
		}

		Connection connection = null;

		for(Tenant tenant : tenants.allValues())
		{
			try
			{
				connection = DriverManager.getConnection(tenant.databaseUrl());
			}
			catch(SQLException exception)
			{
				exception.printStackTrace();
			}
			finally
			{
				if(connection != null)
				{
					try
					{
						connection.close();
					}
					catch(SQLException exception)
					{
						exception.printStackTrace();
					}
				}
			}

			ERXProperties.setStringForKey(tenant.databaseUrl(), "dbConnectURLGLOBAL");

			ERXMigrator migrator = new MultiTenantMigrator("Sample", tenant);

			migrator.migrateToLatest();
		}
	}

	/**
	 * Determines the WOSession class to instantiate.
	 * 
	 * @see com.webobjects.appserver.WOApplication#_sessionClass()
	 */
	@Override
	protected Class<? extends WOSession> _sessionClass()
	{
		return Session.class;
	}

	/**
	 * Install patches including ensuring that Main is correctly resolved at
	 * runtime.
	 * 
	 * @see er.extensions.appserver.ERXApplication#installPatches()
	 */
	@Override
	public void installPatches()
	{
		super.installPatches();

		ERXPatcher.setClassForName(Main.class, "Main");
	}

	public EOEditingContext newEditingContextForTenant(Tenant tenant)
	{
		EOObjectStoreCoordinator objectStoreCoordinator = objectStoreForTenant.get(tenant);

		if(objectStoreCoordinator == null)
		{
			objectStoreCoordinator = new ERXObjectStoreCoordinator();

			EOModelGroup.setModelGroupForObjectStoreCoordinator(objectStoreCoordinator, newModelGroupForTenant(tenant));

			objectStoreForTenant.put(tenant, objectStoreCoordinator);
		}

		return ERXEC.newEditingContext(objectStoreCoordinator);
	}

	private EOModelGroup newModelGroupForTenant(Tenant tenant)
	{
		ERXModelGroup modelGroup = modelGroupsForTenant.get(tenant);

		if(modelGroup == null)
		{
			modelGroup = new ERXModelGroup();

			modelGroup.loadModelsFromLoadedBundles();

			NSArray<EOModel> models = modelGroup.models();

			for(EOModel model : models)
			{
				NSMutableDictionary<String, Object> connectionDictionary = model.connectionDictionary().mutableClone();

				connectionDictionary.takeValueForKey(tenant.databaseUrl(), "URL");

				model.setConnectionDictionary(connectionDictionary.immutableClone());
			}

			modelGroupsForTenant.put(tenant, modelGroup);
		}

		return modelGroup;
	}

	public NSMutableDictionary<String, Tenant> tenants()
	{
		return tenants;
	}
}
