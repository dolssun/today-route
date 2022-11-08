package com.maru.todayroute.ui.mypage

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.maru.todayroute.R

import com.maru.todayroute.databinding.FragmentMyPageBinding
import com.maru.todayroute.ui.MainViewModel
import com.maru.todayroute.util.BaseFragment
import com.maru.todayroute.util.Utils.convertSingleToDoubleDigit
import java.util.*

/*
data class Profiles(val name: String,
                    val Birth: String,
                    val gender: Int, ) {
}

 */
val coupleList = arrayListOf<Profiles>(
    Profiles("김은진", "1998-09-03", "여"),
    Profiles("정근욱","1999-02-14", "남")
)

//class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(R.layout.fragment_my_page) {
    private val activityViewModel: MainViewModel by activityViewModels()

    //activityViewModel.coupleInfo.

    private val calendar: Calendar = GregorianCalendar()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var date = calendar.get(Calendar.DATE)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.activityViewModel = activityViewModel




        binding.rvCoupleList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvCoupleList.setHasFixedSize(true)
        binding.rvCoupleList.adapter = ProfileAdapter(coupleList)


        binding.btnFirstdateEdit.setOnClickListener{
            DatePickerDialog(requireContext(),
                {_, year, m, d ->
                    val month = (m+1).toString().convertSingleToDoubleDigit()
                    val dayOfMonth = d.toString().convertSingleToDoubleDigit()
                    binding.tvFirstDate.setText("${year}-${month}-${dayOfMonth}")
                    this.year = year
                    this.month = m
                    date = d
            }, year, month, date).show()
        }

    }
}