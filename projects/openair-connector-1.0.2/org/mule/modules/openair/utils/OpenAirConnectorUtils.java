/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Iterables
 *  org.jetbrains.annotations.NotNull
 *  org.mule.module.xml.util.XMLUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.mule.modules.openair.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.jetbrains.annotations.NotNull;
import org.mule.module.xml.util.XMLUtils;
import org.mule.modules.openair.utils.TransformationException;
import org.mule.modules.openair.utils.XmlParserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class OpenAirConnectorUtils {
    private static final Logger logger = LoggerFactory.getLogger(OpenAirConnectorUtils.class);
    private static final TransformerFactory TRANSFORMER_FACTORY = XmlParserUtils.getTransformerFactory();

    public static String populateRequest(@NotNull String xmlString, @NotNull Map<String, String> params) {
        for (Map.Entry<String, String> property : params.entrySet()) {
            xmlString = xmlString.replace(String.format("#[payload.%s]", property.getKey()), property.getValue());
        }
        return xmlString;
    }

    public static InputStream getRequestXMLFileName(String operationName) {
        return OpenAirConnectorUtils.openResource(String.format("xml/%sRequest.xml", operationName.toLowerCase()));
    }

    public static InputStream openResource(String resource) {
        return OpenAirConnectorUtils.class.getClassLoader().getResourceAsStream(resource);
    }

    public static String getRequestXSLTFileName(String operationName) {
        return String.format("xml/requests/%sRequest.xsl", operationName.toLowerCase());
    }

    public static String getResponseXSLTFileName(String operationName) {
        return String.format("xml/responses/%sResponse.xsl", operationName.toLowerCase());
    }

    public static Document parseXMLStreamReaderToDocument(XMLStreamReader streamReader) throws ParserConfigurationException, TransformerConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(OpenAirConnectorUtils.transformAsByteArray(streamReader, TRANSFORMER_FACTORY.newTransformer())));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static byte[] transformAsByteArray(XMLStreamReader payload, Transformer transformerTemplate) {
        try (StringWriter outputWriter = new StringWriter();){
            transformerTemplate.transform(XMLUtils.toXmlSource((XMLStreamReader)payload), new StreamResult(outputWriter));
            String output = outputWriter.toString();
            logger.debug(output);
            byte[] byArray = output.getBytes();
            return byArray;
        }
        catch (Exception e) {
            throw new TransformationException(e);
        }
    }

    public static XMLStreamReader transformAsXMLStreamReader(XMLStreamReader payload, Transformer transformerTemplate) throws XMLStreamException {
        return OpenAirConnectorUtils.asXMLStreamReader(OpenAirConnectorUtils.transformAsByteArray(payload, transformerTemplate));
    }

    public static XMLStreamReader asXMLStreamReader(byte[] input) throws XMLStreamException {
        XMLInputFactory xmlInputFactory = XmlParserUtils.getXmlInputFactory();
        return xmlInputFactory.createXMLStreamReader(new ByteArrayInputStream(input), "UTF-8");
    }

    public static String getValueFromXML(Document xmlDocument, String xPathExpression) throws XPathExpressionException {
        return XPathFactory.newInstance().newXPath().compile(xPathExpression).evaluate(xmlDocument);
    }

    public static XMLStreamReader parseStringToXMLStreamReader(String xmlString) throws XMLStreamException {
        return OpenAirConnectorUtils.asXMLStreamReader(xmlString.getBytes());
    }

    public static String splitString(String stringToSplit, String splitOn, int returnItemPosition) {
        return (String)Splitter.on((String)splitOn).splitToList((CharSequence)stringToSplit).get(returnItemPosition);
    }

    public static String splitString(String stringToSplit, String splitOn) {
        return (String)Iterables.getLast((Iterable)Splitter.on((String)splitOn).split((CharSequence)stringToSplit));
    }

    private OpenAirConnectorUtils() {
    }
}
