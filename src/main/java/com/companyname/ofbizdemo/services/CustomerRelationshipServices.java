package com.companyname.ofbizdemo.services;

import java.util.Map;
import java.sql.Timestamp;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;

public class CustomerRelationshipServices {

    private static final String MODULE = CustomerRelationshipServices.class.getName();

    public static Map<String, Object> createCustomerRelationship(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String partyIdTo = (String) context.get("partyIdTo");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");

        try {
            GenericValue partyRelationship = delegator.makeValue("PartyRelationship");
            partyRelationship.set("partyIdTo", partyIdTo);
            partyRelationship.set("partyIdFrom", partyIdFrom);
            partyRelationship.set("roleTypeIdFrom", "CUSTOMER");
            partyRelationship.set("roleTypeIdTo", "CUSTOMER");
            partyRelationship.set("partyRelationshipTypeId", partyRelationshipTypeId);
            partyRelationship.set("fromDate",  new java.sql.Timestamp(System.currentTimeMillis()));

            delegator.create(partyRelationship);

            result.put("successMessage", "Customer relationship created successfully between " + partyIdFrom + " and " + partyIdTo);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error creating customer relationship", MODULE);
            return ServiceUtil.returnError("Error creating customer relationship: " + e.getMessage());
        }

        return result;
    }
}
