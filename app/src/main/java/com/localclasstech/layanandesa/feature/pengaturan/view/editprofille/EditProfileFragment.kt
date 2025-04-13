package com.localclasstech.layanandesa.feature.pengaturan.view.editprofille

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.localclasstech.layanandesa.R
import com.localclasstech.layanandesa.databinding.FragmentEditProfileBinding
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile.EditProfileViewModel
import com.localclasstech.layanandesa.feature.pengaturan.viewmodel.editprofile.EditProfileViewModelFactory
import com.localclasstech.layanandesa.network.ApiService
import com.localclasstech.layanandesa.network.RetrofitClient
import com.localclasstech.layanandesa.settings.PreferencesHelper


class EditProfileFragment : Fragment() {
    private var _binding:FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi ViewModel menggunakan viewModels()
    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(RetrofitClient.clientService, PreferencesHelper(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadUserData()

        viewModel.user.observe(viewLifecycleOwner){ user->
            user?.let {
                binding.etNamaEdit.setText(it.name)
                binding.etEmailEdit.setText(it.email)
                binding.etNikEdit.setText(it.nik)
                binding.etPasswordEdit.setText(it.password)
                binding.etNomorEdit.setText(it.no_telp)
            }



        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        //ini sabar doloh
        binding.btnSimpan.setOnClickListener {
            val name = binding.etNamaEdit.text.toString()
            val email = binding.etEmailEdit.text.toString()
            val nik = binding.etNikEdit.text.toString()
            val password = binding.etPasswordEdit.text.toString()
            val noTelp = binding.etNomorEdit.text.toString()
            // Tambahkan logging untuk semua nilai input
            Log.d("EditProfileFragment", "Data sebelum dikirim:")
            Log.d("EditProfileFragment", "UserID: ${viewModel.user.value?.id ?: 0}")
            Log.d("EditProfileFragment", "Nama: $name")
            Log.d("EditProfileFragment", "Email: $email")
            Log.d("EditProfileFragment", "NIK: $nik")
            Log.d("EditProfileFragment", "No Telp: $noTelp")
            Log.d("EditProfileFragment", "Password: ${if (password.isNotEmpty()) "***" else "Kosong"}")
            viewModel.updateUser(
                userId = viewModel.user.value?.id?: 0,
                name = name,
                email = email,
                nik = nik,
                password = password,
                noTelp = noTelp,
                imageFile = null
            )
        }
    }

}