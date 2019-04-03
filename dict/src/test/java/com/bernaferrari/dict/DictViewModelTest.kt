package com.bernaferrari.dict

import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.bernaferrari.dict.data.DictDataSource
import com.bernaferrari.dict.data.GifItem
import com.bernaferrari.dict.main.DictState
import com.bernaferrari.dict.main.DictViewModel
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.ClassRule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Test the DictViewModel.
 */
class DictViewModelTest {

    companion object {
        @JvmField
        @ClassRule
        val mvrxRule = MvRxTestRule()
    }

    @Test
    fun testInitialState() {
        val initalState = DictState()
        val viewModel = DictViewModel(initalState, FakeDictRepository())

        withState(viewModel) { state ->
            assert(initalState == state)
        }
    }

    @Test
    fun testGetItems() {
        val initalState = DictState()
        val viewModel = DictViewModel(initalState, FakeDictRepository())

        withState(viewModel) { state ->
            assertEquals(true, state.isLoading)
        }

        viewModel.filterRelay.accept("")

        withState(viewModel) { state ->
            assertEquals(3, state.fullList.size)
            assertEquals(3, state.filteredList.size)
            assertEquals(false, state.isLoading)
        }

        viewModel.filterRelay.accept("amé")

        withState(viewModel) { state ->
            println("filtered ${state.filteredList} || relay ${viewModel.filterRelay.value}")
            assertEquals(3, state.fullList.size)
            assertEquals(1, state.filteredList.size)
            assertEquals(false, state.isLoading)
        }
    }


    private class FakeDictRepository : DictDataSource {
        override fun getItems(): Observable<List<GifItem>> = Observable.just(
            listOf(
                GifItem("DeliriousBitterIcelandicsheepdog", "Amém"),
                GifItem("OfficialSpotlessCurassow", "Ídolo"),
                GifItem("DistortedKindlyArmedcrab", "Romanos")
            )
        ).delay(250, TimeUnit.MILLISECONDS)
    }
}