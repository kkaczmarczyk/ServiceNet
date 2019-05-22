package org.benetech.servicenet.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.view.ActivityInfo;
import org.benetech.servicenet.repository.ActivityRepository;
import org.benetech.servicenet.service.ActivityService;
import org.benetech.servicenet.service.ConflictService;
import org.benetech.servicenet.service.OrganizationMatchService;
import org.benetech.servicenet.service.OrganizationService;
import org.benetech.servicenet.service.RecordsService;
import org.benetech.servicenet.service.dto.ActivityDTO;
import org.benetech.servicenet.service.dto.FiltersActivityDTO;
import org.benetech.servicenet.service.dto.OrganizationMatchDTO;
import org.benetech.servicenet.service.dto.RecordDTO;
import org.benetech.servicenet.service.exceptions.ActivityCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service Implementation for managing Activity.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final OrganizationMatchService organizationMatchService;

    private final ConflictService conflictService;

    private final RecordsService recordsService;

    private final OrganizationService organizationService;

    public ActivityServiceImpl(ActivityRepository activityRepository, ConflictService conflictService,
                               OrganizationMatchService organizationMatchService, 
                               RecordsService recordsService, OrganizationService organizationService) {
    this.activityRepository = activityRepository;
    this.organizationMatchService = organizationMatchService;
    this.conflictService = conflictService;
    this.recordsService = recordsService;
    this.organizationService = organizationService;
  }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> getAllOrganizationActivities(Pageable pageable, UUID systemAccountId,
    String search, FiltersActivityDTO filtersForActivity) {
        List<ActivityDTO> activities = new ArrayList<>();
        Page<ActivityInfo> activitiesInfo = findAllActivitiesInfoWithOwnerId(systemAccountId, search, pageable);

        for (ActivityInfo info : activitiesInfo) {
            try {
                if (doesOrganizationMatchFilter(info.getId(), filtersForActivity)) {
                  Optional<ActivityDTO> activityOpt = getEntityActivity(info.getId());
                  activityOpt.ifPresent(activities::add);
                }
            } catch (ActivityCreationException ex) {
                log.error(ex.getMessage());
            }
        }

        return new PageImpl<>(
            activities, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), activitiesInfo.getTotalElements());
    }

    private Boolean doesOrganizationMatchFilter(UUID organizationId, FiltersActivityDTO filtersForActivity) {
      Optional<Organization> organization = organizationService.findOne(organizationId);
      return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActivityDTO> getOneByOrganizationId(UUID organizationId) {
        return getEntityActivity(organizationId);
    }

    private Optional<ActivityDTO> getEntityActivity(UUID orgId) throws ActivityCreationException {
        log.debug("Creating Activity for organization: {}", orgId);
        try {
            Optional<RecordDTO> opt = recordsService.getRecordFromOrganization(orgId, orgId);
            RecordDTO record = opt.orElseThrow(() -> new ActivityCreationException(
                String.format("Activity record couldn't be created for organization: %s", orgId)));

            Optional<ZonedDateTime> lastUpdated = conflictService.findMostRecentStateDate(orgId);
            if (CollectionUtils.isEmpty(record.getConflicts())) {
                return Optional.of(ActivityDTO.builder()
                    .record(record)
                    .organizationMatches(new ArrayList<>())
                    .lastUpdated(lastUpdated.orElse(ZonedDateTime.now()))
                    .build());
            } else {
                List<OrganizationMatchDTO> matches = organizationMatchService.findAllForOrganization(orgId);

                return Optional.of(ActivityDTO.builder()
                    .record(record)
                    .organizationMatches(matches)
                    .lastUpdated(lastUpdated.orElse(ZonedDateTime.now()))
                    .build());
            }
        } catch (IllegalAccessException e) {
            return Optional.empty();
        }
    }

    private Page<ActivityInfo> findAllActivitiesInfoWithOwnerId(UUID ownerId, String search, Pageable pageable) {
        if (ownerId != null) {
            if (StringUtils.isBlank(search)) {
                return activityRepository.findAllWithOwnerId(ownerId, pageable);
            } else {
                return activityRepository.findAllWithOwnerIdAndSearchPhrase(ownerId, search, pageable);
            }
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, Collections.emptyList().size());
        }
    }
}
