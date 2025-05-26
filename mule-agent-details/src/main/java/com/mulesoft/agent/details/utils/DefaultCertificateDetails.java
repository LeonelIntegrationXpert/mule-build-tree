package com.mulesoft.agent.details.utils;

import com.mulesoft.agent.configuration.descriptor.MuleAgentDescriptorWrapper;
import com.mulesoft.agent.configuration.descriptor.YamlBuilder;
import com.mulesoft.agent.configuration.encryption.EncryptionServiceBuilder;
import com.mulesoft.agent.services.EncryptionService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.mule.runtime.core.api.config.MuleProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class DefaultCertificateDetails {
    private static final Logger LOGGER = (Logger) LogManager.getLogger(DefaultCertificateDetails.class);

    private static final String DESCRIPTOR_FILE = "mule-agent.yml";
    private static final String KEYSTORE_FILE = "mule-agent.jks";
    private static final String CONFIGURATION_FOLDER_PROPERTY = "mule.agent.configuration.folder";
    private static final String DEFAULT_KEYSTORE_ALIAS = "agent";
    private static final String KEYSTORE_ALIAS_PROPERTY_KEY = "mule.agent.keystore.alias";

    private String serverId;
    private String contextId;
    private Certificate[] certChain;
    private Key privateKey;

    private EncryptionService encryptorService;

    /**
     * Creates a certificate details object.
     *
     * @param keystoreType The mule agent keystore type that's been used.
     */
    public DefaultCertificateDetails(String keystoreType) {
        String confFolder = System.getProperty(CONFIGURATION_FOLDER_PROPERTY);
        String keystoreAlias = System.getProperty(KEYSTORE_ALIAS_PROPERTY_KEY) != null ? System.getProperty(KEYSTORE_ALIAS_PROPERTY_KEY) : DEFAULT_KEYSTORE_ALIAS;

        encryptorService = EncryptionServiceBuilder.newInstance().build();

        if (StringUtils.isBlank(confFolder)) {
            confFolder = System.getProperty(MuleProperties.MULE_HOME_DIRECTORY_PROPERTY) + File.separator + "conf";
            System.setProperty(CONFIGURATION_FOLDER_PROPERTY, confFolder);
        }

        // Extract jks and mule yml file paths
        String keystoreFilePath = confFolder + File.separator + KEYSTORE_FILE;
        String descriptorFilePath = confFolder + File.separator + DESCRIPTOR_FILE;

        File jksFile = new File(keystoreFilePath);

        try (FileInputStream is = new FileInputStream(jksFile)) {

            char[] keystorePassword = getKeystorePassword(descriptorFilePath);

            KeyStore keystore = KeyStore.getInstance(keystoreType);

            if (keystorePassword != null) {
                keystore.load(is, keystorePassword);
            } else {
                LOGGER.warn("There was an error reading the certificate.");
            }

            // Load certificate chain
            this.certChain = keystore.getCertificateChain(keystoreAlias);

            this.privateKey = keystore.getKey(keystoreAlias, keystorePassword);
            X509Certificate certificateData = (X509Certificate) this.certChain[0];

            // Extract serverId and contextId from certificate
            String[] subjectData = certificateData.getSubjectDN()
                    .toString()
                    .split(", ");
            this.serverId = subjectData[0].substring(3);
            this.contextId = subjectData[1].substring(3);
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException | UnrecoverableKeyException e) {
            LOGGER.warn("There was an error reading the certificate. Reason: {}", ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private char[] getKeystorePassword(String descriptorFilePath) {
        char[] keystorePassword = null;

        try {
            MuleAgentDescriptorWrapper descriptorWrapper = MuleAgentDescriptorWrapper.newInstance(YamlBuilder.newInstance().buildYamlWrapper()).load(descriptorFilePath);
            keystorePassword = encryptorService.decrypt(descriptorWrapper.getKeystorePassword()).toCharArray();
        } catch (Exception e) {
            LOGGER.warn("There was an error decrypting the keystore password. Reason: {}", ExceptionUtils.getRootCauseMessage(e));
        }

        return keystorePassword;
    }


    public String getServerId() {
        return serverId;
    }

    public String getContextId() {
        return contextId;
    }

    public Certificate[] getCertChain() {
        return certChain;
    }

    public Key getPrivateKey() {
        return privateKey;
    }
}
