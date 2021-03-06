package org.benetech.servicenet.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.benetech.servicenet.domain.Silo;
import org.benetech.servicenet.service.dto.ActivityDTO;
import org.benetech.servicenet.service.dto.ActivityFilterDTO;
import org.benetech.servicenet.service.dto.ActivityRecordDTO;
import org.benetech.servicenet.service.dto.OrganizationWithLocationsOptionDTO;
import org.benetech.servicenet.service.dto.provider.ProviderRecordDTO;
import org.benetech.servicenet.service.dto.provider.ProviderRecordForMapDTO;
import org.benetech.servicenet.service.dto.Suggestions;
import org.benetech.servicenet.service.dto.provider.DeactivatedOrganizationDTO;
import org.benetech.servicenet.service.dto.provider.ProviderFilterDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Activity.
 */
public interface ActivityService {

    Page<ActivityDTO> getAllOrganizationActivities(Pageable pageable, UUID systemAccountId,
    String search, ActivityFilterDTO activityFilterDTO);

    Optional<ActivityRecordDTO> getOneByOrganizationId(UUID orgId);

    List<ActivityRecordDTO> getPartnerActivitiesByOrganizationId(UUID orgId);

    Page<ProviderRecordDTO> getPartnerActivitiesForCurrentUser(Pageable pageable);

    List<OrganizationWithLocationsOptionDTO> getOrganizationOptionsForCurrentUser();

    Suggestions getNameSuggestions(ActivityFilterDTO activityFilterDTO, UUID systemAccountId, String search);

    Page<ProviderRecordDTO> getAllPartnerActivities(ProviderFilterDTO providerFilterDTO, String search, Pageable pageable);

    Page<ProviderRecordForMapDTO> getAllPartnerActivitiesForMap(
        Pageable pageable, ProviderFilterDTO providerFilterDTO,
        String search,
        List<Double> boundaries);

    Page<ProviderRecordForMapDTO> getAllPartnerActivitiesForMap(
        Pageable pageable, ProviderFilterDTO providerFilterDTO,
        String search, Silo silo, List<Double> boundaries);

    List<DeactivatedOrganizationDTO> getAllDeactivatedRecords();

    ProviderRecordDTO getPartnerActivityById(UUID id);

    ProviderRecordDTO getPartnerActivityById(UUID id, Silo silo);

    Page<ProviderRecordDTO> getAllPartnerActivitiesPublic(ProviderFilterDTO providerFilterDTO,
        Silo silo, String search, Pageable pageable);

    Page<ProviderRecordDTO> getRecordsToClaim(Pageable pageable, String search);
}
