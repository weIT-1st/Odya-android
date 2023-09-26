package com.weit.presentation.ui.map.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.weit.presentation.BuildConfig
import com.weit.presentation.databinding.FragmentTempBottomSheetBinding
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TempBottomSheetFragment(
    private val placeId : String
): BottomSheetDialogFragment() {

    private val viewModel: TempBottomSheetViewModel by viewModels()

    private var _binding: FragmentTempBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val placesClient by lazy {
        if(!Places.isInitialized()){
            Places.initialize(requireContext(),
//                BuildConfig.GOOGLE_MAP_KEY
            "AIzaSyCddx_HSjudmzxKOlXovmxfjrrdPgPl1jk"
            )
        }
        Places.createClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTempBottomSheetBinding.inflate(inflater, container, false)
        return binding.run {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPlaceImage(placeId, placesClient)
//        viewModel.getPlaceInform(placeId)

        repeatOnStarted(viewLifecycleOwner){
            viewModel.placeImage.collectLatest { bitmap ->
                binding.ivTempPlaceTab.setImageBitmap(bitmap)
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.placeAddress.collectLatest { address ->
                binding.tvTempPlaceTabAddress.text = address
            }
        }

        repeatOnStarted(viewLifecycleOwner){
            viewModel.placeTitle.collectLatest { title ->
                binding.tvTempPlaceTabTitle.text = title
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
