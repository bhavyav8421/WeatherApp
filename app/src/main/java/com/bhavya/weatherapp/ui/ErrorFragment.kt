package com.bhavya.weatherapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bhavya.weatherapp.R
import com.bhavya.weatherapp.databinding.ErrorFragmentBinding

class ErrorFragment : Fragment() {
    companion object {
        fun newInstance(data:String) : ErrorFragment{
            var bundle: Bundle = Bundle();
            bundle.putString("message", data);
            val  errorFragment = ErrorFragment()
            errorFragment.arguments = bundle
            return errorFragment;
        }
    }
    private lateinit var binding: ErrorFragmentBinding;
    private var callback: ErrorFragment.RetryCallback? = null;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ErrorFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ErrorFragment.RetryCallback) {
            callback = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments != null)
            binding.errorDescTextView.text = arguments?.getString(getString(R.string.message))
        binding.retryButton.setOnClickListener(View.OnClickListener {
            callback?.onRetry()
        })
    }

    interface RetryCallback {
        fun onRetry()
    }
}