package com.companyname.ofbizdemo.services;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.sql.Timestamp;
import java.math.RoundingMode;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ModelService;
import org.apache.ofbiz.service.GenericServiceException;

public class UpdateCustomerService {

    private static final String MODULE = UpdateCustomerService.class.getName();

    public static Map<String, Object> updateCustomer(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        String emailAddress = (String) context.get("emailAddress");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String postalAddress = (String) context.get("postalAddress");
        String phoneNumber = (String) context.get("phoneNumber");

        Timestamp today = new Timestamp(System.currentTimeMillis());

        Debug.logInfo("Searching for customer with email: " + emailAddress, MODULE);

        try {
            List<GenericValue> existingCustomers = EntityQuery.use(delegator)
                    .from("FindCustomerView")
                    .where("emailAddress", emailAddress)
                    .queryList();

            if (UtilValidate.isNotEmpty(existingCustomers)) {
                GenericValue existingCustomer = existingCustomers.get(0);
                String partyId = existingCustomer.getString("partyId");

                if (postalAddress != null && !postalAddress.isEmpty()) {
                    updatePostalAddress(delegator, partyId, postalAddress, today);
                }

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    updatePhoneNumber(delegator, partyId, phoneNumber, today);
                }

                result.put("successMessage", "Customer details updated successfully for email: " + emailAddress);

            } else {
                String partyId = delegator.getNextSeqId("Party");
                GenericValue party = delegator.makeValue("Party");
                party.set("partyId", partyId);
                party.set("partyTypeId", "PERSON");
                delegator.create(party);

                GenericValue person = delegator.makeValue("Person");
                person.set("partyId", partyId);
                person.set("firstName", firstName);
                person.set("lastName", lastName);
                delegator.create(person);

                if (postalAddress != null && !postalAddress.isEmpty()) {
                    createPostalAddress(delegator, partyId, postalAddress, today);
                }

                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    createPhoneNumber(delegator, partyId, phoneNumber, today);
                }

                result.put("successMessage", "New customer created successfully with email: " + emailAddress);
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error updating or creating customer", MODULE);
            result = ServiceUtil.returnError("Error updating or creating customer: " + e.getMessage());
        }

        return result;
    }

    private static void updatePostalAddress(Delegator delegator, String partyId, String postalAddress, Timestamp today) throws GenericEntityException {
        GenericValue oldPostalMech = delegator.findOne("PartyContactMech", false, "partyId", partyId);
        if (oldPostalMech != null) {
            oldPostalMech.set("thruDate", today);
            delegator.store(oldPostalMech);
            Debug.logInfo("Marked old postal address as inactive for partyId: " + partyId, MODULE);
        }

        createPostalAddress(delegator, partyId, postalAddress, today);
    }

    private static void createPostalAddress(Delegator delegator, String partyId, String postalAddress, Timestamp today) throws GenericEntityException {
        GenericValue postalContactMech = delegator.makeValue("ContactMech");
        postalContactMech.set("contactMechTypeId", "POSTAL_ADDRESS");
        postalContactMech.set("contactMechId", delegator.getNextSeqId("ContactMech"));
        delegator.create(postalContactMech);

        GenericValue newPostalAddress = delegator.makeValue("PostalAddress");
        newPostalAddress.set("contactMechId", postalContactMech.getString("contactMechId"));
        newPostalAddress.set("address1", postalAddress);
        delegator.create(newPostalAddress);

        GenericValue newPartyContactMech = delegator.makeValue("PartyContactMech");
        newPartyContactMech.set("partyId", partyId);
        newPartyContactMech.set("contactMechId", postalContactMech.getString("contactMechId"));
        newPartyContactMech.set("fromDate", today);
        delegator.create(newPartyContactMech);

        Debug.logInfo("Updated postal address for partyId: " + partyId, MODULE);
    }

    private static void updatePhoneNumber(Delegator delegator, String partyId, String phoneNumber, Timestamp today) throws GenericEntityException {
        GenericValue oldTelecomMech = delegator.findOne("PartyContactMech", false, "partyId", partyId);
        if (oldTelecomMech != null) {
            oldTelecomMech.set("thruDate", today);
            delegator.store(oldTelecomMech);
            Debug.logInfo("Marked old phone number as inactive for partyId: " + partyId, MODULE);
        }

        createPhoneNumber(delegator, partyId, phoneNumber, today);
    }

    private static void createPhoneNumber(Delegator delegator, String partyId, String phoneNumber, Timestamp today) throws GenericEntityException {
        GenericValue telecomContactMech = delegator.makeValue("ContactMech");
        telecomContactMech.set("contactMechTypeId", "TELECOM_NUMBER");
        telecomContactMech.set("contactMechId", delegator.getNextSeqId("ContactMech"));
        delegator.create(telecomContactMech);

        GenericValue telecomNumber = delegator.makeValue("TelecomNumber");
        telecomNumber.set("contactMechId", telecomContactMech.getString("contactMechId"));
        telecomNumber.set("contactNumber", phoneNumber);
        delegator.create(telecomNumber);

        GenericValue newPartyContactMech = delegator.makeValue("PartyContactMech");
        newPartyContactMech.set("partyId", partyId);
        newPartyContactMech.set("contactMechId", telecomContactMech.getString("contactMechId"));
        newPartyContactMech.set("fromDate", today);
        delegator.create(newPartyContactMech);

        Debug.logInfo("Updated phone number for partyId: " + partyId, MODULE);
    }
}