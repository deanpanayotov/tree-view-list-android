package pl.polidea.treeview.demo;

import java.util.Arrays;
import java.util.Set;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.AbstractTreeViewAdapterSimple;
import pl.polidea.treeview.R;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is a very simple adapter that provides very basic tree view with simple item description.
 * 
 */
class SimpleStandardAdapter extends AbstractTreeViewAdapterSimple<Long> {

    private final Set<Long> selected;

    public SimpleStandardAdapter(final TreeViewListDemo treeViewListDemo,
            final Set<Long> selected,
            final TreeStateManager<Long> treeStateManager,
            final int numberOfLevels) {
        super(treeViewListDemo, treeStateManager, numberOfLevels);
        this.selected = selected;
    }

    private String getDescription(final long id) {
        final Integer[] hierarchy = getManager().getHierarchyDescription(id);
        return "Node " + id + Arrays.asList(hierarchy);
    }

    @Override
    public View getNewChildView(final TreeNodeInfo<Long> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) getActivity()
                .getLayoutInflater().inflate(R.layout.demo_list_item, null);
        return updateView(viewLayout, treeNodeInfo);
    }

    @Override
    public LinearLayout updateView(final View view,
            final TreeNodeInfo<Long> treeNodeInfo) {
        final LinearLayout viewLayout = (LinearLayout) view;
        final TextView descriptionView = (TextView) viewLayout
                .findViewById(R.id.demo_list_item_description);
        final TextView levelView = (TextView) viewLayout
                .findViewById(R.id.demo_list_item_level);
        descriptionView.setText(getDescription(treeNodeInfo.getId()));
        levelView.setText(Integer.toString(treeNodeInfo.getLevel()));
        return viewLayout;
    }

    @Override
    public void handleItemClick(final View view, final Object id) {
        final Long longId = (Long) id;
        final TreeNodeInfo<Long> info = getManager().getNodeInfo(longId);
        if (info.isWithChildren()) {
            super.handleItemClick(view, id);
        }
    }

    @Override
    public long getItemId(final int position) {
        return getTreeId(position);
    }
}