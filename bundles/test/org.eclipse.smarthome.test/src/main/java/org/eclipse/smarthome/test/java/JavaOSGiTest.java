/**
 * Copyright (c) 2014,2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.smarthome.test.java;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.Predicate;

import org.apache.felix.connect.PojoServiceRegistryFactoryImpl;
import org.apache.felix.connect.launch.BundleDescriptor;
import org.apache.felix.connect.launch.ClasspathScanner;
import org.apache.felix.connect.launch.PojoServiceRegistry;
import org.apache.felix.connect.launch.PojoServiceRegistryFactory;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.test.internal.java.MissingServiceAnalyzer;
import org.eclipse.smarthome.test.storage.VolatileStorageService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link JavaOSGiTest} is an abstract base class for OSGi based tests. It provides convenience methods to register and
 * unregister mocks as OSGi services. All services, which are registered through the
 * {@link JavaOSGiTest#registerService}
 * methods, are unregistered automatically in the tear down of the test.
 *
 * @author Markus Rathgeb - Create a pure Java implementation based on the Groovy {@code OSGiTest} class
 */
@NonNullByDefault
public class JavaOSGiTest extends JavaTest {

    private final Logger logger = LoggerFactory.getLogger(JavaOSGiTest.class);
    private final Map<String, List<ServiceRegistration<?>>> registeredServices = new HashMap<>();
    protected @NonNullByDefault({}) BundleContext bundleContext;

    @Before
    public void bindBundleContext() throws Exception {
//        System.setProperty("ds.loglevel", "DEBUG");
//        System.setProperty("org.osgi.service.log.admin.loglevel", "DEBUG");

        // ensure felix-connect stores bundles in an unique target directory
        String uid = "" + System.currentTimeMillis();
        String tempDir = "target/bundles/" + uid;
        System.setProperty("org.osgi.framework.storage", tempDir);
        createDirectory(tempDir);

        // use another directory for the jar of the bundle as it cannot be in the same directory
        // as it has a file lock during running the tests which will cause the temp dir to not be
        // fully deleted between tests
        createDirectory("target/test-bundles");

        List<BundleDescriptor> bundles = new LinkedList<>();

        Queue<BundleDescriptor> bundleDescriptors = getBundleDescriptors("(&(Bundle-SymbolicName=*)(!(Bundle-SymbolicName=org.osgi.*)))", getClass().getClassLoader());

        // get the bundles
        bundles.addAll(bundleDescriptors);

        if (logger.isDebugEnabled()) {
            for (int i = 0; i < bundles.size(); i++) {
                BundleDescriptor desc = bundles.get(i);
                logger.debug("Bundle #{} -> {}", i, desc);
            }
        }

        // setup felix-connect to use our bundles
        Map<String, Object> config = new HashMap<>();
        config.put(PojoServiceRegistryFactory.BUNDLE_DESCRIPTORS, bundles);

        PojoServiceRegistry reg = new PojoServiceRegistryFactoryImpl().newPojoServiceRegistry(config);
        bundleContext = reg.getBundleContext();

        assertThat(bundleContext, is(notNullValue()));
    }

    /**
     * Gets list of bundle descriptors.
     * @param bundleFilter Filter expression for OSGI bundles.
     *
     * @return List pointers to OSGi bundles.
     * @throws Exception If looking up the bundles fails.
     */
    private static Queue<BundleDescriptor> getBundleDescriptors(final String bundleFilter, ClassLoader loader) throws Exception {
        List<BundleDescriptor> descriptors = new ClasspathScanner().scanForBundles(bundleFilter, loader);

        Deque<BundleDescriptor> ordered = new ArrayDeque<>(descriptors.size());
        for (BundleDescriptor descriptor : descriptors) {
            if ("org.apache.felix.scr".equals(descriptor.getHeaders().get("Bundle-SymbolicName"))) {
                ordered.addFirst(descriptor);
            } else {
                ordered.add(descriptor);
            }
        }

        return ordered;
    }

    private void createDirectory(String directory) {
        new File(directory).mkdirs();
    }

    /**
     * Initialize the {@link BundleContext}, which is used for registration and unregistration of OSGi services.
     *
     * <p>
     * This uses the bundle context of the test class itself.
     *
     * @return bundle context
     */
    private @Nullable BundleContext initBundleContext() {
        final Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        if (bundle != null) {
            return bundle.getBundleContext();
        } else {
            return null;
        }
    }

    private <T> @Nullable T unrefService(final @Nullable ServiceReference<T> serviceReference) {
        if (serviceReference == null) {
            return null;
        } else {
            return bundleContext.getService(serviceReference);
        }
    }

    /**
     * Get an OSGi service for the given class.
     *
     * @param clazz class under which the OSGi service is registered
     * @return OSGi service or null if no service can be found for the given class
     */
    protected <T> @Nullable T getService(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        final ServiceReference<T> serviceReference = (ServiceReference<T>) bundleContext
                .getServiceReference(clazz.getName());

        if (serviceReference == null) {
            new MissingServiceAnalyzer(System.out, bundleContext).printMissingServiceDetails(clazz);
            return null;
        }

        return unrefService(serviceReference);
    }

    /**
     * Get an OSGi service for the given class and the given filter.
     *
     * @param clazz class under which the OSGi service is registered
     * @param filter
     * @return OSGi service or null if no service can be found for the given class
     */
    protected <T> @Nullable T getService(Class<T> clazz, Predicate<ServiceReference<T>> filter) {
        final ServiceReference<T> serviceReferences[] = getServices(clazz);

        if (serviceReferences == null) {
            new MissingServiceAnalyzer(System.out, bundleContext).printMissingServiceDetails(clazz);
            return null;
        }
        final List<T> filteredServiceReferences = new ArrayList<>(serviceReferences.length);
        for (final ServiceReference<T> serviceReference : serviceReferences) {
            if (filter.test(serviceReference)) {
                filteredServiceReferences.add(unrefService(serviceReference));
            }
        }

        if (filteredServiceReferences.size() > 1) {
            Assert.fail("More than 1 service matching the filter is registered.");
        }
        if (filteredServiceReferences.isEmpty()) {
            new MissingServiceAnalyzer(System.out, bundleContext).printMissingServiceDetails(clazz);
            return null;
        } else {
            T t = filteredServiceReferences.get(0);
            if (t == null) {
                new MissingServiceAnalyzer(System.out, bundleContext).printMissingServiceDetails(clazz);
                return null;
            }
            return t;
        }
    }

    private <T> ServiceReference<T> @Nullable [] getServices(final Class<T> clazz) {
        try {
            @SuppressWarnings("unchecked")
            ServiceReference<T> serviceReferences[] = (ServiceReference<T>[]) bundleContext
                    .getServiceReferences(clazz.getName(), null);
            return serviceReferences;
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid exception for a null filter");
        }
    }

    /**
     * Get an OSGi service for the given class and the given filter.
     *
     * @param clazz class under which the OSGi service is registered
     * @param implementationClass the implementation class
     * @return OSGi service or null if no service can be found for the given class
     */
    protected <T, I extends T> @Nullable I getService(Class<T> clazz, Class<I> implementationClass) {
        @SuppressWarnings("unchecked")
        final I service = (I) getService(clazz, srvRef -> implementationClass.isInstance(unrefService(srvRef)));
        return service;
    }

    /**
     * Register the given object as OSGi service.
     *
     * <p>
     * The first interface is used as OSGi service interface name.
     *
     * @param service service to be registered
     * @return service registration object
     */
    protected ServiceRegistration<?> registerService(final Object service) {
        return registerService(service, getInterfaceName(service), null);
    }

    /**
     * Register the given object as OSGi service. The first interface is used as OSGi service interface name.
     *
     * @param service service to be registered
     * @param properties OSGi service properties
     * @return service registration object
     */
    protected ServiceRegistration<?> registerService(final Object service, final Dictionary<String, ?> properties) {
        return registerService(service, getInterfaceName(service), properties);
    }

    /**
     * Register the given object as OSGi service.
     *
     * <p>
     * The given interface name is used as OSGi service interface name.
     *
     * @param service service to be registered
     * @param interfaceName interface name of the OSGi service
     * @return service registration object
     */
    protected ServiceRegistration<?> registerService(final Object service, final String interfaceName) {
        return registerService(service, interfaceName, null);
    }

    /**
     * Register the given object as OSGi service.
     *
     * <p>
     * The given interface name is used as OSGi service interface name.
     *
     * @param service service to be registered
     * @param interfaceName interface name of the OSGi service
     * @param properties OSGi service properties
     * @return service registration object
     */
    protected ServiceRegistration<?> registerService(final Object service, final String interfaceName,
            final @Nullable Dictionary<String, ?> properties) {
        assertThat(interfaceName, is(notNullValue()));
        final ServiceRegistration<?> srvReg = bundleContext.registerService(interfaceName, service, properties);
        saveServiceRegistration(interfaceName, srvReg);
        return srvReg;
    }

    private void saveServiceRegistration(final String interfaceName, final ServiceRegistration<?> srvReg) {
        List<ServiceRegistration<?>> regs = registeredServices.get(interfaceName);
        if (regs == null) {
            regs = new ArrayList<>();
            registeredServices.put(interfaceName, regs);
        }
        regs.add(srvReg);
    }

    /**
     * Register the given object as OSGi service.
     *
     * <p>
     * The given interface names are used as OSGi service interface name.
     *
     * @param service service to be registered
     * @param interfaceName interface name of the OSGi service
     * @param properties OSGi service properties
     * @return service registration object
     */
    protected ServiceRegistration<?> registerService(final Object service, final String[] interfaceNames,
            final Dictionary<String, ?> properties) {
        assertThat(interfaceNames, is(notNullValue()));

        final ServiceRegistration<?> srvReg = bundleContext.registerService(interfaceNames, service, properties);

        for (final String interfaceName : interfaceNames) {
            saveServiceRegistration(interfaceName, srvReg);
        }

        return srvReg;
    }

    /**
     * Unregister an OSGi service by the given object, that was registered before.
     *
     * <p>
     * The interface name is taken from the first interface of the service object.
     *
     * @param service the service
     * @return the service registration that was unregistered or null if no service could be found
     */
    protected @Nullable ServiceRegistration<?> unregisterService(final Object service) {
        return unregisterService(getInterfaceName(service));
    }

    /**
     * Unregister an OSGi service by the given object, that was registered before.
     *
     * @param interfaceName the interface name of the service
     * @return the first service registration that was unregistered or null if no service could be found
     */
    protected @Nullable ServiceRegistration<?> unregisterService(final String interfaceName) {
        ServiceRegistration<?> reg = null;
        List<ServiceRegistration<?>> regList = registeredServices.remove(interfaceName);
        if (regList != null) {
            reg = regList.get(0);
            regList.forEach(r -> r.unregister());
        }
        return reg;
    }

    /**
     * Returns the interface name for a given service object by choosing the first interface.
     *
     * @param service service object
     * @return name of the first interface if interfaces are implemented
     * @throws IllegalArgumentException if no interface is implemented
     */
    protected String getInterfaceName(final Object service) {
        Class<?>[] classes = service.getClass().getInterfaces();
        if (classes.length >= 1) {
            return classes[0].getName();
        } else {
            throw new IllegalArgumentException(String
                    .format("The given reference (class: %s) does not implement an interface.", service.getClass()));
        }
    }

    /**
     * Registers a volatile storage service.
     */
    protected void registerVolatileStorageService() {
        registerService(new VolatileStorageService());
    }

    @After
    public void unregisterMocks() {
        registeredServices.forEach((interfaceName, services) -> services.forEach(service -> service.unregister()));
        registeredServices.clear();
    }

}
