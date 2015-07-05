/**
 * Copyright 2010-2015 Three Crickets LLC.
 * <p>
 * The contents of this file are subject to the terms of the Apache License
 * version 2.0: http://www.opensource.org/licenses/apache2.0.php
 * <p>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly from Three Crickets
 * at http://threecrickets.com/
 */

package com.mongodb.jvm.json.java;

import java.util.Map;

import com.threecrickets.jvm.json.JsonTransformer;

public class LongTransformer implements JsonTransformer
{
	//
	// JsonTransformer
	//

	public Object transform( Object object )
	{
		if( object instanceof Map )
		{
			@SuppressWarnings("unchecked")
			Object numberLong = ( (Map<String, Object>) object ).get( "$numberLong" );
			if( numberLong != null )
				// Might throw a NumberFormatException
				return new Long( Long.parseLong( numberLong.toString() ) );
		}

		return null;
	}
}
