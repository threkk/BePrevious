package nl.boxlab;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModelUtilities {

	private static final Logger logger = LoggerFactory
			.getLogger(ModelUtilities.class);
	private static Map<Class<? extends Object>, PropertyDescriptor[]> beanInfoMap = new HashMap<Class<? extends Object>, PropertyDescriptor[]>();

	@SuppressWarnings("unchecked")
	public static <T extends Object> T deepClone(T entity) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(entity);

			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return (T) ois.readObject();
		} catch (IOException e) {
			logger.error("failed to clone", e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error("failed to clone", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T shallowClone(T entity) {
		T clone;
		try {
			clone = (T) entity.getClass().newInstance();
		} catch (ReflectiveOperationException ex) {
			logger.error("failed to clone", ex);
			return null;
		}

		merge(entity, clone);

		return clone;
	}

	public static <T extends Object> void merge(T src, T dst) {
		for (PropertyDescriptor descriptor : getDescriptors(src)) {
			Method readMethod = descriptor.getReadMethod();
			Method writeMethod = descriptor.getWriteMethod();

			if (readMethod == null || writeMethod == null) {
				continue;
			}

			try {
				writeMethod.invoke(dst, readMethod.invoke(src));
			} catch (ReflectiveOperationException e) {
				logger.error("failed to merge", e);
			} catch (IllegalArgumentException e) {
				logger.error("failed to merge", e);
			}
		}
	}

	private static <T extends Object> PropertyDescriptor[] getDescriptors(
			T entity) {
		Class<? extends Object> clazz = entity.getClass();
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
