package nl.boxlab.model.utilities;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import nl.boxlab.model.Entity;

public class ModelUtilities {

	private static Map<Class<? extends Entity>, PropertyDescriptor[]> beanInfoMap = new HashMap<Class<? extends Entity>, PropertyDescriptor[]>();

	@SuppressWarnings("unchecked")
	public static <T extends Entity> T deepClone(T entity) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(entity);

			ByteArrayInputStream bais = new ByteArrayInputStream(
			        baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Entity> T shallowClone(T entity) {
		T clone;
		try {
			clone = (T) entity.getClass().newInstance();
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}

		merge(entity, clone);

		return clone;
	}

	public static <T extends Entity> void merge(T src, T dst) {
		for (PropertyDescriptor descriptor : getDescriptors(src)) {
			Method readMethod = descriptor.getReadMethod();
			Method writeMethod = descriptor.getWriteMethod();

			if (readMethod == null || writeMethod == null) {
				continue;
			}

			try {
				writeMethod.invoke(dst, readMethod.invoke(src));
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	private static <T extends Entity> PropertyDescriptor[] getDescriptors(
	        T entity) {
		Class<? extends Entity> clazz = entity.getClass();
		PropertyDescriptor[] propertyDescriptors = beanInfoMap.get(clazz);
		if (propertyDescriptors == null) {
			try {
				propertyDescriptors = Introspector.getBeanInfo(clazz)
				        .getPropertyDescriptors();
			} catch (IntrospectionException e) {
				e.printStackTrace();
			}
			beanInfoMap.put(clazz, propertyDescriptors);
		}

		return propertyDescriptors;
	}
}
