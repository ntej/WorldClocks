package www.ntej.com.worldclocks;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by navatejareddy on 12/31/16.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "www.ntej.com.worldclocks.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider()
    {
        setupSuggestions(AUTHORITY,MODE);
    }
}
