package com.localclasstech.layanandesa.feature.layanan.view.surat

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.feature.layanan.viewmodel.surat.SuratDomisiliViewModel

class SuratDomisiliFragment : Fragment() {

    companion object {
        fun newInstance() = SuratDomisiliFragment()
    }

    private val viewModel: SuratDomisiliViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_surat_domisili, container, false)
    }
}