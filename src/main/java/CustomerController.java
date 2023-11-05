import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/api/v1/customers/*")
public class CustomerController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String res = CustomerUtils.getAllCustomers();
            out.print(String.format("{ \"code\": \"success\", \"message\": \"Customers data retrieved successfully\", \"data\": %s}", res));
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String idStr = pathParts[1];
                long customerId = 0;
                try {
                    customerId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                String res = CustomerUtils.getCustomer(customerId);
                out.print(String.format("{ \"code\": \"success\", \"message\": \"Customer data retrieved successfully\", \"data\": %s}", res));
            } else if (pathParts.length == 3 && pathParts[2].equals("contact-people")) {
                String idStr = pathParts[1];
                long customerId = 0;
                try {
                    customerId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                String res = CustomerUtils.getCustomerContactPeople(customerId);
                out.print(String.format("{ \"code\": \"success\", \"message\": \"Customer Contact People retrieved successfully\", \"data\": %s}", res));
            } else if (pathParts.length == 4 && pathParts[2].equals("contact-people")) {
                String idStr = pathParts[1];
                String contactIdStr = pathParts[3];
                long customerId = 0;
                long contactPersonId = 0;
                try {
                    customerId = Long.parseLong(idStr);
                    contactPersonId = Long.parseLong(contactIdStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                String res = CustomerUtils.getContactPerson(customerId, contactPersonId);
                out.print(String.format("{ \"code\": \"success\", \"message\": \"Customer Contact Person retrieved successfully\", \"data\": %s}", res));
            } else {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
            }
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonInput.append(line);
            }

            String jsonData = jsonInput.toString();

            boolean isSuccess = false;
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                isSuccess = CustomerUtils.addCustomer(jsonData);
                out.write("{ \"code\": \"success\", \"message\": \"Customer Added successfully\" }");
            } else {
                String[] pathParts = pathInfo.split("/");
                String idStr = pathParts[1];
                long customerId = 0;
                try {
                    customerId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                if (pathParts.length == 3 && pathParts[2].equals("contact-people")) {
                    if (isSuccess)
                        isSuccess = CustomerUtils.addContactPerson(customerId, jsonData);
                    out.write("{ \"code\": \"success\", \"message\": \"Customer Contact Person Added successfully\" }");
                } else {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
            }

            if (!isSuccess)
                out.write("{ \"code\": \"error\", \"message\": \"Internal Error\" }");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{ \"code\": \"error\", \"message\": \"Error processing JSON body\" }");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            StringBuilder jsonInput = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonInput.append(line);
            }

            String jsonData = jsonInput.toString();
            String pathInfo = request.getPathInfo();

            boolean isSuccess = false;

            if (pathInfo == null || pathInfo.equals("/")) {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                return;
            } else {
                String[] pathParts = pathInfo.split("/");
                String idStr = pathParts[1];
                long customerId = 0;
                try {
                    customerId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                if (pathParts.length == 2) {
                    isSuccess = CustomerUtils.updateCustomer(customerId, jsonData);
                    if (isSuccess)
                        out.write("{ \"code\": \"success\", \"message\": \"Customer Updated successfully\" }");
                } else if (pathParts.length == 3 && pathParts[2].equals("status")) {
                    isSuccess = CustomerUtils.setStatus(customerId, 1);
                    if (isSuccess)
                        out.write("{ \"code\": \"success\", \"message\": \"Customer status set to Active successfully\" }");
                } else {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
            }

            if (!isSuccess)
                out.write("{ \"code\": \"error\", \"message\": \"Internal Error\" }");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{ \"code\": \"error\", \"message\": \"Error processing JSON body\" }");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String pathInfo = request.getPathInfo();

        boolean isSuccess = false;

        if (pathInfo == null || pathInfo.equals("/")) {
            out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
            return;
        } else {
            String[] pathParts = pathInfo.split("/");
            String idStr = pathParts[1];
            long customerId = 0;
            try {
                customerId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                return;
            }
            if (pathParts.length == 2) {
                isSuccess = CustomerUtils.removeCustomer(customerId);
                if (isSuccess)
                    out.write("{ \"code\": \"success\", \"message\": \"Customer Deleted successfully\" }");
            } else if (pathParts.length == 3 && pathParts[2].equals("status")) {
                isSuccess = CustomerUtils.setStatus(customerId, 0);
                if (isSuccess)
                    out.write("{ \"code\": \"success\", \"message\": \"Customer status set to Inactive successfully\" }");
            } else if (pathParts.length == 4 && pathParts[2].equals("contact-people")) {
                String contactIdStr = pathParts[3];
                long contactPersonId = 0;
                try {
                    contactPersonId = Long.parseLong(contactIdStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                isSuccess = CustomerUtils.removeContactPerson(customerId, contactPersonId);
                if (isSuccess)
                    out.write("{ \"code\": \"success\", \"message\": \"Customer Contact Person Deleted successfully\" }");
            } else {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                return;
            }
        }

        if (!isSuccess)
            out.write("{ \"code\": \"error\", \"message\": \"Internal Error\" }");
    }
}