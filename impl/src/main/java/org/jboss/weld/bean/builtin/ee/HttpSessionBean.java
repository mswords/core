/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.weld.bean.builtin.ee;

import java.lang.annotation.Annotation;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.servlet.http.HttpSession;

import org.jboss.weld.bean.builtin.AbstractStaticallyDecorableBuiltInBean;
import org.jboss.weld.exceptions.IllegalStateException;
import org.jboss.weld.logging.messages.ServletMessage;
import org.jboss.weld.manager.BeanManagerImpl;

/**
 * Built-in bean exposing {@link HttpSession}.
 *
 * @author Jozef Hartinger
 *
 */
public class HttpSessionBean extends AbstractStaticallyDecorableBuiltInBean<HttpSession> {

    private final HttpServletRequestBean requestBean;

    public HttpSessionBean(HttpServletRequestBean requestBean, BeanManagerImpl manager) {
        super(HttpSessionBean.class.getName(), manager, HttpSession.class);
        this.requestBean = requestBean;
    }

    @Override
    protected HttpSession newInstance(InjectionPoint ip, CreationalContext<HttpSession> creationalContext) {
        try {
            return requestBean.create(null).getSession();
        } catch (IllegalStateException e) {
            throw new IllegalStateException(ServletMessage.CANNOT_INJECT_OBJECT_OUTSIDE_OF_SERVLET_REQUEST, e, HttpSession.class.getSimpleName());
        }
    }

    @Override
    public void destroy(HttpSession instance, CreationalContext<HttpSession> creationalContext) {
        // noop
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return SessionScoped.class;
    }
}
