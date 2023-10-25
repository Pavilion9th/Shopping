package uz.itschool.shopping.ui

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.updateLayoutParams
import androidx.navigation.fragment.findNavController
import uz.itschool.shopping.R
import uz.itschool.shopping.adapter.ImageAdapter
import uz.itschool.shopping.databinding.FragmentProductBinding
import uz.itschool.shopping.model.MyBottomSheet
import uz.itschool.shopping.model.Product
import uz.itschool.shopping.service.SharedPrefHelper
import kotlin.math.roundToInt

private const val ARG_PARAM1 = "product"

class ProductFragment : Fragment() {
    private lateinit var product: Product
    private lateinit var binding: FragmentProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            product = it.getSerializable(ARG_PARAM1) as Product
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        handleBackPress()
        setView()

        binding.productScreenAddMb.setOnClickListener {
            MyBottomSheet(requireContext(), product, object : MyBottomSheet.BottomSheetInterface{
                override fun onAdd(product: Product, quantity: Int) {
                    val shared = SharedPrefHelper.getInstance(requireContext())
                    val bundle = Bundle()
                    bundle.putSerializable("product", product)
                    bundle.putInt("quantity", quantity)
                    if (shared.getUser() == null){
                        findNavController().navigate(R.id.action_productFragment_to_loginFragment, bundle)
                    }else{
                        findNavController().navigate(R.id.action_productFragment_to_cartFragment, bundle)
                    }
                }
            })
        }


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setView() {
        binding.productIamgeVp.adapter =
            ImageAdapter(product.images, binding.productIamgeVp, binding.productParentConstraint)
        binding.productScreenBackFab.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.productScreenTitle.text = product.title
        binding.productScreenBrand.text = product.brand
        binding.productScreenPrice.text = product.price.toString() + " $"
        binding.productScreenDescription.text = product.description
        binding.productScreenRating.text =
            ((product.rating * 10).roundToInt().toDouble() / 10).toString()
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.productIamgeVp.layoutParams.height == LayoutParams.MATCH_PARENT) {
                    binding.productIamgeVp.updateLayoutParams {
                        height = 200.toPx(requireContext())
                    }
                    binding.productParentConstraint.setBackgroundColor(Color.WHITE)
                } else {
                    findNavController().popBackStack()
                }
            }
        })
    }

    fun Int.toPx(context: Context) =
        this * context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

}