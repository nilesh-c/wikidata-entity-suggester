package entitysuggester;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.myrrix.client.ClientRecommender;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatedRecommendedItem;
import net.myrrix.client.translating.TranslatingClientRecommender;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 *
 * @author Nilesh Chakraborty
 */
class CustomClientRecommender {

    private TranslatingClientRecommender clientRecommender;
    private String dbHost;
    private String dbName;
    private String dbUser;
    private String dbPassword;

    public CustomClientRecommender(MyrrixClientConfiguration myrrixClientConfiguration) {
        try {
            clientRecommender = new TranslatingClientRecommender(new ClientRecommender(myrrixClientConfiguration));
        } catch (IOException ex) {
            Logger.getLogger(CustomClientRecommender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setDatabaseInfo(String dbHost, String dbName, String dbUser, String dbPassword) {
        this.dbHost = dbHost;
        this.dbName = dbName;
        this.dbPassword = dbPassword;
        this.dbUser = dbUser;
    }

    public void recommend(String idListFile, String recommendTo, String recommendType, int howMany) {
        try {
            clientRecommender.addItemIDs(new File(idListFile));
            System.out.println("Recommending to " + recommendTo);
            List<TranslatedRecommendedItem> recommendations = clientRecommender.recommend(recommendTo, howMany, false, new String[]{recommendType});
            writeResults(recommendations, recommendType, dbHost, dbName, dbUser, dbPassword);
        } catch (TasteException ex) {
            Logger.getLogger(EntitySuggester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void recommendAnonymous(String idListFile, String recommendType, int howMany, String[] list) {
        try {
            clientRecommender.addItemIDs(new File(idListFile));
            float[] values = new float[list.length];
            Arrays.fill(values, 30);
            List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(list, values, howMany, new String[]{recommendType}, "testID");
            writeResults(recommendations, recommendType, dbHost, dbName, dbUser, dbPassword);
        } catch (TasteException ex) {
            Logger.getLogger(CustomClientRecommender.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ingest(String csvFile) {
        try {
            clientRecommender.ingest(new File(csvFile));
            System.out.println("Ingest successfully completed!");
        } catch (TasteException ex) {
            Logger.getLogger(EntitySuggester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeResults(List<TranslatedRecommendedItem> recommendations, String recommendType, String dbHost, String dbName, String dbUser, String dbPassword) {
        if (!recommendations.isEmpty()) {
            Connection connect = null;
            Statement statement = null;

            try {
                Class.forName("com.mysql.jdbc.Driver");
                connect = DriverManager.getConnection("jdbc:mysql://"
                        + dbHost + "/" + dbName + "?"
                        + "user=" + dbUser + "&password=" + dbPassword);

                if (recommendType.equals("property")) {
                    System.out.println("Suggested properties:");
                    statement = connect.createStatement();
                    String query = "SELECT pl_id, pl_text FROM plabel WHERE pl_lang='en' AND pl_id IN (";
                    for (TranslatedRecommendedItem recommendation : recommendations) {
                        query += recommendation.getItemID() + ",";
                    }
                    query = query.substring(0, query.length() - 1) + ")";

                    ResultSet resultSet = statement.executeQuery(query);
                    while (resultSet.next()) {
                        long id = resultSet.getLong("pl_id");
                        String text = resultSet.getString("pl_text");
                        System.out.println(id + " => " + text);
                    }
                    resultSet.close();
                } else {
                    System.out.println("Suggested property ---- value pairs:");
                    Map<Long, String> map = new HashMap<Long, String>();
                    statement = connect.createStatement();
                    String query = "SELECT pl_id, pl_text FROM plabel WHERE pl_lang='en' AND pl_id IN (";
                    for (TranslatedRecommendedItem recommendation : recommendations) {
                        query += recommendation.getItemID().split("----")[0] + ",";
                    }
                    query = query.substring(0, query.length() - 1) + ")";
                    ResultSet resultSet1 = statement.executeQuery(query);
                    while (resultSet1.next()) {
                        long propID = resultSet1.getLong("pl_id");
                        String propText = resultSet1.getString("pl_text");
                        map.put(propID, propText);
                    }
                    resultSet1.close();
                    PreparedStatement ps = connect.prepareStatement("SELECT l_id, l_text FROM label WHERE l_lang='en' AND l_id=?");
                    for (TranslatedRecommendedItem recommendation : recommendations) {
                        String[] pair = recommendation.getItemID().split("----");
                        if(pair.length < 2)
                            continue;
                        try {
                            long value = Long.parseLong(pair[1]);
                            ps.setLong(1, value);
                            resultSet1 = ps.executeQuery();
                            while(resultSet1.next()) {
                                long valID = resultSet1.getLong("l_id");
                                String valText = resultSet1.getString("l_text");
                                System.out.println(pair[0] + " => " + map.get(Long.parseLong(pair[0])) + " ---- " + valID + " => " + valText);
                            }
                        } catch(NumberFormatException ex) {
                            System.out.println(pair[0] + " => " + map.get(Long.parseLong(pair[0])) + " ---- " + pair[1]);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(connect != null)
                        connect.close();
                    if(statement != null)
                        statement.close();
                } catch (SQLException ex) {
                    Logger.getLogger(EntitySuggester.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            System.out.println("No suggestions available");
        }
    }
}
