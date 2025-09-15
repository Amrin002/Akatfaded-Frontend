package com.localclasstech.layanandesa.feature.berita.view

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentDetailBeritaBinding
import com.localclasstech.layanandesa.feature.berita.viewmodel.DetailBeritaViewModel
import com.localclasstech.layanandesa.feature.berita.viewmodel.DetailBeritaViewModelFactory
import com.localclasstech.layanandesa.settings.utils.ImageLoader
import kotlinx.coroutines.launch
class DetailBeritaFragment : Fragment() {
    private var _binding: FragmentDetailBeritaBinding? = null
    private val binding get() = _binding!!
    private var skeletonView: View? = null

    companion object {
        private const val ARG_BERITA_ID = "berita_id"

        fun newInstance(beritaId: Int): DetailBeritaFragment {
            val fragment = DetailBeritaFragment()
            val args = Bundle()
            args.putInt(ARG_BERITA_ID, beritaId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: DetailBeritaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get berita ID from arguments
        val beritaId = arguments?.getInt(ARG_BERITA_ID) ?: -1

        // Create ViewModel using factory
        val factory = DetailBeritaViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, factory)[DetailBeritaViewModel::class.java]

        // Fetch detail berita
        if (beritaId != -1) {
            viewModel.fetchDetailBerita(beritaId)
        } else {
            Toast.makeText(requireContext(), "Invalid Berita ID", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBeritaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.shimmerDetailBerita.root.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.containerDetailBerita.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        // Observe berita detail
        viewModel.beritaDetail.observe(viewLifecycleOwner) { berita ->
            berita?.let {
                lifecycleScope.launch {
                    ImageLoader.loadImage(
                        requireContext(),
                        it.imgBerita,
                        binding.imageBerita
                    )

                    binding.tanggalBerita.text = it.tanggalBerita
                    binding.judulBerita.text = it.judulBerita

                    // Parse HTML content untuk TextView
                    binding.isiBerita.apply {
                        text = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            Html.fromHtml(it.kontenBerita, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            @Suppress("DEPRECATION")
                            Html.fromHtml(it.kontenBerita)
                        }
                        // Enable link clicking jika ada
                        movementMethod = LinkMovementMethod.getInstance()
                    }
                }
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        skeletonView = null
    }
}