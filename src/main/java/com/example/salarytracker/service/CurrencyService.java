package com.example.salarytracker.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyService {

    private final Map<String, Double> rateToUSD = new ConcurrentHashMap<>();

    public CurrencyService() {
        rateToUSD.put("USD", 1.0);
        rateToUSD.put("INR", 0.011);
        rateToUSD.put("AED", 0.27);
        rateToUSD.put("EUR", 1.17);
    }
    			
    public double convert(double amount, String fromCurrency, String toCurrency) {
        if (fromCurrency == null || toCurrency == null) {
        	return amount;
        }
        String from = fromCurrency.toUpperCase();				
        String to = toCurrency.toUpperCase();					
        double fromRate = rateToUSD.getOrDefault(from, 1.0);	
        double toRate = rateToUSD.getOrDefault(to, 1.0);		
        double amountInUsd = amount * fromRate;					
        return new BigDecimal(amountInUsd / toRate).setScale(2, RoundingMode.HALF_UP).doubleValue();			
    }
}
