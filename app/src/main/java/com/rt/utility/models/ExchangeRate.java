package com.rt.utility.models;

import com.rt.utility.helpers.JavaUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.Map;

public class ExchangeRate {
    @JsonProperty("success")
    public Boolean success;

    @JsonProperty("timestamp")
    public long timestamp;

    @JsonProperty("base")
    public String base;

    @JsonProperty("date")
    public Date date;

    @JsonProperty("rates")
    public Map<String, Double> rates;

    public Double getRateBySymbol(String from, String to){
        if(JavaUtils.CheckIfEmptyString(from) || JavaUtils.CheckIfEmptyString(to)){
            return 0.0;
        }

        double fromRate;
        double toRate;

        if(rates.containsKey(from) && rates.containsKey(to)){
            fromRate = rates.get(from);
            toRate = rates.get(to);

            return toRate/fromRate;
        }

        return 0.0;
    }
}
