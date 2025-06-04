/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.util.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.mule.modules.openair.OpenAirConnector;
import org.mule.modules.openair.client.OpenAirHeaderBuilder;
import org.mule.modules.openair.client.ProgramClient;
import org.mule.modules.openair.pool.FileLoadingException;
import org.mule.modules.openair.pool.XsltTransformerPool;
import org.mule.modules.openair.utils.OpenAirConnectorUtils;
import org.mule.modules.openair.utils.TransformationException;
import org.mule.modules.wsdl.openair.internal.runtime.CallDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.ServiceDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.SoapCallException;
import org.mule.modules.wsdl.openair.internal.runtime.SoapClient;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class ProgramClientImpl
implements ProgramClient {
    private static final Logger logger = LoggerFactory.getLogger(OpenAirConnector.class);
    private final Map<String, byte[]> requestStore = new ConcurrentHashMap<String, byte[]>();
    private final XsltTransformerPool transformersPool = new XsltTransformerPool();
    private final ConcurrentHashMap<String, byte[]> parameterLessRepository = new ConcurrentHashMap();
    private final String endpoint;
    private final SoapClient soapClient;

    public ProgramClientImpl(String endpoint, Integer connectionTimeout, Integer readTimeout) {
        this.soapClient = SoapClient.create(ServiceDefinition.create("http://www.openair.com/OAirService", "OAirServiceHandlerService", "OAirService", endpoint, "oair"));
        this.soapClient.setConnectionTimeout(connectionTimeout);
        this.soapClient.setReadTimeout(readTimeout);
        this.endpoint = endpoint;
    }

    @Override
    public XMLStreamReader invokeOperation(String operationName) {
        if (!this.parameterLessRepository.contains(operationName)) {
            try (InputStream requestInputStream = OpenAirConnectorUtils.getRequestXMLFileName(operationName);){
                this.parameterLessRepository.put(operationName, IOUtils.toByteArray((InputStream)requestInputStream));
                logger.info("Created parameter less request for: {}", (Object)operationName);
            }
            catch (IOException e) {
                throw new FileLoadingException(operationName, e);
            }
        }
        return this.invokeOperation(operationName, this.parameterLessRepository.get(operationName), null);
    }

    /*
     * Exception decompiling
     */
    private XMLStreamReader invokeOperation(String operationName, byte[] requestBytes, String requestKey) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public XMLStreamReader invokeOperation(String operationName, XMLStreamReader request) {
        byte[] requestBytes;
        String requestKey = String.format("%s::%s", operationName, request.hashCode());
        logger.debug("ObjectStore Key: {}", (Object)requestKey);
        if (request.getEventType() == 7) {
            requestBytes = this.transform(OpenAirConnectorUtils.getRequestXSLTFileName(operationName), request);
            this.requestStore.put(requestKey, requestBytes);
        } else {
            requestBytes = this.requestStore.get(requestKey);
        }
        return this.invokeOperation(operationName, requestBytes, requestKey);
    }

    private byte[] transform(String transformerKey, XMLStreamReader input) {
        try {
            Transformer transformer = (Transformer)this.transformersPool.borrowObject(transformerKey);
            byte[] output = OpenAirConnectorUtils.transformAsByteArray(input, transformer);
            this.transformersPool.returnObject(transformerKey, transformer);
            return output;
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    @Override
    public String invokeLoginOperation(XMLStreamReader xmlStreamReader) throws SoapCallException, ParserConfigurationException, TransformerConfigurationException, XPathExpressionException, IOException, SAXException {
        String sessionId = OpenAirConnectorUtils.getValueFromXML(OpenAirConnectorUtils.parseXMLStreamReaderToDocument(this.soapClient.invoke(new CallDefinition(this.endpoint, "OpenAir_login"), xmlStreamReader)), "//sessionId/text()");
        this.soapClient.setSoapHeaderBuilder(new OpenAirHeaderBuilder(sessionId));
        return sessionId;
    }

    @Override
    public void invokeLogoutOperation(XMLStreamReader xmlStreamReader) throws SoapCallException {
        this.soapClient.invoke(new CallDefinition(this.endpoint, "OpenAir_logout"), xmlStreamReader);
    }

    @Override
    public XMLStreamReader invoke(CallDefinition callDefinition, XMLStreamReader makeURLXmlStreamReader) throws SoapCallException {
        return this.soapClient.invoke(callDefinition, makeURLXmlStreamReader);
    }
}
