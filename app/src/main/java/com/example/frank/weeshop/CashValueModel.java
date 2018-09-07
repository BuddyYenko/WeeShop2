package com.example.frank.weeshop;

public class CashValueModel {
    private String cashValue, symbol;

    public CashValueModel(){}
    public CashValueModel(String cashValue, String symbol)
    {
        this.cashValue = cashValue;
        this.symbol = symbol;

    }

    public void setCashValue(String cashValue)
    {
        cashValue = cashValue;
    }

    public String getCashValue()
    {
        return cashValue;
    }

    public void setSymbol(String symbol)
    {
        symbol = symbol;
    }

    public String getSymbol()
    {
        return symbol;
    }

}
