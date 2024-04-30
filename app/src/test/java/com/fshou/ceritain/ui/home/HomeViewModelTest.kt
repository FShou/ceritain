package com.fshou.ceritain.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.fshou.ceritain.DataDummy
import com.fshou.ceritain.MainDispatcherRule
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.getOrAwaitValue
import com.fshou.ceritain.ui.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()



    @Mock
    private lateinit var appRepository: AppRepository

    private lateinit var homeViewModel: HomeViewModel


    @Test
    fun `when Get Stories Should Not Null And Return Data`() = runTest {
        val dummyStories = DataDummy.generateStories()
        val data = StoryPagingSource.snapshot(dummyStories)
        val expectedStories =  MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(appRepository.getStories()).thenReturn(expectedStories)

        homeViewModel = HomeViewModel(appRepository)
        val actualStories = homeViewModel.getStories().getOrAwaitValue()


        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        val finalData = differ.snapshot()

        Assert.assertNotNull(finalData)
        Assert.assertEquals(dummyStories.size, finalData.size)
        Assert.assertEquals(dummyStories[0],finalData[0])

    }

    @Test
    fun `when Get Stories Empty Should Not return Data`() = runTest {
        val data = StoryPagingSource.snapshot(emptyList())
        val expectedStories =  MutableLiveData<PagingData<Story>>()
        expectedStories.value = data
        Mockito.`when`(appRepository.getStories()).thenReturn(expectedStories)

        homeViewModel = HomeViewModel(appRepository)
        val actualStories = homeViewModel.getStories().getOrAwaitValue()


        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        val finalData = differ.snapshot()


        Assert.assertEquals(0, finalData.size)
    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class StoryPagingSource : PagingSource<Int, Story>() {

        companion object {
            fun snapshot(items: List<Story>): PagingData<Story> = PagingData.from(items)
        }
        override fun getRefreshKey(state: PagingState<Int, Story>): Int = 0

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> =
            LoadResult.Page(
                emptyList(), 0, 1
            )
    }

}