package com.localclasstech.layanandesa.feature.berita.view

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
import com.localclasstech.layanandesa.databinding.FragmentBeritaModernBinding
import com.localclasstech.layanandesa.databinding.FragmentBeritaSimpleBinding
import com.localclasstech.layanandesa.feature.beranda.view.helper.BerandaModernHelper
import com.localclasstech.layanandesa.feature.beranda.view.helper.BerandaSimpleHelper
import com.localclasstech.layanandesa.feature.berita.view.helper.BeritaModernHelper
import com.localclasstech.layanandesa.feature.berita.view.helper.BeritaSimpleHelper
import com.localclasstech.layanandesa.feature.berita.viewmodel.BeritaViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.settings.PreferencesHelper

class BeritaFragment : Fragment() {
    private lateinit var preferencesHelper: PreferencesHelper

    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels(){
        SharedThemeViewModelFactory(preferencesHelper)
    }

    companion object {
        fun newInstance() = BeritaFragment()
    }

    private val viewModel: BeritaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesHelper = PreferencesHelper(requireContext())
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_berita, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}