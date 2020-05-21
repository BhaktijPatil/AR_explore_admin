package com.liminal.arexploreadmin.ui.aractivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.liminal.arexploreadmin.R;

import java.util.ArrayList;

public class AddActivityFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ar_activity, container, false);

        Spinner activityCategorySpinner = view.findViewById(R.id.activityCategorySpinner);
        ArrayList<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("scan");
        spinnerArray.add("game");
        spinnerArray.add("portal");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_activity_category_textview, spinnerArray);
//        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        activityCategorySpinner.setAdapter(adapter);
        return view;
    }
}
