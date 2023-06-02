package com.weit.presentation.ui.example

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.weit.presentation.databinding.FragmentExampleBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ExampleFragment : BaseFragment<FragmentExampleBinding>(
    FragmentExampleBinding::inflate,
) {

    private val viewModel: ExampleViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.errorEvent.collectLatest {
                sendSnackBar(it.message.toString())
            }
        }
    }
}
