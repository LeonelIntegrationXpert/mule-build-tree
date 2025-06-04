/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 *  javax.wsdl.extensions.soap.SOAPHeader
 */
package org.mule.devkit.3.9.0.internal.ws.metadata.utils;

import com.google.common.base.Optional;
import java.util.List;
import javax.wsdl.BindingOperation;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.extensions.soap.SOAPHeader;

public interface OperationIOResolver {
    public Optional<Message> getMessage(Operation var1);

    public List<SOAPHeader> getHeaders(BindingOperation var1);

    public Optional<String> getBodyPartName(BindingOperation var1);
}
