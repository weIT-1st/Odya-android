package com.weit.presentation.ui.memory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.weit.presentation.databinding.FragmentMemoryBinding
import com.weit.presentation.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MemoryFragment : BaseFragment<FragmentMemoryBinding>(
    FragmentMemoryBinding::inflate,
) {

    private val viewModel: MemoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initListener() {
        binding.btnTravelLogWrite.setOnClickListener {
            val action = MemoryFragmentDirections.actionFragmentMemoryToPostGraph()
            findNavController().navigate(action)
        }
    }

    override fun initCollector() {
    }
}
