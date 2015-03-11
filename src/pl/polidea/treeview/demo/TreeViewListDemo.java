package pl.polidea.treeview.demo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.R;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

/**
 * Demo activity showing how the tree view can be used.
 * 
 */
public class TreeViewListDemo extends Activity {
    private enum AdapterType implements Serializable {
        SIMPLE,
        FANCY
    }
    
    private static final AdapterType DEMO_ADAPTER = AdapterType.SIMPLE;
    private static final boolean DEMO_COLLAPSIBLE = true;
    private static final int[] DEMO_NODES = new int[] { 0, 0, 1, 1, 1, 2, 2, 1,
        1, 2, 1, 0, 0, 0, 1, 2, 3, 2, 0, 0, 1, 2, 0, 1, 2, 0, 1 };
    private static final int LEVEL_NUMBER = 4;
    private static final Set<Long> DEMO_SELECTED = new HashSet<Long>();
    private static final String TAG = TreeViewListDemo.class.getSimpleName();
    
    private TreeViewList treeView;
    private TreeStateManager<Long> manager = null;
    private FancyColouredVariousSizesAdapter fancyAdapter;
    private SimpleStandardAdapter simpleAdapter;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            manager = new InMemoryTreeStateManager<Long>();
            final TreeBuilder<Long> treeBuilder = new TreeBuilder<Long>(manager);
            for (int i = 0; i < DEMO_NODES.length; i++) {
                treeBuilder.sequentiallyAddNextNode((long) i, DEMO_NODES[i]);
            }
            Log.d(TAG, manager.toString());
        } else {
            manager = (TreeStateManager<Long>) savedInstanceState
                    .getSerializable("treeManager");
            if (manager == null) {
                manager = new InMemoryTreeStateManager<Long>();
            }
        }
        setContentView(R.layout.main_demo);
        treeView = (TreeViewList) findViewById(R.id.mainTreeView);
        fancyAdapter = new FancyColouredVariousSizesAdapter(this, DEMO_SELECTED,
                manager, LEVEL_NUMBER);
        simpleAdapter = new SimpleStandardAdapter(this, DEMO_SELECTED, manager,
                LEVEL_NUMBER);
        setTreeAdapter(DEMO_ADAPTER);
        treeView.setCollapsible(DEMO_COLLAPSIBLE);
        registerForContextMenu(treeView);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("treeManager", manager);
        super.onSaveInstanceState(outState);
    }

    protected final void setTreeAdapter(final AdapterType newTreeType) {
        switch (newTreeType) {
        case SIMPLE:
            treeView.setAdapter(simpleAdapter);
            break;
        case FANCY:
            treeView.setAdapter(fancyAdapter);
            break;
        default:
            treeView.setAdapter(simpleAdapter);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        final MenuItem collapsibleMenu = menu
                .findItem(R.id.collapsible_menu_item);
        if (DEMO_COLLAPSIBLE) {
            collapsibleMenu.setTitle(R.string.collapsible_menu_disable);
            collapsibleMenu.setTitleCondensed(getResources().getString(
                    R.string.collapsible_condensed_disable));
        } else {
            collapsibleMenu.setTitle(R.string.collapsible_menu_enable);
            collapsibleMenu.setTitleCondensed(getResources().getString(
                    R.string.collapsible_condensed_enable));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.simple_menu_item) {
            setTreeAdapter(AdapterType.SIMPLE);
        } else if (item.getItemId() == R.id.fancy_menu_item) {
            setTreeAdapter(AdapterType.FANCY);
        } else if (item.getItemId() == R.id.collapsible_menu_item) {
            treeView.setCollapsible(!DEMO_COLLAPSIBLE); //TODO fix this
        } else if (item.getItemId() == R.id.expand_all_menu_item) {
            manager.expandEverythingBelow(null);
        } else if (item.getItemId() == R.id.collapse_all_menu_item) {
            manager.collapseChildren(null);
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v,
            final ContextMenuInfo menuInfo) {
        final AdapterContextMenuInfo adapterInfo = (AdapterContextMenuInfo) menuInfo;
        final long id = adapterInfo.id;
        final TreeNodeInfo<Long> info = manager.getNodeInfo(id);
        final MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
        if (info.isWithChildren()) {
            if (info.isExpanded()) {
                menu.findItem(R.id.context_menu_expand_item).setVisible(false);
                menu.findItem(R.id.context_menu_expand_all).setVisible(false);
            } else {
                menu.findItem(R.id.context_menu_collapse).setVisible(false);
            }
        } else {
            menu.findItem(R.id.context_menu_expand_item).setVisible(false);
            menu.findItem(R.id.context_menu_expand_all).setVisible(false);
            menu.findItem(R.id.context_menu_collapse).setVisible(false);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        final long id = info.id;
        if (item.getItemId() == R.id.context_menu_collapse) {
            manager.collapseChildren(id);
            return true;
        } else if (item.getItemId() == R.id.context_menu_expand_all) {
            manager.expandEverythingBelow(id);
            return true;
        } else if (item.getItemId() == R.id.context_menu_expand_item) {
            manager.expandDirectChildren(id);
            return true;
        } else if (item.getItemId() == R.id.context_menu_delete) {
            manager.removeNodeRecursively(id);
            return true;
        } else {
            return super.onContextItemSelected(item);
        }
    }
}