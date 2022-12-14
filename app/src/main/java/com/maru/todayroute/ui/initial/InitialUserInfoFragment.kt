package com.maru.todayroute.ui.initial

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maru.data.model.Gender
import com.maru.todayroute.R
import com.maru.todayroute.databinding.FragmentInitialUserInfoBinding
import com.maru.todayroute.util.BaseFragment
import com.maru.todayroute.util.Utils.convertSingleToDoubleDigit
import kotlinx.coroutines.launch
import java.util.*

class InitialUserInfoFragment :
    BaseFragment<FragmentInitialUserInfoBinding>(R.layout.fragment_initial_user_info) {

    private val viewModel by activityViewModels<InitialViewModel>()

    private val calendar: Calendar = GregorianCalendar()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var date = calendar.get(Calendar.DATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtonClickListener()
        setupObserver()
    }

    private fun setupButtonClickListener() {
        binding.btnNext.setOnClickListener {
            setExtraUserInfo()
            registerNewUser()
        }

        binding.etUserBirthday.setOnClickListener {
            DatePickerDialog(requireContext(),
                { _, year, m, d ->
                    val month = (m + 1).toString().convertSingleToDoubleDigit()
                    val dayOfMonth = d.toString().convertSingleToDoubleDigit()
                    binding.etUserBirthday.setText("${year}-${month}-${dayOfMonth}")
                    this.year = year
                    this.month = m
                    date = d
                },
                year,
                month,
                date
            ).show()
        }
    }

    private fun setupObserver() {
        viewModel.moveToConnectCoupleFragment.observe(viewLifecycleOwner) {
            findNavController().navigate(R.id.action_initialUserInfoFragment_to_connectCoupleFragment)
        }
    }

    private fun setExtraUserInfo() {
        when (binding.rgGender.checkedRadioButtonId) {
            R.id.rb_female -> viewModel.setUserGender(Gender.F)
            R.id.rb_male -> viewModel.setUserGender(Gender.M)
        }
        viewModel.setUserBirthday(binding.etUserBirthday.text.toString())
    }

    private fun registerNewUser() {
        lifecycleScope.launch {
            viewModel.registerNewUser()
        }
    }
}