package com.companyname.ofbizdemo.services;

import java.util.Map;
import java.sql.Timestamp;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.entity.util.EntityQuery;
import java.util.List;

public class updateCustomerRelationship {

    private static final String MODULE = updateCustomerRelationship.class.getName();

    public static Map<String, Object> updateCustomerRelationship(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String partyIdTo = (String) context.get("partyIdTo");
        String partyIdFrom = (String) context.get("partyIdFrom");
        String partyRelationshipTypeId = (String) context.get("partyRelationshipTypeId");
        String statusId = (String) context.get("statusId");

        try {
            List<GenericValue> existingRelationships = EntityQuery.use(delegator)
                    .from("PartyRelationship")
                    .where("partyIdTo", partyIdTo, "partyIdFrom", partyIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId)
                    .queryList();

            if (existingRelationships == null || existingRelationships.isEmpty()) {
                return ServiceUtil.returnError("PartyRelationship not found with the specified parameters.");
            }

            GenericValue partyRelationship = existingRelationships.get(0);

            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            partyRelationship.set("thruDate", currentTimestamp);

            delegator.store(partyRelationship);

            GenericValue newPartyRelationship = delegator.makeValue("PartyRelationship");
            newPartyRelationship.set("partyIdFrom", partyIdFrom);
            newPartyRelationship.set("partyIdTo", partyIdTo);
            newPartyRelationship.set("partyRelationshipTypeId", partyRelationshipTypeId);
            newPartyRelationship.set("statusId", statusId);
            newPartyRelationship.set("fromDate", currentTimestamp);
            newPartyRelationship.set("roleTypeIdFrom", partyRelationship.getString("roleTypeIdFrom")); // Copy other necessary fields
            newPartyRelationship.set("roleTypeIdTo", partyRelationship.getString("roleTypeIdTo")); // Copy other necessary fields

            delegator.create(newPartyRelationship);

            result.put("successMessage", "Customer relationship updated successfully between " + partyIdFrom + " and " + partyIdTo);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error updating customer relationship", MODULE);
            return ServiceUtil.returnError("Error updating customer relationship: " + e.getMessage());
        }

        return result;
    }
}