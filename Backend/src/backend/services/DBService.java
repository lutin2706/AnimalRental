package backend.services;

import static java.util.Comparator.*;
import static java.util.stream.Collectors.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import backend.domain.Animal;
import backend.domain.ComboBox;
import backend.domain.User;
import backend.exception.ImpossibleUpdateException;

public class DBService {
	// Database stuff
	private String dbName = "sesirental";
	private Statement stmt = null;
	private static Connection conn = null;

	public DBService() {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection("jdbc:mysql://localhost/" + dbName + "?user=root&password=root");
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			}
		}	
	}

	public Animal insertAnimal(Animal animal) throws NullPointerException {
		PreparedStatement insertAnimal = null;
		ResultSet rs = null;
	
		String query = "INSERT INTO animal (type, nom, description, cout, url, dateNaissance) VALUES (?, ?, ?, ?, ?, ?)";
	
		try {
			insertAnimal = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			insertAnimal.setInt(1, animal.getType());
			insertAnimal.setString(2, animal.getNom());
			insertAnimal.setString(3, animal.getDescription());
			insertAnimal.setFloat(4, animal.getCout());
			insertAnimal.setString(5, animal.getUrl());
			insertAnimal.setDate(6, Date.valueOf(animal.getDateNaissance()));
	
			insertAnimal.execute();
	
			rs = insertAnimal.getGeneratedKeys();
			rs.next();
			int animalId = rs.getInt(1);
	
			animal = getAnimal(animalId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (insertAnimal != null) {
				try {
					insertAnimal.close();
				} catch (SQLException ignore) {}
			}
		}
		return animal;
	}

	public List<Animal> getAnimals() {
		ResultSet rs = null;
		List<Animal> animals = new ArrayList<>();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM animal");// A JOIN combobox C on A.type = C.id");
			while(rs.next()) {
				Animal animal = new Animal(
						rs.getInt("id"), rs.getInt("type"), rs.getString("nom"), 
						rs.getString("description"), rs.getFloat("cout"), rs.getString("url"), 
						rs.getDate("dateNaissance").toLocalDate(), rs.getBoolean("statut"));
				if (animal.getStatut())
					animals.add(animal);
			} 
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {}
			}
		}
		return animals.stream().sorted(comparing(Animal::getId)).collect(toList());
	}

	public Animal getAnimal(int animalId) throws NullPointerException {
		PreparedStatement getAnimal = null;
		ResultSet rs = null;
		String query = "SELECT * FROM animal WHERE id = ?";
		Animal animal = null;

		try {
			getAnimal = conn.prepareStatement(query);
			getAnimal.setInt(1, animalId);
			rs = getAnimal.executeQuery();
			rs.next();
			animal = new Animal(
					rs.getInt("id"), rs.getInt("type"), rs.getString("nom"), 
					rs.getString("description"), rs.getFloat("cout"), rs.getString("url"), 
					rs.getDate("dateNaissance").toLocalDate(), rs.getBoolean("statut"));
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (getAnimal != null) {
				try {
					getAnimal.close();
				} catch (SQLException ignore) {}
			}
		}
		return animal;
	}

	private void updateAnimal(Animal animal) {
			PreparedStatement updateAnimal = null;
			String query = "UPDATE animal SET type = ?, nom = ?, description = ?, cout = ?, url = ?, dateNaissance = ?, statut = ? WHERE id = ?";
	
			try {
				updateAnimal = conn.prepareStatement(query);
				updateAnimal.setInt(1, animal.getType());
				updateAnimal.setString(2, animal.getNom());
				updateAnimal.setString(3, animal.getDescription());
				updateAnimal.setFloat(4, animal.getCout());
				updateAnimal.setString(5, animal.getUrl());
				updateAnimal.setDate(6, Date.valueOf(animal.getDateNaissance()));
				updateAnimal.setBoolean(7, animal.getStatut());
				updateAnimal.setInt(8, animal.getId());
				
				int rows = updateAnimal.executeUpdate();
				System.out.println(rows + " ligne(s) a(ont) été modifiée(s)");
			} catch (SQLException ex) {
				System.out.println("SQLException: " + ex.getMessage());
				System.out.println("SQLState: " + ex.getSQLState());
				System.out.println("VendorError: " + ex.getErrorCode());
			} finally {
				if (updateAnimal != null) {
					try {
						updateAnimal.close();
					} catch (SQLException ignore) {}
				}
			}
		}

	public void updateAnimal(int animalId, Animal animal) {
		Animal animalFromDB = getAnimal(animalId);
		
		if (animal.getType() != 0)
			animalFromDB.setType(animal.getType());
		
		if (animal.getNom() != null)
			animalFromDB.setNom(animal.getNom());
		
		if (animal.getDescription() != null)
			animalFromDB.setDescription(animal.getDescription());
		
		if (animal.getCout() != 0)
			animalFromDB.setCout(animal.getCout());
		
		if (animal.getUrl() != null)
			animalFromDB.setUrl(animal.getUrl());
		
		if (animal.getDateNaissance() != null)
			animalFromDB.setDateNaissance(animal.getDateNaissance());

		updateAnimal(animalFromDB);
	}

	public void deleteAnimal(int animalId) throws NullPointerException, ImpossibleUpdateException {
		Animal animal = getAnimal(animalId);
		
		if (animal.getStatut()) {
			animal.desactive();
			updateAnimal(animal);
		} else
			throw new ImpossibleUpdateException("L'animal était déjà inactif");
	}

	public ComboBox insertComboBox(ComboBox comboBox) {
		PreparedStatement insertComboBox = null;
		ResultSet rs = null;
	
		String query = "INSERT INTO combobox (code, valeur) VALUES (?, ?)";
	
		try {
			insertComboBox = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			insertComboBox.setString(1, comboBox.getCode());
			insertComboBox.setString(2, comboBox.getValeur());

	
			insertComboBox.execute();
	
			rs = insertComboBox.getGeneratedKeys();
			rs.next();
			int comboId = rs.getInt(1);
	
			comboBox = getComboBox(comboId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (insertComboBox != null) {
				try {
					insertComboBox.close();
				} catch (SQLException ignore) {}
			}
		}
		return comboBox;
	}

	public List<ComboBox> getComboList() {
		ResultSet rs = null;
		List<ComboBox> comboList = new ArrayList<>();
	
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM combobox");
			while(rs.next()) {
				ComboBox comboBox = new ComboBox(rs.getInt("id"), rs.getString("code"), rs.getString("valeur"), rs.getBoolean("statut"));
				if (comboBox.getStatut())
					comboList.add(comboBox);
			} 
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ignore) {}
			}
		}
		return comboList.stream().sorted(comparing(ComboBox::getId)).collect(toList());
	}

	public List<ComboBox> getComboList(String comboCode) {
		PreparedStatement getComboList = null;
		ResultSet rs = null;
		String query = "SELECT * FROM combobox WHERE code = ?";
		List<ComboBox> comboList = new ArrayList<>();

		try {
			getComboList = conn.prepareStatement(query);
			getComboList.setString(1, comboCode);
			rs = getComboList.executeQuery();
			while(rs.next()) {
				ComboBox comboBox = new ComboBox(
						rs.getInt("id"), rs.getString("code"), rs.getString("valeur"), rs.getBoolean("statut"));
				if (comboBox.getStatut())
					comboList.add(comboBox);
			} 
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (getComboList != null) {
				try {
					getComboList.close();
				} catch (SQLException ignore) {}
			}
		}
		return comboList;
	}

	public ComboBox getComboBox(int comboId) {
		PreparedStatement getComboBox = null;
		ResultSet rs = null;
		String query = "SELECT * FROM combobox WHERE id = ?";
		ComboBox comboBox = null;

		try {
			getComboBox = conn.prepareStatement(query);
			getComboBox.setInt(1, comboId);
			rs = getComboBox.executeQuery();
			rs.next();
			comboBox = new ComboBox(
					rs.getInt("id"), rs.getString("code"), rs.getString("valeur"), rs.getBoolean("statut"));
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (getComboBox != null) {
				try {
					getComboBox.close();
				} catch (SQLException ignore) {}
			}
		}
		return comboBox;
	}

	public void updateComboBox(int comboId, ComboBox comboBox) {
		ComboBox comboFromDB = getComboBox(comboId);
		
		if (comboBox.getCode() != null)
			comboFromDB.setCode(comboBox.getCode());
		
		if (comboBox.getValeur() != null)
			comboFromDB.setValeur(comboBox.getValeur());
		
		updateComboBox(comboFromDB);
	}

	private void updateComboBox(ComboBox comboBox) {
		PreparedStatement updateComboBox = null;
		String query = "UPDATE combobox SET code = ?, valeur = ?, statut = ? WHERE id = ?";

		try {
			updateComboBox = conn.prepareStatement(query);
			updateComboBox.setString(1, comboBox.getCode());
			updateComboBox.setString(2, comboBox.getValeur());
			updateComboBox.setBoolean(3, comboBox.getStatut());
			updateComboBox.setInt(4, comboBox.getId());
			
			int rows = updateComboBox.executeUpdate();
			System.out.println(rows + " ligne(s) a(ont) été modifiée(s)");
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (updateComboBox != null) {
				try {
					updateComboBox.close();
				} catch (SQLException ignore) {}
			}
		}
	}

	public void deleteComboBox(int comboId) throws ImpossibleUpdateException {
		ComboBox comboBox = getComboBox(comboId);
		
		if (comboBox.getStatut()) {
			comboBox.desactive();
			updateComboBox(comboBox);
		} else
			throw new ImpossibleUpdateException("Le combobox était déjà inactif");
	}

	public User getUser(String login) {
		PreparedStatement getUser = null;
		ResultSet rs = null;
		String query = "SELECT * FROM user WHERE login = ?";
		User user = null;

		try {
			getUser = conn.prepareStatement(query);
			getUser.setString(1, login);
			rs = getUser.executeQuery();
			if (rs.next())
				user = new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"));				
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (getUser != null) {
				try {
					getUser.close();
				} catch (SQLException ignore) {}
			}
		}
		return user;
	}

	private User getUser(int userId) {
		PreparedStatement getUser = null;
		ResultSet rs = null;
		String query = "SELECT * FROM user WHERE id = ?";
		User user = null;
	
		try {
			getUser = conn.prepareStatement(query);
			getUser.setInt(1, userId);
			rs = getUser.executeQuery();
			rs.next();
			user = new User(rs.getInt("id"), rs.getString("login"), rs.getString("password"));
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (getUser != null) {
				try {
					getUser.close();
				} catch (SQLException ignore) {}
			}
		}
		return user;
	}

	public User insertUser(User user) {
		PreparedStatement insertUser = null;
		ResultSet rs = null;
	
		String query = "INSERT INTO user (login, password) VALUES (?, ?)";
	
		try {
			insertUser = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			insertUser.setString(1, user.getLogin());
			insertUser.setString(2, user.getPassword());
	
			insertUser.execute();
	
			rs = insertUser.getGeneratedKeys();
			rs.next();
			int userId = rs.getInt(1);
	
			user = getUser(userId);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ignore) {}
			}
			if (insertUser != null) {
				try {
					insertUser.close();
				} catch (SQLException ignore) {}
			}
		}
		return user;
	}

}
