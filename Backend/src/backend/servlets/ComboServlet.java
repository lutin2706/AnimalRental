package backend.servlets;

import static java.util.stream.Collectors.joining;
import static backend.util.Utils.*;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import backend.domain.ComboBox;
import backend.exception.ImpossibleUpdateException;
import backend.services.DBService;

@SuppressWarnings("serial")
public class ComboServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static DBService dbs = new DBService();
	private static Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String token = request.getHeader("Api-Token");
			
			if (token == null) {
				response.sendError(401, PAS_DE_TOKEN);
				return;
			}
			
			String comboJson = request.getReader().lines().collect(joining());
	
			ComboBox comboBox = gson.fromJson(comboJson, ComboBox.class);
			comboBox = dbs.insertComboBox(comboBox);
	
			response.setContentType("Application/json");
			response.getWriter().append(gson.toJson(comboBox)).flush();
		} catch (JsonSyntaxException e) {
			response.sendError(400, ERREUR_SYNTAXE);
		} catch (NullPointerException e1) {
			response.sendError(400, ELEMENT_MANQUANT);
		}
		prepareResponse(response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);

		String[] requestURI = request.getRequestURI().split("/");
		String comboJson = null;

		try {
			// If no parameter after "/" => Get the whole list of comboboxes
			if (requestURI.length == 2) {

				List<ComboBox> comboList = dbs.getComboList();

				comboJson = gson.toJson(comboList);

			} else if (requestURI.length == 4) { // If one parameter after the "/" => Get the whole list of comboboxes for 1 code
				String comboCode = requestURI[3];
				System.out.println("Combo code : " + comboCode);

				List<ComboBox> comboList = dbs.getComboList(comboCode);
				comboJson = gson.toJson(comboList);
			} else { // Else, there is the id of the combo to send

				int comboId = Integer.valueOf(request.getRequestURI().split("/")[2]);

				ComboBox comboBox = dbs.getComboBox(comboId);
				comboJson = gson.toJson(comboBox);
			}
			prepareResponse(response);
		} catch (ArrayIndexOutOfBoundsException e) {
			response.sendError(400, ERREUR_SYNTAXE);
		}
		response.setContentType("Application/json");
		response.getWriter().append(comboJson).flush();
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String token = request.getHeader("Api-Token");
			
			if (token == null) {
				response.sendError(401, PAS_DE_TOKEN);
				return;
			}
				
			int comboId = Integer.valueOf(request.getRequestURI().split("/")[2]);
			System.out.println("Demande de modification du combobox " + comboId);
	
			String comboJson = request.getReader().lines().collect(joining());
	
			ComboBox comboBox = gson.fromJson(comboJson, ComboBox.class);
	
			if (comboBox.getId() != 0 && comboBox.getId() != comboId) {
				response.sendError(400, "L'animal id ne correspond pas à l'animal du body");
				return;
			}
			dbs.updateComboBox(comboId, comboBox);
	
			response.setStatus(200, "ComboBox " + comboId + " mis à jour");
		} catch (NullPointerException e) {
			response.sendError(404, INEXISTANT);
		} catch (ArrayIndexOutOfBoundsException e1) {
			response.sendError(400, ERREUR_SYNTAXE);
		}
		prepareResponse(response);
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String token = request.getHeader("Api-Token");
			
			if (token == null) {
				response.sendError(401, PAS_DE_TOKEN);
				return;
			}
				
			int comboId = Integer.valueOf(request.getRequestURI().split("/")[2]);
			System.out.println("Demande de suppression de la ComboBox " + comboId);

			dbs.deleteComboBox(comboId);

			response.setStatus(200, "ComboBox " + comboId + " supprimé de la table");
		} catch (NullPointerException e) {
			response.sendError(404, INEXISTANT);
		} catch (ImpossibleUpdateException e1) {
			response.sendError(400, e1.getMessage());
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e2) {
			response.sendError(400, ERREUR_SYNTAXE);
		}
		prepareResponse(response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		prepareResponse(response);
	}	

}