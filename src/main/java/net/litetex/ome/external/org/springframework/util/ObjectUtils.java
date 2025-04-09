/*
 * Copyright 2002-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.litetex.ome.external.org.springframework.util;

import java.util.Arrays;


/**
 * Miscellaneous object utility methods.
 *
 * <p>Mainly for internal use within the framework.
 *
 * <p>Thanks to Alex Ruiz for contributing several enhancements to this class!
 *
 * @author Juergen Hoeller
 * @author Keith Donald
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Chris Beams
 * @author Sam Brannen
 * @since 19.03.2004
 */
@SuppressWarnings("all")
public final class ObjectUtils
{
	/**
	 * Determine if the given objects are equal, returning {@code true} if both are {@code null} or {@code false} if
	 * only one is {@code null}.
	 * <p>Compares arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 *
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see Object#equals(Object)
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(final Object o1, final Object o2)
	{
		if(o1 == o2)
		{
			return true;
		}
		if(o1 == null || o2 == null)
		{
			return false;
		}
		if(o1.equals(o2))
		{
			return true;
		}
		if(o1.getClass().isArray() && o2.getClass().isArray())
		{
			return arrayEquals(o1, o2);
		}
		return false;
	}
	
	/**
	 * Compare the given arrays with {@code Arrays.equals}, performing an equality check based on the array elements
	 * rather than the array reference.
	 *
	 * @param o1 first array to compare
	 * @param o2 second array to compare
	 * @return whether the given objects are equal
	 * @see #nullSafeEquals(Object, Object)
	 * @see java.util.Arrays#equals
	 */
	private static boolean arrayEquals(final Object o1, final Object o2)
	{
		if(o1 instanceof final Object[] objects1 && o2 instanceof final Object[] objects2)
		{
			return Arrays.equals(objects1, objects2);
		}
		if(o1 instanceof final boolean[] booleans1 && o2 instanceof final boolean[] booleans2)
		{
			return Arrays.equals(booleans1, booleans2);
		}
		if(o1 instanceof final byte[] bytes1 && o2 instanceof final byte[] bytes2)
		{
			return Arrays.equals(bytes1, bytes2);
		}
		if(o1 instanceof final char[] chars1 && o2 instanceof final char[] chars2)
		{
			return Arrays.equals(chars1, chars2);
		}
		if(o1 instanceof final double[] doubles1 && o2 instanceof final double[] doubles2)
		{
			return Arrays.equals(doubles1, doubles2);
		}
		if(o1 instanceof final float[] floats1 && o2 instanceof final float[] floats2)
		{
			return Arrays.equals(floats1, floats2);
		}
		if(o1 instanceof final int[] ints1 && o2 instanceof final int[] ints2)
		{
			return Arrays.equals(ints1, ints2);
		}
		if(o1 instanceof final long[] longs1 && o2 instanceof final long[] longs2)
		{
			return Arrays.equals(longs1, longs2);
		}
		if(o1 instanceof final short[] shorts1 && o2 instanceof final short[] shorts2)
		{
			return Arrays.equals(shorts1, shorts2);
		}
		return false;
	}
	
	/**
	 * Return a hash code for the given object, typically the value of {@link Object#hashCode()}. If the object is an
	 * array, this method will delegate to one of the {@code Arrays.hashCode} methods. If the object is {@code null},
	 * this method returns {@code 0}.
	 *
	 * @see Object#hashCode()
	 * @see Arrays
	 */
	public static int nullSafeHashCode(Object obj)
	{
		if(obj == null)
		{
			return 0;
		}
		if(obj.getClass().isArray())
		{
			if(obj instanceof Object[] objects)
			{
				return Arrays.hashCode(objects);
			}
			if(obj instanceof boolean[] booleans)
			{
				return Arrays.hashCode(booleans);
			}
			if(obj instanceof byte[] bytes)
			{
				return Arrays.hashCode(bytes);
			}
			if(obj instanceof char[] chars)
			{
				return Arrays.hashCode(chars);
			}
			if(obj instanceof double[] doubles)
			{
				return Arrays.hashCode(doubles);
			}
			if(obj instanceof float[] floats)
			{
				return Arrays.hashCode(floats);
			}
			if(obj instanceof int[] ints)
			{
				return Arrays.hashCode(ints);
			}
			if(obj instanceof long[] longs)
			{
				return Arrays.hashCode(longs);
			}
			if(obj instanceof short[] shorts)
			{
				return Arrays.hashCode(shorts);
			}
		}
		return obj.hashCode();
	}
	
	private ObjectUtils()
	{
	}
}
