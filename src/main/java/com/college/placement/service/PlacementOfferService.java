package com.college.placement.service;

import com.college.placement.dto.OfferCreateDto;
import com.college.placement.entity.PlacementOffer;
import com.college.placement.enums.OfferStatus;

import java.util.List;

public interface PlacementOfferService {
    PlacementOffer createOffer(OfferCreateDto dto, String officerUsername);
    PlacementOffer updateOfferStatus(Long offerId, OfferStatus status, String studentUsername);
    List<PlacementOffer> getOffersForStudent(String studentUsername);
    List<PlacementOffer> getAllOffers();
    PlacementOffer getOfferById(Long id);
}
