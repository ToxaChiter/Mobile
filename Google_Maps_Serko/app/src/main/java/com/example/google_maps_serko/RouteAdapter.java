package com.example.google_maps_serko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {

    private List<Route> routeList;
    private OnItemClickListener listener;

    public RouteAdapter(List<Route> routeList, OnItemClickListener listener) {
        this.routeList = routeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routeList.get(position);
        holder.bind(route, listener);
    }

    @Override
    public int getItemCount() {
        return routeList.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView routeNameTextView;

        RouteViewHolder(View itemView) {
            super(itemView);
            routeNameTextView = itemView.findViewById(R.id.textViewRouteName);
        }

        void bind(final Route route, final OnItemClickListener listener) {
            routeNameTextView.setText(route.getRouteName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(route);
                }
            });
        }
    }

    interface OnItemClickListener {
        void onItemClick(Route route);
    }
}
