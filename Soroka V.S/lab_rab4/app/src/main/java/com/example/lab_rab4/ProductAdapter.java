package com.example.lab_rab4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private LayoutInflater inflater;
    private int resource;
    private TextView selectedCountTextView; // добавлено объявление переменной

    public void setProductList(ArrayList<Product> productList) {
        clear();
        addAll(productList);
    }
    
    public ProductAdapter(Context context, int resource, TextView selectedCountTextView) {
        super(context, resource);
        this.inflater = LayoutInflater.from(context);
        this.resource = resource;
        this.selectedCountTextView = selectedCountTextView; // сохранение переменной
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        final ViewHolder viewHolder;

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = view.findViewById(R.id.product_name);
            viewHolder.priceTextView = view.findViewById(R.id.product_price);
            viewHolder.weightTextView = view.findViewById(R.id.product_weight);
            viewHolder.checkbox = view.findViewById(R.id.product_checkbox);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final Product product = getItem(position);
        viewHolder.nameTextView.setText(product.getName());
        viewHolder.priceTextView.setText(product.getPrice());
        viewHolder.weightTextView.setText(product.getWeight());
        viewHolder.checkbox.setOnCheckedChangeListener(null);
        viewHolder.checkbox.setChecked(product.isSelected());

        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                product.setSelected(isChecked);

                int selectedCount = 0;
                for (int i = 0; i < getCount(); i++) {
                    if (getItem(i).isSelected()) {
                        selectedCount++;
                    }
                }
                selectedCountTextView.setText("Выбрано: " + selectedCount);
            }
        });
        return view;
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView priceTextView;
        TextView weightTextView;
        CheckBox checkbox;
    }
}
