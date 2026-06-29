package com.SaasRRHH.main.services;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class TotpService {
    public static final long STEP_SECONDS = 60L;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generateSecret() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public long currentWindow() {
        return Instant.now().getEpochSecond() / STEP_SECONDS;
    }

    public long secondsRemaining() {
        long epoch = Instant.now().getEpochSecond();
        return STEP_SECONDS - (epoch % STEP_SECONDS);
    }

    public long expiresAtEpoch() {
        return (currentWindow() + 1) * STEP_SECONDS;
    }

    public String code(String secret, long window) {
        try {
            byte[] key = Base64.getUrlDecoder().decode(secret);
            byte[] counter = ByteBuffer.allocate(8).putLong(window).array();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            byte[] hash = mac.doFinal(counter);
            int offset = hash[hash.length - 1] & 0x0F;
            int binary = ((hash[offset] & 0x7F) << 24)
                    | ((hash[offset + 1] & 0xFF) << 16)
                    | ((hash[offset + 2] & 0xFF) << 8)
                    | (hash[offset + 3] & 0xFF);
            return String.format("%06d", binary % 1_000_000);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el codigo TOTP", ex);
        }
    }

    public boolean verify(String secret, long window, String code) {
        long current = currentWindow();
        return (window == current || window == current - 1) && code(secret, window).equals(code);
    }
}
