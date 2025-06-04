/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.Validate
 *  org.jetbrains.annotations.NotNull
 */
package org.mule.modules.wsdl.openair.internal.runtime.header;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.mule.modules.wsdl.openair.internal.runtime.ServiceDefinition;
import org.mule.modules.wsdl.openair.internal.runtime.header.HeaderBuilder;
import org.mule.modules.wsdl.openair.internal.runtime.header.SoapHeaderException;

public class WsseSecurityHeaderBuilder
implements HeaderBuilder {
    public static final String WSDL_SECURITY_UTILITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String WSDL_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private final String username;
    private final String password;

    public WsseSecurityHeaderBuilder(@NotNull String username, @NotNull String password) {
        Validate.notNull((Object)username, (String)"The user name cannot be null or empty.");
        Validate.notNull((Object)password, (String)"The password cannot be null or empty.");
        this.username = username;
        this.password = password;
    }

    @Override
    public void build(@NotNull SOAPHeader header, @NotNull ServiceDefinition soapClient) throws SoapHeaderException {
        try {
            SOAPElement security = header.addChildElement("Security", "wsse", WSDL_SECURITY_NAMESPACE);
            SOAPElement usernameToken = security.addChildElement("UsernameToken", "wsse");
            usernameToken.addAttribute(new QName("xmlns:wsu"), WSDL_SECURITY_UTILITY_NAMESPACE);
            SOAPElement usernameElement = usernameToken.addChildElement("Username", "wsse");
            usernameElement.addTextNode(this.username);
            SOAPElement passwordElement = usernameToken.addChildElement("Password", "wsse");
            passwordElement.addAttribute(QName.valueOf("Type"), "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText");
            passwordElement.addTextNode(this.password);
        }
        catch (SOAPException e) {
            throw new SoapHeaderException(e);
        }
    }
}
