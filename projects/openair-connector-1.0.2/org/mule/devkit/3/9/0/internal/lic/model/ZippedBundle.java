/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 */
package org.mule.devkit.3.9.0.internal.lic.model;

import com.google.common.base.Optional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZippedBundle {
    private Map<String, byte[]> bundleFiles;

    public ZippedBundle(InputStream bundle) {
        this.bundleFiles = this.unZip(bundle);
    }

    public Optional<byte[]> get(String fileName) {
        return Optional.fromNullable((Object)this.bundleFiles.get(fileName));
    }

    private Map<String, byte[]> unZip(InputStream fileIS) {
        HashMap<String, byte[]> entries = new HashMap<String, byte[]>();
        try {
            ZipInputStream zipIS = new ZipInputStream(fileIS);
            ZipEntry ze = zipIS.getNextEntry();
            while (ze != null) {
                entries.put(ze.getName(), this.readEntry(zipIS));
                ze = zipIS.getNextEntry();
            }
            zipIS.closeEntry();
            zipIS.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("failed to read input zip ");
        }
        return entries;
    }

    private byte[] readEntry(ZipInputStream zis) throws IOException {
        int len;
        ByteArrayOutputStream os = new ByteArrayOutputStream(zis.available());
        byte[] buffer = new byte[1024];
        while ((len = zis.read(buffer)) > 0) {
            os.write(buffer, 0, len);
        }
        os.close();
        return os.toByteArray();
    }
}
