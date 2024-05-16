package service;


import Entity.Don;
import cnx.MyConnections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DonService implements CrudService<Don> {
    private Connection cnx;

    public DonService() {
        cnx = MyConnections.getInstance().getCnx();
    }

    @Override
    public Don save(Don don) {
        String sql = "INSERT INTO don(type, montant, date_remise, campagne_id, Donateur) VALUES (?, ?, ?, ?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, don.getType());
            if (don.getMontant() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, don.getMontant());
            }

            ps.setString(3, don.getDate_remise());

            if (don.getCampagne_id() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, don.getCampagne_id());
            }
            // Handling nullable Integer for Donateur
            if (don.getDonateur() == null) {
                ps.setNull(5, Types.INTEGER); // Set the Donateur to NULL if it is null
            } else {
                ps.setInt(5, don.getDonateur()); // Set the Donateur normally if not null
            }
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        don.setId(generatedKeys.getInt(1));
                        return don;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Optional<Don> findById(int id) {
        String sql = "SELECT * FROM don WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Integer campagneId = (Integer) rs.getObject("campagne_id"); // Utilisez getObject et castez
                Integer montant = (Integer) rs.getObject("montant"); // Pareil pour montant si c'est nécessaire

                Don don = new Don(
                        rs.getInt("id"),
                        rs.getString("type"),
                        montant, // Plus besoin de .intValue(), montant est déjà un Integer
                        rs.getString("date_remise"),
                        campagneId // Plus besoin de .intValue(), campagneId est déjà un Integer
                );
                return Optional.of(don);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public List<Don> findAll() {
        List<Don> dons = new ArrayList<>();
        String sql = "SELECT * FROM don";
        try (Statement s = cnx.createStatement();
             ResultSet rs = s.executeQuery(sql)) {

            while (rs.next()) {
                Don don = new Don(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getObject("montant") == null ? null : rs.getInt("montant"),
                        rs.getString("date_remise"),
                        rs.getObject("campagne_id") == null ? null : rs.getInt("campagne_id"),
                        rs.getObject("Donateur") == null ? null : rs.getInt("Donateur")
                );
                dons.add(don);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dons;
    }

    @Override
    public Don update(Don don) {
        String sql = "UPDATE don SET type = ?, montant = ?, date_remise = ?, campagne_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, don.getType());

            if (don.getMontant() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, don.getMontant());
            }

            ps.setString(3, don.getDate_remise());

            if (don.getCampagne_id() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, don.getCampagne_id());
            }

            ps.setInt(5, don.getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return don;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM don WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new RuntimeException("Deletion failed; no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error during deletion", e);
        }
    }




}
