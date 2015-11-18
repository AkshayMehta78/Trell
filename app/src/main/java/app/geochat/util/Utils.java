package app.geochat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.anton46.collectionitempicker.Item;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.geochat.R;
import app.geochat.beans.GeoChat;
import app.geochat.beans.SharedPreferences;
import app.geochat.services.asynctask.LocationService;
import app.geochat.ui.activities.BaseActivity;
import app.geochat.ui.activities.ChatActivity;
import app.geochat.ui.activities.CreateNewTrell;
import app.geochat.ui.activities.MapActivity;
import app.geochat.ui.activities.SearchActivity;

/**
 * Created by akshay on 23/7/15.
 */
public class Utils {

    static MaterialDialog progressDialog;
    private static int startY = -1;
    private static int segmentLength = -1;

    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    /**
     * Matches code in MediaProvider.computeBucketValues. Should be a common
     * function.
     */
    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    /**
     * Send google analytics hit event and sets the screen name that will be visible in analytics
     *
     * @param activity
     */

    /**
     * Setting up toolbar for main page
     *
     * @param context
     * @param title
     * @param activity
     */
    public static void setToolbar(Context context, int title, AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle(context.getText(title));
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
    }


    /**
     * Setting up toolbar
     *
     * @param context
     * @param title
     * @param activity
     */
    public static void setActivityToolbar(Context context, int title, AppCompatActivity activity) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        toolbar.setTitle(context.getText(title));
        activity.setSupportActionBar(toolbar);
        final ActionBar ab = activity.getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showProgress(Context context) {
        progressDialog = new MaterialDialog.Builder(context)
                .content(context.getString(R.string.please_wait))
                .progress(true, 0)
                .show();
    }

    public static void closeProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getDeviceId() != null) {
            return tm.getDeviceId();
        } else {
            String android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            return android_id;
        }
    }

    public static void startHomeActivity(Context context) {
        Intent intent = new Intent(context, BaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

    public static void chooseImageDialog(final Activity context,
                                         final int gallery_req_code, final int camera_req_code,
                                         String message, final Uri mImageCaptureUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("Camera",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(
                                android.provider.MediaStore.EXTRA_OUTPUT,
                                mImageCaptureUri);
                        try {
                            intent.putExtra("return-data", false);
                            context.startActivityForResult(intent,
                                    camera_req_code);
                        } catch (Exception e) {

                            Toast.makeText(context,
                                    context.getString(R.string.no_camera_txt),
                                    Toast.LENGTH_LONG).show();
                        }

                    }
                });
        builder.setNegativeButton("Gallery",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent pickPhoto = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        try {
                            context.startActivityForResult(pickPhoto,
                                    gallery_req_code);
                        } catch (Exception e) {
                            Toast.makeText(context,
                                    context.getString(R.string.no_gallery_txt),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String doCrop(final Activity context,
                                final Uri mImageCaptureUri, final int crop_from_camera) {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = context.getPackageManager()
                .queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(context, "Can not find image crop app",
                    Toast.LENGTH_SHORT).show();
            return getRealPathFromURI(mImageCaptureUri, context);


        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                context.startActivityForResult(i, crop_from_camera);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = context.getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = context.getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(context,
                        cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                context.startActivityForResult(
                                        cropOptions.get(item).appIntent,
                                        crop_from_camera);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        if (mImageCaptureUri != null) {
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
        return null;
    }

    public static String getRealPathFromURI(Uri contentUri, Activity activity) {
        if (contentUri != null) {
            String[] proj = {MediaStore.Audio.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(contentUri,
                    proj, null, null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return null;
    }


    public static void loadImageWithVolley(final Context context, final String url, final ImageView targetView) {
        // Retrieves an image specified by the URL, displays it in the UI.
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        Utils.showToast((Activity) context, bitmap.toString());
                        targetView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Utils.showToast((Activity) context, error.getMessage());
                        targetView.setImageResource(R.drawable.ic_default_profile_pic);
                    }
                });
        // Access the RequestQueue through your singleton class.
        VolleyController.getInstance().addToRequestQueue(request);
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isBluetoothEnabled() {
        boolean flag = false;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                flag = true;
            }
        }
        return flag;
    }


    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
//                        SplashScreen.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Login", "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }


    //Obtaining a registration token for GCM
    public static void registerInBackground(final Context context, final SharedPreferences mAlarmSharedPreference) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String regId = "";

                InstanceID instanceID = InstanceID.getInstance(context);
                try {
                    regId = instanceID.getToken(context.getString(R.string.custom_gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return regId;
            }

            @Override
            protected void onPostExecute(String regId) {
                if (!TextUtils.isEmpty(regId)) {
                    Log.d("GCM", "Registered with GCM Server successfully. RegID: " + regId);
                    mAlarmSharedPreference.setDeviceToken(regId);
                } else {
                    Log.d("GCM", "Reg ID Creation Failed!!!");
                }
            }
        }.execute(null, null, null);
    }

    public static ArrayList<String> getCameraImages(Context context) {

        final String[] projection = {MediaStore.Images.Media.DATA};
        final String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
        final String[] selectionArgs = {CAMERA_IMAGE_BUCKET_ID};
        final Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        ArrayList<String> result = new ArrayList<String>(cursor.getCount());
        if (cursor.moveToFirst()) {
            final int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            do {
                final String data = cursor.getString(dataColumn);
                result.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return result;
    }


    public static String getPath(Activity context, Uri uri) {
        if (uri == null) {
            return null;
        }

        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }


    public static Bitmap getBitmap(Activity activity, Uri pictureUri) {

        Uri uri = pictureUri;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = activity.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Bitmap b = null;
            in = activity.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();
            return b;
        } catch (IOException e) {
            return null;
        }
    }


    public static String fileSize(String url) {
        File file = new File(url);
        long length = file.length();
        length = length / 1024;
        return length + "KB";
    }

    public static String compressImage(String imagePath, Activity activity) {

        String filePath = imagePath;
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "GeoChat/GeoChatImages");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    public static double[] getUserLocation(Activity activity) {
        double[] location = new double[2];
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            location = null;
        } else {
            LocationService appLocationService = new LocationService(activity);
            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

            if (gpsLocation != null) {
                location[0] = gpsLocation.getLatitude();
                location[1] = gpsLocation.getLongitude();
            }
        }
        return location;
    }

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static Uri getImageURI(Context context, Bitmap photo) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), photo, "Title", null);
        return Uri.parse(path);
    }

    public static void slideUpViewAnimator(Context context, EditText editext) {
        Animation slide_up = AnimationUtils.loadAnimation(context, R.anim.slide_up_out);
        editext.startAnimation(slide_up);
    }


    public static String dateDiff(Date date) {
        long oldMillis = date.getTime();
        long currMillis = System.currentTimeMillis();
        String msg = null;
        long diff = java.lang.Math.abs(currMillis - oldMillis);
        long temp = diff / 60000;
        if (temp >= 60) {
            temp = temp / 60;
            if (temp >= 24) {
                temp = temp / 24;
                if (temp > 30) {
                    temp = temp / 30;
                    if (temp > 12) {
                        temp = temp / 12;

                        if (temp == 1) {
                            msg = temp + " year ago";
                        } else {
                            msg = temp + " years ago";
                        }
                    } else {

                        if (temp == 1) {
                            msg = temp + " month ago";
                        } else {
                            msg = temp + " months ago";
                        }
                    }
                } else {

                    if (temp == 1) {
                        msg = temp + " day ago";
                    } else {
                        msg = temp + " days ago";
                    }
                }
            } else {

                if (temp == 1) {
                    msg = temp + " hour ago";
                } else {
                    msg = temp + " hours ago";
                }
            }
        } else {

            if (temp == 1) {
                msg = temp + " min ago";
            } else {
                msg = temp + " mins ago";
            }
        }
        return msg;
    }

    public static Date getDate(String date) {

        String trimdate = date.substring(0, 19);
        SimpleDateFormat inputFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date parsed = null;
        try {
            parsed = inputFormat.parse(trimdate);
        } catch (ParseException e) {


        }
        outputFormat.format(parsed);
        return parsed;
    }

    public static List<Item> getItemListArray(String[] tagsArray) {
        List<Item> list = new ArrayList<>();
        if (tagsArray.length > 0) {
            for (int i = 0; i < tagsArray.length; i++) {
                list.add(new Item(tagsArray[i], tagsArray[i]));
            }
        }
        return list;
    }

    public static boolean isMyPost(String userId, Context context) {
        if (new SharedPreferences(context).getUserId().equalsIgnoreCase(userId))
            return true;
        else
            return false;
    }

    public static void openShareIntent(GeoChat item, Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(getBitmapFromURl(item.getGeoChatImage(), activity, item.getCity()));
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getDescripton());
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(shareIntent);
    }

    public static void openInstagramShareIntent(GeoChat item, Activity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(getBitmapFromURl(item.getGeoChatImage(), activity, item.getCity()));
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setPackage("com.instagram.android");
        activity.startActivity(shareIntent);
    }


    public static String getBitmapFromURl(String urlAddress, Activity activity, String city) {
        String path = "";
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap immutableBpm = BitmapFactory.decodeStream(input);
            Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888, true);
            View view = new View(activity);
            view.draw(new Canvas(mutableBitmap));
            path = MediaStore.Images.Media.insertImage(activity.getContentResolver(), mutableBitmap, city, null);
        } catch (Exception e) {

        }
        return path;
    }

    public static void reportContent(GeoChat item, Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", Constants.ADMINEMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, item.getCheckInLocation() + " - Report");
        activity.startActivity(Intent.createChooser(emailIntent, "Report Content"));
    }

    public static Spanned getFormattedTags(String[] tagsArray, final Activity activity) {
        Spanned finalTags = new SpannableString("");
        final ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#2196F3"));
        final RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
        for (int i = 0; i < tagsArray.length; i++) {
            final String tag = tagsArray[i];
            Spannable wordtoSpan = new SpannableString(tag);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    Utils.startSearchActivity(activity,Constants.SEARCH.TYPE_TAG,tag);
                }
            };
//            wordtoSpan.setSpan(clickableSpan, 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(clickableSpan, 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(span, 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            wordtoSpan.setSpan(new BorderedSpan(activity), 0, tag.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            finalTags = (Spanned) TextUtils.concat(wordtoSpan, "   ", finalTags);
        }
        return finalTags;
    }

    private static void startSearchActivity(Activity activity, String type, String tag) {
        Intent intent = new Intent(activity,SearchActivity.class);
        intent.putExtra(Constants.SEARCH.TYPE,type);
        intent.putExtra(Constants.SEARCH.TAG, tag);
        activity.startActivity(intent);
    }


    public static double[] getLocationDetails(Context context) {
        double[] location = new double[2];
        double latitude, longitude;
        LocationService appLocationService = new LocationService(context);
        Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();

        } else {
            gpsLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);
            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
            } else {
                latitude = 19.23;
                longitude = 72.84;
            }
        }
        location[0] = latitude;
        location[1] = longitude;
        return location;
    }

    public static int getLocationDistance(String noteLat, String noteLong, String loclatitude, String loclongitude) {
        Log.e("Distance",loclatitude+"-"+loclongitude);
        Location source = new Location("Source");
        source.setLatitude(Double.parseDouble(loclatitude));
        source.setLongitude(Double.parseDouble(loclongitude));

        Location destination = new Location("Destination");
        destination.setLatitude(Double.parseDouble(noteLat));
        destination.setLongitude(Double.parseDouble(noteLong));

        return (int) source.distanceTo(destination);
    }

    public static void openFullScreenNote(FragmentActivity activity, GeoChat item) {
        Intent chatIntent = new Intent(activity, ChatActivity.class);
        chatIntent.putExtra(Constants.Preferences.GEOCHAT, item);
        activity.startActivity(chatIntent);
    }

    public static void openFullScreenNoteWithComment(FragmentActivity activity, GeoChat item) {
        Intent chatIntent = new Intent(activity, ChatActivity.class);
        chatIntent.putExtra(Constants.Preferences.GEOCHAT, item);
        chatIntent.putExtra(Constants.Preferences.COMMENT, "1");
        activity.startActivity(chatIntent);
    }


    public static Bitmap getBitmapFromURL(String src, Context context) {
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Bitmap scaledBitmap;
            if (!src.isEmpty()) {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inSampleSize = 2;
                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 90, 90, true);
                myBitmap.recycle();
            } else {
                Bitmap myBitmap =  BitmapFactory.decodeResource(context.getResources(),R.drawable.travel_bg);
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inSampleSize = 2;
                scaledBitmap = Bitmap.createScaledBitmap(myBitmap, 90, 90, true);
                myBitmap.recycle();
            }
            return scaledBitmap;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Spanned getFormattedTagsWithoutClicks(String[] tagsArray, final Activity activity) {
        Spanned finalTags = new SpannableString("");
        final ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#2196F3"));
        final RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.5f);
        for (int i = 0; i < tagsArray.length; i++) {
            final String tag = tagsArray[i];
            Spannable wordtoSpan = new SpannableString(tag);
            wordtoSpan.setSpan(span, 0, tag.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            finalTags = (Spanned) TextUtils.concat(wordtoSpan, " ", finalTags);
        }
        return finalTags;
    }

    public static void openFacebookShareIntent(GeoChat item, FragmentActivity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(getBitmapFromURl(item.getGeoChatImage(), activity, item.getCity()));
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, "www.google.com");
//        shareIntent.setType("text/plain");
        shareIntent.setPackage("com.facebook.katana");
        activity.startActivity(shareIntent);
    }

    public static void openTwitterShareIntent(GeoChat item, FragmentActivity activity) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        Uri uri = Uri.parse(getBitmapFromURl(item.getGeoChatImage(), activity, item.getCity()));
        shareIntent.putExtra(Intent.EXTRA_TEXT, item.getDescripton());
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.setPackage("com.twitter.android");
        activity.startActivity(shareIntent);
    }
}
