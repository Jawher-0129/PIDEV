package Entity;

public class Don {
    private int id;
    private String type;
    private Integer montant;
    private String date_remise;
    private Integer campagne_id; // Clé étrangère pour la relation ManyToOne avec Campagne
    private  Integer Donateur;
    // Constructeur avec ID, utilisé lorsque l'on récupère un Don de la base de données
    public Don(int id, String type, int montant, String date_remise, int campagne_id) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.date_remise = date_remise;
        this.campagne_id = campagne_id;
    }
    public Don(int id, String type, int montant, String date_remise, int campagne_id,int Donateur) {
        this.id = id;
        this.type = type;
        this.montant = montant;
        this.date_remise = date_remise;
        this.campagne_id = campagne_id;
        this.Donateur = Donateur;
    }

    public Integer getDonateur() {
        return Donateur;
    }

    public Don(Integer campagne_id, String date_remise, Integer donateur, Integer montant, String type) {
        this.campagne_id = campagne_id;
        this.date_remise = date_remise;
        Donateur = donateur;
        this.montant = montant;
        this.type = type;
    }


    public void setDonateur(Integer donateur) {
        Donateur = donateur;
    }

    // Constructeur sans ID, utilisé pour créer un nouveau Don avant insertion dans la base de données
    public Don(String type, Integer montant, String date_remise, Integer campagne_id) {
        this.type = type;
        this.montant = montant;
        this.date_remise = date_remise;
        this.campagne_id = campagne_id;
    }

    public Don(int id, String type, Integer montant, String date_remise, Integer campagne_id) {
        this.id = id;
        this.type = type;
        this.montant = montant; // Ici montant est un Integer, peut être null
        this.date_remise = date_remise;
        this.campagne_id = campagne_id; // Ici campagne_id est un Integer, peut être null
    }
    public Don(int id, String type, Integer montant, String date_remise, Integer campagne_id,Integer Donateur) {
        this.id = id;
        this.type = type;
        this.montant = montant; // Ici montant est un Integer, peut être null
        this.date_remise = date_remise;
        this.campagne_id = campagne_id; // Ici campagne_id est un Integer, peut être null
        this.Donateur = Donateur;
    }



    //constructeur pour ajout de nouveau Don sans forcement ajouter un id campagne
    public Don( String type, Integer montant, String date_remise) {

        this.type = type;
        this.montant = montant;
        this.date_remise = date_remise;
    }
    //constructeur pour ajout de nouveau Don sans forcement ajouter un montant & campagne_id
    public Don( String type,  String date_remise) {
        this.type = type;
        this.date_remise = date_remise;
    }

    //constructeur pour ajout de nouveau Don sans forcement ajouter un montant
    public Don( String type,  String date_remise , Integer campagne_id) {
        this.type = type;
        this.date_remise = date_remise;
        this.campagne_id=campagne_id;
    }


    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getMontant() {
        return montant;
    }

    public void setMontant(Integer montant) {
        this.montant = montant;
    }

    public String getDate_remise() {
        return date_remise;
    }

    public void setDate_remise(String date_remise) {
        this.date_remise = date_remise;
    }

    public Integer getCampagne_id() {
        return campagne_id;
    }

    public void setCampagne_id(Integer campagne_id) {
        this.campagne_id = campagne_id;
    }

    // Méthode toString pour la représentation de l'objet en chaîne de caractères
    @Override
    public String toString() {
        return "Don{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", montant=" + montant +
                ", date_remise='" + date_remise + '\'' +
                ", campagne_id=" + campagne_id +
                '}';
    }
}
