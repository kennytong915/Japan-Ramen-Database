package com.ramendirectory.japanramendirectory.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class IPAddressUtil {

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    /**
     * Extracts the client IP address from a request, considering proxy headers
     * @param request The HTTP request
     * @return The client's IP address
     */
    public static String getClientIP(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String ipFromHeader = request.getHeader(header);
            if (StringUtils.hasText(ipFromHeader) && !"unknown".equalsIgnoreCase(ipFromHeader)) {
                // X-Forwarded-For might contain multiple IPs, use the first one (client IP)
                if (header.equals("X-Forwarded-For")) {
                    return ipFromHeader.split(",")[0].trim();
                }
                return ipFromHeader;
            }
        }
        
        // If no headers match, use the remote address
        return request.getRemoteAddr();
    }
} 