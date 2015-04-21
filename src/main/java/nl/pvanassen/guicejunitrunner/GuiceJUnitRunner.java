/**
 * Copyright (C) 2011 Fabio Strozzi (fabio.strozzi@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// GuiceJunitRunner.java, created by Fabio Strozzi on Mar 27, 2011
package nl.pvanassen.guicejunitrunner;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Injector;

/**
 * A JUnit class runner for Guice based applications.
 * 
 * @author Fabio Strozzi
 */
public class GuiceJUnitRunner extends BlockJUnit4ClassRunner {
    private final Injector injector;

    /**
     * Instances a new JUnit runner.
     * 
     * @param klass The test class
     * @throws InitializationError In case of an error while initializing the class 
     */
    public GuiceJUnitRunner(Class<?> klass) throws InitializationError {
        super(klass);
        Class<?>[] classes = GuiceHelper.getModulesFor(klass);
        injector = GuiceHelper.createInjectorFor(classes, null);
    }

    
    /**
     * @see org.junit.runners.BlockJUnit4ClassRunner#createTest()
     */
    @Override
    public Object createTest() throws Exception {
        Object obj = super.createTest();
        injector.injectMembers(obj);
        return obj;
    }

}
