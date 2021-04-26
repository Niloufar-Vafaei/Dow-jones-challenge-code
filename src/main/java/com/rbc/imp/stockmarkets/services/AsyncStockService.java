package com.rbc.imp.stockmarkets.services;

import com.rbc.imp.stockmarkets.exceptions.ServiceException;
import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import com.rbc.imp.stockmarkets.repositories.DowJonesIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncStockService {

    private final DowJonesIndexRepository dowJonesIndexRepository;
    ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AsyncStockService(DowJonesIndexRepository dowJonesIndexRepository) {
        this.dowJonesIndexRepository = dowJonesIndexRepository;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<DowJonesIndexDTO>> getStocksByStockTickerAndQuarter(String stockTicker, Integer quarter) {
        try {
            List<DowJonesIndex> dowJonesIndexList = dowJonesIndexRepository.findByStockAndQuarterOrderById(stockTicker, quarter);
            Type dowJonesIndexDTOListType = new TypeToken<List<DowJonesIndexDTO>>() {
            }.getType();
            List<DowJonesIndexDTO> results = modelMapper.map(dowJonesIndexList, dowJonesIndexDTOListType);
            return CompletableFuture.completedFuture(results);
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Async("asyncExecutor")
    public CompletableFuture<DowJonesIndexDTO> addStockTicker(DowJonesIndexDTO dowJonesIndexDTO) {
        try {
            DowJonesIndex dowJonesIndex = modelMapper.map(dowJonesIndexDTO, DowJonesIndex.class);
            DowJonesIndex response = dowJonesIndexRepository.save(dowJonesIndex);
            return CompletableFuture.completedFuture(modelMapper.map(response, DowJonesIndexDTO.class));
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    @Async("asyncExecutor")
    public void addStockTickers(List<DowJonesIndex> dowJonesIndexes) {
        try {
            dowJonesIndexRepository.saveAll(dowJonesIndexes);
        } catch (Exception ex) {
            throw new ServiceException(ex.getMessage());
        }
    }
}
