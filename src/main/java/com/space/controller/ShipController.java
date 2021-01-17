package com.space.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;

import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private ShipService service;

    @Autowired
    public void setService(ShipService service) {
        this.service = service;
    }
    
    @GetMapping("/ships")
    @ResponseStatus(HttpStatus.OK)
    public List<Ship> getShips(
    		@RequestParam(required = false) String name,
    		@RequestParam(required = false) String planet,
    		@RequestParam(required = false) ShipType shipType,
    		@RequestParam(required = false) Long after,
    		@RequestParam(required = false) Long before,
    		@RequestParam(required = false) Boolean isUsed,
    		@RequestParam(required = false) Double minSpeed,
    		@RequestParam(required = false) Double maxSpeed,
    		@RequestParam(required = false) Integer minCrewSize,
    		@RequestParam(required = false) Integer maxCrewSize,
    		@RequestParam(required = false) Double minRating,
    		@RequestParam(required = false) Double maxRating,
    		@RequestParam(required = false, defaultValue = "ID") ShipOrder order,
    		@RequestParam(required = false, defaultValue = "0") Integer pageNumber,
    		@RequestParam(required = false, defaultValue = "3") Integer pageSize) {
    	
    	  
    	Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        
    	return service.getShips(
        		Specification.where(
        				service.nameFilter(name)
        				.and(service.planetFilter(planet))
        				.and(service.shipTypeFilter(shipType))
        				.and(service.shipProdDateFilter(after, before))
        				.and(service.usedFilter(isUsed))
        				.and(service.speedFilter(minSpeed, maxSpeed))
        				.and(service.crewSizeFilter(minCrewSize, maxCrewSize))
        				.and(service.ratingFilter(minRating, maxRating))), pageable).getContent();
    }
    
    @GetMapping("/ships/count")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCountShip(
    		@RequestParam(required = false) String name,
    		@RequestParam(required = false) String planet,
    		@RequestParam(required = false) ShipType shipType,
    		@RequestParam(required = false) Long after,
    		@RequestParam(required = false) Long before,
    		@RequestParam(required = false) Boolean isUsed,
    		@RequestParam(required = false) Double minSpeed,
    		@RequestParam(required = false) Double maxSpeed,
    		@RequestParam(required = false) Integer minCrewSize,
    		@RequestParam(required = false) Integer maxCrewSize,
    		@RequestParam(required = false) Double minRating,
    		@RequestParam(required = false) Double maxRating) {
    	
        return service.getCountShips(
        		Specification.where(
        				service.nameFilter(name)
        				.and(service.planetFilter(planet))
        				.and(service.shipTypeFilter(shipType))
        				.and(service.shipProdDateFilter(after, before))
        				.and(service.usedFilter(isUsed))
        				.and(service.speedFilter(minSpeed, maxSpeed))
        				.and(service.crewSizeFilter(minCrewSize, maxCrewSize))
        				.and(service.ratingFilter(minRating, maxRating))));
    }

    @PostMapping("/ships")
    @ResponseStatus(HttpStatus.OK)
    public Ship createShip(@RequestBody Ship ship) {
        service.createShip(ship);
        return ship;
        }

    @GetMapping("/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Ship getShip(@PathVariable("id") String id) {
        Long shipId = service.checkId(id);
        return service.getShip(shipId);
    }

    @PostMapping("/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Ship updateShip(@PathVariable("id") String id, @RequestBody Ship ship) {
    	Long shipId = service.checkId(id);
        return service.updateShip(shipId, ship);
    }

    @DeleteMapping("/ships/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteShip(@PathVariable("id") String id) {
    	 Long shipId = service.checkId(id);
         service.deleteShip(shipId);
    }
}