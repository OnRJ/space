package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.ShipNotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ShipServiceImpl implements ShipService {

    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getShip(Long id) {
    	return shipRepository.findById(id)
    			.orElseThrow(() -> new ShipNotFoundException("Ship is not found"));
    }

    @Override
    public Long checkId(String id) {
        if (id == null || id.equals("0") || id.equals("")) {
            throw new BadRequestException("ID is incorrect");
        }
        try {
            Long shipId = Long.parseLong(id);
            return shipId;
        } catch (NumberFormatException e) {
            throw new BadRequestException("ID is not a number", e);
        }
    }
    
	@Override
	public void deleteShip(Long id) {
		if(shipRepository.existsById(id)) {
			shipRepository.deleteById(id);
		} else {
			throw new ShipNotFoundException("Ship is not found");
		}
	}

	@Override
	public Page<Ship> getShips(Specification<Ship> specification, Pageable pageable) {
		return shipRepository.findAll(specification, pageable);
	}
	
	@Override
	public Ship createShip(Ship ship) {

    	if (ship.getName() == null
				|| ship.getPlanet() == null
				|| ship.getShipType() == null
				|| ship.getProdDate() == null
				|| ship.getSpeed() == null
				|| ship.getCrewSize() == null) {
    		throw new BadRequestException("No value for required fields");
		}

		checkParams(ship);
		
		if (ship.getUsed() == null) {
			ship.setUsed(false);
		}
		
		ship.setRating(calculateRating(ship));
		shipRepository.save(ship);
		
		return ship;
	}
	
	@Override
	public Ship updateShip(Long id, Ship modifiedShip) {
		
		checkParams(modifiedShip);
		
		Ship ship = shipRepository.findById(id)
    			.orElseThrow(() -> new ShipNotFoundException("Ship is not found"));
		
		if (modifiedShip.getName() != null) {
			ship.setName(modifiedShip.getName());
		}
		
		if (modifiedShip.getPlanet() != null) {
			ship.setPlanet(modifiedShip.getPlanet());
		}
		
		if (modifiedShip.getShipType() != null) {
			ship.setShipType(modifiedShip.getShipType());
		}
		
		if (modifiedShip.getProdDate() != null) {
			ship.setProdDate(modifiedShip.getProdDate());
		}
		
		if (modifiedShip.getUsed() != null) {
			ship.setUsed(modifiedShip.getUsed());
		}
		
		if (modifiedShip.getSpeed() != null) {
			ship.setSpeed(modifiedShip.getSpeed());
		}
		
		if (modifiedShip.getCrewSize() != null) {
			ship.setCrewSize(modifiedShip.getCrewSize());
		}
		
		ship.setRating(calculateRating(ship));
		
		return shipRepository.save(ship);
	}
	
	private void checkParams(Ship ship) {
		
		if (ship.getName() != null && (ship.getName().length() < 1 || ship.getName().length() > 50)) {
			throw new BadRequestException("Ship name must be from 1 to 50 characters");
		}
		
		if (ship.getPlanet() != null && ship.getPlanet().length() > 50) {
			throw new BadRequestException("Planet name must be from 1 and 50 characters");
		}
		
		if (ship.getProdDate() != null) {
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(ship.getProdDate());
			int prodDate = cal.get(Calendar.YEAR);
			
			if (prodDate < 2800 || prodDate > 3019) {
				throw new BadRequestException("The year must be in the range from 2800 to 3019");
			}
		}
		
		if (ship.getSpeed() != null && (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99)) {
			throw new BadRequestException("Ship speed must be between 0.01 and 0.99");
		}
		
		if (ship.getCrewSize() != null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999)) {
			throw new BadRequestException("The number of crew members must be in the range from 1 to 9999");
		}
	}
	
	private Double calculateRating(Ship ship) {
		Double coefficient = ship.getUsed() ? 0.5 : 1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(ship.getProdDate());
		int prodDate = cal.get(Calendar.YEAR);
		
		Double raiting = (double) (80 * ship.getSpeed() * coefficient) 
				/  (double) (3019 - prodDate + 1);
		return Math.round(raiting * 100.0) / 100.0;
	}

	@Override
	public Specification<Ship> nameFilter(String name) {
		return (r, cq, cb) -> name == null ? null : cb.like(r.get("name"), "%" + name + "%");
	}
	
	@Override
	public Specification<Ship> planetFilter(String planet) {
		return (r, cq, cb) -> planet == null ? null : cb.like(r.get("planet"), "%" + planet + "%");
	}

	@Override
	public Specification<Ship> shipTypeFilter(ShipType shipType) {
		return (r, cq, cb) -> shipType == null ? null : cb.equal(r.get("shipType"), shipType);
	}
	
	@Override
	public Specification<Ship> shipProdDateFilter(Long after, Long before) {
		
		return (r, cq, cb) -> {
            if (after == null && before == null) {
                return null;
            }
            if (after == null) {
            	Date beforeDate = new Date(before);
    			return cb.lessThanOrEqualTo(r.<Date>get("prodDate"), beforeDate);
            }
            if (before == null) {
            	Date afterDate = new Date(after);
    			return cb.greaterThanOrEqualTo(r.<Date>get("prodDate"), afterDate);
            }
            
            Date afterDate = new Date(after);
			Date beforeDate = new Date(before);
            return cb.between(r.<Date>get("prodDate"), afterDate, beforeDate);
        };
	}
	
	@Override
	public Specification<Ship> usedFilter(Boolean isUsed) {
		return (r, cq, cb) -> {
			if (isUsed == null) {
				return null;
            } else if (isUsed) {
                return cb.isTrue(r.get("isUsed"));
            } else {
                return cb.isFalse(r.get("isUsed"));
            }
        };
	}
	
	@Override
	public Specification<Ship> speedFilter(Double minSpeed, Double maxSpeed) {
		
		return (r, cq, cb) -> {
            if (minSpeed == null && maxSpeed == null) {
                return null;
            }
            if (minSpeed == null) {
                return cb.lessThanOrEqualTo(r.get("speed"), maxSpeed);
            }
            if (maxSpeed == null) {
                return cb.greaterThanOrEqualTo(r.get("speed"), minSpeed);
            }
            return cb.between(r.get("speed"), minSpeed, maxSpeed);
        };
	}
	
	@Override
	public Specification<Ship> crewSizeFilter(Integer minCrewSize, Integer maxCrewSize) {
		
		return (r, cq, cb) -> {
            if (minCrewSize == null && maxCrewSize == null) {
                return null;
            }
            if (minCrewSize == null) {
                return cb.lessThanOrEqualTo(r.get("crewSize"), maxCrewSize);
            }
            if (maxCrewSize == null) {
                return cb.greaterThanOrEqualTo(r.get("crewSize"), minCrewSize);
            }
            return cb.between(r.get("crewSize"), minCrewSize, maxCrewSize);
        };
	}
	
	@Override
	public Specification<Ship> ratingFilter(Double minRating, Double maxRating) {
		
		return (r, cq, cb) -> {
            if (minRating == null && maxRating == null) {
                return null;
            }
            if (minRating == null) {
                return cb.lessThanOrEqualTo(r.get("rating"), maxRating);
            }
            if (maxRating == null) {
                return cb.greaterThanOrEqualTo(r.get("rating"), minRating);
            }
            return cb.between(r.get("rating"), minRating, maxRating);
        };
	}

	@Override
	public Integer getCountShips(Specification<Ship> specification) {
		return shipRepository.findAll(specification).size();
	}
}