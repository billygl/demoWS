package com.hacking.demows.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class Utils {
    public static String serialize(Serializable obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        try (ObjectOutputStream out = new ObjectOutputStream(baos)) {
            out.writeObject(obj);
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public  static <T> T deserialize(String base64SerializedObj) throws Exception {
        try (
            ObjectInputStream in = new ObjectInputStream(
                new ByteArrayInputStream(
                    Base64.getDecoder().decode(base64SerializedObj)
                )
            )
        ) {
            @SuppressWarnings("unchecked")
            T obj = (T) in.readObject();
            return obj;
        }
    }

    public static String getCookieValue(HttpServletRequest request, String name ){
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                if(cookie.getName().equalsIgnoreCase(name)){
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    public static String getToken(String requestTokenHeader){
        if (requestTokenHeader != null &&
            requestTokenHeader.startsWith("Bearer ")) {
			return requestTokenHeader.substring(7);
        }
        return null;
    }
}