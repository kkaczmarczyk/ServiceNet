package org.benetech.servicenet.conflict.detector;

import org.benetech.servicenet.domain.Conflict;
import org.benetech.servicenet.domain.Organization;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("checkstyle:booleanExpressionComplexity")
@Service("OrganizationConflictDetector")
public class OrganizationConflictDetector extends AbstractDetector<Organization> implements ConflictDetector<Organization> {

    @Override
    public List<Conflict> detectConflicts(Organization current, Organization offered) {
        List<Conflict> conflicts = new LinkedList<>();

        conflicts.addAll(detectConflicts(current, offered, current.getName(), offered.getName(), "name"));
        conflicts.addAll(detectConflicts(current, offered, current.getAlternateName(), offered.getAlternateName(),
            "alternateName"));
        conflicts.addAll(detectConflicts(current, offered, current.getDescription(), offered.getDescription(),
            "description"));
        conflicts.addAll(detectConflicts(current, offered, current.getEmail(), offered.getEmail(), "email"));
        conflicts.addAll(detectConflicts(current, offered, current.getUrl(), offered.getUrl(), "url"));
        conflicts.addAll(detectConflicts(current, offered, current.getTaxStatus(), offered.getTaxStatus(), "taxStatus"));
        conflicts.addAll(detectConflicts(current, offered, current.getTaxId(), offered.getTaxId(), "taxId"));
        conflicts.addAll(detectConflicts(current, offered, current.getYearIncorporated(), offered.getYearIncorporated(),
            "yearIncorporated"));
        conflicts.addAll(detectConflicts(current, offered, current.getLegalStatus(), offered.getLegalStatus(),
            "legalStatus"));
        conflicts.addAll(detectConflicts(current, offered, current.getActive(), offered.getActive(), "active"));

        return conflicts;
    }
}
