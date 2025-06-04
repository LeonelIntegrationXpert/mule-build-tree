/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Message
 *  javax.wsdl.Operation
 */
package org.mule.devkit.3.9.0.internal.ws.metadata.utils;

import com.google.common.base.Optional;
import java.util.List;
import javax.wsdl.BindingOperation;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.AbstractOperationIOResolver;

public class InputOperationResolver
extends AbstractOperationIOResolver {
    @Override
    public Optional<Message> getMessage(Operation operation) {
        if (operation == null || operation.getInput() == null) {
            return Optional.absent();
        }
        return Optional.fromNullable((Object)operation.getInput().getMessage());
    }

    @Override
    protected Optional<List> extensibilityElements(BindingOperation bindingOperation) {
        if (bindingOperation == null) {
            return Optional.absent();
        }
        return Optional.fromNullable((Object)bindingOperation.getBindingInput().getExtensibilityElements());
    }
}
