package com.rbc.imp.stockmarkets.mappers;

import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import com.rbc.imp.stockmarkets.models.DowJonesIndexDTO;
import org.modelmapper.TypeToken;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class Mapper implements IMapper{
    ModelMapper modelMapper = new ModelMapper();

    /**
     * Description
     * @param dowJonesIndexList is a list of {@link DowJonesIndex}
     * @return  a list of {@link DowJonesIndexDTO}
     *
     */

    public List<DowJonesIndexDTO> map(List<DowJonesIndex> dowJonesIndexList) {
        Type dowJonesIndexDTOListType = new TypeToken<List<DowJonesIndexDTO>>() {
        }.getType();
        return modelMapper.map(dowJonesIndexList, dowJonesIndexDTOListType);
    }

    public DowJonesIndex map(DowJonesIndexDTO dowJonesIndexDTO) {
        return modelMapper.map(dowJonesIndexDTO, DowJonesIndex.class);
    }

    public DowJonesIndexDTO map(DowJonesIndex dowJonesIndex) {
        return modelMapper.map(dowJonesIndex, DowJonesIndexDTO.class);

    }
}



