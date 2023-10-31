package hk231.rsa;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Math {
    static final BigInteger Zero = BigInteger.ZERO;
    static final BigInteger One = BigInteger.ONE;
    static final BigInteger Two = BigInteger.TWO;

    private static SecureRandom rng = new SecureRandom();
    public static final BigInteger SMALL_PRIME_PRODUCT = BigInteger.valueOf(2L * 3 * 5 * 7 * 11 * 13 * 17 * 19 * 23 * 29 * 31 * 37 * 41);
    public Math(SecureRandom random) {
        rng = random;
    }

    public static BigInteger randomRange(BigInteger min, BigInteger max) {
        BigInteger range = max.subtract(min);

        // Check range validation
        if (range.compareTo(Zero) < 0) {
            throw new ArithmeticException("Invalid range: [" + min + "," + max + "]");
        }

        // Special case
        if (range.compareTo(Zero) == 0) {
            return min;
        }

        // Random number
        int bitLength = range.bitLength();
        BigInteger a;
        do {
            a = new BigInteger(bitLength, rng);
        } while (a.compareTo(range) >= 0);
        return a.add(min);
    }

    // Generate a random number in the range min and max
    public static BigInteger randomBigInteger(BigInteger min, BigInteger max) {
        SecureRandom random = new SecureRandom();
        BigInteger range = max.subtract(min).add(One);
        int bitLength = range.bitLength();
        BigInteger randomBigInt;

        do {
            randomBigInt = new BigInteger(bitLength, random);
        } while (randomBigInt.compareTo(range) >= 0);

        return randomBigInt.add(min);
    }

    // Generate a random number of the specified bit length in the range 2^(bits-1) and 2^bits-1
    public static BigInteger randomBigInteger(int bitLength) {
        if (bitLength < 2) {
            throw new ArithmeticException("Prime size must be at least 2 bits");
        }
        BigInteger min = One.shiftLeft(bitLength - 1);
        BigInteger max = One.shiftLeft(bitLength).subtract(One);
        return randomRange(min, max);
    }

    // Thuật toán như sau:
    // - Nếu số mũ là lẻ thì result = result * base % modulus
    // - Nếu số mũ là chẵn thì base = base * base % modulus
    // - Lấy số mũ chia 2
    // Khi số mũ bằng 0 thì dừng
    // Thuật toán có dạng chứng minh như sau:
    // base ^ exponent % modulus = (((base ^ 2) ^ (exponent // 2) % modulus)* (base ^ (exponent % 2) % modulus)) mod
    // modulus
    static BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
//        if (exponent.bitLength() < 32) {
//            return modPow(base, exponent.intValue(), modulus);
//        }
        BigInteger result = BigInteger.ONE;
        while (exponent.compareTo(Zero) > 0) {
            // Check if exponent is even or odd
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            // // Find new base by multiply base itself and mod with modolus
            base = base.multiply(base).mod(modulus);
            // Div exponent by 2
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    static public BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        BigInteger x = Zero, y = One;
        BigInteger lastx = One, lasty = Zero, temp;
        while (!b.equals(Zero)) {
            BigInteger q = a.divide(b);
            BigInteger r = a.mod(b);

            a = b;
            b = r;

            temp = x;
            x = lastx.subtract(q.multiply(x));
            lastx = temp;

            temp = y;
            y = lasty.subtract(q.multiply(y));
            lasty = temp;
        }

        // a * lastx + b * lasty = 1
        return new BigInteger[] { a, lastx, lasty };
    }

    public static BigInteger modInverse(BigInteger e, BigInteger phi) {
        BigInteger x = extendedEuclidean(e, phi)[1];
        if (x.compareTo(Zero) < 0) {
            x = x.add(phi);
        }
        return x;
    }

    public static BigInteger gcd(BigInteger a, BigInteger b) {
        while (!b.equals(Zero)) {
            BigInteger temp = a.mod(b);
            a = b;
            b = temp;
        }
        return a;
    }

    // Fermat Test
    public static boolean fermatTestBase(BigInteger num, BigInteger base) {
        return modPow(base, num.subtract(One), num).equals(One);
    }

    public static boolean isProbablePrime(BigInteger num, int k) {
        // Check if num is negative, return false
        if (!num.testBit(0)) {
            return false;
        }

        // Check if num is smaller than 3, return true
        if (num.compareTo(BigInteger.valueOf(3)) <= 0) {
            return true;
        }

        // Apply fermat test with base 2
        if (!fermatTestBase(num, Two)) {
            return false;
        }

        //
        if (gcd(num, SMALL_PRIME_PRODUCT).compareTo(One) != 0) {
            return false;
        }

        return millerRabinTest(num, BigInteger.valueOf(k));
    }

    public static BigInteger randomPrime(int bitLength) {
        while (true) {
            BigInteger a = Math.randomBigInteger(bitLength);
            if (isProbablePrime(a, 10)) {
                return a;
            }
        }
    }

    // millerRabinTest takes a BigInteger n and an integer k as input and returns
    // true if n is prime and false if n is composite.
    // @param n: the number to be tested
    // @param k: the number of times the test is repeated
    // @return true if n is prime and false if n is composite
    public static boolean millerRabinTest(BigInteger n, BigInteger k) {
        if (n.compareTo(One) <= 0) {
            return false;
        }

        if (n.compareTo(BigInteger.valueOf(3)) <= 0) {
            return true;
        }
        int r = 0;
        BigInteger d = n.subtract(One);
        while (d.mod(Two).equals(Zero)) {
            r++;
            d = d.divide(Two);
        }
        for (BigInteger i = Zero; i.compareTo(k) < 0; i = i.add(One)) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), new SecureRandom());
            } while (a.compareTo(Two) <0 || a.compareTo(n) >= 0);
            BigInteger x = modPow(a, d, n);
            if (x.equals(One) || x.equals(n.subtract(One))) {
                continue;
            }
            boolean isProbablePrime = false;
            for (int j = 0; j < r - 1; j++) {
                x = modPow(x, Two, n);
                if (x.equals(One)) {
                    return false;
                }
                if (x.equals(n.subtract(One))) {
                    isProbablePrime = true;
                    break;
                }
            }
            if (!isProbablePrime) {
                return false;
            }
        }
        return true;
    }
}