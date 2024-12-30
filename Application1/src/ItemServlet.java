import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");
            ResultSet resultSet = connection.createStatement().executeQuery("select * from item");

            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

            while (resultSet.next()) {
                String code = resultSet.getString("code");
                String description = resultSet.getString("description");
                double unitPrice = resultSet.getDouble("unitPrice");
                int qtyOnHand = Integer.parseInt(resultSet.getString("qtyOnHand"));

                JsonObjectBuilder item = Json.createObjectBuilder();
                item.add("code", code);
                item.add("description", description);
                item.add("unitPrice", unitPrice);
                item.add("qtyOnHand", qtyOnHand);
                jsonArrayBuilder.add(item);
            }

            resp.setContentType("application/json");
            resp.getWriter().write(jsonArrayBuilder.build().toString());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String description = req.getParameter("description");


        double unitPrice = 0;
        int qtyOnHand = 0;

        try {
            unitPrice = Double.parseDouble(req.getParameter("unitPrice"));
            qtyOnHand = Integer.parseInt(req.getParameter("qtyOnHand"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid number format for unitPrice or qtyOnHand\"}");
            return;
        }


        if (code == null || code.isEmpty() || description == null || description.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"code or description is null or empty\"}");
            return;
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");
            String sql = "INSERT INTO item (code, description, unitPrice, qtyOnHand) VALUES (?, ?, ?, ?)";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, description);
            preparedStatement.setDouble(3, unitPrice);
            preparedStatement.setInt(4, qtyOnHand);

            int i = preparedStatement.executeUpdate();
            if (i > 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\" : \"Item added successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\" : \"Failed to add item\"}");
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\" : \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            // Ensure the connection is closed
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String description = req.getParameter("description");

        double unitPrice = 0;
        int qtyOnHand = 0;

        try {
            unitPrice = Double.parseDouble(req.getParameter("unitPrice"));
            qtyOnHand = Integer.parseInt(req.getParameter("qtyOnHand"));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"Invalid number format for unitPrice or qtyOnHand\"}");
            return;
        }

        if (code == null || code.isEmpty() || description == null || description.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"code or description is null or empty\"}");
            return;
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");

            String sql = "UPDATE item SET description = ?, unitPrice = ?, qtyOnHand = ? WHERE code = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, description);
            preparedStatement.setDouble(2, unitPrice);
            preparedStatement.setInt(3, qtyOnHand);
            preparedStatement.setString(4, code);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\" : \"Item updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("{\"error\" : \"Failed to update item\"}");
            }

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\" : \"Database error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Item code is required.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM item WHERE code = ?")) {

                statement.setString(1, code);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Item deleted successfully.\"}");
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Item not found.");
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error executing SQL query.");
        }
    }


}
