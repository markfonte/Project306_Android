package com.spotlightapp.spotlight_android.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.spotlightapp.spotlight_android.R
import com.spotlightapp.spotlight_android.adapter.RankingRecyclerAdapter
import com.spotlightapp.spotlight_android.databinding.FragmentRankingBinding
import com.spotlightapp.spotlight_android.util.DC
import com.spotlightapp.spotlight_android.util.InjectorUtils
import com.spotlightapp.spotlight_android.util.RankingDatum
import com.spotlightapp.spotlight_android.util.UserState
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : Fragment() {

    private lateinit var vm: RankingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val factory: RankingViewModelFactory = InjectorUtils.provideRankingViewModelFactory()
        vm = ViewModelProviders.of(this, factory).get(RankingViewModel::class.java)
        val binding: FragmentRankingBinding = DataBindingUtil.inflate<FragmentRankingBinding>(inflater, R.layout.fragment_ranking, container, false).apply {
            viewModel = vm
            lifecycleOwner = this@RankingFragment
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildRankingView()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).validateUser().observe(this, Observer { userState ->
            if (userState == enumValueOf<UserState>(UserState.ValuesNotSet.toString())) {
                (activity as MainActivity).navController.navigate(R.id.action_rankingFragment_to_onboardingFragment, null)
                vm.setBottomNavVisibility(false)
                vm.setAppBarVisibility(false)
                return@Observer
            }
            if (userState == enumValueOf<UserState>(UserState.LoggedIn.toString())) {
                vm.setBottomNavVisibility(true)
                vm.setAppBarVisibility(true)
                return@Observer
            }
            assert(userState == enumValueOf<UserState>(UserState.LoggedOut.toString()) || userState == enumValueOf<UserState>(UserState.EmailNotVerified.toString()))
        })
    }

    override fun onPause() {
        super.onPause()
        saveRankingToDatabase()
    }

    private fun saveRankingToDatabase() {
        vm.rankingAdapter?.let { rankingAdapter ->
            val updatedRanking = HashMap<String, Int>()
            for (i in rankingAdapter.rankingData) {
                updatedRanking[i.HouseId!!] = i.CurrentRank!!
            }
            vm.updateRanking(updatedRanking)
        }
    }

    private fun buildRankingView() {
        ranking_recycler_view.layoutManager = LinearLayoutManager(activity)
        if (vm.staticHouseData.value != null) {
            vm.getCurrentRanking().observe(this, Observer { taskResult ->
                if (taskResult != null) {
                    val staticHouseData = vm.staticHouseData.value as HashMap<String, HashMap<String, String>>
                    val currentRanking: HashMap<String, Int> = taskResult
                    val rankingData: ArrayList<RankingDatum> = arrayListOf()

                    var i = 0
                    for ((rankingKey, rankingValue) in currentRanking) {
                        val currentStaticHouseDatum = staticHouseData[rankingKey]
                        val currentRankingDatum = RankingDatum("", "", "", "", -2, "")
                        currentRankingDatum.HouseId = rankingKey
                        currentRankingDatum.CurrentRank = rankingValue
                        currentRankingDatum.DisplayName = currentStaticHouseDatum?.get("${DC.display_name}")
                        currentRankingDatum.GreekLetters = currentStaticHouseDatum?.get("${DC.greek_letters}")
                        currentRankingDatum.StreetAddress = currentStaticHouseDatum?.get("${DC.street_address}")
                        currentRankingDatum.HouseIndex = i.toString() //Probably will want to delete this
                        rankingData.add(currentRankingDatum)
                        ++i
                    }
                    rankingData.sortBy { rankingDatum -> rankingDatum.CurrentRank }
                    vm.rankingAdapter = RankingRecyclerAdapter(rankingData, context!!)
                    vm.rankingAdapter?.let { rankingAdapter ->
                        ranking_recycler_view.adapter = vm.rankingAdapter
                        // For drag animation
                        val touchHelper = ItemTouchHelper(DragManageAdapter(rankingAdapter,
                                ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
                                ItemTouchHelper.ACTION_STATE_DRAG))
                        touchHelper.attachToRecyclerView(ranking_recycler_view)
                    }
                    vm.isRankingToDisplay.value = !currentRanking.isNullOrEmpty()
                } else {
                    vm.isRankingToDisplay.value = false
                }
                vm.isDataLoading.value = false
            })

        }
    }

    class DragManageAdapter(private val adapter: RankingRecyclerAdapter, dragDirs: Int, swipeDirs: Int) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            adapter.swapItems(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            adapter.draggingFinished()
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }
}
