/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.wsdl.Definition
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.RandomStringUtils
 *  org.apache.commons.lang.StringUtils
 *  org.jsoup.Jsoup
 *  org.mule.api.ConnectionException
 *  org.mule.api.ConnectionExceptionCode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.config;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import javax.wsdl.Definition;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.devkit.3.9.0.api.metadata.ComposedMetaDataKey;
import org.mule.modules.openair.client.ProgramClient;
import org.mule.modules.openair.client.ProgramClientImpl;
import org.mule.modules.openair.exception.OpenAirConnectionException;
import org.mule.modules.openair.metadata.wsdlparser.OpenAirWsdlDatasenseParser;
import org.mule.modules.openair.metadata.wsdlparser.WsdlConverter;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;
import org.mule.modules.wsdl.openair.internal.runtime.CallDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    public static final String TEMPORARY_WSDL_DIRECTORY_PATH = ("file:///" + System.getProperty("java.io.tmpdir") + "/wsdl").replace("\\", "/");
    public static final String TEMPORARY_WSDL_PATH = TEMPORARY_WSDL_DIRECTORY_PATH + "/OpenAir.wsdl";
    private String sessionID;
    private String wsdlLocation;
    private Definition wsdlDefinition;
    private OpenAirWsdlDatasenseParser wsdlDatasenseParser;
    private String endpoint;
    private Integer connectionTimeout;
    private Integer readTimeout;
    private ProgramClient programClient;

    public void connect(String company, String username, String password, String apiNamespace, String apiKey) throws ConnectionException {
        try (InputStream loginStream = OpenAirConnectorUtils.openResource("xml/loginRequest.xml");
             InputStream makeURLStream = OpenAirConnectorUtils.openResource("xml/makeURLRequest.xml");){
            this.programClient = new ProgramClientImpl(this.endpoint, this.connectionTimeout, this.readTimeout);
            this.sessionID = this.programClient.invokeLoginOperation(OpenAirConnectorUtils.parseStringToXMLStreamReader(OpenAirConnectorUtils.populateRequest(IOUtils.toString((InputStream)loginStream), (Map<String, String>)ImmutableMap.builder().put((Object)"company", (Object)company).put((Object)"username", (Object)username).put((Object)"password", (Object)password).put((Object)"apiNamespace", (Object)apiNamespace).put((Object)"apiKey", (Object)apiKey).put((Object)"client", (Object)RandomStringUtils.randomAlphanumeric((int)255)).put((Object)"version", (Object)"v1.0").build())));
            this.wsdlLocation = Jsoup.connect((String)OpenAirConnectorUtils.getValueFromXML(OpenAirConnectorUtils.parseXMLStreamReaderToDocument(this.programClient.invoke(new CallDefinition(this.endpoint, "OpenAir_makeURL"), OpenAirConnectorUtils.parseStringToXMLStreamReader(OpenAirConnectorUtils.populateRequest(IOUtils.toString((InputStream)makeURLStream), (Map<String, String>)ImmutableMap.builder().put((Object)"uid", (Object)this.getSessionID()).put((Object)"page", (Object)"import-export").put((Object)"app", (Object)"ma").build())))), "//url/text()")).get().getElementsMatchingText("Account specific WSDL").attr("href");
            this.wsdlDefinition = new WsdlConverter().convert(this.wsdlLocation);
            this.wsdlDatasenseParser = new OpenAirWsdlDatasenseParser();
            this.wsdlDatasenseParser.addMetadata(Collections.singletonList(new URL(TEMPORARY_WSDL_PATH)), null, null);
        }
        catch (Exception e) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "0", e.getMessage(), (Throwable)e);
        }
    }

    public void disconnect() {
        try (InputStream stream = OpenAirConnectorUtils.openResource("xml/logoutRequest.xml");){
            if (this.isConnected()) {
                XMLStreamReader xmlStreamReader = OpenAirConnectorUtils.asXMLStreamReader(IOUtils.toByteArray((InputStream)stream));
                this.programClient.invokeLogoutOperation(xmlStreamReader);
                this.sessionID = "";
                this.programClient = null;
                xmlStreamReader.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isConnected() {
        try {
            return StringUtils.isNotBlank((String)this.getSessionID()) && this.getProgramClient().invokeOperation("whoami") != null;
        }
        catch (OpenAirConnectionException e) {
            return false;
        }
    }

    public Definition getWsdlDefinition() {
        return this.wsdlDefinition;
    }

    public Map<String, ComposedMetaDataKey> getOperationMetadata() {
        return Maps.uniqueIndex(this.wsdlDatasenseParser.getMetaDataKeys(), (Function)new /* Unavailable Anonymous Inner Class!! */);
    }

    public ProgramClient getProgramClient() {
        return this.programClient;
    }

    public String getSessionID() {
        logger.debug("Session ID: {}", (Object)this.sessionID);
        return this.sessionID;
    }

    public OpenAirWsdlDatasenseParser getWsdlDatasenseParser() {
        return this.wsdlDatasenseParser;
    }

    public String getWsdlLocation() {
        return this.wsdlLocation;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(Integer readTimeout) {
        this.readTimeout = readTimeout;
    }
}
