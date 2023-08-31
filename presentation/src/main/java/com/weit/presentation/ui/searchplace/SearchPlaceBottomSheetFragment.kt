package com.weit.presentation.ui.searchplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.databinding.FragmentBottomSheetPlaceSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchPlaceBottomSheetFragment(): BottomSheetDialogFragment() {

    private val viewModel: SearchPlaceBottomSheetViewModel by viewModels()
    private var _binding: FragmentBottomSheetPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private var pagerAdapter: FragmentStateAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetPlaceSearchBinding.inflate(inflater, container, false)
        setViewPager()
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.tabBsPlaceJourney.setOnClickListener {
//            pagerAdapter?.createFragment(0)
//        }
//
//        binding.tabBsPlaceReview.setOnClickListener {
//            pagerAdapter?.createFragment(1)
//        }
//
//        binding.tabBsPlaceCommunity.setOnClickListener {
//            pagerAdapter?.createFragment(2)
//        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        pagerAdapter = null
    }

    private fun setViewPager(){
        pagerAdapter = SearchPlaceBottomSheetAdapter(this)
        binding.vpBsPlace.adapter = pagerAdapter
    }
}
