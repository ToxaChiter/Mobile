package com.example.google_maps_serko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.location.places.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private List<CustomPlace> placeList;
    private OnItemClickListener listener;

    public PlaceAdapter(List<CustomPlace> placeList, OnItemClickListener listener) {
        this.placeList = placeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        CustomPlace place = placeList.get(position);
        holder.bind(place, listener);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView placeNameTextView;

        PlaceViewHolder(View itemView) {
            super(itemView);
            placeNameTextView = itemView.findViewById(R.id.textViewPlaceName);
        }

        void bind(final CustomPlace place, final OnItemClickListener listener) {
            placeNameTextView.setText(place.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(place);
                }
            });
        }
    }

    interface OnItemClickListener {
        void onItemClick(CustomPlace place);
    }
}
