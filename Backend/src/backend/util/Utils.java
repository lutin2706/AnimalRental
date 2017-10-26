package backend.util;

import java.io.UnsupportedEncodingException;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import backend.exception.WrongTokenException;

public class Utils {

	public static final String ERREUR_SYNTAXE = "Votre requête est toute pourrie. Veuillez ré-essayer.";
	public static final String ELEMENT_MANQUANT = "Il manque des éléments dans votre requête.";
	public static final String INEXISTANT = "Cet élément n'existe pas.";
	public static final String PAS_AUTORISE = "Mauvais login ou password. Allez bouler !";
	public static final String USER_DEJA_EXISTANT = "Ce user existe déjà." ;
	public static final String PAS_DE_TOKEN = "Vous avez oublié le token. Mettez-le ou loggez-vous !";
	
	// Pour hashage
	public static final String SECRET = "BouleDeGomme";
	public static final String ISSUER = "sev";

	private static final Map<String, String> clients;

	static {
		clients = new HashMap<String, String>();
		clients.put("10.10.9.114", "Michel");
		clients.put("10.10.10.123", "Gaël et Fayçal");
	}

	public static void printRequest(HttpServletRequest request) {
		//		try {
		StringBuilder sb = new StringBuilder("------------------------------------------------------------------------------------------\n");
		sb.append(LocalTime.now().toString() + "\nRequest received from: " + request.getRemoteAddr() + " (" + clients.get(request.getRemoteAddr()) + ")\n");
		sb.append(request.getMethod() + " " + request.getRequestURI());
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			sb.append(headerName + ": " + headerValue + "\n");
		}
		System.out.println(sb.toString());
	}

	private static void printResponse(HttpServletResponse response) {
		StringBuilder sb = new StringBuilder();
		sb.append("Response sent:\n");
		Collection<String> headerNames = response.getHeaderNames();
		for (String headerName : headerNames) {
			String headerValue = response.getHeader(headerName);
			sb.append(headerName + ": " + headerValue + "\n");
		}
		// TODO : Add response content
		sb.append("------------------------------------------------------------------------------------------\n");
		System.out.println(sb.toString());
	}

	public static void prepareResponse(HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
		response.setHeader("Access-Control-Allow-Headers", "content-type");
		response.setCharacterEncoding("UTF-8");

		printResponse(response);
	}

	public static String checkToken(String token) throws WrongTokenException {
		String login = null;
		try {
			// TODO Check expiration date
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer(ISSUER)
					.build(); //Reusable verifier instance
			DecodedJWT jwt = verifier.verify(token);
			if (!jwt.getIssuer().equals(ISSUER))
				throw new WrongTokenException("Ce token ne contient pas le bon Issuer");
			if (jwt.getClaim("login") == null)
				throw new WrongTokenException("Ce token ne contient pas de login");
			
			login = jwt.getClaim("login").asString();
		} catch (UnsupportedEncodingException exception){
			//UTF-8 encoding not supported
		} catch (JWTVerificationException exception){
			//Invalid signature/claims
		}
		return login;
	}
}
