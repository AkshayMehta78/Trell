package app.geochat.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import app.geochat.ui.activities.TermsAndConditionActivity;
import app.geochat.R;
import app.geochat.util.Utils;

/**
 * Created by akshaymehta on 05/08/15.
 */
public class AboutUsFragment extends Fragment implements OnClickListener{

    public static final String tag = AboutUsFragment.class.getSimpleName();
    public static final String sActivityName = "About Us";

    View mView;
    private TextView mVisitDesiDimeTextView, mTermsConditionTextView, mShareAppTextView;
    private TextView mBrowseAppsFromDesiDimeTextView;
    private TextView mRateUsTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_aboutus, container, false);
        //set toolbar
        Utils.setToolbar(mView.getContext(), R.string.title_about_us, (AppCompatActivity) getActivity());
        getWidgetReferences();
        setWidgetEvents();
        initialization();
        return mView;
    }

    public void getWidgetReferences() {
        mVisitDesiDimeTextView = (TextView) mView.findViewById(R.id.visitDesiDimeTextView);
        mBrowseAppsFromDesiDimeTextView = (TextView) mView.findViewById(R.id.browseAppsFromDesiDimeTextView);
        mRateUsTextView = (TextView) mView.findViewById(R.id.rateUsTextView);
        mTermsConditionTextView = (TextView) mView.findViewById(R.id.termsConditionTextView);
        mShareAppTextView = (TextView) mView.findViewById(R.id.shareAppTextView);
    }

    public void setWidgetEvents() {
        mVisitDesiDimeTextView.setOnClickListener(this);
        mBrowseAppsFromDesiDimeTextView.setOnClickListener(this);
        mRateUsTextView.setOnClickListener(this);
        mTermsConditionTextView.setOnClickListener(this);
        mShareAppTextView.setOnClickListener(this);
    }

    public void initialization() {

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.visitDesiDimeTextView:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.website_url)));
                startActivity(intent);
                break;

            case R.id.browseAppsFromDesiDimeTextView:
                final String devName = "Desidime.com";
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:" + devName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Desidime.com")));
                }
                break;

            case R.id.rateUsTextView:

                try {
             //       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_MARKET_APP_LINK)));
                } catch (android.content.ActivityNotFoundException anfe) {
             //       startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_PLAY_APP_LINK)));
                }
                break;


            case R.id.termsConditionTextView:
                intent = new Intent(getActivity(), TermsAndConditionActivity.class);
                startActivity(intent);
                break;

            case R.id.shareAppTextView:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
            //    intent.putExtra(Intent.EXTRA_TEXT, Constants.GOOGLE_PLAY_APP_LINK);
                startActivity(Intent.createChooser(intent, "Share"));
                break;
            default:
                break;
        }
    }
}
