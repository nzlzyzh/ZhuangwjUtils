package com.zhy.adapter.recyclerview.wrapper;

import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.adapter.recyclerview.utils.WrapperUtils;
import com.zwj.autolibrary.R;


/**
 * Created by zhy on 16/6/23.
 */
public class LoadMoreWrapper<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int ITEM_TYPE_LOAD_MORE = Integer.MAX_VALUE - 2;

    private RecyclerView.Adapter mInnerAdapter;
    private View mLoadMoreView;
    private int mLoadMoreLayoutId = R.layout.layout_default_loading;
    private boolean isUseDefaultLoading = true;    // 是否是使用默认的加载布局

    private LoadStatus loadStatus = LoadStatus.LOAD_NORMAL;
    private int bgColorId = Color.WHITE;

    /**
     * 加载的状态
     */
    public static enum LoadStatus {
        /**
         * 全部加载完成(没有数据了)
         */
        LOAD_ALL,

        /**
         * 正在加载中
         */
        LOADING,

        /**
         * 处于加载完成，可以继续加载的正常状态(但并不代表后台没有数据了)
         */
        LOAD_NORMAL,

        /**
         * 空数据
         */
        LOAD_EMPTY,

        /**
         * 加载错误
         */
        LOAD_ERROR
    }

    public LoadMoreWrapper(RecyclerView.Adapter adapter) {
        mInnerAdapter = adapter;
    }

    private boolean hasLoadMore() {
        return mLoadMoreView != null || mLoadMoreLayoutId != 0;
    }


    private boolean isShowLoadMore(int position) {
        return hasLoadMore() && (position >= mInnerAdapter.getItemCount());
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowLoadMore(position)) {
            return ITEM_TYPE_LOAD_MORE;
        }
        return mInnerAdapter.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_LOAD_MORE) {
            ViewHolder holder;
            if (mLoadMoreView != null) {
                holder = ViewHolder.createViewHolder(parent.getContext(), mLoadMoreView);
            } else {
                holder = ViewHolder.createViewHolder(parent.getContext(), parent, mLoadMoreLayoutId);
            }
            return holder;
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    private void setLoadMoreContent(ViewHolder holder) {
        if (isUseDefaultLoading) {

            if (loadStatus == LoadStatus.LOAD_ALL) {
                holder.setVisibility(R.id.progress_wheel, false);
                holder.setText(R.id.tv, "已经没有数据了！");
                holder.getConvertView().setBackgroundColor(bgColorId);
            } else if (loadStatus == LoadStatus.LOAD_EMPTY) {
                holder.setVisibility(R.id.progress_wheel, false);
                holder.setText(R.id.tv, "");
                // TODO
                holder.getConvertView().setBackgroundColor(Color.TRANSPARENT);
            } else if (loadStatus == LoadStatus.LOAD_ERROR) {
                holder.setVisibility(R.id.progress_wheel, false);
                holder.setText(R.id.tv, "加载失败，点击重新加载");
                View itemView = holder.getConvertView();
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 第一次设置Listener之后Listener会一直存在，必须判断
                        if(loadStatus == LoadStatus.LOAD_ERROR) {
                            loadStatus = LoadStatus.LOAD_NORMAL;
                            // 刷新最后一个
                            notifyItemChanged(mInnerAdapter.getItemCount());
                            onLoadMoreRequested();
                        }
                    }
                });
                holder.getConvertView().setBackgroundColor(bgColorId);
            } else {
                holder.setVisibility(R.id.progress_wheel, true);
                holder.setText(R.id.tv, "努力加载中...");
                holder.getConvertView().setBackgroundColor(bgColorId);
            }
        }else {
            // TODO 提供回调接口，供使用者自己设置

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isShowLoadMore(position)) {
            if (holder instanceof ViewHolder) {
                setLoadMoreContent((ViewHolder) holder);
            }

            onLoadMoreRequested();
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position);
    }

    private void onLoadMoreRequested() {
        if (mOnLoadMoreListener != null && loadStatus == LoadStatus.LOAD_NORMAL) {
            loadStatus = LoadStatus.LOADING;
            mOnLoadMoreListener.onLoadMoreRequested();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback() {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position) {
                if (isShowLoadMore(position)) {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null) {
                    return oldLookup.getSpanSize(position);
                }
                return 1;
            }
        });
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        mInnerAdapter.onViewAttachedToWindow(holder);

        if (isShowLoadMore(holder.getLayoutPosition())) {
            setFullSpan(holder);
        }
    }

    private void setFullSpan(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;

            p.setFullSpan(true);
        }
    }

    @Override
    public int getItemCount() {
        return  mInnerAdapter.getItemCount() + (hasLoadMore() ? 1 : 0);
    }


    public interface OnLoadMoreListener {
        void onLoadMoreRequested();
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreWrapper setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (loadMoreListener != null) {
            mOnLoadMoreListener = loadMoreListener;
        }
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(View loadMoreView) {
        mLoadMoreView = loadMoreView;
        notUseDefaultLoading();
        return this;
    }

    public LoadMoreWrapper setLoadMoreView(int layoutId) {
        mLoadMoreLayoutId = layoutId;
        notUseDefaultLoading();
        return this;
    }

    /**
     * 基本布局不变，其他一些细微的调整可调用该方法（如padding的调整）
     * @param layoutId
     * @return
     */
    public LoadMoreWrapper setLoadMoreViewWithDefalut(int layoutId) {
        mLoadMoreLayoutId = layoutId;
        return this;
    }

    private void notUseDefaultLoading() {
        isUseDefaultLoading = false;
    }

    /**
     * 加载完全部数据
     */
    public void setLoadAll() {
        loadStatus = LoadStatus.LOAD_ALL;
    }

    public void setEmpty() {
        loadStatus = LoadStatus.LOAD_EMPTY;
    }

    public void reset() {
        loadStatus = loadStatus.LOAD_NORMAL;
    }

    public void setLoadError() {
        loadStatus = loadStatus.LOAD_ERROR;
        // 刷新最后一个
        notifyItemChanged(mInnerAdapter.getItemCount());
    }

    /**
     * 数据添加完后才能重置状态
     */
    public void onFinish() {
        if (loadStatus == LoadStatus.LOADING) {
            loadStatus = loadStatus.LOAD_NORMAL;
        }
    }

    public void setLoadStatus(LoadStatus loadStatus) {
        this.loadStatus = loadStatus;
    }

    public LoadStatus getLoadStatus() {
        return loadStatus;
    }

    public int getBgColorId() {
        return bgColorId;
    }

    public void setBgColorId(int bgColorId) {
        this.bgColorId = bgColorId;
    }
}
