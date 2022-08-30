package com.h.userservice.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomCode {
    public String generateCode() {
        StringBuilder build = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++)
            build.append(random.nextInt(10));
        return build.toString();
    }

    public String generatePassword() {
        StringBuilder stringBuilder = new StringBuilder("");
        Random random = new Random(System.currentTimeMillis());
        int num = 0;
        while (num != 8) {
            //64-122
            int i = random.nextInt() % 59 + 64;
            stringBuilder.append((char) i);
            num++;
        }
        return stringBuilder.toString();
    }
}
