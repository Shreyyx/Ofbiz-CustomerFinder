package com.companyname.ofbizdemo.events;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import java.util.Map;

public class AddNewCustomer {

    public static final String MODULE = AddNewCustomer.class.getName();

    public static String AddNewCustomer (HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        String emailAddress = request.getParameter("emailAddress");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");

        if (UtilValidate.isEmpty(emailAddress) || UtilValidate.isEmpty(firstName) || UtilValidate.isEmpty(lastName)) {
            String errMsg = "Email Address, First Name, and Last Name are required fields.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error"; // Return error view
        }

        try {
            Debug.logInfo("=======Creating new customer using createCustomer service=========", MODULE);

            Map<String, Object> context = UtilMisc.toMap(
                    "emailAddress", emailAddress,
                    "firstName", firstName,
                    "lastName", lastName,
                    "userLogin", userLogin
            );

            Map<String, Object> result = dispatcher.runSync("createCustomer", context);

            if (ServiceUtil.isError(result)) {
                String errorMessage = ServiceUtil.getErrorMessage(result);
                request.setAttribute("_ERROR_MESSAGE_", errorMessage);
                return "error";
            } else {
                String partyId = (String) result.get("partyId");
                String successMessage = (String) result.get("successMessage");

                Debug.logInfo("Customer created successfully with partyId: " + partyId, MODULE);
                request.setAttribute("partyId", partyId);
                request.setAttribute("_EVENT_MESSAGE_", successMessage);
                return "success";
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error creating customer: " + e.getMessage();
            Debug.logError(errMsg, MODULE);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
    }
}
