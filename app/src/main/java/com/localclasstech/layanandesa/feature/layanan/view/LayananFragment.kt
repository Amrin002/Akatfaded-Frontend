package com.localclasstech.layanandesa.feature.layanan.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModel
import com.localclasstech.layanandesa.auth.viewmodel.LoginViewModelFactory
import com.localclasstech.layanandesa.databinding.FragmentLayananBinding
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper.SuratKtmAdater
import com.localclasstech.layanandesa.feature.layanan.viewmodel.LayananViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.LayananViewModelFactory
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper

class LayananFragment : Fragment() {
    private lateinit var preferencesHelper: PreferencesHelper
    private var _binding: FragmentLayananBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LayananViewModel

    private val sharedThemeViewModel: SharedThemeViewModel by activityViewModels() {
        SharedThemeViewModelFactory(preferencesHelper)
    }

    private val loginViewModel: LoginViewModel by activityViewModels {
        LoginViewModelFactory(requireContext())
    }

    companion object {
        fun newInstance() = LayananFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi helper dulu
        preferencesHelper = PreferencesHelper(requireContext())

        // Baru inisialisasi ViewModel
        viewModel = ViewModelProvider(
            this,
            LayananViewModelFactory(
                SuratKtmRepository(RetrofitClient.suratApiService, preferencesHelper),
                SuratDomisiliRepository(RetrofitClient.suratApiService, preferencesHelper)
            )
        )[LayananViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLayananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Memanggil fungsi untuk mengambil data surat
        viewModel.fetchSuratKtmByUser()
        // Membuat adapter untuk RecyclerView dan mengaturnya
        val adapter = SuratKtmAdater(cardSuratList = emptyList())
        binding.recyclerViewSuratItemKtm.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSuratItemKtm.adapter = adapter
        // Variabel untuk menyimpan status visibilitas RecyclerView
        var isRecyclerVisible = false
        // Menangani klik pada tombol dropdown untuk menyembunyikan atau menampilkan RecyclerView
        binding.dropdownSurat.setOnClickListener {
            isRecyclerVisible = !isRecyclerVisible
            // Menampilkan atau menyembunyikan RecyclerView berdasarkan isRecyclerVisible
            binding.recyclerViewSuratItemKtm.visibility = if (isRecyclerVisible) View.VISIBLE else View.GONE
            Log.d("RecyclerDebug", "RecyclerView Toggled: ${if (isRecyclerVisible) "Visible" else "Gone"}")
        }
        // Observer untuk mengamati perubahan data surat
        viewModel.suratListKtm.observe(viewLifecycleOwner) { list ->
            Log.d("RecyclerDebug", "Jumlah surat masuk: ${list.size}")
            adapter.updateDataKtm(list)  // Mengupdate data pada adapter
            binding.tvJumlahSurat.text = "(${list.size})"  // Menampilkan jumlah surat

            // Menampilkan RecyclerView setelah data tersedia
            if (list.isNotEmpty()) {
                // Jika ada data, tampilkan RecyclerView (terlepas dari status dropdown)
                binding.recyclerViewSuratItemKtm.visibility = View.VISIBLE
                Log.d("RecyclerDebug", "RecyclerView Visible")
            } else {
                // Jika tidak ada data, sembunyikan RecyclerView
                binding.recyclerViewSuratItemKtm.visibility = View.GONE
                Log.d("RecyclerDebug", "RecyclerView Gone")
            }
        }
        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
        binding.addSurat.setOnClickListener {
            showBottomSheetDialog()
        }

    }


    private fun showBottomSheetDialog() {
        val dialogView = layoutInflater.inflate(R.layout.layout_buat_surat_dialog, null)

        // Bungkus dengan FrameLayout untuk margin horizontal
        val padding = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
        val wrapper = FrameLayout(requireContext()).apply {
            setPadding(padding, 0, padding, 0)
            addView(dialogView)
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(wrapper) // <-- pakai wrapper di sini, BUKAN dialogView langsung

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        // Inisialisasi tombol dari dialogView (bukan wrapper)
        val btnDomisili = dialogView.findViewById<AppCompatButton>(R.id.addSuratDomisili)
        val btnKtm = dialogView.findViewById<AppCompatButton>(R.id.addSuratKtm)
        val btnKtu = dialogView.findViewById<AppCompatButton>(R.id.addSuratKtu)
        val btnLainnya = dialogView.findViewById<AppCompatButton>(R.id.addSuratLainnya)
        val btnClose = dialogView.findViewById<AppCompatImageButton>(R.id.closeBtn)

        btnDomisili.setOnClickListener {
            Toast.makeText(requireContext(), "Surat Domisili dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnKtm.setOnClickListener {
            Toast.makeText(requireContext(), "Surat KTM dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnKtu.setOnClickListener {
            Toast.makeText(requireContext(), "Surat KTU dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnLainnya.setOnClickListener {
            Toast.makeText(requireContext(), "Surat Lainnya dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



}

