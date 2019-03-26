package com.example.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private SwipeRefreshLayout swiper;
    private RecyclerView recycler_view;
    private Database db_handler;
    private StockAdapter stock_adapter;
    private List<Stock> stock_list = new ArrayList<>();
    private ConnectivityManager connectivityManager;
    private static MainActivity mainActivity;

    private static String marketwatch_url="http://www.marketwatch.com/investing/stock/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mainActivity = this;

        recycler_view = findViewById(R.id.recycler);
        stock_adapter = new StockAdapter(this, stock_list);
        recycler_view.setAdapter(stock_adapter);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        stock_adapter.notifyDataSetChanged();

        swiper =  findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!chekcNetwork()) {
                    swiper.setRefreshing(false);
                    return;
                }

                for (int i=0; i< stock_list.size(); i++){
                    Stock stock = stock_list.get(i);
                    AsynchUpdate update = new AsynchUpdate(mainActivity);
                    update.execute(stock);
                }
                swiper.setRefreshing(false);
            }
        });

        db_handler = new Database(this);
        if (!chekcNetwork()) {
            return;
        }

        ArrayList<String[]> stocks = db_handler.loadStocks();
        for(int i=0; i < stocks.size(); i++){
            String[] db_stock = stocks.get(i);
            Stock s = new Stock(db_stock[0], db_stock[1]);
            AsynchPrice price_data = new AsynchPrice(this);
            price_data.execute(s);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addButton();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        final int position = recycler_view.getChildAdapterPosition(v);
        Stock stock = stock_list.get(position);
        String url = marketwatch_url+stock.getSymbol();
        Intent intent = new Intent((Intent.ACTION_VIEW));
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        final int pos = recycler_view.getChildAdapterPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol " + stock_list.get(pos).getSymbol() + " ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db_handler.deleteStock(stock_list.get(pos).getSymbol());
                stock_list.remove(pos);
                stock_adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return false;
    }

    public void addButton(){
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.add_screen, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Stock Selection");
        builder.setTitle("Please enter a Stock Symbol:");
        builder.setView(view);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText inputTextView = view.findViewById(R.id.add_field);
                String input = inputTextView.getText().toString();

                if (chekcNetwork()){
                    AsynchSymbols symbols = new AsynchSymbols(mainActivity);
                    symbols.execute(input);
                }
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void addData(Stock stock){
        for(int i=0; i < stock_list.size(); i++){
            Stock s = stock_list.get(i);
            if(s.getSymbol().equals(stock.getSymbol())) return;
        }
        stock_list.add(stock);
        Collections.sort(stock_list, new Comparator<Stock>() {
            @Override
            public int compare(Stock o1, Stock o2) {
                return o1.getSymbol().compareTo(o2.getSymbol());
            }
        });
        db_handler.addStock(stock);
        stock_adapter.notifyDataSetChanged();
    }

    public void dataUpdate(Stock stock) {
        int i;
        for(i=0; i < stock_list.size(); i++){
            Stock s = stock_list.get(i);
            if (s.getSymbol().equals(stock.getSymbol())) break;
        }
        stock_list.remove(i);
        stock_list.add(i,stock);
        stock_adapter.notifyDataSetChanged();
    }

    //TODO: create the chenck network func.
    public Boolean chekcNetwork(){
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo !=null && netInfo.isConnectedOrConnecting())return true;
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks caannot be added without a network connection.");
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
    }
}
