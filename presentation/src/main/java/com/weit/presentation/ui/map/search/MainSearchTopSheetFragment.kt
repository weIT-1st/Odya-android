package com.weit.presentation.ui.map.search

import android.os.Bundle
import android.util.Log
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
    val onSearchPlace: (String) -> Unit
) : DialogFragment() {

    private val viewModel: MainSearchTopSheetViewModel by viewModels()

    private var _binding: DialogMainSearchBinding? = null
    private val binding get() = _binding!!

    private val placePredictionAdapter = PlacePredictionAdapter { placeId, placeName ->
        onSearchPlace(placeId)
        viewModel.onSearchNewWord(placeName)
    }

    private val recentPlaceSearchAdapter =
        RecentPlaceSearchAdapter(
            { deleteWord -> viewModel.deleteRecentPlaceSearch(deleteWord) },
            { touchWord -> viewModel.onTouchRecentWord(touchWord) }
        )
    private val odyaHotPlaceAdapter = OdyaHotPlaceAdapter()

    private val hotPlaceRankLayoutManager =
        GridLayoutManager(context, 5, GridLayoutManager.HORIZONTAL, false)

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

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchTerm.collectLatest { searchTerm ->
                viewModel.searchPlace(searchTerm)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchPlaceList.collectLatest { list ->
                placePredictionAdapter.submitList(list)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.recentSearchWords.collectLatest { recentSearchPlace ->
                recentPlaceSearchAdapter.submitList(recentSearchPlace)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.odyaHotPlaceRank.collectLatest { odyaHotPlaceList ->
                odyaHotPlaceAdapter.submitList(odyaHotPlaceList)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.searchFocus.collectLatest { hasFocus ->
                binding.rvPlaceSearchAutoComplete.isVisible = hasFocus
                binding.tvPlaceSearchRecent.isGone = hasFocus
                binding.btnPlaceSearchDelete.isGone = hasFocus
                binding.rvPlaceSearchRecent.isGone = hasFocus
                binding.lyOdyaHotPlace.isGone = hasFocus
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handleEvent(event)
            }
        }
    }

    override fun onDestroy() {
        Log.d("test", "onDestroy")
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvPlaceSearchRecent.adapter = null
        binding.rvPlaceMainHotPlaceRank.adapter = null
        binding.rvPlaceMainHotPlaceRank.layoutManager = null
        binding.rvPlaceSearchAutoComplete.adapter = null
        _binding = null
    }

    private fun onEditFocusChange() {
        binding.etPlaceSearchMain.setOnFocusChangeListener { _, hasFocus ->
            viewModel.changeMainSearchFocus(hasFocus)
        }

        binding.btnPlaceSearchCancel.setOnClickListener {
            viewModel.setBTNPleaseSearchCancelOnClickListener()
        }
    }

    private fun initAutoCompleteRecyclerView() {
        binding.rvPlaceSearchAutoComplete.adapter = placePredictionAdapter
    }

    private fun initRecentSearchRecyclerView() {
        binding.rvPlaceSearchRecent.adapter = recentPlaceSearchAdapter
    }

    private fun initOdyaHotPlaceRankRecyclerView() {
        binding.rvPlaceMainHotPlaceRank.run {
            adapter = odyaHotPlaceAdapter
            layoutManager = hotPlaceRankLayoutManager
        }
    }


    private fun handleEvent(event: MainSearchTopSheetViewModel.Event) {
        when (event) {
            MainSearchTopSheetViewModel.Event.ClickSearchCancelHasFocus -> {
                binding.etPlaceSearchMain.clearFocus()
                viewModel.changeMainSearchFocus(binding.etPlaceSearchMain.hasFocus())
            }

            MainSearchTopSheetViewModel.Event.ClickSearchCancelHasNotFocus -> {
                dismiss()
            }

            MainSearchTopSheetViewModel.Event.SuccessPlusRecentSearch -> {
                dismiss()
            }
        }
    }
}
