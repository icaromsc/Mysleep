package br.edu.ufcspa.snorlax_angelo;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ufcspa.edu.br.sono_angelo_v2.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReportFragment extends Fragment {


    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

}
