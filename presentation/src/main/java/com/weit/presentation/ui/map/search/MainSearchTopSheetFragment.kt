package com.weit.presentation.ui.map.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogMainSearchBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainSearchTopSheetFragment(
    val onSearchPlace : (String) -> Unit
): DialogFragment() {

    private val viewModel: MainSearchTopSheetViewModel by viewModels()

    private var _binding: DialogMainSearchBinding? = null
    private val binding get() = _binding!!

    private val placePredictionAdapter = PlacePredictionAdapter{placeId, placeName ->
        onSearchPlace(placeId)
        viewModel.plusRecentPlaceSearch(placeName)
    }

    private val recentPlaceSearchAdapter = RecentPlaceSearchAdapter {
        viewModel.deleteSomethingRecentPlaceSearch(it)
    }
    private val odyaHotPlaceAdapter = OdyaHotPlaceAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_fullscreen)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMainSearchBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAutoCompleteRecyclerView()
        initRecentSearchRecyclerView()
        initOdyaHotPlaceRankRecyclerView()
        onEditFocusChange()

        repeatOnStarted(viewLifecycleOwner){
            viewModel.searchTerm.collectLatest { searchTerm ->
                viewModel.searchPlace(searchTerm)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.searchPlaceList.collectLatest { list ->
                placePredictionAdapter.submitList(list)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.recentSearchList.collectLatest { recentSearchPlace ->
                recentPlaceSearchAdapter.submitList(recentSearchPlace.toList())
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.odyaHotPlaceRank.collectLatest { odyaHotPlaceList ->
                odyaHotPlaceAdapter.submitList(odyaHotPlaceList)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaceSearchRecent.adapter = null
        binding.rvPlaceMainHotPlaceRank.adapter = null
        binding.rvPlaceSearchAutoComplete.adapter = null
        _binding = null
    }

    private fun onEditFocusChange(){
        binding.etPlaceSearchMain.setOnFocusChangeListener { _ , hasFocus ->

            binding.btnPlaceSearchCancel.setOnClickListener {
                viewModel.setBTNPleaseSearchCancelOnClickListener(hasFocus)
            }

            binding.rvPlaceSearchAutoComplete.isVisible = hasFocus
            binding.tvPlaceSearchRecent.isGone = hasFocus
            binding.btnPlaceSearchDelete.isGone = hasFocus
            binding.rvPlaceSearchRecent.isGone = hasFocus
            binding.lyOdyaHotPlace.isGone = hasFocus
        }
    }

    private fun initAutoCompleteRecyclerView(){
        binding.rvPlaceSearchAutoComplete.adapter = placePredictionAdapter
    }

    private fun initRecentSearchRecyclerView(){
        binding.rvPlaceSearchRecent.adapter = recentPlaceSearchAdapter
        viewModel.getRecentPlaceSearch()
    }

    private fun initOdyaHotPlaceRankRecyclerView(){
        binding.rvPlaceMainHotPlaceRank.adapter = odyaHotPlaceAdapter
        binding.rvPlaceMainHotPlaceRank.layoutManager = GridLayoutManager(context, 5, GridLayoutManager.HORIZONTAL, false)
        viewModel.getOdyaHotPlaceRank()
    }

    private fun handleEvent(event: MainSearchTopSheetViewModel.Event){
        when (event) {
            MainSearchTopSheetViewModel.Event.ClinkSearchCancelHasFocus -> {binding.etPlaceSearchMain.clearFocus()}
            MainSearchTopSheetViewModel.Event.ClinkSearchCancelHasNotFocus -> {dismiss()}
            MainSearchTopSheetViewModel.Event.SuccessPlusRecentSearch -> {dismiss()}
        }
    }
}
