package app.geochat.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import app.geochat.R;
import app.geochat.ui.activities.MapActivity;
import app.geochat.util.Constants;

/**
 * Created by akshaymehta on 08/11/15.
 */
public class UserExploreRecyclerViewAdapter extends RecyclerView.Adapter<UserExploreRecyclerViewAdapter.PlacesCustomViewHolder> {

    private final Activity activity;
    JSONArray jsonArray;
    private int TabPosition;
    private String place;

    public UserExploreRecyclerViewAdapter(Activity activity, JSONArray jsonArray,int TabPosition) {
        this.jsonArray = jsonArray;
        this.activity = activity;
        this.TabPosition = TabPosition;
    }

    @Override
    public void onBindViewHolder(PlacesCustomViewHolder holder, int i) {
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if(TabPosition == 0) {
                place = jsonObject.getString("checkIn");
            } else {
                place = jsonObject.getString("cityName");
            }
            holder.placeTextView.setText(place);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PlacesCustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.places_layout_row, parent,false);
        PlacesCustomViewHolder viewHolder = new PlacesCustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return (null != jsonArray ? jsonArray.length() : 0);
    }


    public class PlacesCustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView placeTextView;

        public PlacesCustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            placeTextView = (TextView) view.findViewById(R.id.placeTextView);
        }

        @Override
        public void onClick(View v) {
            if(TabPosition==0){
                Intent mapIntent = new  Intent(activity,MapActivity.class);
                mapIntent.putExtra(Constants.USER.MAPDATA,jsonArray.toString());
                activity.startActivity(mapIntent);
            }
        }
    }


}
