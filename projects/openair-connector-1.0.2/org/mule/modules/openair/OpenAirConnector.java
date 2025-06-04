/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mule.api.annotations.param.MetaDataStaticKey
 */
package org.mule.modules.openair;

import javax.xml.stream.XMLStreamReader;
import org.mule.api.annotations.param.MetaDataStaticKey;
import org.mule.modules.openair.config.Config;

public class OpenAirConnector {
    private Config config;

    public XMLStreamReader read(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("read", request);
    }

    public XMLStreamReader add(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("add", request);
    }

    public XMLStreamReader makeurl(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("makeURL", request);
    }

    public XMLStreamReader delete(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("delete", request);
    }

    @MetaDataStaticKey(type="OpenAirServerTimeMetaData")
    public XMLStreamReader serverTime() {
        return this.config.getProgramClient().invokeOperation("servertime");
    }

    public XMLStreamReader serverTimeWithTimezone(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("servertimeWithTimezone", request);
    }

    @MetaDataStaticKey(type="OpenAirWhoamiMetaData")
    public XMLStreamReader whoami() {
        return this.config.getProgramClient().invokeOperation("whoami");
    }

    public XMLStreamReader createUser(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("createUser", request);
    }

    public XMLStreamReader createAccount(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("createAccount", request);
    }

    public XMLStreamReader upsert(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("upsert", request);
    }

    public XMLStreamReader modify(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("modify", request);
    }

    public XMLStreamReader submit(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("submit", request);
    }

    @MetaDataStaticKey(type="OpenAirGetCrystalInfoMetaData")
    public XMLStreamReader getCrystalInfo() {
        return this.config.getProgramClient().invokeOperation("getCrystalInfo");
    }

    public XMLStreamReader runReport(String oaObject, XMLStreamReader request) {
        return this.config.getProgramClient().invokeOperation("runReport", request);
    }

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
