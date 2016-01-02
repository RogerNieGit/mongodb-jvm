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

package org.bson.jvm.rhino;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A BSON codec for Rhino's NativeDate (the class is private in Rhino).
 * 
 * @author Tal Liron
 */
@SuppressWarnings("rawtypes")
public class NativeDateCodec implements Codec
{
	//
	// Codec
	//

	public Class getEncoderClass()
	{
		// This is not actually our encoded class, but we need some kind of
		// unique placeholder for BsonTypeClassMap
		return NativeDateCodec.class;
	}

	public void encode( BsonWriter writer, Object object, EncoderContext encoderContext )
	{
		Scriptable nativeDate = (Scriptable) object;

		Object time = ScriptableObject.callMethod( nativeDate, "getTime", null );
		writer.writeDateTime( ( (Number) time ).longValue() );
	}

	public Object decode( BsonReader reader, DecoderContext decoderContext )
	{
		long time = reader.readDateTime();

		Context context = Context.getCurrentContext();
		Scriptable scope = ScriptRuntime.getTopCallScope( context );
		return context.newObject( scope, "Date", new Object[]
		{
			time
		} );
	}
}
