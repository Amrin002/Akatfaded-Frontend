package com.localclasstech.layanandesa.feature.beranda.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.fragment.app.activityViewModels
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentBerandaModernBinding
import com.localclasstech.layanandesa.databinding.FragmentBerandaSimpleBinding
import com.localclasstech.layanandesa.feature.beranda.view.helper.BerandaModernHelper
import com.localclasstech.layanandesa.feature.beranda.view.helper.BerandaSimpleHelper
import com.localclasstech.layanandesa.feature.beranda.viewmodel.BerandaViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper

class BerandaFragment : Fragment() {

    private lateinit var preferencesHelper: PreferencesHelper
    private var modernBinding : FragmentBerandaModernBinding? = null
    private var simpleBinding : FragmentBerandaSimpleBinding? = null
    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels(){
        SharedThemeViewModelFactory(preferencesHelper)
    }


    private val viewModel: BerandaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_beranda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedThemeViewModel.isModernTheme.observe(viewLifecycleOwner) { isModernTheme ->
            if (isModernTheme) {
                val stubModern = view.findViewById<ViewStub>(R.id.stubModern)
                if (stubModern.visibility == View.GONE) {
                    val inflated = stubModern.inflate()
                    modernBinding = FragmentBerandaModernBinding.bind(inflated)
                    BerandaModernHelper(context = requireContext(), binding = modernBinding!!).setupLogic()
                }
            } else {
                val stubSimple = view.findViewById<ViewStub>(R.id.stubSimple)
                if (stubSimple.visibility == View.GONE) {
                    val inflated = stubSimple.inflate()
                    simpleBinding = FragmentBerandaSimpleBinding.bind(inflated)
                    BerandaSimpleHelper(context = requireContext(), binding = simpleBinding!!).setupLogic()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }

}