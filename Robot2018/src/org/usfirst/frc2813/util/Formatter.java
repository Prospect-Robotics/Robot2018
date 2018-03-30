package org.usfirst.frc2813.util;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Helper functions for using arrays as tuples
 */
public final class Formatter {
	/**
	 * Convert a list of parameters to an array, to act as a tuple
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(T... ts) {
		return ts;
	}

	/**
	 * Convert a list of parameters to an array, to act as a tuple
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] tuple(T... ts) {
		return ts;
	}

	/**
	 * Convert primitive arrays to Object arrays, as necessary (Original code from:
	 * https://stackoverflow.com/a/6427453)
	 */
	public static Object[] createArrayFromArrayObject(Object o) {
		if (!o.getClass().getComponentType().isPrimitive())
			return (Object[]) o;

		int element_count = Array.getLength(o);
		Object elements[] = new Object[element_count];

		for (int i = 0; i < element_count; i++)
			elements[i] = Array.get(o, i);

		return elements;
	}

	/**
	 * Convert nested arrays into nested tuples (Original code from:
	 * https://stackoverflow.com/a/6427453)
	 */
	public static String arrayToString(Object[] in) {
		String ret = "{";
		for (Object obj : in) {
			if (obj.getClass().isArray())
				ret += arrayToString(createArrayFromArrayObject(obj));
			else
				ret += obj.toString();
			ret += ",";
		}
		ret = ret.substring(0, ret.length() - 2);
		return ret + "}";
	}

	/**
	 * Format a value - pretty print arrays
	 * 
	 * @param value
	 * @return
	 */
	public static String formatValue(Object value) {
		if (value == null) {
			return null;
		} else if (value.getClass().isArray()) {
			return arrayToString(createArrayFromArrayObject(value));
		} else {
			return value.toString();
		}
	}

	/**
	 * Format a key-value pair
	 */
	public static String formatKeyValue(Object key, Object value, boolean returnNullForNullValues) {
		if (returnNullForNullValues && value == null) {
			return null;
		} else {
			return key + "=" + formatValue(value);
		}
	}

	/**
	 * Format a list of key,value,key,value pairs
	 */
	public static String formatLabelled(Object... objects) {
		return formatKeyValueObjectList(false, objects);
	}

	/**
	 * Format a list of key,value,key,value pairs but skip pairs with null values.
	 */
	public static String formatLabelledSkipNulls(Object... objects) {
		return formatKeyValueObjectList(true, objects);
	}

	/**
	 * Format a list of key,value,key,value pairs
	 */
	public static String formatKeyValueObjectList(Object... objects) {
		return formatKeyValueObjectList(false, objects);
	}

	/**
	 * Format a list of key,value,key,value pairs but skip pairs with null values.
	 */
	public static String formatKeyValueObjectListSkipNulls(Object... objects) {
		return formatKeyValueObjectList(true, objects);
	}

	/**
	 * Private build a formatted list of key-value pairs
	 */
	public static String formatKeyValueObjectList(boolean doNotPrintKeysWithNullValues, Object... objects) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < objects.length; i += 2) {
			Object key = objects[i];
			Object value = objects[i + 1];
			String pair = formatKeyValue(key, value, doNotPrintKeysWithNullValues);
			if (pair != null)
				buf.append(pair);
			if (objects.length > i + 1)
				buf.append(", ");
		}
		/* If we get a bonus value */
		if (objects.length % 2 != 0) {
			Object bonus = objects[objects.length - 1];
			if (bonus != null)
				buf.append(bonus);
		}
		return buf.toString();
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	public static String formatConstructor(String objectClass, Object[] arguments) {
		return objectClass + "(" + formatLabelledSkipNulls(arguments) + ")";
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	@SuppressWarnings("rawtypes")
	public static String formatConstructor(Class objectClass, Object[] arguments) {
		return formatConstructor(objectClass.getSimpleName(), arguments);
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	public static String formatConstructor(String objectClass, List<Object> arguments) {
		return formatConstructor(objectClass, arguments.toArray());
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	public static String formatConstructor(String objectClass) {
		return formatConstructor(objectClass, new Object[0]);
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	@SuppressWarnings("rawtypes")
	public static String formatConstructor(Class objectClass) {
		return formatConstructor(objectClass, new Object[0]);
	}

	/**
	 * Format a description of an object from it's class name and arguments.
	 */
	@SuppressWarnings("rawtypes")
	public static String formatConstructor(Class objectClass, List<Object> arguments) {
		return formatConstructor(objectClass, arguments.toArray());
	}

	/**
	 * More memory friendly than Arrays.deepToString().
	 */
	public static String deepToString(Object[] array) {
		StringBuilder sb = new StringBuilder();
		deepToString(sb, array);
		return sb.toString();
	}

	private static void deepToString(StringBuilder sb, Object[] array) {
		sb.append('[');
		for (Object o : array) {
			if (o instanceof Object[])
				deepToString(sb, (Object[]) o);
			else
				sb.append(o.toString());
			sb.append(',');
			sb.append(' ');
		}
		sb.append(']');
	}

	/**
	 * Convert objects to strings and concatenate them. This is supposed to be as
	 * memory efficient as possible. 40M of free RAM ain't much.
	 */
	private static String concat(Object... objects) {
		StringBuilder sb = new StringBuilder();
		for (Object o : objects)
			sb.append(o.toString());
		return sb.toString();
	}
}
