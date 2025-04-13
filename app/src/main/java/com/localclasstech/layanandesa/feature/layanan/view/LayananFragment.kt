package com.localclasstech.layanandesa.feature.layanan.view

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import androidx.fragment.app.activityViewModels
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentBeritaModernBinding
import com.localclasstech.layanandesa.databinding.FragmentBeritaSimpleBinding
import com.localclasstech.layanandesa.databinding.FragmentLayananModernBinding
import com.localclasstech.layanandesa.databinding.FragmentLayananSimpleBinding
import com.localclasstech.layanandesa.feature.berita.view.helper.BeritaModernHelper
import com.localclasstech.layanandesa.feature.berita.view.helper.BeritaSimpleHelper
import com.localclasstech.layanandesa.feature.layanan.view.helper.LayananModernHeper
import com.localclasstech.layanandesa.feature.layanan.view.helper.LayananSimpleHelper
import com.localclasstech.layanandesa.feature.layanan.viewmodel.LayananViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper

class LayananFragment : Fragment() {
    private lateinit var preferencesHelper: PreferencesHelper
    private var modernBinding: FragmentLayananModernBinding? = null
    private var simpleBinding: FragmentLayananSimpleBinding? = null
    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels() {
        SharedThemeViewModelFactory(preferencesHelper)
    }

    companion object {
        fun newInstance() = LayananFragment()
    }

    private val viewModel: LayananViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_layanan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedThemeViewModel.isModernTheme.observe(viewLifecycleOwner) { isModernTheme ->
            if (isModernTheme) {
                val stubModern = view.findViewById<ViewStub>(R.id.layoutLayananModern)
                if (stubModern.visibility == View.GONE) {
                    val inflated = stubModern.inflate()
                    modernBinding = FragmentLayananModernBinding.bind(inflated)
                    LayananModernHeper(context = requireContext(), binding = modernBinding!!).setupLogic()
                }
            } else {
                val stubSimple = view.findViewById<ViewStub>(R.id.layoutLayananSimple)
                if (stubSimple.visibility == View.GONE) {
                    val inflated = stubSimple.inflate()
                    simpleBinding = FragmentLayananSimpleBinding.bind(inflated)
                    LayananSimpleHelper(context = requireContext(), binding = simpleBinding!!).setupLogic()
                }
            }
            }
        }
    }

