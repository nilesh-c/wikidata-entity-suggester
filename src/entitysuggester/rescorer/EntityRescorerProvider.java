package entitysuggester.rescorer;

import java.io.IOException;
import java.util.concurrent.Callable;
import net.myrrix.common.MyrrixRecommender;
import net.myrrix.common.ReloadingReference;
import net.myrrix.online.AbstractRescorerProvider;
import org.apache.mahout.cf.taste.recommender.IDRescorer;

/**
 * @author Nilesh Chakraborty
 */
public class EntityRescorerProvider extends AbstractRescorerProvider {

    private ReloadingReference<ItemListRetriever> itemList;

    public EntityRescorerProvider() {
        this.itemList = new ReloadingReference<ItemListRetriever>(new Callable<ItemListRetriever>() {

            @Override
            public ItemListRetriever call() throws IOException {
                return new ItemListRetriever(System.getProperty("proplist"));
            }
        });
    }

    @Override
    public IDRescorer getRecommendRescorer(long[] userIDs, MyrrixRecommender recommender, String... args) {
        String type = null;
        IDRescorer idRescorer = null;

        if (args.length < 1) {
            type = "property";
        } else {
            type = args[0];
        }
        
        if (type.equals("property")) {
            idRescorer = new IDRescorer() {

                @Override
                public double rescore(long l, double d) {
                    return d;
                }

                @Override
                public boolean isFiltered(long l) {
                    String stringID = itemList.get().getStringIDFor(l);
                    if(stringID == null)
                        return false;
                    
                    if(stringID.contains("----")) // if it's a <property----value> then exclude/filter it
                    {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        } else if (type.equals("value")) {
            idRescorer = new IDRescorer() {

                @Override
                public double rescore(long l, double d) {
                    return d;
                }

                @Override
                public boolean isFiltered(long l) {
                    String stringID = itemList.get().getStringIDFor(l);
                    if(stringID == null)
                        return false;
                    
                    if (stringID.contains("----")) // if it's a <property----value> then include it
                    {
                        return false;
                    } else {
                        return true;
                    }
                }
            };
        } else {
            throw new RuntimeException("Parameter must be either property or value.");
        }

        return idRescorer;
    }

    @Override
    public IDRescorer getRecommendToAnonymousRescorer(long[] itemIDs, MyrrixRecommender recommender, String... args) {
        return getRecommendRescorer(new long[]{}, recommender, args);
    }
    
    
}
