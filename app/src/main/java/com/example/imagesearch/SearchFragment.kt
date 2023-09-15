package com.example.imagesearch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagesearch.databinding.FragmentSearchBinding
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RvAdapter
    private val rvModelList = mutableListOf<RvModel>()
    private lateinit var viewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)

        adapter = RvAdapter(rvModelList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        // ViewModel 초기화
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // 검색 버튼
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
                saveSearchQuery(query)
                Log.d("버튼", "넘겨주고 있는겨 ~")
            }
        }

        // 아이템 데이터
        adapter.itemClick = object : RvAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val clickedItem = rvModelList[position]
                Log.d("아이템 클릭", "position: $position")

                val selectedItems = viewModel.selectedRvItem.value?.toMutableList() ?: mutableListOf()
                selectedItems.add(clickedItem)
                viewModel.selectedRvItem.value = selectedItems
                Toast.makeText(context, "선택되었다구!!!!", Toast.LENGTH_SHORT).show()
            }
        }

        val savedQuery = loadSearchQuery()
        if (savedQuery.isNotEmpty()) {
            binding.searchEditText.setText(savedQuery)
        }

        // 아이템 간격 조절
        val itemSpacingDecoration = ItemSpacingDecoration(30)
        binding.recyclerView.addItemDecoration(itemSpacingDecoration)

        return binding.root
    }

    // 데이터 추가하고 어뎁터 갱신하기
    private fun performSearch(query: String) {
        // ViewModel을 통해 API 요청
        viewModel.requestImageData(query).enqueue(object : Callback<RvModelList> {
            override fun onResponse(call: Call<RvModelList>, response: Response<RvModelList>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        Log.d("API 응답", "데이터 수: ${it.data.size}")
                        rvModelList.clear()
                        rvModelList.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Log.d("API 응답", "에러 응답 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<RvModelList>, t: Throwable) {
                Log.e("API 에러", "API 요청 실패", t)
            }
        })
    }
    // 검색어 저장
    private fun saveSearchQuery(query: String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("searchQuery", query)
        editor.apply()
    }
    // 검색어 볼러오기
    private fun loadSearchQuery(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("searchQuery", "") ?: ""
    }
}
