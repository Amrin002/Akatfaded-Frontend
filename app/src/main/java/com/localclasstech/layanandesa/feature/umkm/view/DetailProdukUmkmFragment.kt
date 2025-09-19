package com.localclasstech.layanandesa.feature.umkm.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDetailProdukUmkmBinding
import com.localclasstech.layanandesa.feature.umkm.viewmodel.DetailProdukUmkmViewModel
import com.localclasstech.layanandesa.feature.umkm.viewmodel.DetailProdukUmkmViewModelFactory
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper
import com.localclasstech.layanandesa.settings.utils.UrlConstant

class DetailProdukUmkmFragment : Fragment() {

    private var _binding: FragmentDetailProdukUmkmBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailProdukUmkmViewModel
    private lateinit var preferencesHelper: PreferencesHelper

    companion object {
        private const val ARG_UMKM_ID = "umkm_id"

        fun newInstance(umkmId: Int): DetailProdukUmkmFragment {
            val fragment = DetailProdukUmkmFragment()
            val args = Bundle()
            args.putInt(ARG_UMKM_ID, umkmId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize PreferencesHelper
        preferencesHelper = PreferencesHelper(requireContext())

        // Get UMKM ID from arguments
        val umkmId = arguments?.getInt(ARG_UMKM_ID) ?: -1

        // Setup ViewModel with Factory
        val factory = DetailProdukUmkmViewModelFactory(
            RetrofitClient.umkmApiService,
            preferencesHelper
        )
        viewModel = ViewModelProvider(this, factory)[DetailProdukUmkmViewModel::class.java]

        // Load detail UMKM
        if (umkmId != -1) {
            viewModel.loadUmkmDetail(umkmId)
        } else {
            Toast.makeText(requireContext(), "Invalid UMKM ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailProdukUmkmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // WhatsApp contact button
        binding.btnHubungiWhatsApp.setOnClickListener {
            val umkm = viewModel.umkmDetail.value
            umkm?.let {
                val message = viewModel.generateWhatsAppMessage(it)
                openWhatsApp(it.nomor_telepon, message)
            }
        }

        // Share button
        binding.btnShare.setOnClickListener {
            val umkm = viewModel.umkmDetail.value
            umkm?.let {
                val shareText = viewModel.generateShareText(it)
                shareContent(shareText)
            }
        }

        // Social media buttons
        binding.btnFacebook.setOnClickListener {
            val umkm = viewModel.umkmDetail.value
            umkm?.let {
                if (!it.link_facebook.isNullOrEmpty()) {
                    openUrl(it.link_facebook)
                }
            }
        }

        binding.btnInstagram.setOnClickListener {
            val umkm = viewModel.umkmDetail.value
            umkm?.let {
                if (!it.link_instagram.isNullOrEmpty()) {
                    openUrl(it.link_instagram)
                }
            }
        }

        binding.btnTiktok.setOnClickListener {
            val umkm = viewModel.umkmDetail.value
            umkm?.let {
                if (!it.link_tiktok.isNullOrEmpty()) {
                    openUrl(it.link_tiktok)
                }
            }
        }
    }

    private fun observeViewModel() {
        // Observe detail UMKM
        viewModel.umkmDetail.observe(viewLifecycleOwner) { umkm ->
            umkm?.let {
                populateData(it)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Bisa menampilkan shimmer atau progress bar
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun populateData(umkm: com.localclasstech.layanandesa.feature.umkm.data.Umkm) {
        with(binding) {
            // Set nama usaha dan produk
            tvNamaUsaha.text = umkm.nama_usaha
            tvNamaProduk.text = umkm.nama_produk

            // Set harga
            tvHarga.text = viewModel.formatPrice(umkm.harga_produk)

            // Set deskripsi
            tvDeskripsi.text = umkm.deskripsi_produk

            // Set kategori
            tvKategori.text = umkm.kategori_label

            // Set nomor telepon
            tvNomorTelepon.text = umkm.nomor_telepon

            // Load foto produk
            val fullImageUrl = UrlConstant.getValidImageUrl(umkm.foto_produk)
            Glide.with(requireContext())
                .load(fullImageUrl)
                .placeholder(R.drawable.default_umkm)
                .error(R.drawable.default_umkm)
                .into(ivFotoProduk)

            // Setup social media buttons visibility
            setupSocialMediaButtons(umkm)
        }
    }

    private fun setupSocialMediaButtons(umkm: com.localclasstech.layanandesa.feature.umkm.data.Umkm) {
        with(binding) {
            // Facebook
            btnFacebook.visibility = if (!umkm.link_facebook.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Instagram
            btnInstagram.visibility = if (!umkm.link_instagram.isNullOrEmpty()) View.VISIBLE else View.GONE

            // TikTok
            btnTiktok.visibility = if (!umkm.link_tiktok.isNullOrEmpty()) View.VISIBLE else View.GONE

            // Hide social media section if no links available
            if (!viewModel.hasSocialMediaLinks(umkm)) {
                labelSosmed.visibility = View.GONE
                layoutSocialMedia.visibility = View.GONE
            }
        }
    }

    private fun openWhatsApp(phoneNumber: String, message: String) {
        try {
            val convertedNumber = convertToWhatsAppFormat(phoneNumber)

            if (convertedNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Format nomor telepon tidak valid", Toast.LENGTH_SHORT).show()
                return
            }

            val url = "https://wa.me/$convertedNumber?text=${Uri.encode(message)}"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            // Cek apakah WhatsApp tersedia
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Fallback ke browser jika WhatsApp tidak terinstall
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka WhatsApp: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertToWhatsAppFormat(phoneNumber: String): String {
        if (phoneNumber.isBlank()) return ""

        // Bersihkan nomor dari semua karakter non-digit
        val cleanNumber = phoneNumber.replace(Regex("[^0-9]"), "")

        // Validasi panjang minimal
        if (cleanNumber.length < 8) return ""

        return when {
            // Format internasional Indonesia (62xxx)
            cleanNumber.startsWith("62") && cleanNumber.length >= 10 -> {
                // Validasi apakah setelah 62 dimulai dengan 8
                if (cleanNumber.substring(2).startsWith("8")) {
                    cleanNumber
                } else {
                    "" // Invalid Indonesian number
                }
            }

            // Format lokal dengan 0 (08xxx)
            cleanNumber.startsWith("0") && cleanNumber.length >= 9 -> {
                if (cleanNumber.startsWith("08")) {
                    "62${cleanNumber.substring(1)}"
                } else {
                    "" // Invalid format
                }
            }

            // Format tanpa 0 dan 62 (8xxx)
            cleanNumber.startsWith("8") && cleanNumber.length >= 8 -> {
                "62$cleanNumber"
            }

            // Format tidak dikenal
            else -> ""
        }
    }

    private fun shareContent(text: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan produk"))
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}