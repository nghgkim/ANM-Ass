package hk231.rsa;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PrimeGeneration {
    static final BigInteger One = BigInteger.ONE;
    static final BigInteger Zero = BigInteger.ZERO;

    public static BigInteger generate(int bitLength) {
        return randomStrongPrime(bitLength);
    }

    public static BigInteger randomStrongPrime(int bitLength) {
        // Check strong prime number condition
        if ((bitLength < 512) || ((bitLength % 128) != 0)) {
            throw new ArithmeticException("Bits length must be a multiple of 128 and longer than 512 bits");
        }

        // Calculate range for X
        // lower_bound = sqrt(2) * 2^(511 + 128x)
        // upper_bound = 2^(512 + 128x) - 1
        double false_positive_prob = 1E-6;
        int millerRabinRounds = (int)(java.lang.Math.ceil(-java.lang.Math.log(false_positive_prob) / java.lang.Math.log(4)));
        int x = (bitLength - 512) / 128;
        BigInteger sqrtOfTwo = (new BigInteger("14142135623730950489")).divide(new BigInteger("10000000000000000000"));
        BigInteger lower_bound = sqrtOfTwo.multiply(One.shiftLeft(511 + 128 * x));
        BigInteger upper_bound = One.shiftLeft(512 + 128 * x).subtract(One);
        BigInteger X = Math.randomRange(lower_bound, upper_bound);

        // Calculate R
        BigInteger[] p = {Zero, Zero};
        for (int i = 0; i < 2; i++) {
            p[i] = Math.randomPrime(101);
        }
        BigInteger tmp1 = Math.modInverse(p[1], p[0]).multiply(p[1]);
        BigInteger tmp2 = Math.modInverse(p[0], p[1]).multiply(p[0]);
        BigInteger R = tmp1.subtract(tmp2);
        BigInteger increment = p[0].multiply(p[1]);
        X = X.add(R.subtract(X.mod(increment)));
        while (!Math.isProbablePrime(X, millerRabinRounds)) {
            X = X.add(increment);

            if (X.bitLength() > bitLength) {
                throw new RuntimeException("Couldn't find prime of size " + bitLength);
            }
        }
        return X;
    }
}