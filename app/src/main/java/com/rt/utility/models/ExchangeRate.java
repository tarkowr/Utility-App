package com.rt.utility.models;

import com.rt.utility.helpers.JavaUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

public class ExchangeRate {
    @JsonProperty("base")
    public String base;

    @JsonProperty("date")
    public Date date;

    @JsonProperty("rates")
    public Map<String, Double> rates;

    public Double getRateBySymbol(String symbol){
        if(JavaUtils.CheckIfEmptyString(symbol)){
            return 0.0;
        }

        if(rates.containsKey(symbol)){
            return rates.get(symbol);
        }

        return 0.0;
    }
}
