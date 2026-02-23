package com.hotelmanager;

import com.hotelmanager.util.PasswordUtil;

public class GenerateHash {
    public static void main(String[] args) {
        String password = args.length > 0 ? args[0] : "admin123";
        String hash = PasswordUtil.hashPassword(password);
        System.out.println("Password: " + password);
        System.out.println("Hash: " + hash);
    }
}

