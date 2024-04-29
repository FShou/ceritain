package com.fshou.ceritain.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.data.remote.retrofit.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, Story>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(token, position, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory as List<Story>,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }


    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}