package com.rbc.imp.stockmarkets.controllers;

import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import com.rbc.imp.stockmarkets.services.AsyncStockService;
import com.rbc.imp.stockmarkets.util.CsvFileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stocks")

public class StockController {

    private final AsyncStockService asyncStockService;
    private final CsvFileReader csvFileReader;

    @Autowired
    public StockController(AsyncStockService asyncStockService,CsvFileReader csvFileReader) {
        this.asyncStockService = asyncStockService;
        this.csvFileReader =csvFileReader;
    }

    @GetMapping(value = "getByStockTickerAndQuarter", headers = "version=1")
    public CompletableFuture<ResponseEntity<List<DowJonesIndexDTO>>> getByStockTickerAndQuarter(@Valid @RequestParam("stockTicker") String stockTicker,
                                                                                                @RequestParam("quarter") Integer quarter) {
        try {
            final List<DowJonesIndexDTO> results = asyncStockService.getStocksByStockTickerAndQuarter(stockTicker, quarter).get();
            return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().body(results));
        } catch (Exception e) {

            return CompletableFuture.supplyAsync(() ->
                    ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ArrayList<>()));
        }
    }

    @PostMapping(value = "/addStockTicker", headers = "version=1")
    public CompletableFuture<ResponseEntity<DowJonesIndexDTO>> addStockTicker(@Valid @RequestBody DowJonesIndexDTO dowJonesIndexDTO) {
        try {
            final DowJonesIndexDTO result = asyncStockService.addStockTicker(dowJonesIndexDTO).get();
            return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().body(result));
        } catch (Exception e) {
            return CompletableFuture.supplyAsync(() ->
                    ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new DowJonesIndexDTO()));
        }
    }

    @PostMapping(value = "/uploadStock", headers = "version=1")
    public CompletableFuture<ResponseEntity<String>> uploadStock(@Valid @RequestParam("file") MultipartFile file) {
        try {
            var data = csvFileReader.readFile(file);
            asyncStockService.addStockTickers(data);
            return CompletableFuture.supplyAsync(() -> ResponseEntity.ok().body("The file has been uploaded successfully: " + file.getOriginalFilename()));
        } catch (Exception e) {
            String message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return CompletableFuture.supplyAsync(() ->
                    ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message));
        }
    }

}
