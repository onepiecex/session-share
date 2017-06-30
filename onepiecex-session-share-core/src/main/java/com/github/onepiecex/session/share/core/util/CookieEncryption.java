/*
 * Copyright 2015 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.onepiecex.session.share.core.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangziqing on 17/6/22.
 */
public class CookieEncryption {

    public static final String ALGORITHM = "AES";

    private static final Logger logger = LoggerFactory.getLogger(CookieEncryption.class);

    private final Optional<SecretKeySpec> secretKeySpec;

    private static volatile Map<String,CookieEncryption> cookieEncryptionMap = new ConcurrentHashMap<>();

    public static CookieEncryption getInstance(String secret) {
        CookieEncryption cookieEncryption = cookieEncryptionMap.get(secret);
        if(null == cookieEncryption){
            cookieEncryption = new CookieEncryption(secret);
            cookieEncryptionMap.put(secret,cookieEncryption);
        }
        return cookieEncryption;
    }


    private CookieEncryption(String secret) {
        Optional<SecretKeySpec> secretKeySpec;
        try {
            int maxKeyLengthBits = Cipher.getMaxAllowedKeyLength(ALGORITHM);
            if (maxKeyLengthBits == Integer.MAX_VALUE) {
                maxKeyLengthBits = 256;
            }

            secretKeySpec = Optional.of(
                    new SecretKeySpec(secret.getBytes(), 0, maxKeyLengthBits / Byte.SIZE, ALGORITHM));

            logger.info("onepiecex session encryption is using {} / {} bit.", secretKeySpec.get().getAlgorithm(), maxKeyLengthBits);

        } catch (Exception exception) {
            logger.error("Can not create class to encrypt cookie.", exception);
            throw new RuntimeException(exception);
        }
        this.secretKeySpec = secretKeySpec;
    }


    public String encrypt(String data) {

        Objects.requireNonNull(data, "Data to be encrypted");

        if (!secretKeySpec.isPresent()) {
            return data;
        }

        try {
            // encrypt data
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec.get());
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // convert encrypted bytes to string in base64
            return Base64.encodeBase64URLSafeString(encrypted);

        } catch (InvalidKeyException ex) {
            logger.error(getHelperLogMessage(), ex);
            throw new RuntimeException(ex);
        } catch (GeneralSecurityException ex) {
            logger.error("Failed to encrypt data.", ex);
            return "";
        }
    }


    public String decrypt(String data) {

        Objects.requireNonNull(data, "Data to be decrypted");

        if (!secretKeySpec.isPresent()) {
            return data;
        }

        byte[] decoded = Base64.decodeBase64(data);
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec.get());
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (InvalidKeyException ex) {
            logger.error(getHelperLogMessage(), ex);
            throw new RuntimeException(ex);
        } catch (GeneralSecurityException ex) {
            logger.error("Failed to decrypt data.", ex);
            return "";
        }
    }

    private String getHelperLogMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid key provided. Check if application secret is properly set.").append(System.lineSeparator());
        sb.append("You can remove '").append("application.secret").append("' key in configuration file ");
        sb.append("and restart application. onepiecex will generate new key for you.");
        return sb.toString();
    }

}
