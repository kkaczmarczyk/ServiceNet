package org.benetech.servicenet.service.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class FiltersActivityDTO implements Serializable {

  private List<String> citiesFilterList;

  private List<String> regionFilterList;

  private List<String> postalCodesFilterList;

  private List<String> partnersFilterList;
}

