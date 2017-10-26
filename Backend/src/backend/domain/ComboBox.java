package backend.domain;

public class ComboBox {

	private int id;
	private String code;
	private String valeur;
	private transient boolean statut;
	
	public ComboBox(int id, String code, String valeur) {
		this.code = code;
		this.valeur = valeur;
		this.statut = true;
	}

	public ComboBox(int id, String code, String valeur, boolean statut) {
		this.id = id;
		this.code = code;
		this.valeur = valeur;
		this.statut = statut;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValeur() {
		return valeur;
	}

	public void setValeur(String valeur) {
		this.valeur = valeur;
	}

	public int getId() {
		return id;
	}
	
	public boolean getStatut() {
		return statut;
	}
	
	public void desactive() {
		this.statut = false;
	}

	@Override
	public String toString() {
		return "ComboBox [id=" + id + ", code=" + code + ", valeur=" + valeur + ", statut=" + statut +"]";
	}
}
