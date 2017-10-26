package backend.servlets;

import static backend.util.Utils.*;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import backend.domain.Token;
import backend.domain.User;
import backend.services.DBService;
import backend.util.BCrypt;

@SuppressWarnings("serial")
public class LoginServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static DBService dbs = new DBService();
	private static Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);

		//TODO : Check URL (must be /user), and header Api-Token 
		// and send back the object User
		
		prepareResponse(response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		String[] requestURI = request.getRequestURI().split("/");
		try {
			if (!requestURI[1].equals("login")) {
				response.sendError(400, ERREUR_SYNTAXE);
				return;
			}

			String loginJson = request.getReader().lines().collect(joining());

			System.out.println("Trying to login :\n" + loginJson);

			User user = gson.fromJson(loginJson, User.class);
			User userDB = dbs.getUser(user.getLogin());

			if (userDB == null || !BCrypt.checkpw(user.getPassword(), userDB.getPassword())) {
				response.sendError(401, PAS_AUTORISE);
				return;
			}

			// Create token
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			String token = JWT.create().withClaim("login", user.getLogin())
					.withExpiresAt(Date.valueOf(LocalDate.now().plusDays(1L)))
					.withIssuer(ISSUER)
					.sign(algorithm);
			
			Token tokenUser = new Token(token, userDB);
			
			response.setContentType("Application/json");
			response.getWriter().append(gson.toJson(tokenUser)).flush();
		} catch (JsonSyntaxException e) {
			response.sendError(400, ERREUR_SYNTAXE);
		} catch (NullPointerException e1) {
			response.sendError(400, ELEMENT_MANQUANT);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			//UTF-8 encoding not supported
		}
		prepareResponse(response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		prepareResponse(response);
	}	

}