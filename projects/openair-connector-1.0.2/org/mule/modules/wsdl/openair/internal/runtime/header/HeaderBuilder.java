/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.wsdl.openair.internal.runtime.header;

import javax.xml.soap.SOAPHeader;
import org.jetbrains.annotations.NotNull;
import org.mule.modules.wsdl.openair.internal.runtime.ServiceDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.header.SoapHeaderException;

public interface HeaderBuilder {
    public void build(@NotNull SOAPHeader var1, @NotNull ServiceDefinition var2) throws SoapHeaderException;
}
