package app.geochat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.anton46.collectionitempicker.CollectionPicker;
import com.anton46.collectionitempicker.Item;
import com.anton46.collectionitempicker.OnItemClickListener;
import com.facebook.android.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.geochat.R;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 26/10/15.
 */
public class TagsActivity extends AppCompatActivity implements View.OnClickListener{

    private List<Item> items = new ArrayList<>();
    private CollectionPicker mPicker;
    private EditText mTagEditText;
    private Button mAddTagButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        getWidgetReferences();
        bindWidgetEvents();
        initialization();
        setToolbar();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getText(R.string.tags));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void getWidgetReferences() {
        mPicker = (CollectionPicker) findViewById(R.id.collection_item_picker);
        mTagEditText = (EditText) findViewById(R.id.tagEditText);
        mAddTagButton = (Button) findViewById(R.id.addTagButton);
    }

    private void bindWidgetEvents() {
        mAddTagButton.setOnClickListener(this);
    }

    private void initialization() {
        items.add(new Item("justgoodshot","justgoodshot"));
        items.add(new Item("streetstyle","streetstyle"));
        items.add(new Item("stayfocused","stayfocused"));
        items.add(new Item("visualoflife","visualoflife"));
        items.add(new Item("travelgram","travelgram"));
        items.add(new Item("trell","trell"));
        items.add(new Item("morocco","morocco"));
        items.add(new Item("travelingram","travelingram"));
        items.add(new Item("welltravelled","welltravelled"));
        items.add(new Item("travellerbyheart","travellerbyheart"));
        mPicker.setItems(items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.next_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                String tags= "";
                HashMap<String,Object> map = mPicker.getCheckedItems();
                if(map.size()>=1) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        tags = entry.getKey() + "," + tags;
                    }
                    Log.e("tags", tags);
                    Bundle bundle = getIntent().getExtras();
                    bundle.putString(Constants.GEOCHAT.TAGS, tags);
                    Intent intent = new Intent(this, CreateGeoChatActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Bundle bundle = getIntent().getExtras();
                    bundle.putString(Constants.GEOCHAT.TAGS, tags);
                    Intent intent = new Intent(this, CreateGeoChatActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v==mAddTagButton){
            String tag = mTagEditText.getText().toString().trim();
            if(!tag.isEmpty()) {
                items.add(new Item(tag,tag));
                mPicker.drawItemView();
                mTagEditText.setText("");
            }
        }
    }
}
