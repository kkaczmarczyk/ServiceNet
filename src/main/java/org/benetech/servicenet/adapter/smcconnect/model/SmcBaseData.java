package org.benetech.servicenet.adapter.smcconnect.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public abstract class SmcBaseData {

    @SerializedName("location_id")
    private String locationId;

    @SerializedName("organization_id")
    private String organizationId;

    @SerializedName("service_id")
    private String serviceId;

    private String id;
}
