package app.geochat.services.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

import app.geochat.ui.activities.AddTrellImage;
import app.geochat.ui.activities.SelectPhotoActivity;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 08/10/15.
 */
public class AllPhotosAsyncTask extends AsyncTask<String, Void, ArrayList<String>>  {

    private Context mContext;

    public AllPhotosAsyncTask(Context context)
    {
        mContext = context;
    }
    @Override
    protected ArrayList<String>  doInBackground(String... params) {
        return Utils.getCameraImages(mContext);
    }

    @Override
    protected void onPostExecute(ArrayList<String>  result) {
        if(mContext instanceof  SelectPhotoActivity)
            ((SelectPhotoActivity)mContext).renderCameraImages(result);
        else
            ((AddTrellImage)mContext).renderCameraImages(result);

    }

    @Override
    protected void onPreExecute() {}

    @Override
    protected void onProgressUpdate(Void... values) {}

}
