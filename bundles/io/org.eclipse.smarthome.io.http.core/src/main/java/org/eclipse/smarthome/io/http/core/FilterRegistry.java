package org.eclipse.smarthome.io.http.core;

import java.util.List;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

public interface FilterRegistry {

    List<Filter> getFilters(HttpServletRequest request);

}
