package org.eclipse.smarthome.io.http.core.internal.filter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.smarthome.io.http.core.FilterRegistry;
import org.eclipse.smarthome.io.http.core.internal.Activator;
import org.eclipse.smarthome.io.http.core.internal.filter.DefaultFilterRegistry.FilterEntry;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DefaultFilterRegistry extends ServiceTracker<Filter, FilterEntry> implements FilterRegistry {

    public DefaultFilterRegistry() {
        super(Activator.context, Filter.class.getName(), null);
    }

    @Override
    public List<Filter> getFilters(HttpServletRequest request) {
        String uri = request.getRequestURI();

        return getTracked().values().stream().filter(entry -> entry.matches(uri))
                .filter(entry -> entry.initialize(request)).map(FilterEntry::getFilter).collect(Collectors.toList());
    }

    @Override
    public FilterEntry addingService(ServiceReference<Filter> reference) {
        Object property = reference.getProperty(Constants.HTTP_WHITEBOARD_FILTER_PATTERN);
        if (property instanceof String) {
            String pattern = (String) property;
            Filter service = context.getService(reference);
            return new FilterEntry(pattern, service, getName(reference).orElse(service.getClass().getName()),
                    getInitParameters(reference));
        }
        return null;
    }

    private Optional<String> getName(ServiceReference<Filter> reference) {
        Object name = reference.getProperty(Constants.FILTER_NAME);

        return Optional.ofNullable(name).filter(String.class::isInstance).map(String.class::cast);
    }

    private Hashtable<String, String> getInitParameters(ServiceReference<Filter> reference) {
        Hashtable<String, String> properties = new Hashtable<>();
        for (String key : reference.getPropertyKeys()) {
            if (key.startsWith(Constants.FILTER_INIT_PARAM_PREFIX)) {
                Object value = reference.getProperty(key);
                if (value instanceof String) {
                    properties.put(key.substring(Constants.FILTER_INIT_PARAM_PREFIX.length()), (String) value);
                } else {
                    properties.put(key.substring(Constants.FILTER_INIT_PARAM_PREFIX.length()), value.toString());
                }
            }
        }

        return properties;
    }

    @Activate
    void start() {
        super.open();
    }

    @Deactivate
    void stop() {
        super.close();
    }

    static class FilterEntry {

        private final class StaticFilterConfig implements FilterConfig {
            private final HttpServletRequest request;

            private StaticFilterConfig(HttpServletRequest request) {
                this.request = request;
            }

            @Override
            public ServletContext getServletContext() {
                return request.getServletContext();
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return properties.elements();
            }

            @Override
            public String getInitParameter(String name) {
                return properties.get(name);
            }

            @Override
            public String getFilterName() {
                return name;
            }
        }

        private final Logger logger = LoggerFactory.getLogger(FilterEntry.class);
        private final String pattern;
        private final Filter filter;
        private final String name;
        private final Hashtable<String, String> properties;
        private final AtomicBoolean initialized = new AtomicBoolean(false);
        private final AtomicBoolean failed = new AtomicBoolean(false);

        public FilterEntry(String pattern, Filter filter, String name, Hashtable<String, String> properties) {
            this.pattern = pattern;
            this.filter = filter;
            this.name = name;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public Filter getFilter() {
            return filter;
        }

        public boolean matches(String uri) {
            String regexp = "^" + pattern.replace("*", ".*?") + "$";
            return uri.matches(regexp);
        }

        public boolean initialize(HttpServletRequest request) {
            if (failed.get()) {
                return false;
            }

            if (initialized.compareAndSet(false, true)) {
                try {
                    filter.init(new StaticFilterConfig(request));
                    return true;
                } catch (ServletException e) {
                    logger.error("Could not initialize filter {}", name, e);
                    failed.set(true);
                    return false;
                }
            }
            return true;
        }

    }

}
