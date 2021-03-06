package org.benetech.servicenet.adapter.sheltertech.mapper;

import static org.benetech.servicenet.adapter.shared.util.OpeningHoursUtils.getWeekday;
import static org.benetech.servicenet.adapter.shared.util.OpeningHoursUtils.normalizeTime;

import org.benetech.servicenet.adapter.sheltertech.model.ScheduleDayRaw;
import org.benetech.servicenet.adapter.sheltertech.model.ScheduleRaw;
import org.benetech.servicenet.domain.OpeningHours;
import org.benetech.servicenet.domain.RegularSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mapper
public interface ShelterTechRegularScheduleMapper {

    ShelterTechRegularScheduleMapper INSTANCE = Mappers.getMapper(ShelterTechRegularScheduleMapper.class);

    default RegularSchedule mapToRegularSchedule(ScheduleRaw scheduleRaw) {
        if (scheduleRaw == null) {
            return null;
        }

        Set<OpeningHours> openingHours = openingHoursFromScheduleDaysRaw(scheduleRaw.getScheduleDays());

        if (openingHours.isEmpty()) {
            return null;
        }

        return new RegularSchedule().openingHours(openingHours);
    }

    @Named("openingHoursFromScheduleDaysRaw")
    default Set<OpeningHours> openingHoursFromScheduleDaysRaw(List<ScheduleDayRaw> days) {
        Set<OpeningHours> hours =  new HashSet<>();

        if (days == null) {
            return hours;
        }

        for (ScheduleDayRaw raw : days) {
            hours.add(INSTANCE.mapToOpeningHours(raw));
        }

        return hours;
    }

    default OpeningHours mapToOpeningHours(ScheduleDayRaw scheduleRaw) {
        String open = "";
        String close = "";

        if (scheduleRaw.getOpensAt() != null) {
            open = scheduleRaw.getOpensAt().toString();
        } else if (scheduleRaw.getOpenTime() != null) {
            open = scheduleRaw.getOpenTime().toString();
        }

        if (scheduleRaw.getClosesAt() != null) {
            close = scheduleRaw.getClosesAt().toString();
        } else if (scheduleRaw.getCloseTime() != null) {
            close = scheduleRaw.getCloseTime().toString();
        }

        return new OpeningHours()
            .opensAt(normalizeTime(open))
            .closesAt(normalizeTime(close))
            .weekday(getWeekday(scheduleRaw.getScheduleDays()));
    }
}
