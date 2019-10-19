package com.anizmo.outtimereminder;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class Utility {

    public static final String HAS_SET_WORK_HOURS = "HAS_SET_WORK_HOURS";
    public static final String HAS_SET_CONTACT = "HAS_SET_CONTACT";
    public static final String REACHED_MESSAGE = "REACHED_MESSAGE";
    public static final String PHONE_NUMBER = "PHONE_NUMBER";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String HAS_SET_WORK_HOURS_FRAGMENT = "HAS_SET_WORK_HOURS_FRAGMENT";
    public static final String HOURS = "HOURS";
    public static final String MINUTES = "MINUTES";
    public static final String HAS_STARTED_TIMER_FOR_TODAY = "HAS_STARTED_TIMER_FOR_TODAY";
    public static final String OUT_TIME_FOR_TODAY = "OUT_TIME_FOR_TODAY";

    private static final String TAG = "Utility";
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static Pattern pattern;

    /**
     * Validate Email with regular expression
     *
     * @param email email address string
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validateEmail(final String email) {
        if (pattern == null) {
            pattern = Pattern.compile(EMAIL_PATTERN);
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Checks whether the device has an available network connection for data transfer or not.
     *
     * @param context The context in which the check needs to be performed.
     * @return <code>true</code> if network is available, <code>false</code> if not.
     */
    public static boolean checkNetwork(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();

    }

    // Reads an InputStream and converts it to a String.
    public static String streamToString(final InputStream stream) throws IOException {
        final Reader reader = new InputStreamReader(stream, "UTF-8");

        final StringBuilder builder = new StringBuilder();

        try {
            final char[] buffer = new char[500];
            while (reader.read(buffer) != -1) {
                builder.append(buffer);
            }

            return builder.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    // Reads an BufferedReader and converts it to a String.
    public static String streamToString(final BufferedReader reader) throws IOException {
        final StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        return builder.toString();
    }

    public static String getImageBasedOnDayNight(String imageName) {
        return imageName;
    }


    /**
     * Clears all the values from SharedPreferences. Used primarily for the LogOff functionality.
     *
     * @param context The {@link Context} in which the preferences need to be cleared.
     */
    public static void clearPreferences(Context context) {
        final SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPreferences.clear();
        sharedPreferences.apply();
    }

    /**
     * Clears the provided element from sharedpreferences.
     *
     * @param context context of the calling activity
     * @param key     The key to be removed.
     */
    public static void clearPreferences(final Context context, final String key) {
        final SharedPreferences.Editor sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
        sharedPreferences.remove(key);
        sharedPreferences.apply();
    }


    public static int convertToDp(final int valueToConvert, final Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueToConvert,
                context.getResources().getDisplayMetrics());
    }

    /**
     * Creating a temporary file from the {@link Bitmap} in the cache directory.
     * <p>
     * This is done because Multipart request requires a file to be uploaded and not the instance of the bitmap.
     * </p>
     *
     * @param screenshot The {@link Bitmap} which needs to be preserved in a file.
     * @return The {@link File} for the given {@link Bitmap}, <code>null</code> if argument received is <code>null</code> or some {@link Exception} occurs.
     */
    public static File createFileFromBitmap(final Bitmap screenshot, final Context context, final String fileName) {
        // LoggerUtility.debugLog(TAG, "Entered ShareFeedbackIntentService.createFileFromBitmap");
        File file = null;
        if (screenshot != null) {
            try {
                //create a file to write bitmap data
                file = new File(context.getCacheDir(), fileName);
                if (file.exists()) {
                    // LoggerUtility.debugLog(TAG, "File with same name existed previously, deleting it");
                    file.delete();
                }
                file.createNewFile();
                //  LoggerUtility.debugLog(TAG, String.format("File created with the name %s", fileName));

                //Convert bitmap to byte array
                final Bitmap bitmap = screenshot;
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                final FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        // LoggerUtility.debugLog(TAG, "Exiting ShareFeedbackIntentService.createFileFromBitmap with file size=" + (file == null ? 0 : file.length()));
        return file;
    }

    public static Drawable getRotateDrawable(final Drawable d, final float angle) {
        final Drawable[] arD = {d};
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }

    /**
     * Checks if the app is in Background or foreground
     *
     * @param context The {@link Context} for which to check.
     * @return <code>true</code> if the app in in background, <code>false</code> if its in foreground.
     */
    public static boolean isAppInBackground(Context context) {
        boolean isInBackground = true;
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            final List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (final String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            final List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            final ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    /**
     * Creates a watermark in the provided image and returns an updated image.
     *
     * @param src      The {@link Bitmap} in which the watermark has to be added.
     * @param text     The text to be used as watermark.
     * @param location The {@link Point} containing the location within the image where watermark is to be added.
     * @param textSize The <code>float</code> size of the text.
     * @param color    The color to be used for text.
     * @return The newly created {@link Bitmap} with watermark.
     */
    public static Bitmap addWaterMark(final Bitmap src, final String text, final Point location, final float textSize, final int color) {
        //  LoggerUtility.debugLog(TAG, String.format("Entered addWatermark to add a watermark text %s", text));
        final int w = src.getWidth();
        final int h = src.getHeight();
        final Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        final Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        final Paint paint = new Paint();
        paint.setColor(color);
        //paint.setAlpha(alpha);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        canvas.drawText(text, location.x, location.y, paint);
        //   LoggerUtility.debugLog(TAG, "Exiting addWatermark");
        return result;
    }

    /**
     * Adds an Image watermark to the provided {@link Bitmap}
     *
     * @param src       The source {@link Bitmap} in which the watermark is to be added.
     * @param waterMark The watermark {@link Bitmap} which has to be added.
     * @return The final {@link Bitmap} with watermark.
     * @see #addWaterMark(Bitmap, String, Point, float, int)
     * @see #addWaterMark(Bitmap, Bitmap)
     */
    public static Bitmap addWaterMark(final Bitmap src, final Bitmap waterMark) {
        // LoggerUtility.debugLog(TAG, "Entered addWaterMark(Bitmap src, Bitmap watermark)");
        final int w = src.getWidth();
        final int h = src.getHeight();
        final Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        final Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        final int waterMarkX = src.getWidth() - waterMark.getWidth() - 50;
        final int waterMarkY = src.getHeight() - waterMark.getHeight() - 50;
        canvas.drawBitmap(waterMark, waterMarkX, waterMarkY, null);

        src.recycle();
        waterMark.recycle();
        //   LoggerUtility.debugLog(TAG, "Exit addWatermark");
        return result;
    }

    /**
     * Converts DP to pixel
     *
     * @param dp The number in dp to be converted to pixel
     * @return The pixels for the given dp.
     */
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * @see #addToPreferences(Context, String, int)
     */
    public static void addToPreferences(final Context context, final String key, final String value) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void addToPreferences(final Context context, final String key, final int value) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void addToPreferences(final Context context, final String key, final long value) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void addToPreferences(final Context context, final String key, final boolean value) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public static String getStringFromPreference(Context context, String key, String defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static boolean getBoolFromPreference(Context context, String key, boolean defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);
    }

    public static int getIntFromPreference(Context context, String key, int defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(key, defaultValue);
    }

    public static long getLongFromPreference(Context context, String key, int defaultValue) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(key, defaultValue);
    }

    /**
     * Stores the image in local storage and sets the flag in {@link SharedPreferences} to indicate that
     * the user has updated the image. This flag enables the app to detect an image to put it whenever the user
     * visits the page next.
     *
     * @param bitmap Bitmap object which need to write
     */
    public static void writeImageToStorage(final Context context, final String imagePath, final Bitmap bitmap) {
        // LoggerUtility.debugLog(TAG, "Entered writeImageToStorage to store image to private storage");
        final File profilePic = new File(imagePath);
        try {
            final FileOutputStream imageStream = new FileOutputStream(profilePic);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageStream);
            imageStream.flush();
            imageStream.close();
            MediaScannerConnection.scanFile(context, new String[]{profilePic.getPath()}, new String[]{"image/png"}, null);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error while attemtping to access file", e);
        } catch (IOException e) {
            Log.e(TAG, "Error while trying to write file", e);
        }
        //  LoggerUtility.debugLog(TAG, "Exiting writeImageToStorage");
    }

    public static String getStringColorFromInteger(final int color) {
        //Converts int colour to string colour

        return String.format("#%06X", 0xFFFFFF & color);
    }

    public static String formateDateForTrackingScreen(Date date) {
        SimpleDateFormat dt = new SimpleDateFormat("EEEE, MMMM dd");
        return dt.format(date);
    }

    public static void hideKeyboard(Context context, IBinder token) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token, 0);
    }

    public static Bitmap captureScreenShots(AppCompatActivity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        //Find the screen dimensions to create bitmap in the same size.
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static void setHasSetWorkHours(String username, Context context) {
        final SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("HAS_SET_WORK_HOURS", username);
        editor.apply();
    }

    public static String getHasSetWorkHours(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("HAS_SET_WORK_HOURS", null);
    }


    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void clearFilesDirectoryForWave(Context context) {
        File wavFile = context.getFilesDir();
        Stream.of(wavFile.listFiles()).filter(file -> !file.isDirectory() && file.getName().endsWith(".wav")).forEach(File::delete);
    }

    public static int spToPx(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


    public static Bitmap getTempBitmap(Context context, int width, int height, int position) {
        File file = new File(context.getFilesDir(), "Temp" + position + ".png");
        if (file.exists()) {
            return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()), width, height, true);
        }
        return null;
    }

    public static void storeBitmapInTempFolder(Context context, Bitmap bitmap, int position) {
        OutputStream fOut = null;
        File file = new File(context.getFilesDir(), "Temp" + position + ".png");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static String generateBitmapFromView(Context context, View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        FileOutputStream fOut = null;
        try {
            File file = new File(context.getExternalCacheDir(), "cuddle_screenshot.png");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            {
                if (fOut != null) {
                    try {
                        fOut.flush();
                        fOut.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }


    public static int getResource(Context context, String resourceName, String resourceType, String packageName) {
        return context.getResources().getIdentifier(resourceName, resourceType, packageName);
    }

    public static boolean isVisible(Fragment fragment, final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        fragment.getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, width, height);
        return actualPosition.intersect(screen);
    }

    public static String getSHA(PackageInfo info) {
        String SHA = "";
        try {
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                SHA = Base64.encodeToString(md.digest(), Base64.DEFAULT);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        if (BuildConfig.DEBUG) {
            SHA = "VzagO+ufWljKCWHfY1a+0uR8Vek=";
        }
        return SHA;
    }

    public static String basic(String username, String password) {
        String usernameAndPassword = username + ":" + password;
        String encoded = Base64.encodeToString(usernameAndPassword.getBytes(), Base64.NO_WRAP);
        return "Basic " + encoded;
    }

}
