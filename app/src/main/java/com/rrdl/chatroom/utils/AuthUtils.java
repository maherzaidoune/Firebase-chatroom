package com.rrdl.chatroom.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthUtils {
    public static boolean isValidEmail(String email){
        if(email.length() < 2){
            return false;
        }
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.find();
    }
}
