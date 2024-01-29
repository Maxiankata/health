package com.example.healthtracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.setRoundedCorners
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val walkViewModel: WalkViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walkViewModel.walkingStart(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weightRecyclerVal = WeightRecyclerAdapter()
         val userDB = MainActivity.getDatabaseInstance(requireContext())
        val userDao = userDB.dao()
        lifecycleScope.launch{
            val user = homeViewModel.getUser()
            Log.d("CAUGHT USER", user.toString())
            if (user != null) {
                userDao.saveUser(user)

            }
        }
        binding.apply {
            weight.setRoundedCorners(30F)
            water.setRoundedCorners(30F)
            sleep.setRoundedCorners(30F)
            walkViewModel.walkService.currentSteps.observe(viewLifecycleOwner) {
                stepcount.apply {
                    text = buildString {
                        append(getString(R.string.steps))
                        append(it)
                    }
                    setOnLongClickListener {
                        walkViewModel.walkService.resetSteps()
                        true
                    }
                }
                stepsCircularProgressBar.apply {
                    setProgressWithAnimation(it.toFloat())
                    progressMax = 6000f
                }
                walkViewModel.walkService.caloriesBurned.observe(viewLifecycleOwner) {
                    binding.apply {
                        calorieCount.text = buildString {
                            append(getString(R.string.calories))
                            append(it)
                        }
                        caloriesProgressBar.apply {
                            setProgressWithAnimation(it.toFloat())
                            progressMax = 240f

                        }

                    }
                }
                plus.apply {
                    setOnClickListener {
                        // dao_water++, save dao, set new dao to the drinkCurrent, could also be from viewModel actually
                    }
                }
                minus.apply {
                    setOnClickListener {
                        // dao_water--, save dao, set new dao to the drinkCurrent, could also be from viewModel actually
                    }
                }

            }
            textView2.apply {
                text = buildString {
                    append("0") //capture and set water current water dao
                    append("/")
                    append("6") //capture and set water goal dao
                }
            }
            weightRecycler.apply {
                adapter = weightRecyclerVal

                layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
                val fakelayoutmanager = this.layoutManager as GridLayoutManager
                val fakeadapter = adapter

                if (fakeadapter is WeightRecyclerAdapter) {
                    scaleImage.setOnClickListener {
                        val middleItem =
                            fakeadapter.getItem((fakelayoutmanager.findFirstVisibleItemPosition() + fakelayoutmanager.findLastVisibleItemPosition()) / 2)
                        Log.d("Middle Item", middleItem)
                    }
                }
            }
//            secondWeightRecycler.apply{
//                adapter = weightRecyclerVal
//                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
////                    addItemDecoration(DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation ))
//                val fakelayoutmanager = this.layoutManager as LinearLayoutManager
//                val fakeadapter = adapter
//
//                if (fakeadapter is WeightRecyclerAdapter) {
//                    var middleItem =
//                        fakeadapter.getItem((fakelayoutmanager.findFirstVisibleItemPosition() + fakelayoutmanager.findLastVisibleItemPosition()))
//                }
//            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun findMid() {

    }

}