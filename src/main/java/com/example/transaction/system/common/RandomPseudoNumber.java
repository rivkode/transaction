package com.example.transaction.system.common;

import java.util.random.RandomGenerator;

public class RandomPseudoNumber {
    public static void main(String[] args) {
        RandomGenerator generator = RandomGenerator.of("L128X256MixRandom");
        System.out.printf("random number = %d", generator.nextInt(10000));
    }
}
