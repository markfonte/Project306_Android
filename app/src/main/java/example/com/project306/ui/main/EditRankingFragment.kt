package example.com.project306.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment

class EditRankingFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).validateUser()
    }
}
