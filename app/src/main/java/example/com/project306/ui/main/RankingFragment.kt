package example.com.project306.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import example.com.project306.R
import example.com.project306.databinding.FragmentRankingBinding
import example.com.project306.util.InjectorUtils
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
        if ((activity as MainActivity).validateUser()) { //they are logged in
            vm.areValuesSet().observe(this, Observer {
                if (it == false) {
                    (activity as MainActivity).navController.navigate(R.id.action_rankingFragment_to_onboardingFragment, null)
                    vm.setBottomNavVisibility(false)
                    vm.setAppBarVisibility(false)
                } else {
                    vm.setBottomNavVisibility(true)
                    vm.setAppBarVisibility(true)
                }
            })
        }
    }

    private fun buildRankingView() {
        ranking_recycler_view.layoutManager = LinearLayoutManager(activity)
        if (vm.staticHouseData.value != null) {
            vm.getCurrentRanking().observe(this, Observer { currentRankingMap ->
                if (currentRankingMap != null) {
                    //TODO: implement firebase function to get ranking from db and use that result here
                }
            })

        }
    }

    companion object {
        private val LOG_TAG: String = RankingFragment::class.java.name
    }
}
