package com.webobjects.jdbcadaptor;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAttribute;
import com.webobjects.eoaccess.EOSQLExpression;
import com.webobjects.eoaccess.synchronization.EOSchemaGenerationOptions;
import com.webobjects.eoaccess.synchronization.EOSchemaSynchronizationFactory;
import com.webobjects.foundation.NSArray;

public class DOitDerbyPlugIn extends DerbyPlugIn
{
	public static class DOitDerbySynchronizationFactory extends DerbySynchronizationFactory
	{
		public DOitDerbySynchronizationFactory( final EOAdaptor adaptor )
		{
			super( adaptor );
		}

		@Override
		public NSArray<EOSQLExpression> statementsToInsertColumnForAttribute( final EOAttribute attribute, final EOSchemaGenerationOptions options )
		{
			String columnCreationClause = _columnCreationClauseForAttribute( attribute );

			String expression = "alter table " + formatTableName( attribute.entity().externalName() ) + " add column " + columnCreationClause;

			// Removing the not null constraint because Derby requires a set
			// default parameter and we can't provide
			expression = expression.toLowerCase();
			expression = expression.replace( " not null", "" );

			return new NSArray( _expressionForString( expression ) );
		}
	}

	public DOitDerbyPlugIn( final JDBCAdaptor adaptor )
	{
		super( adaptor );
	}

	@Override
	public EOSchemaSynchronizationFactory createSchemaSynchronizationFactory()
	{
		return new DOitDerbySynchronizationFactory( adaptor() );
	}
}
