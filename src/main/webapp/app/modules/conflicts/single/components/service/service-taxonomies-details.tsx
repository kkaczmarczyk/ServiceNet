import React from 'react';
import '../../single-record-view.scss';
import { connect } from 'react-redux';
import { IActivity } from 'app/shared/model/activity.model';
import { AdditionalDetails } from '../additional-details';
import { Translate } from 'react-jhipster';
import { Badge } from 'reactstrap';
import { IServiceTaxonomy } from 'app/shared/model/service-taxonomy.model';

export interface IServiceTaxonomiesDetailsProp extends StateProps, DispatchProps {
  activity: IActivity;
  taxonomies: IServiceTaxonomy[];
}

export class ServiceTaxonomiesDetails extends React.Component<IServiceTaxonomiesDetailsProp> {
  getTextField = (taxonomy, fieldName) => ({
    type: 'text',
    fieldName,
    defaultValue: taxonomy[fieldName]
  });

  render() {
    const { taxonomies } = this.props;
    const fields = taxonomies.map(taxonomy => this.getTextField(taxonomy, 'taxonomyDetails'));

    return fields.length > 0 ? (
      <AdditionalDetails
        {...this.props}
        fields={fields}
        entityClass={'ServiceTaxonomy'}
        customHeader={false}
        additionalFields={false}
        toggleAvailable
        isCustomToggle={false}
        customToggleValue={false}
      />
    ) : (
      <h4>
        <Badge color="secondary">
          <Translate contentKey="singleRecordView.details.noRecords" />
        </Badge>
      </h4>
    );
  }
}

const mapStateToProps = () => ({});

const mapDispatchToProps = {};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ServiceTaxonomiesDetails);
