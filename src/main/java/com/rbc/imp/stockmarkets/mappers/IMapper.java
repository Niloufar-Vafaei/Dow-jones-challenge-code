package com.rbc.imp.stockmarkets.mappers;

import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;

import java.util.List;

public interface IMapper {
     List<DowJonesIndexDTO> map(List<DowJonesIndex> dowJonesIndexList) ;

     DowJonesIndex map(DowJonesIndexDTO dowJonesIndexDTO);

     DowJonesIndexDTO map(DowJonesIndex dowJonesIndex) ;
}
