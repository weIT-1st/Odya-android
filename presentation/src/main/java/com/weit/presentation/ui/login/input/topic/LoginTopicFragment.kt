package com.weit.presentation.ui.login.input.topic

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.weit.presentation.R
import com.weit.presentation.databinding.DialogLoginTopicBinding
import com.weit.presentation.ui.base.BaseFragment
import com.weit.presentation.ui.util.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginTopicFragment : BaseFragment<DialogLoginTopicBinding>(
    DialogLoginTopicBinding::inflate
) {
    private val viewModel: LoginTopicViewModel by viewModels()

    private val topicAdapter = LoginTopicAdapter { viewModel.updateFavoriteTopics(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setTopicRV()
    }

    override fun initCollector() {
        repeatOnStarted(viewLifecycleOwner) {
            viewModel.nickname.collectLatest { nickname ->
                val mainText: String = String.format(resources.getString(R.string.login_topic_question), nickname)
                val spannableStringBuilder = SpannableStringBuilder(mainText)
                spannableStringBuilder.apply {
                    setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary)),
                        0,
                        nickname.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                binding.tvLoginTopicQuestion.text = spannableStringBuilder
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.topics.collectLatest { list ->
                topicAdapter.submitList(list)
            }
        }

        repeatOnStarted(viewLifecycleOwner) {
            viewModel.event.collectLatest { event ->
                handelEvent(event)
            }
        }
    }

    private fun setTopicRV() {
        binding.rvLoginTopic.run {
            adapter = topicAdapter
            layoutManager = StaggeredGridLayoutManager(3, LinearLayoutManager.VERTICAL)
        }
    }

    private fun handelEvent(event: LoginTopicViewModel.Event) {
        when (event) {
            LoginTopicViewModel.Event.NeedToMinimumFavoriteTopicCount -> {
                sendSnackBar("관심 토픽을 3개이상 골라 주세요!")
            }
            LoginTopicViewModel.Event.RegisterTopicSuccess -> {
//                val action = LoginTopicFragmentDirections.actionLoginTopicFragmentToLoginFriendFragment()
//                findNavController().navigate(action)
            }
        }
    }
}
