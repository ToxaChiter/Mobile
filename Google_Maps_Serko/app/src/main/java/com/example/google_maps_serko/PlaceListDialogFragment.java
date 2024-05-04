package com.example.google_maps_serko;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.places.Place;

import java.util.List;

public class PlaceListDialogFragment extends DialogFragment {

    private List<CustomPlace> placeList;
    private PlaceAdapter placeAdapter;
    private PlaceAdapter.OnItemClickListener listener;

    public PlaceListDialogFragment(List<CustomPlace> placeList, PlaceAdapter.OnItemClickListener listener) {
        this.placeList = placeList;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        placeAdapter = new PlaceAdapter(placeList, listener);
        recyclerView.setAdapter(placeAdapter);

        return recyclerView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
