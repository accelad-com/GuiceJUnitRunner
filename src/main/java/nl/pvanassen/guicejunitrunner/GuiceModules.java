package nl.pvanassen.guicejunitrunner;

import java.lang.annotation.*;

/**
 * The annotation to define the modules to load
 * @author Fabio Strozzi
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GuiceModules {
    /**
     * @return Modules to load during the unit test
     */
    Class<?>[] value();
}