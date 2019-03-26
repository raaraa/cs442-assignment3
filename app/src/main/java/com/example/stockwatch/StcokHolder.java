package com.example.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class StcokHolder extends RecyclerView.ViewHolder {
    public TextView name;
    public TextView symbol;
    public TextView price;
    public TextView pchange;

    public StcokHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        symbol =  itemView.findViewById(R.id.symbol);
        pchange = itemView.findViewById(R.id.pchange);
        price = itemView.findViewById(R.id.price);
    }
}
