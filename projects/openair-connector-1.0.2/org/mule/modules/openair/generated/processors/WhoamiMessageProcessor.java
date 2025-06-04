/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.DefaultMuleMessage
 *  org.mule.api.MuleEvent
 *  org.mule.api.MuleException
 *  org.mule.api.config.ConfigurationException
 *  org.mule.api.devkit.ProcessAdapter
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.lifecycle.InitialisationException
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.registry.RegistrationException
 *  org.mule.common.DefaultResult
 *  org.mule.common.FailureType
 *  org.mule.common.Result
 *  org.mule.common.Result$Status
 *  org.mule.common.metadata.ConnectorMetaDataEnabled
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.DefaultPojoMetaDataModel
 *  org.mule.common.metadata.DefaultSimpleMetaDataModel
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModel
 *  org.mule.common.metadata.MetaDataModelProperty
 *  org.mule.common.metadata.OperationMetaDataEnabled
 *  org.mule.common.metadata.datatype.DataType
 *  org.mule.common.metadata.datatype.DataTypeFactory
 *  org.mule.common.metadata.key.property.MetaDataKeyProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty$TypeScope
 *  org.mule.devkit.processor.DevkitBasedMessageProcessor
 *  org.mule.security.oauth.callback.ProcessCallback
 */
package org.mule.modules.openair.generated.processors;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.registry.RegistrationException;
import org.mule.common.DefaultResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.common.metadata.ConnectorMetaDataEnabled;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.DefaultPojoMetaDataModel;
import org.mule.common.metadata.DefaultSimpleMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModel;
import org.mule.common.metadata.MetaDataModelProperty;
import org.mule.common.metadata.OperationMetaDataEnabled;
import org.mule.common.metadata.datatype.DataType;
import org.mule.common.metadata.datatype.DataTypeFactory;
import org.mule.common.metadata.key.property.MetaDataKeyProperty;
import org.mule.common.metadata.key.property.TypeDescribingProperty;
import org.mule.devkit.3.9.0.internal.metadata.MetaDataGeneratorUtils;
import org.mule.devkit.3.9.0.internal.metadata.fixes.STUDIO7157;
import org.mule.devkit.processor.DevkitBasedMessageProcessor;
import org.mule.security.oauth.callback.ProcessCallback;

public class WhoamiMessageProcessor
extends DevkitBasedMessageProcessor
implements MessageProcessor,
OperationMetaDataEnabled {
    public WhoamiMessageProcessor(String operationName) {
        super(operationName);
    }

    public void initialise() throws InitialisationException {
    }

    public void start() throws MuleException {
        super.start();
    }

    public void stop() throws MuleException {
        super.stop();
    }

    public void dispose() {
        super.dispose();
    }

    public MuleEvent doProcess(MuleEvent event) throws Exception {
        Object moduleObject = null;
        moduleObject = this.findOrCreate(null, false, event);
        ProcessTemplate processTemplate = ((ProcessAdapter)moduleObject).getProcessTemplate();
        Object resultPayload = processTemplate.execute((ProcessCallback)new /* Unavailable Anonymous Inner Class!! */, (MessageProcessor)this, event);
        event.getMessage().setPayload(resultPayload);
        ((DefaultMuleMessage)event.getMessage()).setMimeType("application/xml");
        return event;
    }

    public Result<MetaData> getInputMetaData() {
        return new DefaultResult(null, Result.Status.SUCCESS);
    }

    public Result<MetaData> getOutputMetaData(MetaData inputMetadata) {
        DefaultMetaDataKey metaDataKey = new DefaultMetaDataKey("OpenAirWhoamiMetaData", null);
        metaDataKey.setCategory("WhoamiMetaData");
        metaDataKey.addProperty((MetaDataKeyProperty)new TypeDescribingProperty(TypeDescribingProperty.TypeScope.OUTPUT, "whoami"));
        Result<MetaData> genericMetaData = this.getGenericMetaData((MetaDataKey)metaDataKey);
        if (Result.Status.FAILURE.equals((Object)genericMetaData.getStatus())) {
            return genericMetaData;
        }
        MetaDataModel metaDataPayload = ((MetaData)genericMetaData.get()).getPayload();
        DefaultMetaDataKey keyForStudio = new DefaultMetaDataKey("OpenAirWhoamiMetaData", null);
        keyForStudio.setCategory("WhoamiMetaData");
        metaDataPayload.addProperty((MetaDataModelProperty)STUDIO7157.getStructureIdentifierMetaDataModelProperty((MetaDataKey)keyForStudio, false, false));
        MetaDataModel wrappedMetaDataModel = metaDataPayload;
        return new DefaultResult((Object)MetaDataGeneratorUtils.extractPropertiesToMetaData(wrappedMetaDataModel, (MetaData)genericMetaData.get()));
    }

    private MetaDataModel getPojoOrSimpleModel(Class clazz) {
        DataType dataType = DataTypeFactory.getInstance().getDataType(clazz);
        if (DataType.POJO.equals((Object)dataType)) {
            return new DefaultPojoMetaDataModel(clazz);
        }
        return new DefaultSimpleMetaDataModel(dataType);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public Result<MetaData> getGenericMetaData(MetaDataKey metaDataKey) {
        try {
            ConnectorMetaDataEnabled connector = (ConnectorMetaDataEnabled)this.findOrCreate(null, false, null);
            try {
                Result metadata = connector.getMetaData(metaDataKey);
                if (Result.Status.FAILURE.equals((Object)metadata.getStatus())) {
                    return metadata;
                }
                if (metadata.get() == null) {
                    return new DefaultResult(null, Result.Status.FAILURE, "There was an error processing metadata at OpenAirConnector at whoami retrieving was successful but result is null");
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
}
