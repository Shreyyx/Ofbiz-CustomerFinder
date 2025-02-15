package com.companyname.ofbizdemo.services;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.ServiceUtil;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.service.ModelService;

public class findCustomer {

    private static final String MODULE = findCustomer.class.getName();

    public static Map<String, Object> findCustomer(DispatchContext dctx, Map<String, Object> context) {
        String emailAddress = (String) context.get("emailAddress");
        String firstName = (String) context.get("firstName");
        String lastName = (String) context.get("lastName");
        String contactNumber = (String) context.get("contactNumber");
        String postalAddress = (String) context.get("postalAddress");

        Debug.logInfo("Searching for customer with email: " + emailAddress + ", firstName: " + firstName + ", lastName: " + lastName, MODULE);

        Map<String, Object> result = new HashMap<>();

        try {
            Delegator delegator = dctx.getDelegator();

            List<EntityCondition> conditions = new ArrayList<>();

            if (emailAddress != null && !emailAddress.isEmpty()) {
                Debug.logInfo("Email address is not null", MODULE);
                conditions.add(EntityCondition.makeCondition("emailAddress", EntityOperator.LIKE, "%" + emailAddress + "%"));
            }
            if (firstName != null && !firstName.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("firstName", EntityOperator.LIKE, "%" + firstName + "%"));
            }
            if (lastName != null && !lastName.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("lastName", EntityOperator.LIKE, "%" + lastName + "%"));
            }
            if (contactNumber != null && !contactNumber.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("contactNumber", EntityOperator.LIKE, "%" + contactNumber + "%"));
            }
            if (postalAddress != null && !postalAddress.isEmpty()) {
                conditions.add(EntityCondition.makeCondition("postalAddress", EntityOperator.LIKE, "%" + postalAddress + "%"));
            }

            EntityCondition combinedCondition = EntityCondition.makeCondition(conditions, EntityOperator.AND);

            List<GenericValue> customers = delegator.findList("FindCustomerView", combinedCondition, null, null, null, false);  // Changed to "Party" - replace with your actual entity name

            if (customers != null && !customers.isEmpty()) {

                Debug.logInfo("customer"+customers.get(0),MODULE);
                result.put("customerList", customers);
            } else {
                result.put("customerList", new ArrayList<>());
            }

        } catch (GenericEntityException e) {
            Debug.logError(e, "Error occurred while searching for customers", MODULE);
            result.put(ModelService.ERROR_MESSAGE, "Error occurred while searching for customers: " + e.getMessage());
            result.put("customerList", new ArrayList<>());
            return ServiceUtil.returnError(e.getMessage()); // Use ServiceUtil for error handling
        }

        return result;
    }
}
