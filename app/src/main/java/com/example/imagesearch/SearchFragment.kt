package com.example.imagesearch

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.imagesearch.databinding.FragmentSearchBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var adapter: RvAdapter
    private val rvModelList = mutableListOf<RvModel>()
    private lateinit var viewModel: SharedViewModel

    private lateinit var prefs : SharedPreferences
    private lateinit var editPrefs: SharedPreferences.Editor

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
        prefs = requireContext().getSharedPreferences("pref_file", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()

        // 검색 버튼
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString()
            if (query.isNotEmpty()) {
                performSearch(query)
                Log.d("버튼", "넘겨주고 있는겨 ~")
            }
        }

        // 이전 검색어 입력
        val lastSearchQuery = loadSearchQuery()
        if (lastSearchQuery.isNotEmpty()) {
            binding.searchEditText.setText(lastSearchQuery)
        }
        Log.d("마지막 검색어 : ", lastSearchQuery)

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
                        saveSearchQuery(query)
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
    private fun saveSearchQuery(query: String) {
        prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        editPrefs = prefs.edit()
        editPrefs.putString("searchQuery", query)
        editPrefs.apply()
    }
    private fun loadSearchQuery(): String {
        prefs = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("searchQuery", "") ?: ""
    }
}
