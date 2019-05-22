import React from 'react';
import { Button, Col, Container, Row, Collapse, Card, CardBody } from 'reactstrap';
import { Translate } from 'react-jhipster';
import Select from 'react-select';
import { IRootState } from 'app/shared/reducers';
import { getPostalCodeList, getRegionList, getCityList, getPartnerList, updateActivityFilter } from './filter-activity.reducer';
import { getEntities } from 'app/shared/reducers/activity.reducer';

import { RouteComponentProps } from 'react-router-dom';
import { connect } from 'react-redux';
import _ from 'lodash';
import value from '*.json';

export interface IFilterActivityState {
  cityDropdownOpen: boolean;
  countyDropdownOpen: boolean;
  zipDropdownOpen: boolean;
  partnerDropdownOpen: boolean;
  selectedCity: any;
  selectedCounty: any;
  selectedZip: any;
  selectedPartner: any;
  activityFilter: any;
}

export interface IFilterActivityProps extends StateProps, DispatchProps, RouteComponentProps<{}> {
  filterCollapseExpanded: boolean;
  getActivityEntities(): any;
}

export class FilterActivity extends React.Component<IFilterActivityProps, IFilterActivityState> {
  static getPartnerMocks() {
    return [
      { value: 1, label: 'ShelterTech' },
      { value: 2, label: 'UWBA' },
      { value: 3, label: 'Healthleads' },
      { value: 4, label: 'Eden' }
    ];
  }

  constructor(props: any) {
    super(props);

    this.toggleCityDropdown = this.toggleCityDropdown.bind(this);
    this.toggleCountyDropdown = this.toggleCountyDropdown.bind(this);
    this.toggleZipDropdown = this.toggleZipDropdown.bind(this);
    this.togglePartnerDropdown = this.togglePartnerDropdown.bind(this);
    this.handleCityChange = this.handleCityChange.bind(this);
    this.applyFilter = this.applyFilter.bind(this);

    this.state = {
      cityDropdownOpen: false,
      countyDropdownOpen: false,
      zipDropdownOpen: false,
      partnerDropdownOpen: false,
      selectedCity: [],
      selectedCounty: [],
      selectedZip: [],
      selectedPartner: [],
      activityFilter: []
    };
  }

  componentDidMount() {
    this.getPostalCodeList();
    this.getRegionList();
    this.getCityList();
    // this.getPartnerList();
  }
  getPartnerList() {
    throw new Error('Method not implemented.');
  }
  getPostalCodeList() {
    this.props.getPostalCodeList();
  }
  getRegionList() {
    this.props.getRegionList();
  }
  getCityList() {
    this.props.getCityList();
  }

  applyFilter() {
    this.props.getActivityEntities();
  }

  toggleCityDropdown() {
    this.setState(prevState => ({
      cityDropdownOpen: !prevState.cityDropdownOpen
    }));
  }

  toggleCountyDropdown() {
    this.setState(prevState => ({
      countyDropdownOpen: !prevState.countyDropdownOpen
    }));
  }

  toggleZipDropdown() {
    this.setState(prevState => ({
      zipDropdownOpen: !prevState.zipDropdownOpen
    }));
  }

  togglePartnerDropdown() {
    this.setState(prevState => ({
      partnerDropdownOpen: !prevState.partnerDropdownOpen
    }));
  }

  handleCityChange = selectedCity => {
    this.setState({ selectedCity });

    const citiesFilterList = selectedCity.map(city => city.value);
    const newActivityFilter = this.props.activityFilter;
    newActivityFilter.citiesFilterList = citiesFilterList;

    this.props.updateActivityFilter(newActivityFilter);
  };

  handleCountyChange = selectedCounty => {
    this.setState({ selectedCounty });

    const regionFilterList = selectedCounty.map(county => county.value);
    const newActivityFilter = this.props.activityFilter;
    newActivityFilter.regionFilterList = regionFilterList;

    this.props.updateActivityFilter(newActivityFilter);
  };

  handleZipChange = selectedZip => {
    this.setState({ selectedZip });

    const postalCodesFilterList = selectedZip.map(county => county.value);
    const newActivityFilter = this.props.activityFilter;
    newActivityFilter.postalCodesFilterList = postalCodesFilterList;

    this.props.updateActivityFilter(newActivityFilter);
  };

  handlePartnerChange = selectedPartner => {
    this.setState({ selectedPartner });
  };

  render() {
    const { filterCollapseExpanded, postalCodeList, cityList, regionList } = this.props;
    return (
      <div>
        <Collapse isOpen={filterCollapseExpanded}>
          <Card>
            <CardBody>
              <Container>
                <Row>
                  <Col md="3">
                    <Translate contentKey="serviceNetApp.activity.home.filter.city" />
                    <Select
                      value={this.props.activityFilter.citiesFilterList.map(city => ({ label: city, value: city }))}
                      onChange={this.handleCityChange}
                      options={cityList.map(city => ({ label: city, value: city }))}
                      isMulti
                    />
                  </Col>
                  <Col md="3">
                    <Translate contentKey="serviceNetApp.activity.home.filter.county" />
                    <Select
                      value={this.props.activityFilter.regionFilterList.map(city => ({ label: city, value: city }))}
                      onChange={this.handleCountyChange}
                      options={regionList.map(region => ({ label: region, value: region }))}
                      isMulti
                    />
                  </Col>
                  <Col md="3">
                    <Translate contentKey="serviceNetApp.activity.home.filter.zip" />
                    <Select
                      value={this.props.activityFilter.postalCodesFilterList.map(city => ({ label: city, value: city }))}
                      onChange={this.handleZipChange}
                      options={postalCodeList.map(postalCode => ({ label: postalCode, value: postalCode }))}
                      isMulti
                    />
                  </Col>
                  <Col md="3">
                    <Translate contentKey="serviceNetApp.activity.home.filter.partner" />
                    <Select
                      value={this.state.selectedPartner}
                      onChange={this.handlePartnerChange}
                      options={FilterActivity.getPartnerMocks()}
                      isMulti
                    />
                  </Col>
                </Row>
                <Row>
                  <Col md={{ size: 2, offset: 10 }}>
                    <Button color="primary" onClick={this.applyFilter} style={{ marginTop: '1rem' }} block>
                      <Translate contentKey="serviceNetApp.activity.home.filter.applyFilter" />
                    </Button>
                  </Col>
                </Row>
              </Container>
            </CardBody>
          </Card>
        </Collapse>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  postalCodeList: storeState.filterActivity.postalCodeList,
  regionList: storeState.filterActivity.regionList,
  cityList: storeState.filterActivity.cityList,
  partnerList: storeState.filterActivity.partnerList,
  activityFilter: storeState.filterActivity.activityFilter
});

const mapDispatchToProps = { getPostalCodeList, getRegionList, getCityList, getPartnerList, updateActivityFilter };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(FilterActivity);
