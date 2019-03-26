package com.example.stockwatch;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsynchSymbols extends AsyncTask<String, Void, String> {
    private MainActivity mainActivity;
    private final String iex = "https://api.iextrading.com/1.0/ref-data/symbols";
    private String ticker;

    public AsynchSymbols(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        JSONArray jsar = new JSONArray();
        ticker = params[0];
        try {
            URL url = new URL(iex);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream input_stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(input_stream)));
            String stocks = reader.readLine();
            return stocks;
            //jsar = new JSONArray(sb.toString());
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        final ArrayList<Stock> parsed_stocks = parseStocks(s);
        final Stock[] stock = new Stock[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

        if (parsed_stocks.size() == 1) {
            stock[0] = parsed_stocks.get(0);
            AsynchPrice price_data = new AsynchPrice(mainActivity);
            price_data.execute(stock[0]);
        }
        else if (parsed_stocks.isEmpty()) {
            builder.setTitle("Symbol Not Found: " + ticker);
            builder.setMessage("Data for stock symbol");
            AlertDialog alert = builder.create();
            alert.show();
            return;
        }
        else {
            CharSequence[] stock_list = new CharSequence[parsed_stocks.size()];

            for (int i =0; i < parsed_stocks.size(); i++) {
                Stock single = parsed_stocks.get(i);
                CharSequence match = single.getSymbol()+" - "+single.getName();
                stock_list[i] = match;
            }
            builder.setTitle("Make a selection");
            builder.setItems(stock_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stock[0] = parsed_stocks.get(which);
                    AsynchPrice price_data = new AsynchPrice(mainActivity);
                    price_data.execute(stock[0]);
                }
            });
            builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private ArrayList<Stock> parseStocks(String s){
        ArrayList<Stock> all_stocks = new ArrayList<>();
        try {
            JSONArray json_arr = new JSONArray(s);
            for (int i =0; i <json_arr.length(); i++){
                JSONObject json_stock = (JSONObject) json_arr.get(i);

                if(json_stock.getString("symbol").toLowerCase().equals(ticker.toLowerCase())) {
                    Stock stock = new Stock(json_stock.getString("symbol"), json_stock.getString("name"));
                    all_stocks.add(stock);
                }
                else if(json_stock.getString("name").toLowerCase().contains(ticker.toLowerCase())){
                    Stock stock = new Stock(json_stock.getString("symbol"), json_stock.getString("name"));
                    all_stocks.add(stock);
                }
            }
            return all_stocks;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
