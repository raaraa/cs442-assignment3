package com.example.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StcokHolder>{
    private MainActivity mainActivity;
    private List<Stock> stock_list;

    public StockAdapter(MainActivity mainActivity, List<Stock> stock_list) {
        this.mainActivity = mainActivity;
        this.stock_list = stock_list;
    }

    @Override
    public StcokHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stock_row, parent, false);
        view.setOnLongClickListener(mainActivity);
        view.setOnClickListener(mainActivity);
        return new StcokHolder(view);
    }

    @Override
    public void onBindViewHolder(StcokHolder holder, int position) {
        Stock stock = stock_list.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.name.setText(stock.getName());
        holder.price.setText(String.valueOf(stock.getPrice()));

        if (stock.getPriceChange() < 0) {
            //TODO: Change colors for positive or negative percent change

            holder.pchange.setText("▼ "+String.valueOf(stock.getPriceChange())+" ("+String.valueOf(stock.getChangePercent())+"%)");
            holder.symbol.setTextColor(Color.RED);
            holder.name.setTextColor(Color.RED);
            holder.price.setTextColor(Color.RED);
            holder.pchange.setTextColor(Color.RED);

        }else{
            holder.pchange.setText("▲ "+String.valueOf(stock.getPriceChange())+" ("+String.valueOf(stock.getChangePercent())+"%)");
            holder.symbol.setTextColor(Color.GREEN);
            holder.name.setTextColor(Color.GREEN);
            holder.price.setTextColor(Color.GREEN);
            holder.pchange.setTextColor(Color.GREEN);
        }
    }
    @Override
    public int getItemCount() {
        return stock_list.size();
    }
}
