package com.example.healthtracker.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.healthtracker.databinding.FragmentHomeBinding
import com.example.healthtracker.ui.setRoundedCorners

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel:HomeViewModel by activityViewModels()
    //FIXME check the comments in WalkViewModel
    val walkViewModel:WalkViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walkViewModel.walkingStart(requireContext())

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val weightRecyclerVal = WeightRecyclerAdapter()

        binding.apply {
            //FIXME styling is best applied in the layout definition, in this case in the XML file
            // You can set a rounded corners drawable as background to the containers and set the desired
            // background tint on it. The default outlineProvider is set to background, so the view will
            // drop the expected shadow when elevation is applied. Also, if you want your UI
            // to look consistent across devices - this will not work, as you are setting the corner radius to
            // 30px and it will be different on different densities.
            // If you want to stick to this implementation you need to pass dps. An easy way to do this is to
            // define the radius value in dimens.xml and then use resources.getDimensionPixelSize(resId)
            weight.setRoundedCorners(30F)
            water.setRoundedCorners(30F)
            sleep.setRoundedCorners(30F)
            walkViewModel.walkService.currentSteps.observe(viewLifecycleOwner) {
                stepcount.apply {
                    //FIXME use string resources
                    text = "Steps: $it"
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
                    //FIXME use string resources
                    calorieCount.text = "Calories: $it"
                    caloriesProgressBar.apply {
                        setProgressWithAnimation(it.toFloat())
                        progressMax = 240f

                    }

                }
            }
        }
            weightRecycler.apply {
                adapter = weightRecyclerVal

                layoutManager = GridLayoutManager(context,  1, GridLayoutManager.VERTICAL, false)
                //FIXME this does not make sense as declared here - you don't have to obtain the same references from the recycler view
                val fakelayoutmanager = this.layoutManager as GridLayoutManager
                val fakeadapter = adapter

                if (fakeadapter is WeightRecyclerAdapter) {
                    scaleImage.setOnClickListener { var middleItem =
                        fakeadapter.getItem((fakelayoutmanager.findFirstVisibleItemPosition() + fakelayoutmanager.findLastVisibleItemPosition())/2)
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

    fun findMid(){

    }

}