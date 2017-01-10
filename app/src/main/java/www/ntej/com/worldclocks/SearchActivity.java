package www.ntej.com.worldclocks;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ntej.www.TimeAndDateGenerator;

import java.util.ArrayList;

import other.CommonFunctionalityClass;
import utilities.SearchableAdapter;

public class SearchActivity extends AppCompatActivity {

    Toolbar toolbar; //for search toolbar

    ListView searchListView;

    ArrayList<String> timeArrayList = new ArrayList<>();

    SearchableAdapter searchableAdapter;

    SearchRecentSuggestions suggestions;

    CommonFunctionalityClass commonFunctionalityClass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        commonFunctionalityClass = new CommonFunctionalityClass(this);

        searchListView = (ListView)findViewById(R.id.timeZonesListViewSearch);

        timeArrayList = TimeAndDateGenerator.getAllZoneIdsWithTimeAndDate();

        searchableAdapter = new SearchableAdapter(this,R.layout.zones_list_view,timeArrayList);
        searchListView.setAdapter(searchableAdapter);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

         suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY,MySuggestionProvider.MODE);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {



        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //Saving the search history
            suggestions.saveRecentQuery(query,null);

            searchResultViewer(query);

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.tool_bar_menu_search, menu);

        //Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.info:

                commonFunctionalityClass.ShowAboutDialog();

                return true;
        }
        return true;
    }

    public void searchResultViewer(String query)
    {
        SearchActivity.this.searchableAdapter.getFilter().filter(query);
    }

}
