package com.rbc.imp.stockmarkets.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rbc.imp.stockmarkets.mappers.Mapper;
import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import com.rbc.imp.stockmarkets.services.IAsyncStockService;
import com.rbc.imp.stockmarkets.util.CsvFileReader;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mvcController;

    @MockBean
    private IAsyncStockService service;

    @MockBean
    private CsvFileReader csvFileReader;

    @Before
    public void setUp() {
    }

    private CompletableFuture<List<DowJonesIndexDTO>> getDowJonesDataSetDTOList() {
        DowJonesIndexDTO data1 = new DowJonesIndexDTO();
        DowJonesIndexDTO data2 = new DowJonesIndexDTO();
        DowJonesIndexDTO data3 = new DowJonesIndexDTO();

        data1.setDate("1/1/2019");
        data1.setQuarter("1");
        data1.setStock("AA");

        data2.setDate("2/1/2019");
        data2.setQuarter("1");
        data2.setStock("AA");

        data3.setDate("3/1/2019");
        data3.setQuarter("1");
        data3.setStock("AA");

        List<DowJonesIndexDTO> dowJonesList = Arrays.asList(data1, data2, data3);

        return CompletableFuture.completedFuture(dowJonesList);
    }

    @Test
    public void givenDowJonesDataSet_whenCallGetByStockTickerAndQuarter_thenReturnJsonArray()
            throws Exception {

        //Given
        final var mockData = getDowJonesDataSetDTOList();
        given(service.getStocksByStockTickerAndQuarter("AA", 1))
                .willReturn(mockData);

        //When
        final var mvcResult = mvcController
                .perform(get("/api/stocks/getByStockTickerAndQuarter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("version", 1)
                        .queryParam("stockTicker", "AA")
                        .queryParam("quarter", "1"))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        //Then
        mvcController.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[0].date").value(mockData.get().get(0).getDate()));

        verify(service, VerificationModeFactory.times(1))
                .getStocksByStockTickerAndQuarter("AA", 1);

        reset(service);
    }

    @Test
    public void givenDowJonesRecord_whenCalladdStockTicker_thenReturnJsonArray()
            throws Exception {

        //Given
        final var mockData = getDowJonesDataSetDTO();
        given(service.addStockTicker(Mockito.any()))
                .willReturn(CompletableFuture.completedFuture(mockData));

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(mockData);

        //When
        final MvcResult mvcResult = mvcController.perform(post("/api/stocks/addStockTicker")
                .contentType(MediaType.APPLICATION_JSON)
                .header("version", 1)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        //Then
        mvcController.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(mockData.getDate()));

        verify(service, VerificationModeFactory.times(1))
                .addStockTicker(Mockito.any());

        reset(service);
    }

    private DowJonesIndexDTO getDowJonesDataSetDTO() {
        var dowJonesDataSetDTO = new DowJonesIndexDTO();
        dowJonesDataSetDTO.setDate("1/1/2019");
        dowJonesDataSetDTO.setQuarter("1");
        dowJonesDataSetDTO.setStock("AA");

        return dowJonesDataSetDTO;
    }

    @Test
    public void givenDowJonesFile_whenCallUploadStock_thenReturn() throws Exception {
        //Given
        final var mockDowJonesIndexList = getMockDowJonesIndexList();
        final var mockFile = getMockFile();
        given(csvFileReader.readFile(mockFile))
                .willReturn(CompletableFuture.completedFuture(mockDowJonesIndexList));

        service.addStockTickers(mockDowJonesIndexList);

        //When
        final var mvcResult = mvcController
                .perform(MockMvcRequestBuilders.multipart("/api/stocks/uploadStock")
                        .file(mockFile)
                        .header("version", 1))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();

        //Then
        mvcController.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(status().is(200));
    }

    private MockMultipartFile getMockFile() {
        return new MockMultipartFile("file", "filename.txt", "text/plain", "some xml".getBytes());
    }
    private List<DowJonesIndex> getMockDowJonesIndexList() {
        var dowJonesIndex = new DowJonesIndex();
        dowJonesIndex.setDate("1/1/2019");
        dowJonesIndex.setQuarter(1);
        dowJonesIndex.setStock("AA");

        return new ArrayList<>(List.of(dowJonesIndex));

    }
    @Test
    public void givenEmptyMockFile_WhenCallFileReader_ReturnIOException() throws Exception {

        final var mockFile = getMockFile();
        //given
        given(csvFileReader.readFile(mockFile))
                .willThrow(new IOException());

        //when
        final var mvcResult = mvcController
                .perform(MockMvcRequestBuilders.multipart("/api/stocks/uploadStock")
                        .file(mockFile)
                        .header("version", 1))
                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                .andReturn();
        //Then
        mvcController.perform(asyncDispatch(mvcResult))
                .andExpect(status().isExpectationFailed());
    }
}
