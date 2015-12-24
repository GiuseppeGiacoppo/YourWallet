package com.giacsoft.yourwallet;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static final String ACCOUNT_ID = "ACCOUNTID";
    public static final String CAT_PREV_ACTIVITY = "CATPREVACTIVITY";
    public static final String FILTRO_PREV_ACTIVITY = "FILTROPREVACTIVITY";

    public static final int ADD = 1;
    public static final int EDIT = 2;
    public static final int DELETE = 3;

    public static final int ASC = 1;
    public static final int DESC = 2;

    public static final int AMOUNT_ALL=0;
    public static final int AMOUNT_POSITIVE=1;
    public static final int AMOUNT_NEGATIVE=2;

    public static final int ONE_SECOND = 1000;
    public static final int ONE_MINUTE = 60000;
    public static final int ONE_HOUR = 3600000;
    public static final int ONE_DAY = 86400000;

    public static String getId(Context c) {

        StringBuffer b = new StringBuffer();

        b.append(Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID));
        b.append(Build.BRAND);
        b.append(Build.DEVICE);
        b.append(Build.VERSION.RELEASE);
        b.append(Build.BOARD);
        b.append(Build.ID);

        return md5sum(b.toString());
    }

    public static String md5sum(String s) {

        try {

            MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(s.getBytes());

            byte resultSum[] = md.digest();

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < resultSum.length; i++) {
                String h = Integer.toHexString(0xFF & resultSum[i]);

                while (h.length() < 2)
                    h = "0" + h;

                hexString.append(h);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String capitalize(String s) {
        if (s.length() == 0)
            return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    public static double getMax(double[] array) {
        double max = array[0];
        for (int x = 0; x < array.length; x++) {
            if (max < array[x]) max = array[x];
        }
        return max;
    }

    public static double getMax(double x, double y) {
        return x < y ? y : x;
    }

    public static double getMin(double[] array) {
        double min = array[0];
        for (int x = 0; x < array.length; x++) {
            if (min > array[x]) min = array[x];
        }
        return min;
    }

    public static double getMin(double x, double y) {
        return x > y ? y : x;
    }
}