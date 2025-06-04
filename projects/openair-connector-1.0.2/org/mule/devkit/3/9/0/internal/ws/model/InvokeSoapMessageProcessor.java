/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.config.ConfigurationException
 *  org.mule.api.devkit.ProcessAdapter
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.registry.RegistrationException
 *  org.mule.common.DefaultResult
 *  org.mule.common.FailureType
 *  org.mule.common.Result
 *  org.mule.common.Result$Status
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.OperationMetaDataEnabled
 *  org.mule.common.metadata.key.property.MetaDataKeyProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty$TypeScope
 *  org.mule.devkit.processor.DevkitBasedMessageProcessor
 *  org.mule.security.oauth.callback.ProcessCallback
 */
package org.mule.devkit.3.9.0.internal.ws.model;

import java.lang.reflect.Type;
import java.util.List;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.config.ConfigurationException;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.RegistrationException;
import org.mule.common.DefaultResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.OperationMetaDataEnabled;
import org.mule.common.metadata.key.property.MetaDataKeyProperty;
import org.mule.common.metadata.key.property.TypeDescribingProperty;
import org.mule.devkit.3.9.0.internal.ws.common.WsdlAdapter;
import org.mule.devkit.3.9.0.internal.ws.metadata.WsdlMetaDataDescriptor;
import org.mule.devkit.processor.DevkitBasedMessageProcessor;
import org.mule.security.oauth.callback.ProcessCallback;

public class InvokeSoapMessageProcessor
extends DevkitBasedMessageProcessor
implements MessageProcessor,
OperationMetaDataEnabled {
    protected Object type;

    public InvokeSoapMessageProcessor(String operationName) {
        super(operationName);
    }

    public MuleEvent doProcess(MuleEvent event) throws Exception {
        WsdlAdapter moduleObject = (WsdlAdapter)this.findOrCreate(null, false, event);
        String _transformedType = (String)this.evaluateAndTransform(this.getMuleContext(), event, (Type)((Object)String.class), null, this.type);
        ProcessTemplate processTemplate = ((ProcessAdapter)moduleObject).getProcessTemplate();
        List<Class<? extends Exception>> managedExceptions = moduleObject.managedExceptions();
        Object resultPayload = processTemplate.execute((ProcessCallback)new /* Unavailable Anonymous Inner Class!! */, (MessageProcessor)this, event);
        return (MuleEvent)resultPayload;
    }

    public Result<MetaData> getInputMetaData() {
        return this.getMetaDataResult(TypeDescribingProperty.TypeScope.INPUT);
    }

    public Result<MetaData> getOutputMetaData(MetaData inputMetadata) {
        return this.getMetaDataResult(TypeDescribingProperty.TypeScope.OUTPUT);
    }

    private Result<MetaData> getMetaDataResult(TypeDescribingProperty.TypeScope scope) {
        if (this.type == null || this.type.toString() == null) {
            return new DefaultResult(null, Result.Status.FAILURE, "There was an error retrieving metadata from parameter [type] at processor invoke at connector");
        }
        DefaultMetaDataKey metaDataKey = new DefaultMetaDataKey(this.type.toString(), null);
        metaDataKey.addProperty((MetaDataKeyProperty)new TypeDescribingProperty(scope, "invoke"));
        Result<MetaData> genericMetaData = this.getGenericMetaData((MetaDataKey)metaDataKey);
        return genericMetaData;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Result<MetaData> getGenericMetaData(MetaDataKey metaDataKey) {
        try {
            WsdlMetaDataDescriptor invokeMessageProcessorMetaDataDescriptor = new WsdlMetaDataDescriptor();
            WsdlAdapter moduleObject = (WsdlAdapter)this.findOrCreate(null, false, null);
            try {
                Result<MetaData> metadata = invokeMessageProcessorMetaDataDescriptor.getMetaData(metaDataKey, moduleObject);
                if (Result.Status.FAILURE.equals((Object)metadata.getStatus())) {
                    return metadata;
                }
                if (metadata.get() == null) {
                    return new DefaultResult(null, Result.Status.FAILURE, "There was an error processing metadata at WsdlConnector at invoke retrieving was successful but result is null");
                }
                return metadata;
            }
            catch (Exception e) {
                return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
            }
        }
        catch (ClassCastException cast) {
            return new DefaultResult(null, Result.Status.FAILURE, "There was an error getting metadata, there was no connection manager available. Maybe you're trying to use metadata from an Oauth connector");
        }
        catch (ConfigurationException e) {
            return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
        }
        catch (RegistrationException e) {
            return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
        }
        catch (IllegalAccessException e) {
            return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
        }
        catch (InstantiationException e) {
            return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
        }
        catch (Exception e) {
            return new DefaultResult(null, Result.Status.FAILURE, e.getMessage(), FailureType.UNSPECIFIED, (Throwable)e);
        }
    }

    public void setType(Object value) {
        this.type = value;
    }

    static /* synthetic */ MuleContext access$000(InvokeSoapMessageProcessor x0) {
        return x0.muleContext;
    }

    static /* synthetic */ MuleContext access$100(InvokeSoapMessageProcessor x0) {
        return x0.muleContext;
    }
}
