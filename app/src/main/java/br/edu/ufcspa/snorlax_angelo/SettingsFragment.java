package br.edu.ufcspa.snorlax_angelo;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import ufcspa.edu.br.snorlax_angelo.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

View myView;
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView= inflater.inflate(R.layout.fragment_settings, container, false);
        return myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DataBaseAdapter data = DataBaseAdapter.getInstance(getActivity());

        TextView txt = (TextView) myView.findViewById(R.id.txtSettings);
        txt.setText(""+data.getUserId());
    }
}
