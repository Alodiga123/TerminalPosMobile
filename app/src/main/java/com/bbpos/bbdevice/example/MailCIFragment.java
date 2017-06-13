package com.bbpos.bbdevice.example;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by usuario on 13/06/17.
 */

public class MailCIFragment extends Fragment{

    View rootView =null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.main_fragment,container,false);
        final Button startButton = (Button) rootView.findViewById(R.id.startButton);
        final BaseActivity activity = (BaseActivity) getActivity();

    return rootView;
    }
}
