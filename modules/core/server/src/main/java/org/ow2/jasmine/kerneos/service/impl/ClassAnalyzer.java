/**
 * JASMINe
 * Copyright (C) 2009 Bull S.A.S.
 * Contact: jasmine@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */

package org.ow2.jasmine.kerneos.service.impl;

import java.io.Serializable;
import java.lang.Class;
import java.lang.reflect.*;
import java.util.LinkedList;
import java.util.List;

public class ClassAnalyzer {

    private List<Class> classList = new LinkedList<Class>();

    public ClassAnalyzer add(Class cls) {
        // No already registred class
        if (!classList.contains(cls)) {
            classList.add(cls);
        }
        return this;
    }

    public ClassAnalyzer analyze(Class cls) {
        _analyzeClass(cls);
        return this;
    }

    public Class[] compile() {
        Class[] ret = new Class[classList.size()];
        return classList.toArray(ret);
    }

    private void _analyzeClass(Class cls) {
        List<Class> list = new LinkedList<Class>();

        // Get Methods
        for (Method method : cls.getMethods()) {
            if ((method.getModifiers() & Modifier.PUBLIC) != 0) {
                // Get Parameters
                for (Class mcls : method.getParameterTypes()) {
                    getClasses(list, mcls);
                }
                for (Type type : method.getGenericParameterTypes()) {
                    getClasses(list, type);
                }

                // Get Return Type
                getClasses(list, method.getReturnType());
                getClasses(list, method.getGenericReturnType());
            }
        }

        // Get Fields
        for (Field field : cls.getFields()) {
            if ((field.getModifiers() & Modifier.PUBLIC) != 0) {
                getClasses(list, field.getType());
                getClasses(list, field.getGenericType());
            }
        }

        // Add to the global list
        classList.addAll(list);

        // Recursive class discovery
        for (Class clazz : list) {
            _analyzeClass(clazz);
        }
    }

    private void getClasses(List<Class> list, Class cls) {
        if (isUsefull(list, cls))
            list.add(cls);
    }

    private void getClasses(List<Class> list, Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            if (pt.getRawType() instanceof Class) {
                Class clazz = (Class) pt.getRawType();
                if (isUsefull(list, clazz)) {
                    list.add(clazz);
                }
            }
            Type[] parameters = pt.getActualTypeArguments();
            for (Type ptype : parameters) {
                if (ptype instanceof Class) {
                    Class clazz = (Class) ptype;
                    if (isUsefull(list, clazz)) {
                        list.add(clazz);
                    }
                } else {
                    getClasses(list, ptype);
                }
            }

        }
    }

    // Used during analyze for filter
    private boolean isUsefull(List<Class> list, Class cls) {

        // No already registred class
        if (classList.contains(cls)) {
            return false;
        }
        if (list.contains(cls)) {
            return false;
        }

        // No class from the RT
        if (cls.getClassLoader() == null)
            return false;

        // Only class
        if (cls.isInterface())
            return false;

        // Only serializable
        boolean serializable = false;
        for (Class<?> icls : cls.getInterfaces()) {
            if (icls == Serializable.class) {
                serializable = true;
            }
        }
        if (!serializable)
            return false;

        return true;
    }
}
