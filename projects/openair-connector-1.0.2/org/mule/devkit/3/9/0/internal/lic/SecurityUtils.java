/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.io.IOUtils
 */
package org.mule.devkit.3.9.0.internal.lic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class SecurityUtils {
    public static final String RSA = "RSA";
    public static final String SHA_512_WITH_RSA = "SHA512withRSA";
    public static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";

    public static boolean verify(byte[] signatureToVerify, byte[] dataFile, PublicKey publicKey) throws Exception {
        Signature signatureVerifier = Signature.getInstance(SHA_512_WITH_RSA);
        signatureVerifier.initVerify(publicKey);
        byte[] buffer = new byte[1024];
        ByteArrayInputStream bufin = new ByteArrayInputStream(dataFile);
        while (bufin.available() != 0) {
            int len = bufin.read(buffer);
            signatureVerifier.update(buffer, 0, len);
        }
        bufin.close();
        return signatureVerifier.verify(signatureToVerify);
    }

    public static PublicKey loadPublic(String keyPath) throws Exception {
        InputStream keyIS = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyPath);
        if (keyIS == null) {
            throw new IllegalArgumentException("key " + keyPath + " not found as resource");
        }
        return SecurityUtils.loadPublic(IOUtils.toByteArray((InputStream)keyIS));
    }

    public static PublicKey loadPublic(byte[] key) throws Exception {
        return KeyFactory.getInstance(RSA).generatePublic(new X509EncodedKeySpec(key));
    }

    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        return SecurityUtils.decrypt(data, key, RSA_ECB_PKCS1_PADDING);
    }

    private static byte[] decrypt(byte[] data, Key key, String transformation) throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(2, key);
        return cipher.doFinal(Base64.decodeBase64((byte[])data));
    }
}
