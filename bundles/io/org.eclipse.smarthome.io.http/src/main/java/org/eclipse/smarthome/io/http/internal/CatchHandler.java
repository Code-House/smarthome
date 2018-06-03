package org.eclipse.smarthome.io.http.internal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.smarthome.io.http.Handler;
import org.eclipse.smarthome.io.http.HandlerContext;

public class CatchHandler implements Handler {

    private final Handler delegate;

    public CatchHandler(Handler delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getPriority() {
        return delegate.getPriority();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, HandlerContext context) {
        try {
            delegate.handle(request, response, context);
        } catch (Exception e) {
            if (!context.hasError()) {
                context.error(e);
            }
        }
    }

    @Override
    public void handleError(HttpServletRequest request, HttpServletResponse response, HandlerContext context) {
        delegate.handleError(request, response, context);
    }

}
