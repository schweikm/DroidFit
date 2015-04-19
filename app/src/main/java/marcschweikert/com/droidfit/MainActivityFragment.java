package marcschweikert.com.droidfit;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Created by Marc on 4/18/2015.
 */
public class MainActivityFragment extends ListFragment {
    private int itemSelected = -1;

    @Override
    public void onResume() {
        super.onResume();
        //TODO: get current user's activities
        setListAdapter(getAllActivities());
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO: save activities?
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("No Activites");
        setListAdapter(getAllActivities());
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_activity_menu, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        final MenuItem deleteMI = menu.findItem(R.id.menu_main_delete);
        final MenuItem editMI = menu.findItem(R.id.menu_main_edit);
        final MenuItem newMI = menu.findItem(R.id.menu_main_new);
        final MenuItem viewMI = menu.findItem(R.id.menu_main_view);

        if (itemSelected != -1) {
            deleteMI.setEnabled(true);
            editMI.setEnabled(true);
            viewMI.setEnabled(true);
            newMI.setEnabled(true);
        } else {
            newMI.setEnabled(true);
            deleteMI.setEnabled(false);
            editMI.setEnabled(false);
            viewMI.setEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.menu_main_new) {
            Log.i(getClass().getSimpleName(), "NEW ACTIVITY " + itemSelected);
        }
        if (item.getItemId() == R.id.menu_main_delete) {
            Log.i(getClass().getSimpleName(), "DELETE ACTIVITY " + itemSelected);
        }
        if (item.getItemId() == R.id.menu_main_edit) {
            Log.i(getClass().getSimpleName(), "EDIT ACTIVITY " + itemSelected);
        }
        if (item.getItemId() == R.id.menu_main_view) {
            Log.i(getClass().getSimpleName(), "VIEW ACTIVITY " + itemSelected);
        }

        return true;
    }

    @Override
    public void onListItemClick(final ListView l, final View v, final int position, final long id) {
        itemSelected = position;
        getActivity().invalidateOptionsMenu();
    }

    public ArrayAdapter<String> getAllActivities() {
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice);

        adapter.add("Running");
        adapter.add("Cycling");
        adapter.add("Swimming");

        return adapter;
    }
}
