package net.mobindustry.mobigram.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.mobindustry.mobigram.R;
import net.mobindustry.mobigram.core.ApiHelper;
import net.mobindustry.mobigram.model.foursquare.FoursquareVenue;
import net.mobindustry.mobigram.model.holder.FoursquareHolder;
import net.mobindustry.mobigram.model.holder.MessagesFragmentHolder;
import net.mobindustry.mobigram.ui.adapters.FoursquareAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FoursquareListFragment extends Fragment implements Serializable {

    private FragmentTransaction ft;
    private FoursquareAdapter foursquareAdapter;
    private List<FoursquareVenue> foursquareVenueList = new ArrayList<>();
    private double lng;
    private double lat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        foursquareAdapter = new FoursquareAdapter(getActivity());
        foursquareAdapter.clear();
        FoursquareHolder foursquareHolder = FoursquareHolder.getInstance();
        foursquareVenueList = foursquareHolder.getFoursquareVenueList();
        foursquareAdapter.addAll(foursquareVenueList);
        return inflater.inflate(R.layout.foursquare_list_fragment_layout, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView foursquareList = (ListView) getActivity().findViewById(R.id.foursquare_list);
        foursquareList.setAdapter(foursquareAdapter);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_foursquare_list);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.text_nearest_checkpoints);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationFragment locationFragment;
                locationFragment = new LocationFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.transparent_content, locationFragment);
                ft.commit();
            }
        });

        foursquareList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lat = foursquareVenueList.get(position).getFoursquareLocation().getLatitude();
                lng = foursquareVenueList.get(position).getFoursquareLocation().getLongitude();
                ApiHelper.sendGeoPointMessage(MessagesFragmentHolder.getChat().id, lng, lat);
                getActivity().finish();
            }
        });
    }
}
