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

package org.bson.jvm.nashorn;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import jdk.nashorn.internal.objects.NativeBoolean;

/**
 * A BSON codec for Nashorn's {@link NativeBoolean}.
 * 
 * @author Tal Liron
 */
public class NativeBooleanCodec implements Codec<NativeBoolean>
{
	//
	// Codec
	//

	public Class<NativeBoolean> getEncoderClass()
	{
		return NativeBoolean.class;
	}

	public void encode( BsonWriter writer, NativeBoolean nativeBoolean, EncoderContext encoderContext )
	{
		writer.writeBoolean( nativeBoolean.getValue() );
	}

	public NativeBoolean decode( BsonReader reader, DecoderContext decoderContext )
	{
		return (NativeBoolean) NativeBoolean.constructor( true, null, reader.readBoolean() );
	}
}
