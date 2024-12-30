import com.mysql.cj.protocol.Resultset;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {
    private final List<CustomerDTO> customerList = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Establish database connection

        customerList.clear();


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");
            ResultSet resultSet = connection.prepareStatement("select * from customer").executeQuery();

            //create json array
            JsonArrayBuilder allCustomer = Json.createArrayBuilder();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
               /* customerList.add(new CustomerDTO(id, name, address));*/

                JsonObjectBuilder customer = Json.createObjectBuilder();
                customer.add("id", id);
                customer.add("name", name);
                customer.add("address", address);
                allCustomer.add(customer);

            }

            resp.setContentType("application/json");
            resp.getWriter().write(allCustomer.build().toString());

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");

        if (id == null || id.isEmpty() || name == null || name.isEmpty() || address == null || address.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"id, name, or address is null or empty\"}");
            return;
        }


        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123")) {
            String sql = "INSERT INTO customer (id, name, address) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, address);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\" : \"Customer added successfully\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    resp.getWriter().write("{\"error\" : \"Failed to add customer\"}");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\" : \"Database error occurred\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");


        if (id == null || id.isEmpty() || name == null || name.isEmpty() || address == null || address.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\" : \"id, name or address is null or empty\"}");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123")) {
            String updateQuery = "UPDATE customer SET name = ?, address = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setString(1, name);
                stmt.setString(2, address);
                stmt.setString(3, id);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated == 0) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\" : \"Customer with the given id not found\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\" : \"Customer updated successfully\"}");
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\" : \"Database error occurred\"}");
            e.printStackTrace();
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        if (id == null || id.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Customer ID is required.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/company", "root", "Ijse@123");
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM customer WHERE id = ?")) {

                statement.setString(1, id);

                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write("{\"message\": \"Customer deleted successfully.\"}");
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Customer not found.");
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
