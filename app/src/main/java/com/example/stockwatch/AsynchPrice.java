package com.example.stockwatch;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AsynchPrice extends AsyncTask<Stock, Void, String> {
    //private String test = "https://api.iextrading.com/1.0/stock/TSLA/quote?displayPercent=true";
    private MainActivity mainActivity;
    private Stock stock;

    public AsynchPrice(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Stock... params) {
        stock = params[0];
        try {
            String iex = "https://api.iextrading.com/1.0/stock/"+stock.getSymbol()+"/quote?displayPercent=true";
            URL url = new URL(iex);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream input_stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(input_stream)));
            String data = reader.readLine();
            return data;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        parseJson(s);
        if (stock.getPrice() == 0){
            return;
        }
        else mainActivity.addData(stock);
    }

    public void parseJson(String s){
        try {
            JSONObject json_stock = new JSONObject(s);

            stock.setChangePercent(json_stock.getDouble("changePercent"));
            stock.setPriceChange(json_stock.getDouble("change"));
            stock.setPrice(json_stock.getDouble("latestPrice"));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
