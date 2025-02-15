package com.companyname.ofbizdemo.services;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.security.Security;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.service.GenericServiceException;

public class CreateCustomerService {

    private static final String MODULE = CreateCustomerService.class.getName();

    public static Map<String, Object> createCustomer(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Security security = dctx.getSecurity();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String emailAddress = (String) context.get("emailAddress");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");

        try {
            List<GenericValue> existingCustomers = EntityQuery.use(delegator)
                    .from("FindCustomerView")
                    .where("emailAddress", emailAddress)
                    .queryList();
            if (UtilValidate.isNotEmpty(existingCustomers)) {
                Debug.logWarning("Customer with email " + emailAddress + " already exists.", MODULE);
                result.put(ModelService.ERROR_MESSAGE, "Customer with email " + emailAddress + " already exists.");
                return result;
            }

            String partyId = delegator.getNextSeqId("Party");
            GenericValue party = delegator.makeValue("Party");
            party.set("partyId", partyId);
            party.set("partyTypeId", "PERSON");
            delegator.create(party);
            Debug.logInfo("Creating Party with partyId: " + partyId + ", partyTypeId: PERSON", MODULE);

            GenericValue person = delegator.makeValue("Person");
            person.set("partyId", partyId);
            person.set("firstName", firstName);
            person.set("lastName", lastName);
            delegator.create(person);

            GenericValue partyRole = delegator.makeValue("PartyRole");
            partyRole.set("partyId", partyId);
            partyRole.set("roleTypeId", "CUSTOMER");
            delegator.create(partyRole);

            String contactMechId = delegator.getNextSeqId("ContactMech");
            GenericValue contactMech = delegator.makeValue("ContactMech");
            contactMech.set("contactMechId", contactMechId);
            contactMech.set("contactMechTypeId", "EMAIL_ADDRESS");
            contactMech.set("infoString", emailAddress);
            delegator.create(contactMech);

            GenericValue partyContactMech = delegator.makeValue("PartyContactMech");
            partyContactMech.set("partyId", partyId);
            partyContactMech.set("contactMechId", contactMechId);
            partyContactMech.set("fromDate", new java.sql.Timestamp(System.currentTimeMillis()));
            delegator.create(partyContactMech);


            result.put("partyId", partyId);
            result.put("successMessage", "Customer created successfully with partyId: " + partyId);

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error creating customer", MODULE);
            result = ServiceUtil.returnError("Error creating customer: " + e.getMessage());
            result.put(ModelService.ERROR_MESSAGE, "Error creating customer: " + e.getMessage());

            return result;
        }

        return result;
    }
}
