package reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import string.ConstantUtility;
import string.StringUtility;

public class ReflectionUtil {
	private static final String GETTER_METHOD_REGEX = "^get[A-Z].*";
	  private static final String BOOLEAN_IS_METHOD_REGEX = "^is[A-Z].*";

	  private ReflectionUtil() {

	  }

	  public static List<String> findAllGetterMethods(Class<?> c) {
	    ArrayList<String> list = new ArrayList<>();
	    Method[] methods = c.getDeclaredMethods();
	    for (Method method : methods) {
	      if (isGetter(method)) {
	        list.add(method.getName());
	      }
	    }
	    return list;
	  }

	  private static boolean isGetter(Method method) {
	    if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0) {
	      if (method.getName().matches(GETTER_METHOD_REGEX)
	          && !method.getReturnType().equals(void.class))
	        return true;
	      if (method.getName().matches(BOOLEAN_IS_METHOD_REGEX)
	          && method.getReturnType().equals(boolean.class))
	        return true;
	    }
	    return false;
	  }

	  private static <T> String getMethodValue(Class<T> clazz, T obj, String name) {
	    Method method;
	    try {
	      method = clazz.getDeclaredMethod(name);
	      return StringUtility.trimToDefault(method.invoke(obj), ConstantUtility.BLANK);
	    } catch (Exception e) {
	    }
	    return ConstantUtility.BLANK;
	  }

	  public static <T> String[] getValuesByMapping(Class<T> clazz, T obj,
	      List<String> headerColumnMappings) {
	    String[] row = new String[headerColumnMappings.size()];
	    for (int i = 0; i < headerColumnMappings.size(); i++) {
	      row[i] = getMethodValue(clazz, obj, headerColumnMappings.get(i));
	    }
	    return row;
	  }
}
