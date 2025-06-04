/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.pool.KeyedObjectPool
 *  org.mule.api.MetadataAware
 *  org.mule.api.MuleContext
 *  org.mule.api.MuleEvent
 *  org.mule.api.context.MuleContextAware
 *  org.mule.api.devkit.ProcessAdapter
 *  org.mule.api.devkit.ProcessTemplate
 *  org.mule.api.devkit.capability.Capabilities
 *  org.mule.api.devkit.capability.ModuleCapability
 *  org.mule.api.lifecycle.Disposable
 *  org.mule.api.lifecycle.Initialisable
 *  org.mule.api.processor.MessageProcessor
 *  org.mule.api.retry.RetryPolicyTemplate
 *  org.mule.common.DefaultResult
 *  org.mule.common.DefaultTestResult
 *  org.mule.common.FailureType
 *  org.mule.common.Result
 *  org.mule.common.Result$Status
 *  org.mule.common.TestResult
 *  org.mule.common.Testable
 *  org.mule.common.metadata.ConnectorMetaDataEnabled
 *  org.mule.common.metadata.DefaultMetaDataKey
 *  org.mule.common.metadata.MetaData
 *  org.mule.common.metadata.MetaDataFailureType
 *  org.mule.common.metadata.MetaDataKey
 *  org.mule.common.metadata.MetaDataModelProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty
 *  org.mule.common.metadata.key.property.TypeDescribingProperty$TypeScope
 *  org.mule.common.metadata.property.StructureIdentifierMetaDataModelProperty
 *  org.mule.config.PoolingProfile
 *  org.mule.devkit.processor.ExpressionEvaluatorSupport
 */
package org.mule.modules.openair.generated.connectivity;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool.KeyedObjectPool;
import org.mule.api.MetadataAware;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.context.MuleContextAware;
import org.mule.api.devkit.ProcessAdapter;
import org.mule.api.devkit.ProcessTemplate;
import org.mule.api.devkit.capability.Capabilities;
import org.mule.api.devkit.capability.ModuleCapability;
import org.mule.api.lifecycle.Disposable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.processor.MessageProcessor;
import org.mule.api.retry.RetryPolicyTemplate;
import org.mule.common.DefaultResult;
import org.mule.common.DefaultTestResult;
import org.mule.common.FailureType;
import org.mule.common.Result;
import org.mule.common.TestResult;
import org.mule.common.Testable;
import org.mule.common.metadata.ConnectorMetaDataEnabled;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataFailureType;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.MetaDataModelProperty;
import org.mule.common.metadata.key.property.TypeDescribingProperty;
import org.mule.common.metadata.property.StructureIdentifierMetaDataModelProperty;
import org.mule.config.PoolingProfile;
import org.mule.devkit.3.9.0.api.exception.ConfigurationWarning;
import org.mule.devkit.3.9.0.api.lifecycle.LifeCycleManager;
import org.mule.devkit.3.9.0.api.lifecycle.MuleContextAwareManager;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectionManager;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorAdapter;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementConnectorFactory;
import org.mule.devkit.3.9.0.internal.connection.management.ConnectionManagementProcessTemplate;
import org.mule.devkit.3.9.0.internal.connection.management.UnableToAcquireConnectionException;
import org.mule.devkit.3.9.0.internal.connectivity.ConnectivityTestingErrorHandler;
import org.mule.devkit.3.9.0.internal.metadata.MetaDataGeneratorUtils;
import org.mule.devkit.processor.ExpressionEvaluatorSupport;
import org.mule.modules.openair.config.Config;
import org.mule.modules.openair.generated.adapters.OpenAirConnectorConnectionManagementAdapter;
import org.mule.modules.openair.generated.connectivity.ConfigOpenAirConnectorAdapter;
import org.mule.modules.openair.generated.connectivity.ConnectionManagementConfigOpenAirConnectorConnectionKey;
import org.mule.modules.openair.generated.pooling.DevkitGenericKeyedObjectPool;
import org.mule.modules.openair.metadata.AddMetaData;
import org.mule.modules.openair.metadata.CreateAccountMetaData;
import org.mule.modules.openair.metadata.CreateUserMetaData;
import org.mule.modules.openair.metadata.DeleteMetaData;
import org.mule.modules.openair.metadata.GetCrystalInfoMetaData;
import org.mule.modules.openair.metadata.MakeurlMetaData;
import org.mule.modules.openair.metadata.ModifyMetaData;
import org.mule.modules.openair.metadata.ReadMetaData;
import org.mule.modules.openair.metadata.RunReportMetaData;
import org.mule.modules.openair.metadata.ServerTimeMetaData;
import org.mule.modules.openair.metadata.ServerTimeWithTimezoneMetaData;
import org.mule.modules.openair.metadata.SubmitMetaData;
import org.mule.modules.openair.metadata.UpsertMetaData;
import org.mule.modules.openair.metadata.WhoamiMetaData;

public class OpenAirConnectorConfigConnectionManagementConnectionManager
extends ExpressionEvaluatorSupport
implements MetadataAware,
MuleContextAware,
ProcessAdapter<OpenAirConnectorConnectionManagementAdapter>,
Capabilities,
Disposable,
Initialisable,
Testable,
ConnectorMetaDataEnabled,
ConnectionManagementConnectionManager<ConnectionManagementConfigOpenAirConnectorConnectionKey, OpenAirConnectorConnectionManagementAdapter, Config> {
    private String company;
    private String username;
    private String password;
    private String apiNamespace;
    private String apiKey;
    private String endpoint;
    private Integer connectionTimeout;
    private Integer readTimeout;
    protected MuleContext muleContext;
    private KeyedObjectPool connectionPool;
    protected PoolingProfile poolingProfile;
    protected RetryPolicyTemplate retryPolicyTemplate;
    private static final String MODULE_NAME = "OpenAir";
    private static final String MODULE_VERSION = "1.0.2";
    private static final String DEVKIT_VERSION = "3.9.0";
    private static final String DEVKIT_BUILD = "UNNAMED.2793.f49b6c7";
    private static final String MIN_MULE_VERSION = "3.7";

    public void setUsername(String value) {
        this.username = value;
    }

    public String getUsername() {
        return this.username;
    }

    public void setApiNamespace(String value) {
        this.apiNamespace = value;
    }

    public String getApiNamespace() {
        return this.apiNamespace;
    }

    public void setCompany(String value) {
        this.company = value;
    }

    public String getCompany() {
        return this.company;
    }

    public void setPassword(String value) {
        this.password = value;
    }

    public String getPassword() {
        return this.password;
    }

    public void setApiKey(String value) {
        this.apiKey = value;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setEndpoint(String value) {
        this.endpoint = value;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setConnectionTimeout(Integer value) {
        this.connectionTimeout = value;
    }

    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setReadTimeout(Integer value) {
        this.readTimeout = value;
    }

    public Integer getReadTimeout() {
        return this.readTimeout;
    }

    public void setMuleContext(MuleContext value) {
        this.muleContext = value;
    }

    @Override
    public MuleContext getMuleContext() {
        return this.muleContext;
    }

    public void setPoolingProfile(PoolingProfile value) {
        this.poolingProfile = value;
    }

    public PoolingProfile getPoolingProfile() {
        return this.poolingProfile;
    }

    public void setRetryPolicyTemplate(RetryPolicyTemplate value) {
        this.retryPolicyTemplate = value;
    }

    @Override
    public RetryPolicyTemplate getRetryPolicyTemplate() {
        return this.retryPolicyTemplate;
    }

    public void initialise() {
        this.connectionPool = new DevkitGenericKeyedObjectPool(new ConnectionManagementConnectorFactory(this), this.poolingProfile);
        if (this.retryPolicyTemplate == null) {
            this.retryPolicyTemplate = (RetryPolicyTemplate)this.muleContext.getRegistry().lookupObject("_defaultRetryPolicyTemplate");
        }
    }

    public void dispose() {
        try {
            this.connectionPool.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public OpenAirConnectorConnectionManagementAdapter acquireConnection(ConnectionManagementConfigOpenAirConnectorConnectionKey key) throws Exception {
        return (OpenAirConnectorConnectionManagementAdapter)this.connectionPool.borrowObject((Object)key);
    }

    @Override
    public void releaseConnection(ConnectionManagementConfigOpenAirConnectorConnectionKey key, OpenAirConnectorConnectionManagementAdapter connection) throws Exception {
        this.connectionPool.returnObject((Object)key, (Object)connection);
    }

    @Override
    public void destroyConnection(ConnectionManagementConfigOpenAirConnectorConnectionKey key, OpenAirConnectorConnectionManagementAdapter connection) throws Exception {
        this.connectionPool.invalidateObject((Object)key, (Object)connection);
    }

    public boolean isCapableOf(ModuleCapability capability) {
        if (capability == ModuleCapability.LIFECYCLE_CAPABLE) {
            return true;
        }
        return capability == ModuleCapability.CONNECTION_MANAGEMENT_CAPABLE;
    }

    public <P> ProcessTemplate<P, OpenAirConnectorConnectionManagementAdapter> getProcessTemplate() {
        return new ConnectionManagementProcessTemplate(this, this.muleContext);
    }

    @Override
    public ConnectionManagementConfigOpenAirConnectorConnectionKey getDefaultConnectionKey() {
        return new ConnectionManagementConfigOpenAirConnectorConnectionKey(this.getCompany(), this.getUsername(), this.getPassword(), this.getApiNamespace(), this.getApiKey());
    }

    @Override
    public ConnectionManagementConfigOpenAirConnectorConnectionKey getEvaluatedConnectionKey(MuleEvent event) throws Exception {
        if (event != null) {
            String _transformedCompany = (String)this.evaluateAndTransform(this.muleContext, event, this.getClass().getDeclaredField("company").getGenericType(), null, this.getCompany());
            if (_transformedCompany == null) {
                throw new UnableToAcquireConnectionException("Parameter company in method connect can't be null because is not @Optional");
            }
            String _transformedUsername = (String)this.evaluateAndTransform(this.muleContext, event, this.getClass().getDeclaredField("username").getGenericType(), null, this.getUsername());
            if (_transformedUsername == null) {
                throw new UnableToAcquireConnectionException("Parameter username in method connect can't be null because is not @Optional");
            }
            String _transformedPassword = (String)this.evaluateAndTransform(this.muleContext, event, this.getClass().getDeclaredField("password").getGenericType(), null, this.getPassword());
            if (_transformedPassword == null) {
                throw new UnableToAcquireConnectionException("Parameter password in method connect can't be null because is not @Optional");
            }
            String _transformedApiNamespace = (String)this.evaluateAndTransform(this.muleContext, event, this.getClass().getDeclaredField("apiNamespace").getGenericType(), null, this.getApiNamespace());
            if (_transformedApiNamespace == null) {
                throw new UnableToAcquireConnectionException("Parameter apiNamespace in method connect can't be null because is not @Optional");
            }
            String _transformedApiKey = (String)this.evaluateAndTransform(this.muleContext, event, this.getClass().getDeclaredField("apiKey").getGenericType(), null, this.getApiKey());
            if (_transformedApiKey == null) {
                throw new UnableToAcquireConnectionException("Parameter apiKey in method connect can't be null because is not @Optional");
            }
            return new ConnectionManagementConfigOpenAirConnectorConnectionKey(_transformedCompany, _transformedUsername, _transformedPassword, _transformedApiNamespace, _transformedApiKey);
        }
        return this.getDefaultConnectionKey();
    }

    public String getModuleName() {
        return MODULE_NAME;
    }

    public String getModuleVersion() {
        return MODULE_VERSION;
    }

    public String getDevkitVersion() {
        return DEVKIT_VERSION;
    }

    public String getDevkitBuild() {
        return DEVKIT_BUILD;
    }

    public String getMinMuleVersion() {
        return MIN_MULE_VERSION;
    }

    @Override
    public ConnectionManagementConfigOpenAirConnectorConnectionKey getConnectionKey(MessageProcessor messageProcessor, MuleEvent event) throws Exception {
        return this.getEvaluatedConnectionKey(event);
    }

    @Override
    public ConnectionManagementConnectionAdapter newConnection() {
        ConfigOpenAirConnectorAdapter connection = new ConfigOpenAirConnectorAdapter();
        connection.setEndpoint(this.getEndpoint());
        connection.setConnectionTimeout(this.getConnectionTimeout());
        connection.setReadTimeout(this.getReadTimeout());
        return connection;
    }

    @Override
    public ConnectionManagementConnectorAdapter newConnector(ConnectionManagementConnectionAdapter<Config, ConnectionManagementConfigOpenAirConnectorConnectionKey> connection) {
        OpenAirConnectorConnectionManagementAdapter connector = new OpenAirConnectorConnectionManagementAdapter();
        connector.setConfig(connection.getStrategy());
        return connector;
    }

    @Override
    public ConnectionManagementConnectionAdapter getConnectionAdapter(ConnectionManagementConnectorAdapter adapter) {
        OpenAirConnectorConnectionManagementAdapter connector = (OpenAirConnectorConnectionManagementAdapter)adapter;
        ConnectionManagementConnectionAdapter strategy = (ConnectionManagementConnectionAdapter)((Object)connector.getConfig());
        return strategy;
    }

    public TestResult test() {
        try {
            ConfigOpenAirConnectorAdapter strategy = (ConfigOpenAirConnectorAdapter)this.newConnection();
            MuleContextAwareManager.setMuleContext(strategy, this.muleContext);
            LifeCycleManager.executeInitialiseAndStart(strategy);
            ConnectionManagementConnectorAdapter connectorAdapter = this.newConnector(strategy);
            MuleContextAwareManager.setMuleContext(connectorAdapter, this.muleContext);
            LifeCycleManager.executeInitialiseAndStart(connectorAdapter);
            strategy.test(this.getDefaultConnectionKey());
            return new DefaultTestResult(Result.Status.SUCCESS);
        }
        catch (ConfigurationWarning warning) {
            return (DefaultTestResult)ConnectivityTestingErrorHandler.buildWarningTestResult((Exception)((Object)warning));
        }
        catch (Exception e) {
            return (DefaultTestResult)ConnectivityTestingErrorHandler.buildFailureTestResult(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public Result<List<MetaDataKey>> getMetaDataKeys() {
        OpenAirConnectorConnectionManagementAdapter connection = null;
        ConnectionManagementConfigOpenAirConnectorConnectionKey key = this.getDefaultConnectionKey();
        try {
            connection = this.acquireConnection(key);
            try {
                ArrayList<MetaDataKey> gatheredMetaDataKeys = new ArrayList<MetaDataKey>();
                RunReportMetaData runReportMetaData = new RunReportMetaData();
                runReportMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(runReportMetaData.getMetadataKeys(), "RunReportMetaData"));
                WhoamiMetaData whoamiMetaData = new WhoamiMetaData();
                whoamiMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(whoamiMetaData.getMetadataKeys(), "WhoamiMetaData"));
                CreateAccountMetaData createAccountMetaData = new CreateAccountMetaData();
                createAccountMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(createAccountMetaData.getMetadataKeys(), "CreateAccountMetaData"));
                ServerTimeMetaData serverTimeMetaData = new ServerTimeMetaData();
                serverTimeMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(serverTimeMetaData.getMetadataKeys(), "ServerTimeMetaData"));
                CreateUserMetaData createUserMetaData = new CreateUserMetaData();
                createUserMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(createUserMetaData.getMetadataKeys(), "CreateUserMetaData"));
                GetCrystalInfoMetaData getCrystalInfoMetaData = new GetCrystalInfoMetaData();
                getCrystalInfoMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(getCrystalInfoMetaData.getMetadataKeys(), "GetCrystalInfoMetaData"));
                ServerTimeWithTimezoneMetaData serverTimeWithTimezoneMetaData = new ServerTimeWithTimezoneMetaData();
                serverTimeWithTimezoneMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(serverTimeWithTimezoneMetaData.getMetadataKeys(), "ServerTimeWithTimezoneMetaData"));
                UpsertMetaData upsertMetaData = new UpsertMetaData();
                upsertMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(upsertMetaData.getMetadataKeys(), "UpsertMetaData"));
                ModifyMetaData modifyMetaData = new ModifyMetaData();
                modifyMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(modifyMetaData.getMetadataKeys(), "ModifyMetaData"));
                MakeurlMetaData makeurlMetaData = new MakeurlMetaData();
                makeurlMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(makeurlMetaData.getMetadataKeys(), "MakeurlMetaData"));
                DeleteMetaData deleteMetaData = new DeleteMetaData();
                deleteMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(deleteMetaData.getMetadataKeys(), "DeleteMetaData"));
                AddMetaData addMetaData = new AddMetaData();
                addMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(addMetaData.getMetadataKeys(), "AddMetaData"));
                SubmitMetaData submitMetaData = new SubmitMetaData();
                submitMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(submitMetaData.getMetadataKeys(), "SubmitMetaData"));
                ReadMetaData readMetaData = new ReadMetaData();
                readMetaData.setConnector(connection);
                gatheredMetaDataKeys.addAll(MetaDataGeneratorUtils.fillCategory(readMetaData.getMetadataKeys(), "ReadMetaData"));
                DefaultResult defaultResult = new DefaultResult(gatheredMetaDataKeys, Result.Status.SUCCESS);
                return defaultResult;
            }
            catch (Exception e) {
                DefaultResult defaultResult;
                block19: {
                    defaultResult = new DefaultResult(null, Result.Status.FAILURE, "There was an error retrieving the metadata keys from service provider after acquiring connection, for more detailed information please read the provided stacktrace", (FailureType)MetaDataFailureType.ERROR_METADATA_KEYS_RETRIEVER, (Throwable)e);
                    if (connection == null) break block19;
                    try {
                        this.releaseConnection(key, connection);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                return defaultResult;
                catch (Exception e2) {
                    try {
                        this.destroyConnection(key, connection);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    DefaultResult defaultResult2 = ConnectivityTestingErrorHandler.buildFailureTestResult(e2);
                    return defaultResult2;
                }
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            if (connection != null) {
                try {
                    this.releaseConnection(key, connection);
                }
                catch (Exception exception) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public Result<MetaData> getMetaData(MetaDataKey metaDataKey) {
        OpenAirConnectorConnectionManagementAdapter connection = null;
        ConnectionManagementConfigOpenAirConnectorConnectionKey key = this.getDefaultConnectionKey();
        try {
            connection = this.acquireConnection(key);
            try {
                MetaData metaData = null;
                TypeDescribingProperty property = (TypeDescribingProperty)metaDataKey.getProperty(TypeDescribingProperty.class);
                String category = ((DefaultMetaDataKey)metaDataKey).getCategory();
                if (category == null) throw new Exception("Invalid key type. There is no matching category for [" + metaDataKey.getId() + "]. All keys must contain a category with any of the following options:[RunReportMetaData, WhoamiMetaData, CreateAccountMetaData, ServerTimeMetaData, CreateUserMetaData, GetCrystalInfoMetaData, ServerTimeWithTimezoneMetaData, UpsertMetaData, ModifyMetaData, MakeurlMetaData, DeleteMetaData, AddMetaData, SubmitMetaData, ReadMetaData]");
                if (category.equals("RunReportMetaData")) {
                    RunReportMetaData runReportMetaData = new RunReportMetaData();
                    runReportMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? runReportMetaData.getOutputMetadata(metaDataKey) : runReportMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("WhoamiMetaData")) {
                    WhoamiMetaData whoamiMetaData = new WhoamiMetaData();
                    whoamiMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? whoamiMetaData.getOutputMetadata(metaDataKey) : whoamiMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("CreateAccountMetaData")) {
                    CreateAccountMetaData createAccountMetaData = new CreateAccountMetaData();
                    createAccountMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? createAccountMetaData.getOutputMetadata(metaDataKey) : createAccountMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("ServerTimeMetaData")) {
                    ServerTimeMetaData serverTimeMetaData = new ServerTimeMetaData();
                    serverTimeMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? serverTimeMetaData.getOutputMetadata(metaDataKey) : serverTimeMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("CreateUserMetaData")) {
                    CreateUserMetaData createUserMetaData = new CreateUserMetaData();
                    createUserMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? createUserMetaData.getOutputMetadata(metaDataKey) : createUserMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("GetCrystalInfoMetaData")) {
                    GetCrystalInfoMetaData getCrystalInfoMetaData = new GetCrystalInfoMetaData();
                    getCrystalInfoMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? getCrystalInfoMetaData.getOutputMetadata(metaDataKey) : getCrystalInfoMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("ServerTimeWithTimezoneMetaData")) {
                    ServerTimeWithTimezoneMetaData serverTimeWithTimezoneMetaData = new ServerTimeWithTimezoneMetaData();
                    serverTimeWithTimezoneMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? serverTimeWithTimezoneMetaData.getOutputMetadata(metaDataKey) : serverTimeWithTimezoneMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("UpsertMetaData")) {
                    UpsertMetaData upsertMetaData = new UpsertMetaData();
                    upsertMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? upsertMetaData.getOutputMetadata(metaDataKey) : upsertMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("ModifyMetaData")) {
                    ModifyMetaData modifyMetaData = new ModifyMetaData();
                    modifyMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? modifyMetaData.getOutputMetadata(metaDataKey) : modifyMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("MakeurlMetaData")) {
                    MakeurlMetaData makeurlMetaData = new MakeurlMetaData();
                    makeurlMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? makeurlMetaData.getOutputMetadata(metaDataKey) : makeurlMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("DeleteMetaData")) {
                    DeleteMetaData deleteMetaData = new DeleteMetaData();
                    deleteMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? deleteMetaData.getOutputMetadata(metaDataKey) : deleteMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("AddMetaData")) {
                    AddMetaData addMetaData = new AddMetaData();
                    addMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? addMetaData.getOutputMetadata(metaDataKey) : addMetaData.getInputMetadata(metaDataKey);
                } else if (category.equals("SubmitMetaData")) {
                    SubmitMetaData submitMetaData = new SubmitMetaData();
                    submitMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? submitMetaData.getOutputMetadata(metaDataKey) : submitMetaData.getInputMetadata(metaDataKey);
                } else {
                    if (!category.equals("ReadMetaData")) throw new Exception("Invalid key type. There is no matching category for [" + metaDataKey.getId() + "]. All keys must contain a category with any of the following options:[RunReportMetaData, WhoamiMetaData, CreateAccountMetaData, ServerTimeMetaData, CreateUserMetaData, GetCrystalInfoMetaData, ServerTimeWithTimezoneMetaData, UpsertMetaData, ModifyMetaData, MakeurlMetaData, DeleteMetaData, AddMetaData, SubmitMetaData, ReadMetaData]" + ", but found [" + category + "] instead");
                    ReadMetaData readMetaData = new ReadMetaData();
                    readMetaData.setConnector(connection);
                    metaData = property != null && property.getTypeScope().equals((Object)TypeDescribingProperty.TypeScope.OUTPUT) ? readMetaData.getOutputMetadata(metaDataKey) : readMetaData.getInputMetadata(metaDataKey);
                }
                metaData.getPayload().addProperty((MetaDataModelProperty)new StructureIdentifierMetaDataModelProperty(metaDataKey, false));
                DefaultResult defaultResult = new DefaultResult((Object)metaData);
                return defaultResult;
            }
            catch (Exception e) {
                DefaultResult defaultResult = new DefaultResult(null, Result.Status.FAILURE, MetaDataGeneratorUtils.getMetaDataException(metaDataKey), (FailureType)MetaDataFailureType.ERROR_METADATA_RETRIEVER, (Throwable)e);
                if (connection == null) return defaultResult;
                try {
                    this.releaseConnection(key, connection);
                    return defaultResult;
                }
                catch (Exception exception) {
                    // empty catch block
                }
                return defaultResult;
                catch (Exception e2) {
                    try {
                        this.destroyConnection(key, connection);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    DefaultResult defaultResult2 = ConnectivityTestingErrorHandler.buildFailureTestResult(e2);
                    return defaultResult2;
                }
                catch (Throwable throwable) {
                    throw throwable;
                }
            }
        }
        finally {
            if (connection != null) {
                try {
                    this.releaseConnection(key, connection);
                }
                catch (Exception exception) {}
            }
        }
    }
}
