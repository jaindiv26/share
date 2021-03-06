package org.odk.share.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.odk.share.R;
import org.odk.share.dao.InstancesDao;
import org.odk.share.dao.TransferDao;
import org.odk.share.dto.Instance;
import org.odk.share.dto.TransferInstance;
import org.odk.share.provider.InstanceProviderAPI;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.odk.share.activities.MainActivity.FORM_DISPLAY_NAME;
import static org.odk.share.activities.MainActivity.FORM_ID;
import static org.odk.share.activities.MainActivity.FORM_VERSION;

/**
 * Created by laksh on 6/27/2018.
 */

public class StatisticsFragment extends Fragment {

    @BindView(R.id.tvFormsSent)
    TextView formsSent;
    @BindView(R.id.tvFormsReceived)
    TextView formsReceived;
    @BindView(R.id.tvFormsReviewed)
    TextView formsReviewed;
    @BindView(R.id.formTitle)
    TextView title;
    @BindView(R.id.formSubTitle)
    TextView subtitle;

    private String formVersion;
    private String formId;
    private String formName;

    public StatisticsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        ButterKnife.bind(this, view);

        formVersion = getActivity().getIntent().getStringExtra(FORM_VERSION);
        formId = getActivity().getIntent().getStringExtra(FORM_ID);
        formName = getActivity().getIntent().getStringExtra(FORM_DISPLAY_NAME);

        title.setText(formName);
        StringBuilder sb = new StringBuilder();
        if (formVersion != null) {
            sb.append(getString(R.string.version, formVersion)).append(" ");
        }
        sb.append(getString(R.string.id, formId));

        subtitle.setText(sb);
        return view;
    }

    @Override
    public void onResume() {
        String []selectionArgs;
        String selection;

        if (formVersion == null) {
            selectionArgs = new String[]{formId};
            selection = InstanceProviderAPI.InstanceColumns.JR_FORM_ID + "=? AND "
                    + InstanceProviderAPI.InstanceColumns.JR_VERSION + " IS NULL";
        } else {
            selectionArgs = new String[]{formId, formVersion};
            selection = InstanceProviderAPI.InstanceColumns.JR_FORM_ID + "=? AND "
                    + InstanceProviderAPI.InstanceColumns.JR_VERSION + "=?";
        }
        Cursor cursor = new InstancesDao().getInstancesCursor(selection, selectionArgs);
        HashMap<Long, Instance> instanceMap = new InstancesDao().getMapFromCursor(cursor);

        Cursor transferCursor = new TransferDao().getSentInstancesCursor();
        List<TransferInstance> transferInstances = new TransferDao().getInstancesFromCursor(transferCursor);
        int sentCount = 0;
        for (TransferInstance instance : transferInstances) {
            if (instanceMap.containsKey(instance.getInstanceId())) {
                sentCount++;
            }
        }
        formsSent.setText(String.valueOf(sentCount));

        transferCursor = new TransferDao().getReceiveInstancesCursor();
        transferInstances = new TransferDao().getInstancesFromCursor(transferCursor);
        int receiveCount = 0;
        for (TransferInstance instance : transferInstances) {
            if (instanceMap.containsKey(instance.getInstanceId())) {
                receiveCount++;
            }
        }
        formsReceived.setText(String.valueOf(receiveCount));

        transferCursor = new TransferDao().getReviewedInstancesCursor();
        transferInstances = new TransferDao().getInstancesFromCursor(transferCursor);
        int reviewCount = 0;
        for (TransferInstance instance : transferInstances) {
            if (instanceMap.containsKey(instance.getInstanceId())) {
                reviewCount++;
            }
        }
        formsReviewed.setText(String.valueOf(reviewCount));

        super.onResume();
    }
}