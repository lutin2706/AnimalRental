package backend.servlets;

import static backend.util.Utils.*;
import static java.util.stream.Collectors.joining;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import backend.domain.User;
import backend.services.DBService;
import backend.util.BCrypt;

@SuppressWarnings("serial")
public class UserServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static DBService dbs = new DBService();
	private static Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String userJson = request.getReader().lines().collect(joining());

			System.out.println("Received a new user :" + userJson);

			User user = gson.fromJson(userJson, User.class);
			if (dbs.getUser(user.getLogin()) != null) {
				response.sendError(400, USER_DEJA_EXISTANT);
			}
			
			// Hash a password for the first time
			String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

			user.setPassword(hashed);
			user = dbs.insertUser(user);
			
			if (user == null) {
				response.sendError(400, ERREUR_SYNTAXE);
			}

			response.setContentType("Application/json");
			response.getWriter().append(gson.toJson(user)).flush();
		} catch (JsonSyntaxException e) {
			response.sendError(400, ERREUR_SYNTAXE);
		} catch (NullPointerException e1) {
			response.sendError(400, ELEMENT_MANQUANT);
		}
		prepareResponse(response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		prepareResponse(response);
	}	

}