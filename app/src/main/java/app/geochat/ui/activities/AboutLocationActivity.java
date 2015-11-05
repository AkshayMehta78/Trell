package app.geochat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import app.geochat.R;
import app.geochat.util.Constants;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 26/10/15.
 */
public class AboutLocationActivity extends AppCompatActivity {

    private EditText mDescriptionEditText;
    private TextView mMaxCountTextView;
    private int mMaxCount = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getWidgetReferences();
        bindWidgetEvents();
        initialization();
        setToolbar();

        Utils.slideUpViewAnimator(this, mDescriptionEditText);

        mDescriptionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int newLength = mMaxCount - s.length();
                mMaxCountTextView.setText(String.valueOf(newLength));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getText(R.string.description));
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
    }

    private void getWidgetReferences() {
        mDescriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        mMaxCountTextView = (TextView) findViewById(R.id.maxCountTextView);
    }

    private void bindWidgetEvents() {
    }

    private void initialization() {
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
                String description = mDescriptionEditText.getText().toString().trim() + "";
                Bundle bundle = getIntent().getExtras();
                bundle.putString(Constants.GEOCHAT.DESCRIPTION, description);
                Intent intent = new Intent(this, SelectPhotoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
