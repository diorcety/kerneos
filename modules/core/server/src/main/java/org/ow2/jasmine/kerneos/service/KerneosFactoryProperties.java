package org.ow2.jasmine.kerneos.service;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface KerneosFactoryProperties {

    KerneosFactory.SCOPE scope();
}
