package nl.pvanassen.guicejunitrunner;

import org.junit.runners.model.InitializationError;

import com.google.inject.*;

class GuiceHelper {

    /**
     * @param classes
     * @return
     * @throws InitializationError
     */
    static Injector createInjectorFor(Class<?>[] classes, Module extraModule) throws InitializationError {
        Module[] modules;
        if (extraModule != null) {
            modules = new Module[classes.length + 1];
        }
        else {
            modules = new Module[classes.length];
        }
        for (int i = 0; i < classes.length; i++) {
            try {
                modules[i] = (Module) (classes[i]).newInstance();
            } catch (InstantiationException e) {
                throw new InitializationError(e);
            } catch (IllegalAccessException e) {
                throw new InitializationError(e);
            }
        }
        if (extraModule != null) {
            modules[classes.length] = extraModule;
        }
        return Guice.createInjector(modules);
    }

    /**
     * Gets the Guice modules for the given test class.
     * 
     * @param klass
     *            The test class
     * @return The array of Guice {@link Module} modules used to initialize the
     *         injector for the given test.
     * @throws InitializationError
     */
    static Class<?>[] getModulesFor(Class<?> klass) throws InitializationError {
        GuiceModules annotation = klass.getAnnotation(GuiceModules.class);
        if (annotation == null) {
            throw new InitializationError(
                    "Missing @GuiceModules annotation for unit test '" + klass.getName()
                            + "'");
        }
        return annotation.value();
    }
}
