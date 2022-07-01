package com.mailorderpharma.drugservice.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mailorderpharma.drugservice.dao.DrugDetailsRepository;
import com.mailorderpharma.drugservice.dao.DrugLocationRepository;
import com.mailorderpharma.drugservice.entity.DrugDetails;
import com.mailorderpharma.drugservice.entity.DrugLocationDetails;
import com.mailorderpharma.drugservice.entity.Stock;
import com.mailorderpharma.drugservice.entity.ResponseForSuccess;
import com.mailorderpharma.drugservice.exception.DrugNotFoundException;
import com.mailorderpharma.drugservice.exception.InvalidTokenException;
import com.mailorderpharma.drugservice.exception.StockNotFoundException;
import com.mailorderpharma.drugservice.restclients.AuthFeign;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DrugDetailsServiceImpl implements DrugDetailsService {

	@Autowired
	private DrugDetailsRepository drugDetailsRepository;

	@Autowired
	private DrugLocationRepository drugLocationRepository;

	@Autowired
	private AuthFeign authFeign;


	String drugNotFound = "Drug Not Found";

	String invalidToken = "Invalid Token Received";

	String stockNotAvailable = "Stock Unavailable at your location";

	/**
	 * 
	 * @param id
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	@Override
	public DrugDetails getDrugById(String id, String token) throws InvalidTokenException,DrugNotFoundException {
		log.info("Start DrugDetailsServiceImpl: getDrugById");
		DrugDetails drugDetails = null;
		if (authFeign.getValidity(token).getBody().isValid()) {
			
				drugDetails = drugDetailsRepository.findById(id).orElseThrow(() -> new DrugNotFoundException(drugNotFound));
				log.info("DrugDetails: " + drugDetails.getDrugName());
		} else {
			throw new InvalidTokenException(invalidToken);			
		}
		
		log.info("End DrugDetailsServiceImpl: getDrugById");
		return drugDetails;
	}


	/**
	 * 
	 * @param name
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 */
	@Override
	public DrugDetails getDrugByName(String name, String token) throws InvalidTokenException {
		log.info("Start DrugDetailsServiceImpl: getDrugByName");
		if (authFeign.getValidity(token).getBody().isValid()) {
			DrugDetails drugDetails = null;
			try {
				drugDetails = drugDetailsRepository.findBydrugName(name).get();
				log.info("DrugDetails: " + drugDetails.getDrugName());
				return drugDetails;
			} catch (NoSuchElementException e) {
				throw new DrugNotFoundException(drugNotFound);
			}
		} else
			throw new InvalidTokenException(invalidToken);
	
	}

	/**
	 * 
	 * @param id
	 * @param location
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	@Override
	public Stock getDispatchableDrugStock(String id, String location, String token)
			throws InvalidTokenException, StockNotFoundException {
		log.info("Start DrugDetailsServiceImpl: getDispatchableDrugStock");
		if (authFeign.getValidity(token).getBody().isValid()) {
			DrugDetails details = null;
			try {
				details = drugDetailsRepository.findById(id).get();
				log.info("DrugDetails: "+details.getDrugName());
			} catch (Exception e) {

				throw new DrugNotFoundException(drugNotFound);
			}
			Stock stock = null;
			for (DrugLocationDetails dld : details.getDruglocationQuantities()) {
				if (dld.getLocation().equals(location)) {
					stock = new Stock(id, details.getDrugName(), details.getExpiryDate(), dld.getQuantity());
					break;
				}
			}
			if (stock == null)
				throw new StockNotFoundException(stockNotAvailable);
			else
			{
				log.info("End DrugDetailsServiceImpl: getDispatchableDrugStock");
				return stock;
			}
		}
		throw new InvalidTokenException(invalidToken);
	}

	/**
	 * 
	 * @param id
	 * @param location
	 * @param quantity
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	@Override
	public ResponseEntity<ResponseForSuccess> updateQuantity(String drugName, String location, int quantity, String token)
			throws InvalidTokenException, StockNotFoundException {
		log.info("Start DrugDetailsServiceImpl: updateQuantity");
		if (authFeign.getValidity(token).getBody().isValid()) {
			DrugDetails details = new DrugDetails();
			try {
				details = drugDetailsRepository.findBydrugName(drugName).get();
				log.info("DrugDetails: "+details.getDrugName());
			} catch (Exception e) {

				throw new DrugNotFoundException(invalidToken);
			}
			List<DrugLocationDetails> drugLocation = details.getDruglocationQuantities().stream()
					.filter(x -> x.getLocation().equalsIgnoreCase(location)).collect(Collectors.toList());
			log.info("DrugLocationDetails: "+drugLocation);
			
			if (drugLocation.isEmpty()) {
				throw new StockNotFoundException(stockNotAvailable);
			}

			else if (drugLocation.get(0).getQuantity() >= quantity && quantity >0) {

				DrugLocationDetails allDetails = drugLocationRepository.findByserialId(drugLocation.get(0).getSerialId()).get(0);
				int val = allDetails.getQuantity() - quantity;
				allDetails.setQuantity(val);
				drugLocationRepository.save(allDetails);
				log.info("End DrugDetailsServiceImpl: updateQuantity");
				return new ResponseEntity<>(new ResponseForSuccess("Refill Done Successfully"),
						HttpStatus.OK);
			} else
				throw new StockNotFoundException(stockNotAvailable);
		}
		throw new InvalidTokenException(invalidToken);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public List<DrugDetails> getAllDrugs() {
		log.info("Start DrugDetailsServiceImpl:  getAllDrugs");
		return drugDetailsRepository.findAll();
	}

	/**
	 * 
	 * @param name
	 * @param location
	 * @param token
	 * @return
	 * @throws InvalidTokenException
	 * @throws StockNotFoundException
	 */
	/*
	 * @Override public Stock getDispatchableDrugStockByName(String name, String
	 * location, String token) throws InvalidTokenException, StockNotFoundException
	 * { log.info("start--serviceimpl--getDispatchableDrugStockByName"); if
	 * (authFeign.getValidity(token).getBody().isValid()) { DrugDetails details =
	 * null; try { details = drugDetailsRepository.findBydrugName(name).get(); }
	 * catch (Exception e) {
	 * 
	 * throw new DrugNotFoundException(drugNotFound); } Stock stock = null; for
	 * (DrugLocationDetails dld : details.getDruglocationQuantities()) { if
	 * (dld.getLocation().equals(location)) { stock = new Stock(details.getDrugId(),
	 * name, details.getExpiryDate(), dld.getQuantity()); } } if (stock == null)
	 * throw new StockNotFoundException(stockNotAvailable); else {
	 * log.info("end--serviceimpl--getDispatchableDrugStockByName"); return stock; }
	 * } throw new InvalidTokenException(invalidToken);
	 * 
	 * }
	 */
}

