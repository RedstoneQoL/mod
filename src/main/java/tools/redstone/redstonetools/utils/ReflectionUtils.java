package tools.redstone.redstonetools.utils;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import rip.hippo.inject.DoctorModule;
import sun.misc.Unsafe;
import tools.redstone.redstonetools.features.AbstractFeature;
import tools.redstone.redstonetools.features.Feature;
import tools.redstone.redstonetools.features.arguments.Argument;
import tools.redstone.redstonetools.features.arguments.serializers.GenericArgumentType;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static DoctorModule[] modules;
    private static Set<? extends AbstractFeature> features;
    private static Set<Class<? extends GenericArgumentType>> arguments;

    private ReflectionUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final MethodHandles.Lookup INTERNAL_LOOKUP;
    private static final Unsafe unsafe;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new ExceptionInInitializerError(t);
        }

        try {
            // get lookup
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.publicLookup();
            INTERNAL_LOOKUP = (MethodHandles.Lookup) unsafe.getObject(
                    unsafe.staticFieldBase(field),
                    unsafe.staticFieldOffset(field));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static MethodHandles.Lookup getInternalLookup() {
        return INTERNAL_LOOKUP;
    }

    public static DoctorModule[] getModules() {
        if (modules == null) {
            try {
                modules = serviceLoad(DoctorModule.class)
                        .toArray(DoctorModule[]::new);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load modules", e);
            }
        }
        return modules;
    }

    public static Set<Class<? extends GenericArgumentType>> getAllArguments() {
        if (arguments == null) {
            try {
                arguments = findClasses(GenericArgumentType.class)
                        .stream()
                        .filter(argument -> !Modifier.isAbstract(argument.getModifiers()))
                        .collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return arguments;
    }

    public static Set<? extends AbstractFeature> getFeatures() {
        if (features == null) {
            try {
                features = serviceLoad(AbstractFeature.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load features", e);
            }
        }
        return features;
    }

    public static <T> Set<Class<? extends T>> findClasses(Class<T> clazz) throws IOException {
        ClassLoader cl = ReflectionUtils.class.getClassLoader();
        Enumeration<URL> serviceFiles = cl.getResources("META-INF/services/" + clazz.getName());
        Set<String> classNames = new HashSet<>();
        while (serviceFiles.hasMoreElements()) {
            URL serviceFile = serviceFiles.nextElement();
            try (var reader = serviceFile.openStream()) {
                classNames.addAll(IOUtils.readLines(reader, "UTF-8"));
            }
        }
        //noinspection unchecked
        return classNames.stream()
                .filter(it -> !it.isEmpty() && !it.isBlank())
                .map(ReflectionUtils::loadClass)
                .filter(Objects::nonNull)
                .filter(clazz::isAssignableFrom)
				.map(it -> (Class<? extends T>) it)
                .collect(Collectors.toSet());
    }

    private static <T> Set<? extends T> serviceLoad(Class<T> clazz) throws IOException {
        return findClasses(clazz)
                .stream()
                .map(it -> {
                    try {
                        return it.getDeclaredConstructor().newInstance();
                    } catch (ReflectiveOperationException e) {
                        throw new RuntimeException("Failed to instantiate " + it, e);
                    }
                })
                .map(clazz::cast)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable Class<? extends T> loadClass(String className) {
        try {
            return (Class<? extends T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class " + className, e);
        } catch (NoClassDefFoundError e) {
            LOGGER.warn("Failed to load class {}, required {}", className, e.getMessage());
        }
        return null;
    }

    public static List<Argument<?>> getArguments(Class<? extends AbstractFeature> featureClass) {
        return Arrays.stream(featureClass.getFields())
                .filter(field -> Argument.class.isAssignableFrom(field.getType()))
                .map(field -> {
                    if (!Modifier.isPublic(field.getModifiers())
                            || !Modifier.isStatic(field.getModifiers())
                            || !Modifier.isFinal(field.getModifiers())) {
                        throw new RuntimeException("Field " + field.getName() + " of feature " + featureClass.getName()
                                + " is not public static final");
                    }

                    try {
                        var argument = (Argument<?>) field.get(null);

                        return argument.ensureNamed(field.getName());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to get value of field " + field.getName(), e);
                    }
                })
                .collect(Collectors.toList());
    }

    public static Feature getFeatureInfo(Class<? extends AbstractFeature> featureClass) {
        var feature = featureClass.getAnnotation(Feature.class);

        if (feature == null) {
            throw new RuntimeException("No feature annotation found for feature " + featureClass.getName());
        }

        return feature;
    }
}
