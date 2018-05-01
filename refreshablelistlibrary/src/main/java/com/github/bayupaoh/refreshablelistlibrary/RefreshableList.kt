package com.github.bayupaoh.refreshablelistlibrary

import com.github.bayupaoh.refreshablelistlibrary.R
import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.AttributeSet
import android.view.View
import com.jakewharton.rxbinding.support.v7.widget.scrollEvents
import kotlinx.android.synthetic.main.refreshable_list.view.*

class RefreshableList: SwipeRefreshLayout {
    constructor(context: Context?) : super(context!!) {
        inflateContentView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        inflateContentView()
    }

    private val layoutManager: LinearLayoutManager by lazy { LinearLayoutManager(context) }
    private val lastItemPosition: Int get() = recyclerViewResfrehable.adapter.itemCount - 1

    private var onLoadMoreListener: OnLoadMoreListener? = null

    fun showLoadingIndicator() {
        isRefreshing = true
    }

    fun hideLoadingIndicator() {
        isRefreshing = false
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    private fun inflateContentView() {
        View.inflate(context, R.layout.refreshable_list, this)
        setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        if (isInEditMode) return

        recyclerViewResfrehable.apply { layoutManager = this@RefreshableList.layoutManager }.setHasFixedSize(true)
        composeBinding()
    }

    fun setAsGrid(span: Int) {
        recyclerViewResfrehable.apply { layoutManager = GridLayoutManager(context, span) }.setHasFixedSize(true)

    }

    fun setAsList() {
        recyclerViewResfrehable.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun hasLastItemInListReached() = layoutManager.findLastVisibleItemPosition() == lastItemPosition


    private fun composeBinding() {
        recyclerViewResfrehable.scrollEvents()
                .filter {
                    val result = it.view().childCount > 0 &&
                            hasLastItemInListReached() &&
                            !isRefreshing
                    result
                }.subscribe({
                    onLoadMoreListener?.onLoadMore()
                }, {})


    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}