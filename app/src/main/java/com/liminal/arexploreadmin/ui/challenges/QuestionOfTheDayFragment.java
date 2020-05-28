package com.liminal.arexploreadmin.ui.challenges;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liminal.arexploreadmin.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionOfTheDayFragment extends Fragment {

    public QuestionOfTheDayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question_of_the_day, container, false);
    }
}
