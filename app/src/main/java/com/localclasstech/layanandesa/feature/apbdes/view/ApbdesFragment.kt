package com.localclasstech.layanandesa.feature.apbdes.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.feature.apbdes.viewmodel.ApbdesViewModel

class ApbdesFragment : Fragment() {

    companion object {
        fun newInstance() = ApbdesFragment()
    }

    private val viewModel: ApbdesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_apbdes, container, false)
    }
}