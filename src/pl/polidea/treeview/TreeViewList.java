package pl.polidea.treeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Tree view, expandable multi-level.
 * 
 * <pre>
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_collapsible
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_indent_width
 * attr ref pl.polidea.treeview.R.styleable#TreeViewList_handle_trackball_press
 * </pre>
 */
public class TreeViewList extends ListView {
    private static final int DEFAULT_INDENT = 60; //TODO dp
    private int indentWidth = 0;
    private AbstractTreeViewAdapterSimple<?> treeAdapter;
    private boolean collapsible;
    private boolean handleTrackballPress;

    public TreeViewList(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.style.treeViewListStyle);
    }

    public TreeViewList(final Context context) {
        this(context, null);
    }

    public TreeViewList(final Context context, final AttributeSet attrs,
            final int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TreeViewList);
        indentWidth = typedArray.getDimensionPixelSize(
                R.styleable.TreeViewList_indent_width, DEFAULT_INDENT);  
        collapsible = typedArray.getBoolean(R.styleable.TreeViewList_collapsible, true);
        handleTrackballPress = typedArray.getBoolean(
                R.styleable.TreeViewList_handle_trackball_press, true);
        typedArray.recycle();
    }

    @Override
    public void setAdapter(final ListAdapter adapter) {
        if (!(adapter instanceof AbstractTreeViewAdapterSimple)) {
            throw new TreeConfigurationException(
                    "The adapter is not of TreeViewAdapter type");
        }
        treeAdapter = (AbstractTreeViewAdapterSimple<?>) adapter;
        syncAdapter();
        super.setAdapter(treeAdapter);
    }

    private void syncAdapter() {
        Log.d("test", "syncAdapter "+indentWidth);
        treeAdapter.setIndentWidth(indentWidth);
        treeAdapter.setCollapsible(collapsible);
        if (handleTrackballPress) {
            setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView< ? > parent,
                        final View view, final int position, final long id) {
                    treeAdapter.handleItemClick(view, view.getTag());
                }
            });
        } else {
            setOnClickListener(null);
        }

    }

    public void setIndentWidth(final int indentWidth) {
        Log.d("test", "TreeViewList.setIndentWidth "+indentWidth);
        this.indentWidth = indentWidth;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setCollapsible(final boolean collapsible) {
        this.collapsible = collapsible;
        syncAdapter();
        treeAdapter.refresh();
    }

    public void setHandleTrackballPress(final boolean handleTrackballPress) {
        this.handleTrackballPress = handleTrackballPress;
        syncAdapter();
        treeAdapter.refresh();
    }

    public int getIndentWidth() {
        return indentWidth;
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public boolean isHandleTrackballPress() {
        return handleTrackballPress;
    }

}
