import './home.scss';

import React from 'react';
import InfiniteScroll from 'react-infinite-scroller';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Translate, translate, getSortState, IPaginationBaseState, Storage } from 'react-jhipster';
import { connect } from 'react-redux';
import ReactGA from 'react-ga';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Row, Col, Alert, Container, Progress, Spinner, InputGroup, Input, Jumbotron, Button } from 'reactstrap';

import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';
import { getEntities, reset } from 'app/shared/reducers/activity.reducer';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import ActivityElement from './activity-element';
import SortActivity from './sort-activity';
import { SORT_ARRAY, getSearchPreferences, setSort, setSearchPhrase } from 'app/shared/util/search-utils';
import FilterActivity from './filter-activity';

const SEARCH_TIMEOUT = 1000;

export interface IHomeProp extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface IHomeState extends IPaginationBaseState {
  dropdownOpen: boolean;
  filterCollapseExpanded: boolean;
  loggingOut: boolean;
  searchPhrase: string;
  typingTimeout: number;
  activityFilter: any;
}

export class Home extends React.Component<IHomeProp, IHomeState> {
  constructor(props) {
    super(props);

    const { searchPhrase, sort, order } = getSearchPreferences(this.props.account.login);

    this.state = {
      ...getSortState(this.props.location, ITEMS_PER_PAGE),
      sort,
      order,
      dropdownOpen: false,
      filterCollapseExpanded: false,
      loggingOut: this.props.location.state ? this.props.location.state.loggingOut : false,
      searchPhrase,
      typingTimeout: 0,
      activityFilter: []
    };
  }

  componentDidMount() {
    this.reset();
  }

  componentDidUpdate(prevProps) {
    if (this.props.account.login && !(prevProps.account && prevProps.account.login)) {
      const { searchPhrase, sort, order } = getSearchPreferences(this.props.account.login);
      this.setState({ searchPhrase, sort, order });
    }

    if (this.props.updateSuccess || (this.props.loginSuccess === true && prevProps.loginSuccess === false)) {
      this.reset();
    }
  }

  sort = prop => () => {
    setSort(this.props.account.login, prop);

    ReactGA.event({ category: 'UserActions', action: 'Sorting Records' });

    this.setState({ sort: prop }, () => {
      this.reset();
    });
  };

  toggleSort = () => {
    this.setState(prevState => ({
      dropdownOpen: !prevState.dropdownOpen
    }));
  };

  toggleFilter = () => {
    this.setState(prevState => ({
      filterCollapseExpanded: !prevState.filterCollapseExpanded
    }));
    // console.log(this.state.filterCollapseExpanded);
  };

  reset = () => {
    this.props.reset();
    if (!this.state.loggingOut) {
      Promise.all([this.props.getSession()]).then(() => {
        this.setState({ activePage: 1 }, () => {
          this.getEntities();
        });
      });
    } else {
      this.setState({ activePage: 1, loggingOut: false });
    }
  };

  handleLoadMore = () => {
    if (window.pageYOffset > 0 && this.props.totalItems > this.props.activityList.length) {
      this.setState({ activePage: this.state.activePage + 1 }, () => this.getEntities());
    }
  };

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order } = this.state;
    // tslint:disable-next-line:no-console
    console.log('SUBMITTING', this.props.activityFilter);
    if (this.props.isAuthenticated) {
      this.props.getEntities(this.state.searchPhrase, activePage - 1, itemsPerPage, `${sort},${order}`, this.props.activityFilter);
    }
  };

  searchEntities = () => {
    this.props.reset();
    this.getEntities();
  };

  changeSearchPhrase = event => {
    if (this.state.typingTimeout) {
      clearTimeout(this.state.typingTimeout);
    }

    const searchPhrase = event.target.value;
    setSearchPhrase(this.props.account.login, searchPhrase);

    ReactGA.event({ category: 'UserActions', action: 'Searching Records' });

    this.setState({
      searchPhrase,
      typingTimeout: setTimeout(() => {
        this.searchEntities();
      }, SEARCH_TIMEOUT)
    });
  };

  clearSearchBar = () => {
    if (this.state.searchPhrase !== '') {
      setSearchPhrase(this.props.account.login, '');

      this.setState({
        searchPhrase: '',
        typingTimeout: setTimeout(() => {
          this.searchEntities();
        }, SEARCH_TIMEOUT)
      });
    }
  };

  render() {
    const { account, activityList } = this.props;
    return (
      <Container>
        <Row>
          <Col>
            {account && account.login ? null : (
              <div>
                <Alert color="warning">
                  <Translate contentKey="global.messages.info.authenticated.prefix">If you want to </Translate>
                  <Link to="/login" className="alert-link">
                    <Translate contentKey="global.messages.info.authenticated.link"> sign in</Translate>
                  </Link>
                  <Translate contentKey="global.messages.info.authenticated.suffix">
                    , you can try the default accounts:
                    <br />- Administrator (login=&quot;admin&quot; and password=&quot;admin&quot;)
                    <br />- User (login=&quot;user&quot; and password=&quot;user&quot;).
                  </Translate>
                </Alert>

                <Alert color="warning">
                  <Translate contentKey="global.messages.info.register.noaccount">You do not have an account yet?</Translate>
                  &nbsp;
                  <Link to="/register" className="alert-link">
                    <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
                  </Link>
                </Alert>
              </div>
            )}
          </Col>
        </Row>
        {account && account.login ? (
          <InfiniteScroll
            pageStart={this.state.activePage}
            loadMore={this.handleLoadMore}
            hasMore={this.state.activePage - 1 < this.props.links.next}
            loader={<Spinner color="primary" />}
            threshold={0}
            initialLoad={false}
          >
            <Container>
              <Row>
                <Col sm="12" className="searchBar">
                  <FontAwesomeIcon icon="search" size="lg" className="searchIcon" />
                  <Input
                    bsSize="lg"
                    className="searchInput"
                    type="search"
                    name="search"
                    id="searchBar"
                    placeholder={translate('serviceNetApp.activity.home.search.placeholder')}
                    value={this.state.searchPhrase}
                    onChange={this.changeSearchPhrase}
                  />
                </Col>
                <div className="searchClearIconContainer" onClick={this.clearSearchBar}>
                  <FontAwesomeIcon icon="times-circle" size="lg" className="searchClearIcon" />
                </div>
              </Row>
              <Row>
                <Col className="col-auto mr-auto">
                  <h2 id="main-page-title">
                    <Translate contentKey="serviceNetApp.activity.unresolved.title" />
                  </h2>
                </Col>
                <Col className="col-1">
                  <div className="text-center">
                    {this.props.activityList.length} / {this.props.totalItems}
                  </div>
                  <Progress color="info" value={(this.props.activityList.length / this.props.totalItems) * 100} />
                </Col>
                <Col className="col-auto">
                  <SortActivity
                    dropdownOpen={this.state.dropdownOpen}
                    toggleSort={this.toggleSort}
                    sort={this.state.sort}
                    sortFunc={this.sort}
                    values={SORT_ARRAY}
                  />
                </Col>
                <Col className="col-auto">
                  <Button color="primary" onClick={this.toggleFilter} style={{ marginBottom: '1rem' }}>
                    <Translate contentKey="serviceNetApp.activity.home.filter.toggle" />
                  </Button>
                </Col>
              </Row>
              <Row>
                <Col md="12">
                  <FilterActivity filterCollapseExpanded={this.state.filterCollapseExpanded} getActivityEntities={this.getEntities} />
                </Col>
              </Row>
              {activityList.map((activity, i) => (
                <Link key={`linkToActivity${i}`} to={`/single-record-view/${activity.record.organization.id}`} className="alert-link">
                  <ActivityElement activity={activity} />
                </Link>
              ))}
              {activityList.length === 0 ? (
                <Row>
                  <Col md="8">
                    <Translate contentKey="serviceNetApp.activity.empty" />
                  </Col>
                </Row>
              ) : null}
            </Container>
          </InfiniteScroll>
        ) : null}
      </Container>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  loginSuccess: storeState.authentication.loginSuccess,
  activityList: storeState.activity.entities,
  totalItems: storeState.activity.totalItems,
  links: storeState.activity.links,
  entity: storeState.activity.entity,
  updateSuccess: storeState.activity.updateSuccess,
  activityFilter: storeState.filterActivity.activityFilter
});

const mapDispatchToProps = {
  getSession,
  getEntities,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Home);
