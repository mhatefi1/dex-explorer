package com.apk.signature.Util;

import net.lingala.zip4j.exception.ZipException;

public interface ReadBytesFromZipListener {
    boolean onReadManifest(byte[] bs);

    boolean onReadDex(byte[] bs);

    void onZipError(Exception e);

    void onManifestError(Exception e);

    void onDexError(Exception e);

    void onEnd();

}
