package br.com.doit.multitenant.migrations;

import com.webobjects.eocontrol.EOEditingContext;

import er.extensions.migration.ERXMigrationDatabase;
import er.extensions.migration.ERXMigrationTable;

public class Sample0 extends ERXMigrationDatabase.Migration
{
	@Override
	public void downgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable
	{
		// DO NOTHING
	}

	@Override
	public void upgrade(EOEditingContext editingContext, ERXMigrationDatabase database) throws Throwable
	{
		ERXMigrationTable userTable = database.newTableNamed("users");

		userTable.newIntegerColumn("id", false);
		userTable.newStringColumn("login", 255, true);
		userTable.create();
		userTable.setPrimaryKey("id");
	}
}