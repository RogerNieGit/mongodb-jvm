package com.mongodb.rhino;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BSONObject;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Direct conversion between native Rhino objects and BSON.
 * <p>
 * This class can be used directly in Rhino.
 * 
 * @author Tal Liron
 */
public class BSON
{
	//
	// Static operations
	//

	/**
	 * Convert from native Rhino to BSON.
	 * 
	 * @param object
	 *        A Rhino native object
	 * @return A BSON object
	 */
	public static DBObject to( ScriptableObject object )
	{
		BasicDBObject bson = new BasicDBObject();

		Object[] ids = object.getAllIds();
		for( Object id : ids )
		{
			String key = id.toString();
			Object value = forBson( ScriptableObject.getProperty( object, key ), key );
			bson.put( key, value );
		}

		return bson;
	}

	/**
	 * Convert from BSON to a Rhino-compatible object.
	 * 
	 * @param bson
	 *        A BSON object
	 * @return A Rhino-compatible object
	 */
	public static Object from( BSONObject bson )
	{
		return forRhino( bson );
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private

	/**
	 * If necessary, convert from native Rhino to a type supported by BSON.
	 * 
	 * @param object
	 *        An object
	 * @param key
	 *        The key used for the object (optional)
	 * @return An object ready to be put inside a BSON object
	 */
	private static Object forBson( Object object, String key )
	{
		if( object instanceof NativeJavaObject )
		{
			// This happens either because the developer purposely creates a
			// Java object, or because it was returned from a Java call and
			// wrapped by Rhino.

			return ( (NativeJavaObject) object ).unwrap();
		}
		else if( object instanceof NativeArray )
		{
			// Convert Rhino array to list

			NativeArray array = (NativeArray) object;
			int length = (int) array.getLength();
			List<Object> list = new ArrayList<Object>( length );

			for( int i = 0; i < length; i++ )
				list.add( forBson( ScriptableObject.getProperty( array, i ), null ) );

			return list;
		}
		else if( object instanceof ScriptableObject )
		{
			ScriptableObject scriptable = (ScriptableObject) object;
			if( scriptable.getClassName().equals( "Date" ) )
			{
				// The NativeDate class is private in Rhino, but we can access
				// it like a regular object.

				Object time = ScriptableObject.callMethod( scriptable, "getTime", null );
				if( time instanceof Number )
					return new Date( ( (Number) time ).longValue() );
			}

			// Convert

			return to( scriptable );
		}
		else if( object instanceof Undefined )
		{
			return null;
		}
		else if( "_id".equals( key ) )
		{
			return new ObjectId( object.toString() );
		}
		else
			return object;
	}

	/**
	 * If necessary, convert from BSON to a type supported by Rhino.
	 * 
	 * @param object
	 *        An object
	 * @return An object ready to be put inside a Rhino object
	 */
	private static Object forRhino( Object object )
	{
		// System.out.println( object.getClass() );
		if( object instanceof BSONObject )
		{
			// Convert BSON object to NativeObject

			BSONObject bsonObject = (BSONObject) object;
			NativeObject nativeObject = new NativeObject();

			for( String key : bsonObject.keySet() )
			{
				Object value = forRhino( bsonObject.get( key ) );
				ScriptableObject.putProperty( nativeObject, key, value );
			}

			return nativeObject;
		}
		else if( object instanceof List<?> )
		{
			// Convert list to NativeArray

			List<?> list = (List<?>) object;
			NativeArray array = new NativeArray( list.size() );

			int index = 0;
			for( Object item : list )
				ScriptableObject.putProperty( array, index++, forRhino( item ) );

			return array;
		}
		else if( object instanceof Date )
		{
			// The NativeDate class is private in Rhino, but we can create
			// it indirectly like a regular object.

			Date date = (Date) object;
			Context context = Context.getCurrentContext();
			Scriptable scope = ScriptRuntime.getTopCallScope( context );
			Scriptable nativeDate = context.newObject( scope, "Date", new Object[]
			{
				date.getTime()
			} );

			return nativeDate;
		}
		else if( object instanceof ObjectId )
		{
			return ( (ObjectId) object ).toStringMongod();
		}
		else if( object instanceof Symbol )
		{
			return ( (Symbol) object ).getSymbol();
		}
		else
			return object;
	}
}
