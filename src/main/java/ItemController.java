import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

@WebServlet("/api/v1/items/*")
public class ItemController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            out.print(ItemUtils.getAllItems());
        } else {
            String[] pathParts = pathInfo.split("/");
            if (pathParts.length == 2) {
                String idStr = pathParts[1];
                long itemId = 0;
                try {
                    itemId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                out.print(ItemUtils.getItem(itemId));
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

            boolean isSuccess = ItemUtils.addItem(jsonData);

            if (isSuccess)
                out.write("{ \"code\": \"success\", \"message\": \"Item data added successfully\" }");
            else
                out.write("{ \"code\": \"error\", \"message\": \"Internal Error\" }");
        } catch (Exception e) {
            e.printStackTrace();
            out.write("{ \"code\": \"error\", \"message\": \"Error processing JSON body\" }");
        }
        out.flush();
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
                long itemId = 0;
                try {
                    itemId = Long.parseLong(idStr);
                } catch (NumberFormatException e) {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                    return;
                }
                if (pathParts.length == 2) {
                    isSuccess = ItemUtils.updateItem(itemId, jsonData);
                    out.write("{ \"code\": \"success\", \"message\": \"Item Updated successfully\" }");
                } else if (pathParts.length == 3 && pathParts[2].equals("code")) {
                    isSuccess = ItemUtils.setStatus(itemId, 1);
                    out.write("{ \"code\": \"success\", \"message\": \"Item status set to Active successfully\" }");
                } else {
                    out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
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
            long itemId = 0;
            try {
                itemId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                return;
            }
            if (pathParts.length == 2) {
                isSuccess = ItemUtils.removeItem(itemId);
                out.write("{ \"code\": \"success\", \"message\": \"Item Deleted successfully\" }");
            } else if (pathParts.length == 3 && pathParts[2].equals("code")) {
                isSuccess = ItemUtils.setStatus(itemId, 0);
                out.write("{ \"code\": \"success\", \"message\": \"Item status set to Inactive successfully\" }");
            } else {
                out.write("{ \"code\": \"error\", \"message\": \"Invalid URL\" }");
                return;
            }
        }

        if (!isSuccess)
            out.write("{ \"code\": \"error\", \"message\": \"Internal Error\" }");
    }
}