package com.weit.presentation.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding>(
    private val bindingFactory: (LayoutInflater) -> VB,
) : AppCompatActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingFactory(layoutInflater)
        setContentView(binding.root)
        initListener()
        initCollector()
    }

    protected open fun initListener() {
        // 호출을 BaseActivity로 위임하기 위한 빈 메소드 입니다.
    }

    protected open fun initCollector() {
        // 호출을 BaseActivity로 위임하기 위한 빈 메소드 입니다.
    }
}
