package nl.boxlab.table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TableColumnFactory
{

	private static final Logger logger = LoggerFactory
			.getLogger(TableColumnFactory.class);

	public static List<ColumnDefinition> createColumnDefinitions(
			TableViewDefinition view, ResourceBundle bundle)
	{
		List<ColumnDefinition> result = new ArrayList<ColumnDefinition>();
		Class<?> viewClass = view.getClass();

		while (viewClass != null) {
			for (Method getter : viewClass.getDeclaredMethods()) {
				Column column = getter.getAnnotation(Column.class);
				if (column == null) {
					continue;
				}

				Class<?>[] arguments = getter.getParameterTypes();
				if (arguments.length != 1) {
					logger.warn(
							"The field {} is annotated but does not conform the correct signature",
							getter);
					continue;
				}

				Method setter = findSetter(viewClass, getter);
				/**
				 * Workaround for bug #4071957: (reflect) Method.invoke access
				 * control does not understand inner class scoping
				 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4071957
				 * fixes: allows an anonymous inner class of rowView to be used
				 */
				getter.setAccessible(true);
				if (setter != null) {
					setter.setAccessible(true);
				}

				ColumnDefinition columnController = new MethodColumnDefinition(
						view, column, getter, setter);
				if (bundle != null) {
					String columnKey = columnController.getKey();
					String localizedTitle = getLocalizedTitle(viewClass,
							bundle, columnKey);
					columnController.setTitle(localizedTitle);
				}
				result.add(columnController);
			}
			viewClass = viewClass.getSuperclass();
		}

		// sort on column order
		Collections.sort(result, ColumnDefinition.COMPARATOR_ORDER);

		return result;
	}

	/**
	 * find a setter method based on the name of the getter method
	 * 
	 * @param getter
	 *            the getter method to use as a reference to find the setter
	 *            method
	 * @return returns the setter that was found that is likely paired to the
	 *         given getter, or null if there was either no setter, or an
	 *         exception occured.
	 */
	private static Method findSetter(Class<?> clazz, Method getter)
	{
		Method setter = null;

		if (getter.getName().startsWith("get")) {
			Class<?> returnType = getter.getReturnType();
			Class<?>[] arguments = getter.getParameterTypes();
			try {
				setter = clazz.getMethod("set" + getter.getName().substring(3),
						arguments[0], returnType);
			} catch (SecurityException ball) {
				logger.error("Security manager does not like what we did", ball);
			} catch (NoSuchMethodException ball) {
				// no setter found, it is a readonly column
			}
		}

		return setter;
	}

	private static String getLocalizedTitle(Class<?> viewClass,
			ResourceBundle bundle, String columnKey)
	{
		String prefix;
		if (viewClass.getAnnotation(Localization.class) != null) {
			prefix = viewClass.getAnnotation(Localization.class).value();
		} else {
			prefix = viewClass.getCanonicalName();
		}

		if (prefix == null) {
			prefix = "";
		} else if (prefix.length() > 0) {
			prefix += ".";
		}

		String key = prefix + columnKey;
		String value;
		try {
			String title = bundle.getString(key);
			value = title;
		} catch (MissingResourceException ball) {
			// indicate the missing localization for the column name
			value = "[" + key + "]";
		}
		return value;
	}
}
