package org.benetech.servicenet.adapter.eden;

import org.apache.commons.lang3.StringUtils;
import org.benetech.servicenet.adapter.eden.model.Accessibility;
import org.benetech.servicenet.adapter.eden.model.Agency;
import org.benetech.servicenet.adapter.eden.model.Contact;
import org.benetech.servicenet.adapter.eden.model.ContactDetails;
import org.benetech.servicenet.adapter.eden.model.Day;
import org.benetech.servicenet.adapter.eden.model.Hours;
import org.benetech.servicenet.adapter.eden.model.Name;
import org.benetech.servicenet.adapter.eden.model.Program;
import org.benetech.servicenet.adapter.eden.model.Site;
import org.benetech.servicenet.adapter.eden.model.Weekday;
import org.benetech.servicenet.adapter.shared.util.LocationUtils;
import org.benetech.servicenet.domain.AccessibilityForDisabilities;
import org.benetech.servicenet.domain.Eligibility;
import org.benetech.servicenet.domain.Language;
import org.benetech.servicenet.domain.Location;
import org.benetech.servicenet.domain.OpeningHours;
import org.benetech.servicenet.domain.Organization;
import org.benetech.servicenet.domain.Phone;
import org.benetech.servicenet.domain.PhysicalAddress;
import org.benetech.servicenet.domain.PostalAddress;
import org.benetech.servicenet.domain.Service;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(unmappedTargetPolicy = IGNORE)
public interface EdenDataMapper {

    String LISTS_DELIMITER = "; ";

    EdenDataMapper INSTANCE = Mappers.getMapper(EdenDataMapper.class);
    String PRIMARY = "Primary";
    String PHONE_NUMBER = "PhoneNumber";
    String EMAIL_ADDRESS = "EmailAddress";
    String WEBSITE = "Website";
    String PHYSICAL_LOCATION = "PhysicalLocation";
    String POSTAL_ADDRESS = "PostalAddress";
    String ACTIVE = "Active";

    default Organization extractOrganization(Agency agency) {
        Organization result = new Organization();

        result.setName(extractName(agency.getNames()));
        result.setAlternateName(extractAlternateName(agency.getNames()));
        result.setEmail(extractEmail(agency.getContactDetails()));
        result.setUrl(extractUrl(agency.getContactDetails()));
        result.setActive(agency.getStatus().equals(ACTIVE));

        return result;
    }

    default Service extractService(Program program) {
        Service result = new Service();

        result.setName(extractName(program.getNames()));
        result.setAlternateName(extractAlternateName(program.getNames()));
        result.setDescription(program.getDescriptionText());
        result.setEmail(extractEmail(program.getContactDetails()));
        result.setUrl(extractUrl(program.getContactDetails()));
        result.setFees(program.getFees());
        result.setApplicationProcess(program.getApplicationProcess());

        return result;
    }

    default List<OpeningHours> extractOpeningHours(Hours hours) {
        if (hours == null || hours.getDays() == null) {
            return new ArrayList<>();
        }
        Day[] days = hours.getDays();
        return Arrays.stream(days).map(day -> new OpeningHours()
            .weekday(getIdByTheWeekday(day.getDayOfWeek()))
            .opensAt(day.getOpens())
            .closesAt(day.getCloses()))
            .collect(Collectors.toList());
    }


    @Mapping(source = "disabled", target = "accessibility")
    AccessibilityForDisabilities mapAccessibility(Accessibility accessibility);

    default AccessibilityForDisabilities extractAccessibilityForDisabilities(Site site) {
        return site.getAccessibility() != null && site.getAccessibility().getDisabled() != null
            ? mapAccessibility(site.getAccessibility())
            : null;
    }

    default int getIdByTheWeekday(String weekday) {
        return Weekday.valueOf(weekday.toUpperCase(Locale.ROOT)).getNumber();
    }

    Phone mapContactToPhone(Contact contact);

    default Phone extractPhone(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(PHONE_NUMBER))
            .findFirst().map(entry -> mapContactToPhone(entry.getContact()))
            .orElse(null);
    }

    @Mapping(source = "line1", target = "address1")
    @Mapping(source = "zipPostalCode", target = "postalCode")
    PhysicalAddress mapToPhysicalAddress(Contact contact);

    @Mapping(source = "line1", target = "address1")
    @Mapping(source = "zipPostalCode", target = "postalCode")
    PostalAddress mapToPostalAddress(Contact contact);

    default Location extractLocation(Contact contact) {
        Location result = new Location().name(extractLocationName(contact));
        result.setLatitude(contact.getLatitude());
        result.setLongitude(contact.getLongitude());
        return result;
    }

    default Location extractLocation(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(PHYSICAL_LOCATION))
            .findFirst().map(entry -> extractLocation(entry.getContact()))
            .orElse(null);
    }

    private String extractLocationName(Contact contact) {
        return LocationUtils.buildLocationName(contact.getCity(), contact.getStateProvince(), contact.getLine1());
    }

    default Set<Language> extractLangs(Program program) {
        String[] langs = program.getLanguagesOffered().split(LISTS_DELIMITER);
        return Arrays.stream(langs).map(lang -> new Language().language(lang)).collect(Collectors.toSet());
    }

    default Eligibility extractEligibility(Program program) {
        return StringUtils.isNotBlank(program.getEligibility())
            ? new Eligibility().eligibility(program.getEligibility())
            : null;
    }

    default PhysicalAddress extractPhysicalAddress(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(PHYSICAL_LOCATION))
            .findFirst().map(entry -> mapToPhysicalAddress(entry.getContact()))
            .orElse(null);
    }

    default PostalAddress extractPostalAddress(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(POSTAL_ADDRESS))
            .findFirst().map(entry -> mapToPostalAddress(entry.getContact()))
            .orElse(null);
    }

    default String extractEmail(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(EMAIL_ADDRESS))
            .findFirst().map(entry -> entry.getContact().getAddress())
            .orElse(null);
    }

    default String extractUrl(ContactDetails[] contactDetails) {
        return Arrays.stream(contactDetails)
            .filter(entry -> entry.getContact().getType().equals(WEBSITE))
            .findFirst().map(entry -> entry.getContact().getUrl())
            .orElse(null);
    }

    private String extractName(Name[] names) {
        for (Name name : names) {
            if (name.getPurpose().equals(PRIMARY)) {
                return name.getValue();
            }
        }
        throw new IllegalArgumentException("No primary name found");
    }

    private String extractAlternateName(Name[] names) {
        return Arrays.stream(names)
            .filter(name -> !name.getPurpose().equals(PRIMARY))
            .findFirst().map(Name::getValue)
            .orElse(null);
    }
}
