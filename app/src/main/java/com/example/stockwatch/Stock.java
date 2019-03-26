package com.example.stockwatch;

public class Stock {
    private String symbol;
    private String name;
    private double price;
    private double price_change;
    private double change_percent;

    public Stock(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceChange() {
        return price_change;
    }

    public double getChangePercent() {
        return change_percent;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPriceChange(double price_change) {
        this.price_change = price_change;
    }

    public void setChangePercent(double change_percent) {
        this.change_percent = change_percent;
    }
}
