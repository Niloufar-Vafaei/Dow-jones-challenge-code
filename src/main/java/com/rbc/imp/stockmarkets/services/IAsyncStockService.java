package com.rbc.imp.stockmarkets.services;

import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IAsyncStockService {

    CompletableFuture<List<DowJonesIndexDTO>> getStocksByStockTickerAndQuarter(String stockTicker, Integer quarter);

    CompletableFuture<DowJonesIndexDTO> addStockTicker(DowJonesIndexDTO dowJonesIndexDTO);

    void addStockTickers(List<DowJonesIndex> dowJonesIndexes);

}
