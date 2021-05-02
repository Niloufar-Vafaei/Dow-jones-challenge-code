package com.rbc.imp.stockmarkets.util;

import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IFileReader {
    CompletableFuture<List<DowJonesIndex>> readFile(MultipartFile file) throws IOException;
}
