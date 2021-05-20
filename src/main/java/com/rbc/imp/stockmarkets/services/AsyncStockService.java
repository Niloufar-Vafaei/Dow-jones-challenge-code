package com.rbc.imp.stockmarkets.services;

import com.rbc.imp.stockmarkets.mappers.IMapper;
import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import com.rbc.imp.stockmarkets.repositories.IDowJonesIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
public class AsyncStockService implements IAsyncStockService {

    private final IDowJonesIndexRepository IDowJonesIndexRepository;
    private final IMapper modelMapper;

    @Autowired
    public AsyncStockService(IDowJonesIndexRepository IDowJonesIndexRepository, IMapper modelMapper) {
        this.IDowJonesIndexRepository = IDowJonesIndexRepository;
        this.modelMapper = modelMapper;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<DowJonesIndexDTO>> getStocksByStockTickerAndQuarter(String stockTicker, Integer quarter) {
        return CompletableFuture.completedFuture(IDowJonesIndexRepository.findByStockAndQuarterOrderById(stockTicker, quarter))
                .thenApply(modelMapper::map);
    }

    @Async("asyncExecutor")
    public CompletableFuture<DowJonesIndexDTO> addStockTicker(DowJonesIndexDTO dowJonesIndexDTO) {
        return CompletableFuture.completedFuture(IDowJonesIndexRepository
                .save(modelMapper.map(dowJonesIndexDTO)))
                .thenApply(modelMapper::map);
    }

    @Async("asyncExecutor")
    public void addStockTickers(List<DowJonesIndex> dowJonesIndexes) {
        IDowJonesIndexRepository.saveAll(dowJonesIndexes);
    }
}
