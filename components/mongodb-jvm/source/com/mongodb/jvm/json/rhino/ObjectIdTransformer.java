/**
 * Copyright 2010-2016 Three Crickets LLC.
 * <p>
 * The contents of this file are subject to the terms of the Apache License
 * version 2.0: http://www.opensource.org/licenses/apache2.0.php
 * <p>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly from Three Crickets
 * at http://threecrickets.com/
 */

package com.mongodb.jvm.json.rhino;

import org.bson.types.ObjectId;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.UniqueTag;

import com.threecrickets.jvm.json.JsonImplementation;
import com.threecrickets.jvm.json.JsonTransformer;

/**
 * Transforms a Rhino {@link Scriptable} with a "$oid" key into a BSON
 * {@link ObjectId}.
 * 
 * @author Tal Liron
 */
public class ObjectIdTransformer implements JsonTransformer
{
	//
	// JsonTransformer
	//

	public Object transform( Object object, JsonImplementation implementation )
	{
		if( object instanceof Scriptable )
		{
			Scriptable scriptable = (Scriptable) object;
			Object oid = scriptable.get( "$oid", scriptable );
			if( ( oid != null ) && ( oid.getClass() != UniqueTag.class ) )
				return new ObjectId( (String) oid );
		}

		return null;
	}
}
