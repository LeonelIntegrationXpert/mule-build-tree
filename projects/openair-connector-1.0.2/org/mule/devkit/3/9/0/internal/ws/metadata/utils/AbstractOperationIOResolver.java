/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.extensions.soap.SOAPBody
 *  javax.wsdl.extensions.soap.SOAPHeader
 */
package org.mule.devkit.3.9.0.internal.ws.metadata.utils;

import com.google.common.base.Optional;
import java.util.ArrayList;
import java.util.List;
import javax.wsdl.BindingOperation;
import javax.wsdl.extensions.soap.SOAPBody;
import javax.wsdl.extensions.soap.SOAPHeader;
import org.mule.devkit.3.9.0.internal.ws.metadata.utils.OperationIOResolver;

public abstract class AbstractOperationIOResolver
implements OperationIOResolver {
    @Override
    public List<SOAPHeader> getHeaders(BindingOperation bindingOperation) {
        ArrayList<SOAPHeader> result = new ArrayList<SOAPHeader>();
        Optional<List> extensibilityElementsOptional = this.extensibilityElements(bindingOperation);
        if (extensibilityElementsOptional.isPresent()) {
            List extensibilityElements = (List)extensibilityElementsOptional.get();
            for (Object element : extensibilityElements) {
                if (element == null || !(element instanceof SOAPHeader)) continue;
                result.add((SOAPHeader)element);
            }
        }
        return result;
    }

    @Override
    public Optional<String> getBodyPartName(BindingOperation bindingOperation) {
        Optional<List> listOptional = this.extensibilityElements(bindingOperation);
        if (!listOptional.isPresent()) {
            return Optional.absent();
        }
        for (Object object : (List)listOptional.get()) {
            if (!(object instanceof SOAPBody)) continue;
            SOAPBody soapBody = (SOAPBody)object;
            List soapBodyParts = soapBody.getParts();
            if (soapBodyParts == null || soapBodyParts.isEmpty()) {
                return Optional.absent();
            }
            if (soapBodyParts.size() > 1) {
                throw new RuntimeException("Warning: Operation Messages With More Than 1 Part Are Not Supported.");
            }
            String partName = (String)soapBodyParts.get(0);
            return Optional.of((Object)partName);
        }
        return Optional.absent();
    }

    protected abstract Optional<List> extensibilityElements(BindingOperation var1);
}
