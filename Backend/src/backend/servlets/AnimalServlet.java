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

import backend.domain.Animal;
import backend.exception.ImpossibleUpdateException;
import backend.exception.WrongTokenException;
import backend.services.DBService;

@SuppressWarnings("serial")
public class AnimalServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	private static DBService dbs = new DBService();
	private static Gson gson = Converters.registerLocalDate(new GsonBuilder()).create();

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		
		String[] requestURI = request.getRequestURI().split("/");
		String animalJson = null;

		// If no parameter after "/" => Get the whole list of animals
		if (requestURI.length == 2) {

			List<Animal> animals = dbs.getAnimals();

			animalJson = gson.toJson(animals);

		} else { // Else, there is the id of the animal to send
			int animalId = Integer.valueOf(request.getRequestURI().split("/")[2]);

			Animal animal = dbs.getAnimal(animalId);
			animalJson = gson.toJson(animal);
		}
		
		response.setContentType("Application/json");
		response.getWriter().append(animalJson).flush();
		prepareResponse(response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String token = request.getHeader("Api-Token");
			// check token
			// TODO : Need to use Utils.checkToken method, to investigate
			if (token == null) {
				response.sendError(401, PAS_DE_TOKEN);
				return;
			}
			
			String login = checkToken(token);
			
			String animalJson = request.getReader().lines().collect(joining());

			System.out.println("Received a new animal :" + animalJson);

			Animal animal = gson.fromJson(animalJson, Animal.class);
			System.out.println(animal);
			animal = dbs.insertAnimal(animal);

			response.setContentType("Application/json");
			response.getWriter().append(gson.toJson(animal)).flush();
		} catch (JsonSyntaxException e) {
			response.sendError(400, ERREUR_SYNTAXE);
		} catch (NullPointerException e1) {
			response.sendError(400, ELEMENT_MANQUANT);
		} catch (WrongTokenException e2) {
			response.sendError(401, e2.getMessage());
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
				
			int animalId = Integer.valueOf(request.getRequestURI().split("/")[2]);
			System.out.println("Demande de suppression de l'animal " + animalId);

			dbs.deleteAnimal(animalId);

			response.setStatus(200, "Animal " + animalId + " supprimé de la table");
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
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		printRequest(request);
		try {
			String token = request.getHeader("Api-Token");
			
			if (token == null) {
				response.sendError(401, PAS_DE_TOKEN);
				return;
			}
				
			int animalId = Integer.valueOf(request.getRequestURI().split("/")[2]);
			System.out.println("Demande de modification de l'animal " + animalId);

			String animalJson = request.getReader().lines().collect(joining());

			Animal animal = gson.fromJson(animalJson, Animal.class);

			if (animal.getId() != 0 && animal.getId() != animalId) {
				response.sendError(400, "L'animal id ne correspond pas à l'animal du body");
				return;
			}
			dbs.updateAnimal(animalId, animal);

			response.setStatus(200, "Animal " + animalId + " mis à jour");
		} catch (NullPointerException e) {
			response.sendError(404, INEXISTANT);
		} catch (ArrayIndexOutOfBoundsException e1) {
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