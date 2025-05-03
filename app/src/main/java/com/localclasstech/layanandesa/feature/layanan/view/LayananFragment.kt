package com.localclasstech.layanandesa.feature.layanan.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.localclasstech.layanandesa.feature.layanan.data.DataClassCardSurat
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratDomisiliRepository
import com.localclasstech.layanandesa.feature.layanan.data.repository.SuratKtmRepository
import com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper.SuratKtmAdater
import com.localclasstech.layanandesa.feature.layanan.viewmodel.LayananViewModel
import com.localclasstech.layanandesa.feature.layanan.viewmodel.LayananViewModelFactory
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.SharedThemeViewModelFactory
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.settings.utils.Constant
import com.localclasstech.layanandesa.feature.layanan.view.helper.surathelper.SuratDomisiliAdapter
import com.localclasstech.layanandesa.feature.layanan.view.surat.SuratDomisiliFragment
import com.localclasstech.layanandesa.feature.layanan.view.surat.SuratKtmFragment


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

        // Inisialisasi adapter dan recycler view
        setupRecyclerView()

        // Mengatur status awal tampilan recycler view
        configureDropdownBehavior()

        // Mengamati perubahan data dan error
        observeViewModelData()

//        Loading SHimer
        observeLoadingState()
        // Menangani tombol tambah surat
        binding.addSurat.setOnClickListener {
            showBottomSheetDialog()
        }

        // Mengambil data surat dari server
        viewModel.fetchSuratKtmByUser(id)
        viewModel.fetchSuratDomisiliByUser(id)
    }

    private fun observeLoadingState() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                // Show shimmer effect
                binding.shimmerLayoutSurat.visibility = View.VISIBLE
                binding.shimmerLayoutSurat.startShimmer()

                // Hide recyclerviews and filter layouts during loading
                binding.recyclerViewSuratItemKtm.visibility = View.GONE
                binding.recyclerViewSuratItemDomisili.visibility = View.GONE

                // Hide filter layouts
                binding.layoutFilterSktm.visibility = View.GONE
                binding.layoutFilterSdomisili.visibility = View.GONE
            } else {
                // Hide shimmer effect
                binding.shimmerLayoutSurat.stopShimmer()
                binding.shimmerLayoutSurat.visibility = View.GONE

                // Show filter layouts after loading is complete
                binding.layoutFilterSktm.visibility = View.VISIBLE
                binding.layoutFilterSdomisili.visibility = View.VISIBLE

                // RecyclerViews will be managed by dropdown behavior
            }
        }
    }

    /**
     * Fungsi untuk menyiapkan RecyclerView dan adapter-nya
     */
    private fun setupRecyclerView() {
        val adapter = SuratKtmAdater(
            cardSuratList = emptyList(),
            object : SuratKtmAdater.OnAdapterListener {
                override fun onClick(cardSuratList: DataClassCardSurat) {
                    navigateToDetailSuratKtm(cardSuratList.id)
                }

                override fun onUpdete(cardSuratList: DataClassCardSurat) {
                    // Fungsi ini akan diimplementasikan nanti
                }
            }
        )
        binding.recyclerViewSuratItemKtm.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
            visibility = View.GONE // Sembunyikan recycler view saat awal
        }

//        Adapter Surat Domisili
        val adapterDomisili = SuratDomisiliAdapter(
            cardSuratList = emptyList(),
            object : SuratDomisiliAdapter.OnAdapterListener {
                override fun onClick(cardSuratList: DataClassCardSurat) {
                    navigateToDetailSuratDomisili(cardSuratList.id)
                }

                override fun onUpdete(cardSuratList: DataClassCardSurat) {
                    TODO("Not yet implemented")
                }
            }
        )
        binding.recyclerViewSuratItemDomisili.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapterDomisili
            visibility = View.GONE // Sembunyikan recycler view saat awal
        }
    }

    private fun navigateToDetailSuratDomisili(suratId: Int) {
        val bundle = Bundle().apply {
            putInt("id_surat", suratId)
            putInt("type", Constant.TYPE_DETAIL)
        }
        val fragment = SuratDomisiliFragment().apply {
            arguments = bundle
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()


    }

    /**
     * Fungsi untuk navigasi ke halaman detail surat
     * @param suratId ID surat yang akan ditampilkan detailnya
     */
    private fun navigateToDetailSuratKtm(suratId: Int) {
        // Siapkan bundle dengan ID surat dan tipe operasi
        val bundle = Bundle().apply {
            putInt("id_surat", suratId)
            putInt("type", Constant.TYPE_DETAIL)
        }

        // Buat instance fragment baru dengan bundle
        val fragment = SuratKtmFragment().apply {
            arguments = bundle
        }

        // Navigasi ke fragment detail
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }



    /**
     * Fungsi untuk mengatur perilaku dropdown surat
     */
    private fun configureDropdownBehavior() {
        // Variabel untuk menyimpan status visibilitas RecyclerView
        var isRecyclerVisible = false

        // Menangani klik pada area dropdown
        binding.layoutFilterSktm.setOnClickListener {
            // Toggle status visibilitas
            isRecyclerVisible = !isRecyclerVisible

            // Tampilkan atau sembunyikan RecyclerView sesuai status
            binding.recyclerViewSuratItemKtm.visibility =
                if (isRecyclerVisible) View.VISIBLE else View.GONE

            // Ubah ikon dropdown sesuai status
            binding.dropdownSurat.setImageResource(
                if (isRecyclerVisible) R.drawable.ic_dropdown_close
                else R.drawable.ic_dropdown
            )

            Log.d("RecyclerDebug", "RecyclerView Toggled: ${if (isRecyclerVisible) "Visible" else "Gone"}")
        }

        var isRecyclerVisibleDomisili = false
        binding.layoutFilterSdomisili.setOnClickListener{
            isRecyclerVisibleDomisili = !isRecyclerVisibleDomisili
            binding.recyclerViewSuratItemDomisili.visibility =
                if (isRecyclerVisibleDomisili) View.VISIBLE else View.GONE
            binding.dropdownSuratDomisili.setImageResource(
                if (isRecyclerVisibleDomisili) R.drawable.ic_dropdown_close
                else R.drawable.ic_dropdown
            )
            Log.d("RecyclerDebug", "RecyclerView Toggled: ${if (isRecyclerVisibleDomisili) "Visible" else "Gone"}")
        }
    }

    /**
     * Fungsi untuk mengamati perubahan data dari ViewModel
     */
    private fun observeViewModelData() {
        // Memantau perubahan data surat
        viewModel.suratListKtm.observe(viewLifecycleOwner) { list ->
            Log.d("RecyclerDebug", "Jumlah surat masuk: ${list.size}")

            // Update adapter dengan data baru
            (binding.recyclerViewSuratItemKtm.adapter as SuratKtmAdater).updateDataKtm(list)

            // Update label jumlah surat
            binding.tvJumlahSurat.text = "(${list.size})"

            // Jika dropdown terbuka dan tidak ada data, sembunyikan RecyclerView
            val isRecyclerVisible = binding.recyclerViewSuratItemKtm.visibility == View.VISIBLE
            if (isRecyclerVisible && list.isEmpty()) {
                binding.recyclerViewSuratItemKtm.visibility = View.GONE
            }
        }

        // Memantau pesan error
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (!errorMsg.isNullOrEmpty()) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.suratListDomisili.observe(viewLifecycleOwner) { list ->
            Log.d("RecyclerDebugDomisili", "Jumlah surat masuk: ${list.size}")
            (binding.recyclerViewSuratItemDomisili.adapter as SuratDomisiliAdapter).updateDataDomisili(
                list
            )
            binding.tvJumlahSuratDomisili.text = "(${list.size})"
            val isRecyclerVisible = binding.recyclerViewSuratItemDomisili.visibility == View.VISIBLE
            if (isRecyclerVisible && list.isEmpty()) {
                binding.recyclerViewSuratItemDomisili.visibility = View.GONE
            }

        }
    }

    /**
     * Menampilkan dialog bottom sheet untuk memilih jenis surat
     */
    private fun showBottomSheetDialog() {
        // Membuat tampilan dialog
        val dialogView = layoutInflater.inflate(R.layout.layout_buat_surat_dialog, null)

        // Bungkus dengan FrameLayout untuk margin horizontal
        val padding = resources.getDimensionPixelSize(R.dimen.dialog_horizontal_margin)
        val wrapper = FrameLayout(requireContext()).apply {
            setPadding(padding, 0, padding, 0)
            addView(dialogView)
        }

        // Inisialisasi dialog
        val dialog = Dialog(requireContext()).apply {
            setContentView(wrapper)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Inisialisasi tombol-tombol pada dialog
        setupDialogButtons(dialogView, dialog)

        // Tampilkan dialog
        dialog.show()
    }

    /**
     * Mengatur fungsi tombol-tombol pada dialog
     * @param dialogView Tampilan dialog
     * @param dialog Objek dialog
     */
    private fun setupDialogButtons(dialogView: View, dialog: Dialog) {
        // Inisialisasi tombol dari dialogView
        val btnDomisili = dialogView.findViewById<AppCompatButton>(R.id.addSuratDomisili)
        val btnKtm = dialogView.findViewById<AppCompatButton>(R.id.addSuratKtm)
        val btnKtu = dialogView.findViewById<AppCompatButton>(R.id.addSuratKtu)
        val btnLainnya = dialogView.findViewById<AppCompatButton>(R.id.addSuratLainnya)
        val btnClose = dialogView.findViewById<AppCompatImageButton>(R.id.closeBtn)

        // Tombol surat domisili
        btnDomisili.setOnClickListener {
            Toast.makeText(requireContext(), "Surat Domisili dipilih", Toast.LENGTH_SHORT).show()
            navigateToCreateSuratDomisili(Constant.TYPE_CREATE)
            dialog.dismiss()
        }

        // Tombol surat KTM
        btnKtm.setOnClickListener {
            Toast.makeText(requireContext(), "Surat KTM dipilih", Toast.LENGTH_SHORT).show()
            navigateToCreateSuratKtm(Constant.TYPE_CREATE)
            dialog.dismiss()
        }

        // Tombol surat KTU
        btnKtu.setOnClickListener {
            Toast.makeText(requireContext(), "Surat KTU dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Tombol surat lainnya
        btnLainnya.setOnClickListener {
            Toast.makeText(requireContext(), "Surat Lainnya dipilih", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        // Tombol tutup dialog
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
    }

    /**
     * Navigasi ke halaman pembuatan surat baru
     * @param type Tipe operasi (create, update, dll)
     */
    private fun navigateToCreateSuratKtm(type: Int) {
        // Siapkan bundle dengan tipe operasi
        val bundle = Bundle().apply {
            putInt("type", type)
        }

        // Buat instance fragment baru dengan bundle
        val fragment = SuratKtmFragment().apply {
            arguments = bundle
        }

        // Navigasi ke fragment pembuatan surat
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun navigateToCreateSuratDomisili(type: Int) {
        // Siapkan bundle dengan tipe operasi
        val bundle = Bundle().apply {
            putInt("type", type)
        }

        // Buat instance fragment baru dengan bundle
        val fragment = SuratDomisiliFragment().apply {
            arguments = bundle
        }

        // Navigasi ke fragment pembuatan surat
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentView, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.shimmerLayoutSurat.stopShimmer()
        _binding = null
    }


}

