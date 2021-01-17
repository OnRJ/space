package com.space.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.space.model.Ship;
import com.space.model.ShipType;

public interface ShipService {
    Ship getShip(Long id);
    Long checkId(String id);
    Page<Ship> getShips(Specification<Ship> specification, Pageable pageable);
    Ship createShip(Ship ship);
    Ship updateShip(Long id, Ship ship);
    void deleteShip(Long id);
    Specification<Ship> nameFilter(String name);
    Specification<Ship> planetFilter(String planet);
    Specification<Ship> shipTypeFilter(ShipType shipType);
    Specification<Ship> shipProdDateFilter(Long after, Long before);
    Specification<Ship> usedFilter(Boolean isUsed);
    Specification<Ship> speedFilter(Double minSpeed, Double maxSpeed);
    Specification<Ship> crewSizeFilter(Integer minCrewSize, Integer maxCrewSize);
    Specification<Ship> ratingFilter(Double minRating, Double maxRating);
	Integer getCountShips(Specification<Ship> specification);
}