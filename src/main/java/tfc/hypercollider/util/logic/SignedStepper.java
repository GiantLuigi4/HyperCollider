package tfc.hypercollider.util.logic;

public class SignedStepper {
    public static int step(int sign, int value) {
//        return value + sign;
        return sign;
    }

    public static boolean checkDone(int sign, int value, int start, int end) {
        if (sign == 1) {
            return value <= end;
        } else {
            return value >= end;
        }
    }

    protected static double roundAway(double value) {
        if (value > 0) return Math.ceil(value);
        return Math.floor(value);
    }

    protected static double roundTo(double value) {
        if (value > 0) return Math.floor(value);
        return Math.ceil(value);
    }

    public static int getStartValue(int xSign, double minX, double maxX, double motionX, int padding) {
        if (xSign == 1) {
            return (int) Math.floor(maxX) - padding;
        } else {
            return (int) Math.ceil(minX) + padding;
        }
    }

    public static int getEndValue(int xSign, double minX, double maxX, double motionX, int padding) {
        if (xSign == 1) {
            return (int) Math.ceil(maxX + motionX) + padding;
        } else {
            return (int) Math.floor(minX + motionX) - padding;
        }
    }
}
