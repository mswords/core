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
package org.jboss.weld.tests.contexts.conversation.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/servlet/*")
public class Servlet extends HttpServlet {

    @Inject
    private Message message;

    @Inject
    private Conversation conversation;

    @Inject
    private DestroyedConversationObserver observer;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/display")) {
            printInfo(resp.getWriter());
        } else if (uri.endsWith("/redirect")) {
            resp.sendRedirect("servlet/display"); // do a redirect, the cid is not propagated;
        } else if (uri.endsWith("/begin")) {
            conversation.begin();
            printInfo(resp.getWriter());
        } else if (uri.endsWith("/end")) {
            conversation.end();
            printInfo(resp.getWriter());
        } else if (uri.endsWith("/set")) {
            setMessage(req);
            printInfo(resp.getWriter());
        } else if (uri.endsWith("/invalidateSession")) {
            observer.reset();
            req.getSession().invalidate();
            printInfo(resp.getWriter());
        } else if (uri.endsWith("/listConversationsDestroyedWhileBeingAssociated")) {
            PrintWriter writer = resp.getWriter();
            writer.append("ConversationsDestroyedWhileBeingAssociated: ");
            printSessionIds(writer, observer.getAssociatedConversationIds());
        } else if (uri.endsWith("/listConversationsDestroyedWhileBeingDisassociated")) {
            PrintWriter writer = resp.getWriter();
            writer.append("ConversationsDestroyedWhileBeingDisassociated: ");
            printSessionIds(writer, observer.getDisassociatedConversationIds());
        } else {
            resp.setStatus(404);
        }
        resp.setContentType("text/plain");
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        if (uri.endsWith("/set")) {
            setMessage(req);
            printInfo(resp.getWriter());
        } else {
            resp.setStatus(404);
        }
        resp.setContentType("text/plain");
    }

    private void printInfo(PrintWriter writer) {
        writer.append("message: " + message.getValue());
        writer.append("\n");
        writer.append("cid: [" + conversation.getId());
        writer.append("]");
        writer.append("\n");
        writer.append("transient: " + conversation.isTransient());
        writer.append("\n");
    }

    private void printSessionIds(PrintWriter writer, Set<String> ids) {
        for (String id : ids) {
            writer.append("<" + id + ">");
        }
    }
    
    private void setMessage(HttpServletRequest request) {
        String value = request.getParameter("message");
        if (value == null) {
            throw new IllegalArgumentException("message must be specified");
        }
        message.setValue(value);
    }
}
