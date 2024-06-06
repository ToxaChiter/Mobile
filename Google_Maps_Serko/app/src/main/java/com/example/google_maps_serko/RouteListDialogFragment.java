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

import java.util.List;

public class RouteListDialogFragment extends DialogFragment {

    private List<Route> routes;
    private RouteAdapter routeAdapter;
    private RouteAdapter.OnItemClickListener listener;

    public RouteListDialogFragment(List<Route> routes, RouteAdapter.OnItemClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        routeAdapter = new RouteAdapter(routes, listener);
        recyclerView.setAdapter(routeAdapter);

        return recyclerView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
}
