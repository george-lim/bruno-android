package com.cs446.group7.bruno.utils;

// A simple vector utility that houses useful vector methods
public class Vector {
    // NOTE: - Vector is a utility class, should not be instantiated
    private Vector() {}

    // MARK: - N dimension vector methods

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

    // MARK: - 3 dimension vector methods

    // Returns dot product of vector1 and vector2
    public static float dotProduct3D(final float[] vector1, final float[] vector2) {
        float result = 0;
        for (int i = 0; i < 3; ++i) {
            result += vector1[i] * vector2[i];
        }
        return result;
    }
}
