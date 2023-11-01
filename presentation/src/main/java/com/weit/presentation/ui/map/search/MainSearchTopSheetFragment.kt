package com.weit.presentation.ui.map.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        GlobalScope.launch(Dispatchers.IO){
            viewModel.plusRecentPlaceSearch(placeName)
        }.invokeOnCompletion {
            dismiss()
        }
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onEditFocusChange(){
        binding.etPlaceSearchMain.setOnFocusChangeListener { _ , hasFocus ->
            if (hasFocus){
                binding.rvPlaceSearchAutoComplete.visibility = View.VISIBLE
                binding.tvPlaceSearchRecent.visibility = View.GONE
                binding.btnPlaceSearchDelete.visibility = View.GONE
                binding.rvPlaceSearchRecent.visibility = View.GONE
                binding.lyOdyaHotPlace.visibility = View.GONE
                binding.btnPlaceSearchCancel.setOnClickListener {
                    binding.etPlaceSearchMain.clearFocus()
                }
            } else {
                binding.rvPlaceSearchAutoComplete.visibility = View.GONE
                binding.tvPlaceSearchRecent.visibility = View.VISIBLE
                binding.btnPlaceSearchDelete.visibility = View.VISIBLE
                binding.rvPlaceSearchRecent.visibility = View.VISIBLE
                binding.lyOdyaHotPlace.visibility = View.VISIBLE
                binding.btnPlaceSearchCancel.setOnClickListener {
                    dismiss()
                }
            }
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
}
