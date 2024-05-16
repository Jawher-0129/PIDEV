// package service;
package service;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cnx.MyConnections;

public class StatisticsService {
    private Connection cnx;

    public StatisticsService() {
        this.cnx = MyConnections.getInstance().getCnx();
    }

    public List<String> retrieveDonorEmails() {
        List<String> emails = new ArrayList<>();

        String sql = "SELECT email FROM user1 WHERE roles = '[\"ROLE_DONATEUR\"]' ";

        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String email = rs.getString("email");
                emails.add(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    public Map<Boolean, Integer> countDonationsWithWithoutCampaign() {
        Map<Boolean, Integer> results = new HashMap<>();
        String sqlWithCampaign = "SELECT COUNT(*) FROM don WHERE campagne_id IS NOT NULL";
        String sqlWithoutCampaign = "SELECT COUNT(*) FROM don WHERE campagne_id IS NULL";

        try (Statement stmt = cnx.createStatement()) {
            ResultSet rs = stmt.executeQuery(sqlWithCampaign);
            if (rs.next()) {
                results.put(true, rs.getInt(1)); // Donations with campaign
            }

            rs = stmt.executeQuery(sqlWithoutCampaign);
            if (rs.next()) {
                results.put(false, rs.getInt(1)); // Donations without campaign
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }


    public Map<String, Integer> countDonationsPerCampaign() {
        // Cette requête SQL joint les tables campagne et don et compte les dons pour chaque campagne, y compris celles sans dons
        String sql = "SELECT c.id, COALESCE(COUNT(d.id), 0) as donation_count " +
                "FROM campagne c LEFT JOIN don d ON c.id = d.campagne_id " +
                "GROUP BY c.id";

        Map<String, Integer> result = new HashMap<>();
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("id"), rs.getInt("donation_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Integer> countDonationsPerCampaign2() {
        String sql = "SELECT c.id, c.titre as campaign_name, COALESCE(COUNT(d.id), 0) as donation_count " +
                "FROM campagne c LEFT JOIN don d ON c.id = d.campagne_id " +
                "GROUP BY c.id, c.titre";

        Map<String, Integer> result = new HashMap<>();
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String campaignDetails = rs.getString("campaign_name") + " - " + rs.getInt("donation_count") + " donations";
                result.put(campaignDetails, rs.getInt("donation_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    public Map<String, Integer> countDonationsPerMonth() {
        String sql = "SELECT DATE_FORMAT(date_remise, '%Y-%m') as month, COUNT(*) as donation_count " +
                "FROM don GROUP BY DATE_FORMAT(date_remise, '%Y-%m')";
        Map<String, Integer> result = new HashMap<>();
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("month"), rs.getInt("donation_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Map<String, Integer> countCampaignsPerMonth() {
        String sql = "SELECT DATE_FORMAT(date_debut, '%Y-%m') as month, COUNT(*) as campaign_count " +
                "FROM campagne GROUP BY DATE_FORMAT(date_debut, '%Y-%m')";
        Map<String, Integer> result = new HashMap<>();
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.put(rs.getString("month"), rs.getInt("campaign_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


}
