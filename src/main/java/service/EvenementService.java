package service;

import Entity.Evenement;
import cnx.MyConnections;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EvenementService implements IService<Evenement> {
    private Connection cnx;
    private PreparedStatement pst;
    private Statement ste;
    private ResultSet rs;
    private Set<Integer> usedActualiteIds;

    public EvenementService() {
        cnx = MyConnections.getInstance().getCnx();
        usedActualiteIds = new HashSet<>();
        loadUsedActualiteIds(); // Load existing used ids from the database
    }


    private void loadUsedActualiteIds() {
        String query = "SELECT DISTINCT actualite_id FROM evenement WHERE actualite_id IS NOT NULL";
        try {
            ste = cnx.createStatement();
            ResultSet rs = ste.executeQuery(query);
            while (rs.next()) {
                usedActualiteIds.add(rs.getInt("actualite_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Check if an actualiteId is already used
    private boolean isActualiteIdUsed(int actualiteId) {
        return usedActualiteIds.contains(actualiteId);
    }

    @Override
    public void add(Evenement evenement) {
        // Check if id_actualite is provided and not already used
        if (evenement.getId_actualite() != -1 && isActualiteIdUsed(evenement.getId_actualite())) {
            throw new IllegalArgumentException("id_actualite is already used.");
        }

        String sql = "INSERT INTO evenement (Titre, Date, Duree, lieu, Objectif, image, actualite_id,user_id) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
        try {
            pst = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, evenement.getTitre());
            pst.setDate(2, new java.sql.Date(evenement.getDate().getTime()));
            pst.setInt(3, evenement.getDuree());
            pst.setString(4, evenement.getLieu());
            pst.setString(5, evenement.getObjectif());
            pst.setString(6, evenement.getImage());

            // Check if id_actualite is provided, if not, set it to -1
            if (evenement.getId_actualite() != -1) {
                pst.setInt(7, evenement.getId_actualite());
                // Add the used actualiteId to the set
                usedActualiteIds.add(evenement.getId_actualite());
            } else {
                pst.setNull(7, Types.INTEGER);
            }
            pst.setInt(8, evenement.getId_user());

            pst.executeUpdate();
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                evenement.setId_evenement(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM evenement WHERE id_evenement = ?";
        try {
            pst = cnx.prepareStatement(sql);
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Evenement evenement, int id) {
        // Check if id_actualite is provided and not already used
        if (evenement.getId_actualite() != -1 && isActualiteIdUsedByOtherEvent(evenement.getId_evenement(), evenement.getId_actualite())) {
            throw new IllegalArgumentException("id_actualite is already used by another event.");
        }

        String sql = "UPDATE evenement SET Titre = ?, Date = ?, Duree = ?, lieu = ?, Objectif = ?, image = ?, actualite_id = ? WHERE id_evenement = ?";
        try {
            pst = cnx.prepareStatement(sql);
            pst.setString(1, evenement.getTitre());
            pst.setDate(2, new java.sql.Date(evenement.getDate().getTime()));
            pst.setInt(3, evenement.getDuree());
            pst.setString(4, evenement.getLieu());
            pst.setString(5, evenement.getObjectif());
            pst.setString(6, evenement.getImage());

            // Check if id_actualite is provided, if not, set it to -1
            if (evenement.getId_actualite() != -1) {
                pst.setInt(7, evenement.getId_actualite());
            } else {
                pst.setNull(7, Types.INTEGER);
            }

            pst.setInt(8, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isActualiteIdUsedByOtherEvent(int eventId, int actualiteId) {
        String sql = "SELECT COUNT(*) AS count FROM evenement WHERE id_evenement <> ? AND actualite_id = ?";
        try {
            pst = cnx.prepareStatement(sql);
            pst.setInt(1, eventId);
            pst.setInt(2, actualiteId);
            rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public List<Evenement> getAll() {
        List<Evenement> evenementList = new ArrayList<>();
        String sql = "SELECT * FROM evenement";
        try {
            pst = cnx.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id_evenement = rs.getInt("id_evenement");
                String titre = rs.getString("Titre");
                Date date = rs.getDate("Date");
                int duree = rs.getInt("Duree");
                String lieu = rs.getString("lieu");
                String objectif = rs.getString("Objectif");
                String image = rs.getString("image");
                int actualiteId = rs.getInt("actualite_id");

                Evenement evenement = new Evenement(id_evenement, titre, date, duree, lieu, objectif, image, actualiteId);
                evenementList.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return evenementList;
    }

    @Override
    public Evenement getById(int id) {
        String sql = "SELECT * FROM evenement WHERE id_evenement = ?";
        try {
            pst = cnx.prepareStatement(sql);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                int id_evenement = rs.getInt("id_evenement");
                String titre = rs.getString("Titre");
                Date date = rs.getDate("Date");
                int duree = rs.getInt("Duree");
                String lieu = rs.getString("lieu");
                String objectif = rs.getString("Objectif");
                String image = rs.getString("image");
                int actualiteId = rs.getInt("actualite_id");

                return new Evenement(id_evenement, titre, date, duree, lieu, objectif, image, actualiteId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getActualiteIdByTitle(String title) {
        String query = "SELECT id_actualite FROM actualite WHERE titre = ?";
        try {
            pst = cnx.prepareStatement(query);
            pst.setString(1, title);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_actualite");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // ya33tini -1 kn famech
    }
    public List<String> getAllActualiteTitles() {
        List<String> titles = new ArrayList<>();
        String query = "SELECT titre FROM actualite";
        try {
            ste = cnx.createStatement();
            ResultSet rs = ste.executeQuery(query);
            while (rs.next()) {
                String titre = rs.getString("titre");
                titles.add(titre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }
    public List<Evenement> getLatestEvents(int count) {
        List<Evenement> allEvents = getAll();

        // Sort the events by date in descending order
        Collections.sort(allEvents, Comparator.comparing(Evenement::getDate).reversed());

        // Take the first 'count' events
        return allEvents.subList(0, Math.min(count, allEvents.size()));
    }
    public int getTotalEvents() {
        String query = "SELECT COUNT(*) AS total FROM evenement";
        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAvailableEvents() {
        String query = "SELECT COUNT(*) AS total FROM evenement WHERE date >= CURRENT_DATE";
        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getDistinctDatesOfEvents() {
        List<String> dates = new ArrayList<>();
        String query = "SELECT DISTINCT DATE_FORMAT(date, '%Y-%m-%d') AS event_date FROM evenement";
        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                dates.add(resultSet.getString("event_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dates;
    }

    public int getEventCountByDate(String date) {
        String query = "SELECT COUNT(*) AS event_count FROM evenement WHERE DATE_FORMAT(date, '%Y-%m-%d') = ?";
        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("event_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Integer> getDistinctDurationsOfEvents() {
        List<Integer> durations = new ArrayList<>();
        String query = "SELECT DISTINCT duree FROM evenement";
        try {
            Statement statement = cnx.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                durations.add(resultSet.getInt("duree"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return durations;
    }

    public int getEventCountByDuration(int duration) {
        String query = "SELECT COUNT(*) AS event_count FROM evenement WHERE duree = ?";
        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setInt(1, duration);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("event_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<String> getEventNamesByDate(String date) {
        List<String> eventNames = new ArrayList<>();
        List<Evenement> events = getEventsByDate(date);
        for (Evenement event : events) {
            eventNames.add(event.getTitre());
        }
        return eventNames;
    }

    private List<Evenement> getEventsByDate(String date) {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement WHERE DATE_FORMAT(date, '%Y-%m-%d') = ?";
        try {
            PreparedStatement preparedStatement = cnx.prepareStatement(query);
            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id_evenement = resultSet.getInt("id_evenement");
                String titre = resultSet.getString("Titre");
                Date eventDate = resultSet.getDate("Date");
                int duree = resultSet.getInt("Duree");
                String lieu = resultSet.getString("lieu");
                String objectif = resultSet.getString("Objectif");
                String image = resultSet.getString("image");
                int actualiteId = resultSet.getInt("actualite_id");

                Evenement event = new Evenement(id_evenement, titre, eventDate, duree, lieu, objectif, image, actualiteId);
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }


}
