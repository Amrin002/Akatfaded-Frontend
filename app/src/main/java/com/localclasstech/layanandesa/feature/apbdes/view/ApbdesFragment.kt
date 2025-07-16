package com.localclasstech.layanandesa.feature.apbdes.view

import android.app.AlertDialog
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentApbdesBinding
import com.localclasstech.layanandesa.feature.apbdes.data.network.ApbdesItem
import com.localclasstech.layanandesa.feature.apbdes.viewmodel.ApbdesViewModel
import com.localclasstech.layanandesa.feature.apbdes.viewmodel.ApbdesViewModelFactory
import com.localclasstech.layanandesa.settings.utils.UrlConstant
import java.util.Calendar

class ApbdesFragment : Fragment() {

    private var _binding: FragmentApbdesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ApbdesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApbdesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupClickListeners()
        observeViewModel()
        setupPhotoView()
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupViewModel() {
        val factory = ApbdesViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[ApbdesViewModel::class.java]
    }

    private fun setupClickListeners() {
        // Back button


        // Filter tahun click listener
        binding.filterTahunApbdes.setOnClickListener {
            showYearPickerDialog()
        }
    }

    private fun setupPhotoView() {
        // Konfigurasi PhotoView jika diperlukan
        binding.imageApbdes.apply {
            // Set scale bounds untuk zoom
            setScaleLevels(1.0f, 3.0f, 5.0f) // minimum, medium, maximum zoom

            // Optional: Set click listener untuk PhotoView
            setOnPhotoTapListener { view, x, y ->
                // Handle tap pada foto jika diperlukan
            }

            // Optional: Set scale change listener
            setOnScaleChangeListener { scaleFactor, focusX, focusY ->
                // Handle scale change jika diperlukan
            }
        }
    }

    private fun observeViewModel() {
        // Observe available years for filter
        viewModel.availableYears.observe(viewLifecycleOwner) { years ->
            if (years.isNotEmpty()) {
                // Set tahun terbaru sebagai default jika belum ada yang dipilih
                val currentSelectedYear = viewModel.selectedYear.value
                if (currentSelectedYear == null || !years.contains(currentSelectedYear)) {
                    viewModel.setSelectedYear(years.first())
                }
            }
        }

        // Observe selected year
        viewModel.selectedYear.observe(viewLifecycleOwner) { year ->
            binding.txtTahun.text = year.toString()
        }

        // Observe selected APBDes data
        viewModel.selectedApbdes.observe(viewLifecycleOwner) { apbdes ->
            apbdes?.let { updateUIWithApbdesData(it) }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator jika ada
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun updateUIWithApbdesData(apbdes: ApbdesItem) {
        // Update TextViews dengan data dari server
        binding.biayaPendapatan.text = viewModel.formatCurrency(apbdes.pendapatan)
        binding.penyelenggaraBiaya.text = viewModel.formatCurrency(apbdes.penyelenggaraan)
        binding.pelaksanaanPembangunanBiaya.text = viewModel.formatCurrency(apbdes.pelaksanaan)
        binding.pembinaanMasyarakatBiaya.text = viewModel.formatCurrency(apbdes.pembinaan)
        binding.penanggulanBencanaBiaya.text = viewModel.formatCurrency(apbdes.penanggulangan)

        // Load image using UrlConstant utility
        loadImageWithGlide(apbdes.file)
    }

    private fun loadImageWithGlide(imagePath: String?) {
        // Gunakan UrlConstant untuk mendapatkan URL gambar yang valid
        val validImageUrl = UrlConstant.getValidImageUrl(imagePath, fallbackToDefault = true)

        // Untuk PhotoView, gunakan Glide dengan cara yang berbeda
        val requestOptions = RequestOptions()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_close_clear_cancel)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()

        Glide.with(this)
            .load(validImageUrl)
            .apply(requestOptions)
            .into(binding.imageApbdes) // PhotoView masih bisa menggunakan .into()
    }

    private fun showYearPickerDialog() {
        val years = viewModel.availableYears.value ?: return
        val currentYear = viewModel.selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR)

        val yearStrings = years.map { it.toString() }.toTypedArray()
        val currentIndex = years.indexOf(currentYear)

        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Tahun")
            .setSingleChoiceItems(yearStrings, currentIndex) { dialog, which ->
                val selectedYear = years[which]
                viewModel.setSelectedYear(selectedYear)
                dialog.dismiss()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}