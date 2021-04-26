package com.rbc.imp.stockmarkets.util;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvFileReader {

    public List<DowJonesIndex> readFile(MultipartFile file) throws IOException {
        ModelMapper modelMapper = new ModelMapper();
        CsvMapper csvMapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        ObjectReader objectReader = csvMapper.readerFor(DowJonesIndexDTO.class).with(schema);
        List<DowJonesIndex> dataSet = new ArrayList<>();

        MappingIterator<DowJonesIndexDTO> mi = objectReader.readValues(file.getInputStream());
        while (mi.hasNext()) {
            DowJonesIndex current = modelMapper.map(mi.next(), DowJonesIndex.class);
            dataSet.add(current);
        }
        return dataSet;
    }
}
