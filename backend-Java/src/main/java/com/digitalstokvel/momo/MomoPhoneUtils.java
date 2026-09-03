package com.digitalstokvel.momo;

public final class MomoPhoneUtils {
    private MomoPhoneUtils() { }
    public static String normalize(String phone) {
        String digits = phone == null ? "" : phone.replaceAll("\\D", "");
        if (digits.startsWith("0")) digits = "27" + digits.substring(1);
        if (!digits.matches("27\\d{9}")) throw new IllegalArgumentException("A valid South African MSISDN is required");
        return digits;
    }
}
