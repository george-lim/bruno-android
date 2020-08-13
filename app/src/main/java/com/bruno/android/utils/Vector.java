package com.bruno.android.utils;

// A simple vector utility that houses useful vector methods
public class Vector {
    // NOTE: - Vector is a utility class, should not be instantiated
    private Vector() {}

    // MARK: - Vector exceptions

    // Exception caused by vector1 and vector2 not being the same dimension
    public static class UnequalDimensionException extends RuntimeException {
        public UnequalDimensionException() {
            super("Vector operation requires input vectors to be the same dimension");
        }
    }

    // MARK: - Vector operations

    // Returns vector component sum
    public static float componentSum(final float[] vector) {
        float result = 0;
        for (float i : vector) {
            result += i;
        }
        return result;
    }

    // Returns vector norm
    public static float norm(final float[] vector) {
        float result = 0;
        for (float i : vector) {
            result += i * i;
        }
        return (float)Math.sqrt(result);
    }

    // Returns normalized vector
    public static float[] normalize(final float[] vector) {
        float[] result = new float[vector.length];
        float norm = norm(vector);
        for (int i = 0; i < vector.length; ++i) {
            result[i] = vector[i] / norm;
        }
        return result;
    }

    // Returns dot product of vector1 and vector2
    public static float dotProduct(final float[] vector1, final float[] vector2)
            throws UnequalDimensionException {
        if (vector1.length != vector2.length) {
            throw new UnequalDimensionException();
        }
        float result = 0;
        for (int i = 0; i < vector1.length; ++i) {
            result += vector1[i] * vector2[i];
        }
        return result;
    }
}
