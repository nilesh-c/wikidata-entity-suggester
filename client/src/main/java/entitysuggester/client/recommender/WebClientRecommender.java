package entitysuggester.client.recommender;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
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
public class WebClientRecommender extends AbstractClientRecommender {

    public WebClientRecommender(MyrrixClientConfiguration myrrixClientConfiguration) {
        super(myrrixClientConfiguration);
    }

    @Override
    public List<TranslatedRecommendedItem> recommend(String idListFile, String recommendTo, String recommendType, int howMany) throws TasteException {
        clientRecommender.addItemIDs(new File(idListFile));
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommend(recommendTo, howMany, false, new String[]{recommendType});
        return recommendations;
    }

    @Override
    public List<TranslatedRecommendedItem> recommendAnonymous(String idListFile, String recommendType, int howMany, String[] list) throws TasteException {
        clientRecommender.addItemIDs(new File(idListFile));
        float[] values = new float[list.length];
        Arrays.fill(values, 30);
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(list, values, howMany, new String[]{recommendType}, "testID");
        return recommendations;
    }

    public List<TranslatedRecommendedItem> recommendAnonymous(URI idListFile, String recommendType, int howMany, String[] list) throws TasteException {
        clientRecommender.addItemIDs(new File(idListFile));
        float[] values = new float[list.length];
        Arrays.fill(values, 30);
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(list, howMany);
        return recommendations;
    }

    @Override
    public void ingest(Reader csvReader) throws TasteException {
        clientRecommender.ingest(csvReader);
    }
}
