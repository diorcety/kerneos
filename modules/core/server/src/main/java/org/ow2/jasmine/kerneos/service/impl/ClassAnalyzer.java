/**
 * Kerneos
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ClassAnalyzer {

    private List<Class> classList = new LinkedList<Class>();

    /**
     * Add a class to the list
     * @param cls The class to add to the list
     * @return The ClassAnalyzer object itself
     */
    public ClassAnalyzer add(final Class cls) {
        // No already registred class
        if (!classList.contains(cls)) {
            classList.add(cls);
        }
        return this;
    }

    /**
     * Analyze recursively the classes used by the public methods and fields
     * @param cls The class to analyze
     * @return The ClassAnalyzer object itself
     */
    public ClassAnalyzer analyze(final Class cls) {
        analyzeClass(cls);
        return this;
    }

    /**
     * Compile the list to a class array
     * @return An array containing the class list
     */
    public Class[] compile() {
        Class[] ret = new Class[classList.size()];
        return classList.toArray(ret);
    }

    /**
     * Analyze recursively the classes used by the public methods and fields
     * @param cls The class to analyze
     */
    private void analyzeClass(final Class cls) {
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
            analyzeClass(clazz);
        }
    }

    /**
     * Add the class to the list if it is usefull
     * @param list The list where the class will be added
     * @param cls The class to add
     */
    private void getClasses(final List<Class> list, final Class cls) {
        if (isUsefull(list, cls)) {
            list.add(cls);
        }
    }

    /**
     * Explore the type for finding used classes
     * @param list The list where the classes will be added
     * @param type The type to explore
     */
    private void getClasses(final List<Class> list, final Type type) {
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

    /**
     * Add the class to the list if it is usefull
     * @param list The list where the classes will be added
     * @param cls The class to add
     * @return True if the class is usefull
     */
    private boolean isUsefull(final List<Class> list, final Class cls) {

        // No already registred class
        if (classList.contains(cls)) {
            return false;
        }
        if (list.contains(cls)) {
            return false;
        }

        // No class from the RT
        if (cls.getClassLoader() == null) {
            return false;
        }

        // Only class
        if (cls.isInterface()) {
            return false;
        }

        // Only serializable
        boolean serializable = false;
        for (Class<?> icls : cls.getInterfaces()) {
            if (icls == Serializable.class) {
                serializable = true;
            }
        }
        if (!serializable) {
            return false;
        }

        return true;
    }
}
