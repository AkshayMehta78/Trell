package app.geochat.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import app.geochat.DesidimeApplication;
import app.geochat.R;
import app.geochat.beans.SharedPreferences;
import app.geochat.ui.activities.BaseActivity;
import app.geochat.ui.activities.CheckInActivity;
import app.geochat.ui.activities.CreateGeoChatActivity;
import app.geochat.ui.activities.HomeActivity;
import app.geochat.ui.activities.SelectPhotoActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by akshay on 23/7/15.
 */
public class Utils {

    static ProgressDialog progressDialog;
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
     * Setting up toolbar without pager
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

    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showProgress(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getResources().getString(R.string.app_name));
        progressDialog.setMessage(context.getResources().getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();
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

    public static void createGeoChat(Context context) {
        Intent intent = new Intent(context, CheckInActivity.class);
        context.startActivity(intent);
    }


    public static ArrayList<String>  getCameraImages(Context context) {
        ArrayList<String>  result = new  ArrayList<String> ();
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "/DCIM/Camera");
        File[] listFile;
        if (file.isDirectory()) {
            listFile = file.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                result.add(listFile[i].getAbsolutePath());
            }
        }
        return result;
    }


    public static String getPath(Activity context, Uri uri) {
        if( uri == null ) {
            return null;
        }

        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }


    public static Bitmap getBitmap(Activity activity,Uri pictureUri) {

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


    public static String fileSize(String url)
    {
        File file = new File(url);
        long length = file.length();
        length = length/1024;
        return length+"KB";
    }

    public static String compressImage(String imagePath,Activity activity) {

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
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
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
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
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
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
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

}
