package entitysuggester.client.recommender;

import java.io.File;
import java.io.Reader;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.client.translating.TranslatedRecommendedItem;
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
        List<TranslatedRecommendedItem> recommendations = clientRecommender.recommendToAnonymous(list, values, howMany, new String[]{recommendType}, "testID");
        return recommendations;
    }

    @Override
    public void ingest(Reader csvReader) throws TasteException {
        clientRecommender.ingest(csvReader);
    }
}
