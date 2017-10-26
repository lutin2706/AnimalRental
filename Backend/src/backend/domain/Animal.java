package backend.domain;

import java.time.LocalDate;

public class Animal {

	private int id;
	private int type;
	private String nom;
	private String description;
	private float cout;
	private String url;
	private LocalDate dateNaissance;
	private transient boolean statut;
	
	public Animal(int id, int type, String nom, String description, float cout, String url,
			LocalDate dateNaissance) {
		this.id = id;
		this.type = type;
		this.nom = nom;
		this.description = description;
		this.cout = cout;
		this.url = url;
		this.dateNaissance = dateNaissance;
		this.statut = true;
	}

	public Animal(int id, int type, String nom, String description, float cout, String url,
			LocalDate dateNaissance, boolean statut) {
		this.id = id;
		this.type = type;
		this.nom = nom;
		this.description = description;
		this.cout = cout;
		this.url = url;
		this.dateNaissance = dateNaissance;
		this.statut = statut;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCout(float cout) {
		this.cout = cout;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDateNaissance(LocalDate dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public String getNom() {
		return nom;
	}

	public String getDescription() {
		return description;
	}

	public float getCout() {
		return cout;
	}

	public String getUrl() {
		return url;
	}

	public LocalDate getDateNaissance() {
		return dateNaissance;
	}

	
	public boolean getStatut() {
		return statut;
	}

	
	public void desactive() {
		this.statut = false;
	}

	@Override
	public String toString() {
		return "Animal [id=" + id + ", type=" + type + ", nom=" + nom + ", description=" + description + ", cout="
				+ cout + ", url=" + url + ", dateNaissance=" + dateNaissance + ", statut =" + statut + "]";
	}
	
	
}
