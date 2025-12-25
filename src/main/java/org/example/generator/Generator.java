package org.example.generator;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {
    private static final int MAX_RECURSION_DEPTH = 100;
    private static final String SCAN_PACKAGE = "org.example.classes";

    private final Random random = new Random();
    private final Map<Class<?>, List<Class<?>>> implementationCache = new HashMap<>();
    private boolean isScanned = false;

    public Object generateValueOfType(Class<?> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return generateValueOfType(clazz, 0, new HashSet<>());
    }

    private Object generateValueOfType(Class<?> clazz, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (depth > MAX_RECURSION_DEPTH) {
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return random.nextInt(1000);
        }
        if (clazz == double.class || clazz == Double.class) {
            return random.nextDouble() * 1000;
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return random.nextBoolean();
        }
        if (clazz == long.class || clazz == Long.class) {
            return random.nextLong() % 1000;
        }
        if (clazz == float.class || clazz == Float.class) {
            return random.nextFloat() * 1000;
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return (byte) random.nextInt(256);
        }
        if (clazz == short.class || clazz == Short.class) {
            return (short) random.nextInt(1000);
        }
        if (clazz == char.class || clazz == Character.class) {
            return (char) ('a' + random.nextInt(26));
        }

        if (clazz == String.class) {
            return generateRandomString();
        }

        if (clazz == List.class) {
            return new ArrayList<>();
        }

        if (generationPath.contains(clazz)) {
            return null;
        }

        generationPath.add(clazz);

        try {
            if (clazz.isInterface()) {
                return generateInterfaceImplementation(clazz, depth, generationPath);
            }

            if (!clazz.isAnnotationPresent(Generatable.class)) {
                return null;
            }

            return generateInstanceFromConstructor(clazz, depth, generationPath);
        } finally {
            generationPath.remove(clazz);
        }
    }

    private String generateRandomString() {
        int length = 5 + random.nextInt(10);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }

    private Object generateInterfaceImplementation(Class<?> interfaceClass, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!isScanned) {
            scanAllClasses();
        }

        List<Class<?>> implementations = implementationCache.getOrDefault(interfaceClass, new ArrayList<>());

        if (implementations.isEmpty()) {
            return null;
        }

        Class<?> selectedImpl = implementations.get(random.nextInt(implementations.size()));
        return generateValueOfType(selectedImpl, depth, generationPath);
    }

    private void scanAllClasses() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = SCAN_PACKAGE.replace('.', '/');
            URL resource = classLoader.getResource(path);

            if (resource == null) {
                return;
            }

            File directory = new File(resource.getFile());
            if (!directory.exists()) {
                return;
            }

            List<Class<?>> allClasses = new ArrayList<>();
            scanDirectory(directory, SCAN_PACKAGE, allClasses);

            for (Class<?> clazz : allClasses) {
                if (!clazz.isInterface() && clazz.isAnnotationPresent(Generatable.class)) {
                    for (Class<?> interfaceClass : clazz.getInterfaces()) {
                        implementationCache.computeIfAbsent(interfaceClass, k -> new ArrayList<>()).add(clazz);
                    }
                }
            }

            isScanned = true;
        } catch (Exception e) {
        }
    }

    private void scanDirectory(File directory, String packageName, List<Class<?>> allClasses) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + '.' + file.getName(), allClasses);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    allClasses.add(clazz);
                } catch (ClassNotFoundException e) {
                }
            }
        }
    }

    private Object generateInstanceFromConstructor(Class<?> clazz, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 0) {
            return null;
        }

        Constructor<?> selectedConstructor = Arrays.stream(constructors)
                .max((c1, c2) -> Integer.compare(c1.getParameterCount(), c2.getParameterCount()))
                .orElse(constructors[0]);

        Object[] parameters = generateConstructorParameters(selectedConstructor, depth + 1, generationPath);

        selectedConstructor.setAccessible(true);
        return selectedConstructor.newInstance(parameters);
    }

    private Object[] generateConstructorParameters(Constructor<?> constructor, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Parameter[] parameters = constructor.getParameters();
        Object[] parameterValues = new Object[parameters.length];

        Type[] genericParameterTypes = constructor.getGenericParameterTypes();

        for (int i = 0; i < parameters.length; i++) {
            Class<?> paramType = parameters[i].getType();
            Type genericType = genericParameterTypes[i];

            if (genericType instanceof ParameterizedType) {
                parameterValues[i] = generateParameterizedType((ParameterizedType) genericType, depth, generationPath);
            } else {
                parameterValues[i] = generateValueOfType(paramType, depth, generationPath);
            }
        }

        return parameterValues;
    }

    private Object generateParameterizedType(ParameterizedType parameterizedType, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Type rawType = parameterizedType.getRawType();

        if (rawType == List.class) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                Type elementType = typeArguments[0];
                return generateList(elementType, depth, generationPath);
            }
        }

        return new ArrayList<>();
    }

    private List<Object> generateList(Type elementType, int depth, Set<Class<?>> generationPath) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        List<Object> list = new ArrayList<>();

        int size = random.nextInt(6);

        if (elementType instanceof Class) {
            Class<?> elementClass = (Class<?>) elementType;
            for (int i = 0; i < size; i++) {
                Object element = generateValueOfType(elementClass, depth, generationPath);
                if (element != null) {
                    list.add(element);
                }
            }
        }

        return list;
    }
}
