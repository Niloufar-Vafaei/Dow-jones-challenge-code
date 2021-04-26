package com.rbc.imp.stockmarkets.repositories;

import com.rbc.imp.stockmarkets.models.DowJonesIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DowJonesIndexRepository extends JpaRepository<DowJonesIndex, Long> {

    List<DowJonesIndex> findByStockAndQuarterOrderById(String stock, Integer quarter);
}