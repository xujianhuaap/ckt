package me.ketie.app.android.ui.launch;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

import me.ketie.app.android.R;

/**
 * Created by henjue on 2015/4/10.
 */
public class HomeListFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homelist,null,false);
    }

    public static HomeListFragment newInstance(){
        return new HomeListFragment();
    }

}
