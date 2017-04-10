/**
 * Copyright 2010-2017 Three Crickets LLC.
 * <p>
 * The contents of this file are subject to the terms of the Apache License
 * version 2.0: http://www.opensource.org/licenses/apache2.0.php
 * <p>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly from Three Crickets
 * at http://threecrickets.com/
 */

package com.mongodb.jvm.json.generic;

import java.util.Map;

import com.mongodb.DBRef;
import com.threecrickets.jvm.json.JsonImplementation;
import com.threecrickets.jvm.json.JsonTransformer;

/**
 * Transforms a JVM {@link Map} with a "$ref" key into a BSON {@link DBRef}.
 * 
 * @author Tal Liron
 */
public class DBRefTransformer implements JsonTransformer
{
	//
	// JsonTransformer
	//

	public Object transform( Object object, JsonImplementation implementation )
	{
		if( object instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) object;

			Object ref = map.get( "$ref" );
			if( ref != null )
			{
				Object id = map.get( "$id" );
				if( id != null )
					return new DBRef( ref.toString(), id );
			}
		}

		return null;
	}
}
