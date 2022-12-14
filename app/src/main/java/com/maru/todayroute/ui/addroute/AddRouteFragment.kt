package com.maru.todayroute.ui.addroute

import android.app.Activity.RESULT_OK
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.maru.todayroute.R
import com.maru.todayroute.databinding.FragmentAddRouteBinding
import com.maru.todayroute.util.BaseFragment
import com.maru.todayroute.util.MapViewLifecycleObserver
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRouteFragment : BaseFragment<FragmentAddRouteBinding>(R.layout.fragment_add_route),
    OnMapReadyCallback {

    private val args by navArgs<AddRouteFragmentArgs>()

    private val viewModel: AddRouteViewModel by viewModels()
    private lateinit var naverMap: NaverMap

    private val photoListAdapter by lazy { PhotoListAdapter() }
    private val photoList = mutableListOf<Bitmap>()

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data?.clipData != null) {
                    val data = result.data?.clipData
                    data?.let { clipData ->
                        val count = data.itemCount

                        if (10 < count || 10 < photoList.size + count) {
                            Toast.makeText(
                                requireContext(),
                                "????????? ?????? 10????????? ?????? ???????????????.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            for (i in 0 until clipData.itemCount) {
                                val uri = clipData.getItemAt(i).uri
                                if (28 <= Build.VERSION.SDK_INT) {
                                    val source = ImageDecoder.createSource(
                                        requireActivity().contentResolver,
                                        uri
                                    )
                                    photoList.add(ImageDecoder.decodeBitmap(source))
                                } else {
                                    photoList.add(
                                        MediaStore.Images.Media.getBitmap(
                                            requireActivity().contentResolver,
                                            uri
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ???????????? ??? ?????? ????????? ??????
                    result.data?.data?.let { uri ->
                        if (28 <= Build.VERSION.SDK_INT) {
                            val source =
                                ImageDecoder.createSource(requireActivity().contentResolver, uri)
                            photoList.add(ImageDecoder.decodeBitmap(source))
                        } else {
                            photoList.add(
                                MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    uri
                                )
                            )
                        }
                    }
                }

                photoListAdapter.submitList(ArrayList(photoList))
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycle.addObserver(
            MapViewLifecycleObserver(
                binding.mvMap,
                savedInstanceState
            )
        )
        viewModel.setGeoCoordList(args.geoCoordArray.toList())
        binding.mvMap.getMapAsync(this)
        setupButtonClickListener()
        setUpObserver()
        addTextChangedListener()
        initRecyclerViewAdapter()
    }

    private fun initRecyclerViewAdapter() {
        binding.rvPhotoList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = photoListAdapter
        }
    }

    private fun addTextChangedListener() {
        binding.etLocation.doAfterTextChanged { input ->
            if (input != null && input.isEmpty()) {
                binding.etLocation.hint = "?????? ??????"
            }
        }

        binding.etTitle.doAfterTextChanged { input ->
            if (input != null && input.isEmpty()) {
                binding.etLocation.hint = "??????"
            }
        }

        binding.etContents.doAfterTextChanged { input ->
            if (input != null && input.isEmpty()) {
                binding.etLocation.hint = "????????? ????????? ?????? ????????? ??????????????????!"
            }
        }
    }

    private fun setupButtonClickListener() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.tvLocation.setOnClickListener {
//            showAddLocationDialog()
//        }

        binding.btnAddPhotos.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MediaStore.Images.Media.CONTENT_TYPE
            )
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            activityResultLauncher.launch(intent)
        }
    }

//    private fun showAddLocationDialog() {
//        val container = FrameLayout(requireContext())
//        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//        params.topMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
//
//        val editText = EditText(context)
//        editText.layoutParams = params
//        container.addView(editText)
//
//        val alertDialogBuilder = AlertDialog.Builder(requireContext())
//            .setTitle("?????? ??????")
//            .setView(container)
//            .setIcon(R.drawable.ic_baseline_location_on_24)
//            .setPositiveButton("??????") { dialog, _ ->
//                binding.tvLocation.text = editText.text.toString()
//                dialog.dismiss()
//            }
//        alertDialogBuilder.show()
//    }

    private fun setUpObserver() {
        viewModel.drawRoute.observe(viewLifecycleOwner) { geoCoordList ->
            drawRoute(geoCoordList)
        }

        viewModel.centerCoord.observe(viewLifecycleOwner) { center ->
            moveCameraToCenterCoordinate(center)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        with(viewModel) {
            getCenterCoordinate()
            drawRoute()
        }
        unableGesturesExceptZoom()
    }

    private fun drawRoute(geoCoordList: List<LatLng>) {
        val path = PathOverlay()
        path.color = Color.rgb(114, 149, 185)
        path.outlineColor = Color.rgb(114, 149, 185)
        path.width = 20
        path.coords = geoCoordList
        path.map = naverMap
    }

    private fun moveCameraToCenterCoordinate(center: LatLng) {
        val cameraUpdate =
            CameraUpdate.scrollTo(center).animate(CameraAnimation.Easing)
        naverMap.moveCamera(CameraUpdate.zoomTo(14.0))
        naverMap.moveCamera(cameraUpdate)
    }

    private fun unableGesturesExceptZoom() {
        with(naverMap.uiSettings) {
            isScrollGesturesEnabled = false
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mvMap.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mvMap.onLowMemory()
    }
}