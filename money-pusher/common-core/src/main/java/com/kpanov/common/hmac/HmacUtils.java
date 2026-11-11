package com.kpanov.common.hmac;

import com.kpanov.common.exception.HmacSignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacUtils {
    public static String hmacSha256(byte[] data, String secret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(secretKeySpec);

            byte[] digest = mac.doFinal(data);
            var sb = new StringBuilder();
            for (byte b: digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new HmacSignatureException(e.getMessage());
        }
    }

    private static final String ALGORITHM = "HmacSHA256";
}