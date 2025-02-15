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

public class UpdateNewCustomer {

    public static final String MODULE = UpdateNewCustomer.class.getName();

    public static String UpdateNewCustomer(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");

        String emailAddress = request.getParameter("emailAddress");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String postalAddress = request.getParameter("postalAddress");
        String phoneNumber = request.getParameter("phoneNumber");

        if (UtilValidate.isEmpty(emailAddress)) {
            String errMsg = "Email Address is a required field.";
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error"; // Return error view
        }

        try {
            Debug.logInfo("=======Updating customer using updateNewCustomer service=========", MODULE);

            Map<String, Object> context = UtilMisc.toMap(
                    "emailAddress", emailAddress,
                    "firstName", firstName,
                    "lastName", lastName,
                    "postalAddress", postalAddress,
                    "phoneNumber", phoneNumber,
                    "userLogin", userLogin
            );

            Map<String, Object> result = dispatcher.runSync("updateCustomer", context);

            if (ServiceUtil.isError(result)) {
                String errorMessage = ServiceUtil.getErrorMessage(result);
                request.setAttribute("_ERROR_MESSAGE_", errorMessage);
                return "error";
            } else {
                String successMessage = (String) result.get("successMessage");
                request.setAttribute("_EVENT_MESSAGE_", successMessage);
                return "success";
            }
        } catch (GenericServiceException e) {
            String errMsg = "Error updating customer: " + e.getMessage();
            Debug.logError(errMsg, MODULE);
            request.setAttribute("_ERROR_MESSAGE_", errMsg);
            return "error";
        }
    }
}
