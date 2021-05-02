package com.rbc.imp.stockmarkets.controllers;

import com.rbc.imp.stockmarkets.exceptions.GlobalException;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import com.rbc.imp.stockmarkets.services.IAsyncStockService;
import com.rbc.imp.stockmarkets.util.CsvFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stocks")

public class StockController {

    private final IAsyncStockService asyncStockService;
    private final CsvFileReader csvFileReader;

    @Autowired
    public StockController(IAsyncStockService asyncStockService, CsvFileReader csvFileReader) {
        this.asyncStockService = asyncStockService;
        this.csvFileReader = csvFileReader;
    }

    //Its depend of consumer to be much familiar with version in side url or header
    //ex: put version in url help developer to figure out in first moment which version of service has bug
    //put version in header help if your services does not have version much more easiest way
    // is put it in side header.
    //if i want to pass data through url would be look like /getByStockTickerAndQuarter/{stockTicker}/{quarter}
    //and ues @PathVariable for reading data
    // i think use get and block the result because we want to be sure get all of the result and after
    // that pass them to response body
    //I use supplyAsync because i want to return some result from my background task
    //we usually have many ways to achieve the same goal in spring boot one of them is
    //defining response class or using ResponseEntity
    //Pagination handle with Pageable and Page spring framework
    @GetMapping(value = "getByStockTickerAndQuarter", headers = "version=1")
    public CompletableFuture<ResponseEntity<List<DowJonesIndexDTO>>>
    getByStockTickerAndQuarter(@Valid @RequestParam("stockTicker") String stockTicker, @RequestParam("quarter") Integer quarter) {


        return asyncStockService.getStocksByStockTickerAndQuarter(stockTicker, quarter)
                .thenApply(dowJonesIndexDTOList -> {
                    if (dowJonesIndexDTOList.isEmpty())
                        return ResponseEntity.notFound().build();
                    else
                        return ResponseEntity.ok().body(dowJonesIndexDTOList);
                });
    }

    @PostMapping(value = "/addStockTicker", headers = "version=1")
    public CompletableFuture<ResponseEntity<DowJonesIndexDTO>> addStockTicker(@Valid @RequestBody DowJonesIndexDTO dowJonesIndexDTO) {

        return asyncStockService.addStockTicker(dowJonesIndexDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(throwable -> {
                    throw new GlobalException(throwable);
                });
    }

    @PostMapping(value = "/uploadStock", headers = "version=1")
    public CompletableFuture<ResponseEntity<String>> uploadStock(@Valid @RequestParam("file") MultipartFile file) throws IOException {
        return csvFileReader.readFile(file)
                .thenAccept(asyncStockService::addStockTickers)
                .thenApply(e -> ResponseEntity.ok("The file has been uploaded successfully: " + file.getOriginalFilename()))
                .exceptionally(throwable -> ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body("Could not upload the file: " + file.getOriginalFilename() + "!"));

    }

}
