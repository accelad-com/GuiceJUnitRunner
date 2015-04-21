package nl.pvanassen.guicejunitrunner;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;

/**
 * 
 * Runs test methods within a MyBatis-controlled SQL transaction and rolls back on test completion (failed or success).
 * 
 * Example:
 * 
 * <pre>
 * &#064;RunWith(MyBatisTestRollbackRunner.class)
 * public class UserDaoTest implements MyBatisModuleProvider {
 *    &#064;Inject
 *    private UserDao userDao;
 *    
 *    &#064;Override
 *    public MyBatisModule getMyBatisModule() {
 *       return new PooledJdbcMyBatisModule() {
 *          &#064;Override
 *          public void initializeForTest() {
 *             addMapperClass(UserMapper.class);
 *             addMapperClass(AddressMapper.class);
 *          }
 *       };
 *    }
 * 
 *    &#064;Test
 *    ...
 * }
 * 
 * &#064;ImplementedBy(UserDaoMyBatis.class)
 * public interface UserDao {
 *    ...
 * }
 * 
 * public class UserDaoMyBatis implements UserDao {
 *    &#064;Inject
 *    private UserMapper userMapper;
 *    
 *    &#064;Inject
 *    private AddressMapper addressMapper;
 *    
 *    ...
 * }
 * </pre>
 */
public class GuiceMyBatisRollbackJUnitRunner extends BlockJUnit4ClassRunner {
    private final Logger logger = LoggerFactory.getLogger(GuiceMyBatisRollbackJUnitRunner.class);
    private final Injector injector;

    @Inject
    private SqlSessionManager sqlSessionManager;
    
    /**
     * Instances a new JUnit runner.
     * 
     * @param klass The test class
     * @throws InitializationError In case of an error while initializing the class 
     */
    public GuiceMyBatisRollbackJUnitRunner(final Class<?> klass) throws InitializationError {
        super(klass);
        Class<?>[] classes = GuiceHelper.getModulesFor(klass);
        injector = GuiceHelper.createInjectorFor(classes, new AbstractModule() {
            @Override
            protected void configure() {
                bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transactional.class), new MethodInterceptor() {
                    @Override
                    public Object invoke(MethodInvocation invocation) throws Throwable {
                        if (sqlSessionManager != null && sqlSessionManager.isManagedSessionStarted()) {
                            sqlSessionManager.clearCache();
                        }
                        return invocation.proceed();
                    }
                });
            }
        });

    }

    /**
     * {@inheritDoc}
     *
     * @see org.junit.runners.BlockJUnit4ClassRunner#methodInvoker(org.junit.runners.model.FrameworkMethod, java.lang.Object)
     */
    @Override
    protected Statement methodInvoker(final FrameworkMethod method, final Object test) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    sqlSessionManager.startManagedSession();
                    logger.debug("running method: " + method.getMethod().getName());
                    method.invokeExplosively(test);
                } finally {
                    try {
                        sqlSessionManager.rollback(true);
                    } finally {
                        sqlSessionManager.close();
                    }
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     * 
     * Fills the test and injects the required interceptors
     * 
     * @see org.junit.runners.BlockJUnit4ClassRunner#createTest()
     */
    @Override
    public Object createTest() throws Exception {
        Object obj = super.createTest();
        injector.injectMembers(obj);
        injector.injectMembers(this);
        return obj;
    }
}
